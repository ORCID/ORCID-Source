/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.core.manager.WorkContributorManager;
import org.orcid.core.manager.WorkExternalIdentifierManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContributorAttributes;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkContributorEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.pojo.ajaxForm.Citation;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.ajaxForm.Work;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.orcid.pojo.ajaxForm.WorkTitle;
import org.orcid.utils.BibtexException;
import org.orcid.utils.BibtexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author rcpeters
 */
@Controller("worksController")
@RequestMapping(value = { "/works" })
public class WorksController extends BaseWorkspaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorksController.class);

    private static final String WORKS_MAP = "WORKS_MAP";

    @Resource
    private ThirdPartyImportManager thirdPartyImportManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource
    private ProfileWorkManager profileWorkManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private WorkManager workManager;

    @Resource
    private WorkContributorManager workContributorManager;

    @Resource
    private WorkExternalIdentifierManager workExternalIdentifierManager;

    /**
     * Removes a work from a profile
     * */
    @RequestMapping(value = "/works.json", method = RequestMethod.DELETE)
    public @ResponseBody
    Work removeWorkJson(HttpServletRequest request, @RequestBody Work work) {
        OrcidWork delWork = work.toOrcidWork();

        // Get cached profile
        OrcidProfile currentProfile = getEffectiveProfile();
        OrcidWorks works = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getOrcidWorks();
        Work deletedWork = new Work();
        if (works != null) {
            List<OrcidWork> workList = works.getOrcidWork();
            Iterator<OrcidWork> workIterator = workList.iterator();
            while (workIterator.hasNext()) {
                OrcidWork orcidWork = workIterator.next();
                if (delWork.equals(orcidWork)) {
                    workIterator.remove();
                    deletedWork = work;
                }
            }
            works.setOrcidWork(workList);
            currentProfile.getOrcidActivities().setOrcidWorks(works);
            profileWorkManager.removeWork(currentProfile.getOrcid().getValue(), work.getPutCode().getValue());
        }

        return deletedWork;
    }

    /**
     * List works associated with a profile
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/works.json", method = RequestMethod.GET)
    public @ResponseBody
    List<Work> getWorkJson(HttpServletRequest request, @RequestParam(value = "workIds") String workIdsStr) {
        List<Work> workList = new ArrayList<>();
        Work work = null;
        String[] workIds = workIdsStr.split(",");

        if (workIds != null) {
            HashMap<String, Work> worksMap = (HashMap<String, Work>) request.getSession().getAttribute(WORKS_MAP);
            // this should never happen, but just in case.
            if (worksMap == null) {
                createWorksIdList(request);
                worksMap = (HashMap<String, Work>) request.getSession().getAttribute(WORKS_MAP);
            }
            for (String workId : workIds) {
                work = worksMap.get(workId);
                workList.add(work);
            }
        }

        return workList;
    }

    /**
     * Returns a blank work
     * */
    @RequestMapping(value = "/work.json", method = RequestMethod.GET)
    public @ResponseBody
    Work getWork(HttpServletRequest request) {
        Work w = new Work();

        // work title and subtitle
        Text wtt = new Text();
        wtt.setRequired(true);
        WorkTitle wt = new WorkTitle();
        wt.setTitle(wtt);
        Text wst = new Text();
        wt.setSubtitle(wst);
        w.setWorkTitle(wt);

        // set citation text and type
        Citation c = new Citation();
        Text ctText = new Text();
        ctText.setValue(CitationType.FORMATTED_UNSPECIFIED.value());
        c.setCitationType(ctText);
        Text cText = new Text();
        c.setCitation(cText);
        w.setCitation(c);

        Text wTypeText = new Text();
        wTypeText.setValue("");
        wTypeText.setRequired(true);
        w.setWorkType(wTypeText);

        Date d = new Date();
        d.setDay("");
        d.setMonth("");
        d.setYear("");
        w.setPublicationDate(d);

        WorkExternalIdentifier wdi = new WorkExternalIdentifier();
        Text wdiT = new Text();
        Text wdiType = new Text();
        wdiType.setValue("");
        wdi.setWorkExternalIdentifierId(wdiT);
        wdi.setWorkExternalIdentifierType(wdiType);
        List<WorkExternalIdentifier> wdiL = new ArrayList<WorkExternalIdentifier>();
        wdiL.add(wdi);
        w.setWorkExternalIdentifiers(wdiL);

        Text uText = new Text();
        w.setUrl(uText);

        Contributor contr = new Contributor();
        List<Contributor> contrList = new ArrayList<Contributor>();
        Text rText = new Text();
        rText.setValue("");
        contr.setContributorRole(rText);

        Text sText = new Text();
        sText.setValue("");
        contr.setContributorSequence(sText);
        contrList.add(contr);
        w.setContributors(contrList);

        Text disText = new Text();
        w.setShortDescription(disText);

        OrcidProfile profile = getEffectiveProfile();
        Visibility v = Visibility.valueOf(profile.getOrcidInternal().getPreferences().getWorkVisibilityDefault().getValue());
        w.setVisibility(v);

        return w;
    }

    /**
     * Returns a blank work
     * */
    @RequestMapping(value = "/work.json", method = RequestMethod.POST)
    public @ResponseBody
    Work postWork(HttpServletRequest request, @RequestBody Work work) {
        work.setErrors(new ArrayList<String>());

        workCitationValidate(work);
        workWorkTitleTitleValidate(work);
        workWorkTitleSubtitleValidate(work);
        workdescriptionValidate(work);
        workWorkTypeValidate(work);
        workWorkExternalIdentifiersValidate(work);
        workUrlValidate(work);

        copyErrors(work.getCitation().getCitation(), work);
        copyErrors(work.getCitation().getCitationType(), work);
        copyErrors(work.getWorkTitle().getTitle(), work);
        copyErrors(work.getShortDescription(), work);
        copyErrors(work.getWorkTitle().getSubtitle(), work);
        copyErrors(work.getWorkType(), work);
        copyErrors(work.getUrl(), work);
        for (Contributor c : work.getContributors()) {
            copyErrors(c.getContributorRole(), work);
            copyErrors(c.getContributorSequence(), work);
        }

        for (WorkExternalIdentifier wId : work.getWorkExternalIdentifiers()) {
            copyErrors(wId.getWorkExternalIdentifierId(), work);
            copyErrors(wId.getWorkExternalIdentifierType(), work);
        }

        if (work.getErrors().size() == 0) {
            // Get current profile
            OrcidProfile currentProfile = getEffectiveProfile();
            OrcidWork newOw = work.toOrcidWork();
            newOw.setPutCode("-1"); // put codes of -1 override new works
                                    // visibility filtering settings.

            WorkEntity workEntity = toWorkEntity(newOw);
            // Create work
            workEntity = workManager.addWork(workEntity);

            if (work.getWorkExternalIdentifiers() != null) {
                for (WorkExternalIdentifier wei : work.getWorkExternalIdentifiers()) {
                    if (!PojoUtil.isEmpty(wei.getWorkExternalIdentifierId())) {
                        org.orcid.persistence.jpa.entities.WorkExternalIdentifierEntity newWeiJpa = new org.orcid.persistence.jpa.entities.WorkExternalIdentifierEntity();
                        newWeiJpa.setIdentifier(wei.getWorkExternalIdentifierId().getValue());
                        newWeiJpa.setDateCreated(new java.util.Date());
                        newWeiJpa.setIdentifierType(wei.toWorkExternalIdentifier().getWorkExternalIdentifierType());
                        newWeiJpa.setLastModified(new java.util.Date());
                        newWeiJpa.setWork(workEntity);

                        workExternalIdentifierManager.addWorkExternalIdentifier(newWeiJpa);
                    }
                }

            }

            // Create profile work relationship
            profileWorkManager.addProfileWork(currentProfile.getOrcid().getValue(), workEntity.getId(), newOw.getVisibility());

            // Set the id (put-code) to the new work
            newOw.setPutCode(String.valueOf(workEntity.getId()));

            // Check if the user have orcid activities, if not, initialize them
            if (currentProfile.getOrcidActivities() == null)
                currentProfile.setOrcidActivities(new OrcidActivities());
            // Check if the user have works, if not, initialize them
            if (currentProfile.getOrcidActivities().getOrcidWorks() == null)
                currentProfile.getOrcidActivities().setOrcidWorks(new OrcidWorks());

            // Add the new work to the list of works
            currentProfile.getOrcidActivities().getOrcidWorks().getOrcidWork().add(newOw);

        }

        return work;
    }

    /**
     * Gets an orcidWork and generates a workEntity
     * 
     * @param orcidWork
     *            The orcid work used to generate the work entity
     * @return a workEntity populated with the information from the workEntity
     * */
    private WorkEntity toWorkEntity(OrcidWork orcidWork) {
        WorkEntity workEntity = new WorkEntity();
        workEntity.setCitation(orcidWork.getWorkCitation().getCitation());
        workEntity.setCitationType(orcidWork.getWorkCitation().getWorkCitationType());
        workEntity.setDateCreated(new java.util.Date());
        workEntity.setDescription(orcidWork.getShortDescription());
        workEntity.setLastModified(new java.util.Date());
        workEntity.setPublicationDate(toFuzzyDate(orcidWork.getPublicationDate()));
        workEntity.setSubtitle(orcidWork.getWorkTitle().getSubtitle().getContent());
        workEntity.setTitle(orcidWork.getWorkTitle().getTitle().getContent());
        workEntity.setWorkType(orcidWork.getWorkType());
        workEntity.setWorkUrl(orcidWork.getUrl().getValue());
        WorkContributors workContributors = orcidWork.getWorkContributors();
        if (workContributors != null) {
            workEntity.setContributorsJson(JsonUtils.convertToJsonString(workContributors));
        }
        return workEntity;
    }

    /**
     * Old way of doing work contributors
     * 
     * Generate a list of work contributors entities based on the current
     * profile and the list of work contributors that comes from the user
     * request.
     * 
     * @param currentProfile
     *            The current logged in user
     * @param workContributors
     *            The work contributors that comes from the user request
     * @param workEntity
     *            The work is just created as part of the request
     * 
     * @return a list of work contributor entities
     * */
    private Set<WorkContributorEntity> toWorkContributorEntityList(OrcidProfile currentProfile, WorkContributors workContributors, WorkEntity workEntity) {
        if (workContributors == null || workContributors.getContributor() == null)
            return new TreeSet<WorkContributorEntity>();

        TreeSet<WorkContributorEntity> result = new TreeSet<WorkContributorEntity>();

        ContributorAttributes emptyContributorAttributes = new ContributorAttributes();
        org.orcid.jaxb.model.message.Contributor emptyContributor = new org.orcid.jaxb.model.message.Contributor();
        emptyContributor.setContributorAttributes(emptyContributorAttributes);

        for (org.orcid.jaxb.model.message.Contributor contributor : workContributors.getContributor()) {
            if (!contributor.equals(emptyContributor)) {
                WorkContributorEntity workContributorEntity = new WorkContributorEntity();
                workContributorEntity.setContributorEmail(currentProfile.getOrcidBio().getContactDetails().retrievePrimaryEmail() != null ? currentProfile.getOrcidBio()
                        .getContactDetails().retrievePrimaryEmail().getValue() : null);
                workContributorEntity.setProfile(new ProfileEntity(currentProfile.getOrcid().getValue()));
                workContributorEntity.setWork(workEntity);
                workContributorEntity.setCreditName(currentProfile.getOrcidBio().getPersonalDetails().getCreditName() != null ? currentProfile.getOrcidBio()
                        .getPersonalDetails().getCreditName().getContent() : null);

                ContributorAttributes contributorAttributes = contributor.getContributorAttributes();
                if (contributorAttributes != null) {
                    ContributorRole contributorRole = contributorAttributes.getContributorRole();
                    SequenceType contributorSequence = contributorAttributes.getContributorSequence();
                    workContributorEntity.setContributorRole(contributorRole);
                    workContributorEntity.setSequence(contributorSequence);
                }
                result.add(workContributorEntity);
            }
        }

        return result;
    }

    /**
     * Transform a PublicationDate into a PuzzyDate
     * 
     * @param publicationDate
     * 
     * @return a fuzzy date
     * */
    private PublicationDateEntity toFuzzyDate(PublicationDate publicationDate) {
        PublicationDateEntity fuzzyDate = new PublicationDateEntity();
        String year = publicationDate.getYear() == null ? null : publicationDate.getYear().getValue();
        String month = publicationDate.getMonth() == null ? null : publicationDate.getMonth().getValue();
        String day = publicationDate.getDay() == null ? null : publicationDate.getDay().getValue();
        if (year != null)
            fuzzyDate.setYear(Integer.valueOf(year));
        if (month != null)
            fuzzyDate.setMonth(Integer.valueOf(month));
        if (day != null)
            fuzzyDate.setDay(Integer.valueOf(day));
        return fuzzyDate;
    }

    @RequestMapping(value = "/work/workTitle/titleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workWorkTitleTitleValidate(@RequestBody Work work) {
        work.getWorkTitle().getTitle().setErrors(new ArrayList<String>());
        if (work.getWorkTitle().getTitle().getValue() == null || work.getWorkTitle().getTitle().getValue().trim().length() == 0) {
            setError(work.getWorkTitle().getTitle(), "NotBlank.manualWork.title");
        } else {
            if (work.getWorkTitle().getTitle().getValue().trim().length() > 1000) {
                setError(work.getWorkTitle().getTitle(), "manualWork.length_less_1000");
            }
        }
        return work;
    }

    @RequestMapping(value = "/work/workTitle/subtitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workWorkTitleSubtitleValidate(@RequestBody Work work) {
        work.getWorkTitle().getSubtitle().setErrors(new ArrayList<String>());
        if (work.getWorkTitle().getSubtitle().getValue() != null && work.getWorkTitle().getSubtitle().getValue().length() > 1000) {
            setError(work.getWorkTitle().getSubtitle(), "manualWork.length_less_1000");
        }
        return work;
    }

    @RequestMapping(value = "/work/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workUrlValidate(@RequestBody Work work) {
        work.getUrl().setErrors(new ArrayList<String>());
        if (work.getUrl().getValue() != null && work.getUrl().getValue().length() > 350) {
            setError(work.getUrl(), "manualWork.length_less_350");
        }
        return work;
    }

    @RequestMapping(value = "/work/descriptionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workdescriptionValidate(@RequestBody Work work) {
        work.getShortDescription().setErrors(new ArrayList<String>());
        if (work.getShortDescription().getValue() != null && work.getShortDescription().getValue().length() > 5000) {
            setError(work.getShortDescription(), "manualWork.length_less_5000");
        }
        return work;
    }

    @RequestMapping(value = "/work/workTypeValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workWorkTypeValidate(@RequestBody Work work) {
        work.getWorkType().setErrors(new ArrayList<String>());
        if (work.getWorkType().getValue() == null || work.getWorkType().getValue().trim().length() == 0) {
            setError(work.getWorkType(), "NotBlank.manualWork.workType");
        }

        return work;
    }

    @RequestMapping(value = "/work/workExternalIdentifiersValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workWorkExternalIdentifiersValidate(@RequestBody Work work) {
        for (WorkExternalIdentifier wId : work.getWorkExternalIdentifiers()) {
            wId.getWorkExternalIdentifierId().setErrors(new ArrayList<String>());
            wId.getWorkExternalIdentifierType().setErrors(new ArrayList<String>());
            // if has id type must be specified
            if (wId.getWorkExternalIdentifierId().getValue() != null && !wId.getWorkExternalIdentifierId().getValue().trim().equals("")
                    && (wId.getWorkExternalIdentifierType().getValue() == null || wId.getWorkExternalIdentifierType().getValue().equals(""))) {
                setError(wId.getWorkExternalIdentifierType(), "NotBlank.currentWorkExternalIds.idType");
            } else if (wId.getWorkExternalIdentifierId().getValue() != null && wId.getWorkExternalIdentifierId().getValue().length() > 2084) {
                setError(wId.getWorkExternalIdentifierId(), "manualWork.length_less_2084");
            }
            // if type is set a id must set
            if (wId.getWorkExternalIdentifierType().getValue() != null && !wId.getWorkExternalIdentifierType().getValue().trim().equals("")
                    && (wId.getWorkExternalIdentifierId().getValue() == null || wId.getWorkExternalIdentifierId().getValue().trim().equals(""))) {
                setError(wId.getWorkExternalIdentifierId(), "NotBlank.currentWorkExternalIds.id");
            }
        }

        return work;
    }

    @RequestMapping(value = "/work/citationValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workCitationValidate(@RequestBody Work work) {
        work.getCitation().getCitation().setErrors(new ArrayList<String>());
        work.getCitation().getCitationType().setErrors(new ArrayList<String>());

        // Citations must have a type if citation text has a vale
        if (PojoUtil.isEmpty(work.getCitation().getCitationType()) && !PojoUtil.isEmpty(work.getCitation().getCitation())) {
            setError(work.getCitation().getCitationType(), "NotBlank.manualWork.citationType");
        } else if (!work.getCitation().getCitationType().getValue().trim().equals(CitationType.FORMATTED_UNSPECIFIED.value())
                && !PojoUtil.isEmpty(work.getCitation().getCitationType())) {
            // citation should not be blank if citation type is set
            if (work.getCitation().getCitation() == null || work.getCitation().getCitation().getValue().trim().equals("")) {
                setError(work.getCitation().getCitation(), "NotBlank.manualWork.citation");
            }

            // if bibtext must be valid
            if (work.getCitation().getCitationType().getValue().equals(CitationType.BIBTEX.value())) {
                try {
                    BibtexUtils.validate(work.getCitation().getCitation().getValue());
                } catch (BibtexException e) {
                    setError(work.getCitation().getCitation(), "manualWork.bibtext.notValid");
                }
            }

        }

        return work;
    }

    private static void copyErrors(ErrorsInterface from, ErrorsInterface into) {
        for (String s : from.getErrors()) {
            into.getErrors().add(s);
        }
    }

    private void setError(ErrorsInterface ei, String msg) {
        ei.getErrors().add(getMessage(msg));
    }

    /**
     * List works associated with a profile
     * */
    @RequestMapping(value = "/workIds.json", method = RequestMethod.GET)
    public @ResponseBody
    List<String> getWorksJson(HttpServletRequest request) {
        // Get cached profile
        List<String> workIds = createWorksIdList(request);
        return workIds;
    }

    /**
     * created a work id list and sorts a map associated with the list in in the
     * session
     * 
     */
    private List<String> createWorksIdList(HttpServletRequest request) {
        OrcidProfile currentProfile = getEffectiveProfile();
        OrcidWorks orcidWorks = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getOrcidWorks();

        HashMap<String, Work> worksMap = new HashMap<String, Work>();
        List<String> workIds = new ArrayList<String>();
        if (orcidWorks != null) {
            for (OrcidWork work : orcidWorks.getOrcidWork()) {
                try {
                    worksMap.put(work.getPutCode(), Work.valueOf(work));
                    workIds.add(work.getPutCode());
                } catch (Exception e) {
                    LOGGER.error("ProfileWork failed to parse as Work. Put code" + work.getPutCode());
                }
            }
            request.getSession().setAttribute(WORKS_MAP, worksMap);
        }
        return workIds;
    }

    /**
     * Saves A work
     * */
    @RequestMapping(value = "/profileWork.json", method = RequestMethod.PUT)
    public @ResponseBody
    Work updateProfileWorkJson(HttpServletRequest request, @RequestBody Work work) {
        // Get cached profile
        OrcidWork ow = work.toOrcidWork();
        OrcidProfile currentProfile = getEffectiveProfile();
        OrcidWorks orcidWorks = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getOrcidWorks();
        if (orcidWorks != null) {
            List<OrcidWork> orcidWorksList = orcidWorks.getOrcidWork();
            if (orcidWorksList != null) {
                for (OrcidWork orcidWork : orcidWorksList) {
                    // If the put codes are equal, we know that they are the
                    // same work
                    if (orcidWork.getPutCode().equals(ow.getPutCode())) {
                        // Update the privacy of the work
                        profileWorkManager.updateWork(currentProfile.getOrcid().getValue(), ow.getPutCode(), ow.getVisibility());
                    }
                }
            }
        }
        return work;
    }
}