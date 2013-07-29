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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jbibtex.ParseException;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.frontend.web.util.NumberList;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.adapter.Jpa2JaxbAdapter;
import org.orcid.pojo.ThirdPartyRedirect;
import org.orcid.pojo.ajaxForm.Citation;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.Registration;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;
import org.springframework.web.util.HtmlUtils;

/**
 * @author Will Simpson
 */
@Controller("workspaceController")
@RequestMapping(value = { "/my-orcid", "/workspace" })
public class WorkspaceController extends BaseWorkspaceController {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceController.class);

    private static final String WORKS_MAP = "WORKS_MAP";

    @Resource
    private ThirdPartyImportManager thirdPartyImportManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;

    @Resource
    private ProfileWorkManager profileWorkManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @ModelAttribute("thirdPartiesForImport")
    public List<OrcidClient> retrieveThirdPartiesForImport() {
        return thirdPartyImportManager.findOrcidClientsWithPredefinedOauthScopeWorksImport();
    }
    
    @ModelAttribute("workTypes")
    public Map<String, String> retrieveWorkTypesAsMap() {
        Map<String, String> workTypes = new TreeMap<String, String>();
        workTypes.put("", "Pick a publication type");
        for (WorkType workType : WorkType.values()) {
            workTypes.put(workType.value(), StringUtils.capitalize(workType.value().replace('-', ' ')));
        }

        workTypes.remove(WorkType.BIBLE.value());
        return workTypes;
    }

    @ModelAttribute("citationTypes")
    public Map<String, String> retrieveTypesAsMap() {
        Map<String, String> citationTypes = new TreeMap<String, String>();
        citationTypes.put("", "Pick a citation type");
        for (CitationType citationType : CitationType.values()) {
            String value = citationType.value().replace("formatted-", "");
            citationTypes.put(citationType.value(), StringUtils.upperCase(value));
        }
        return citationTypes;
    }

    @ModelAttribute("years")
    public Map<String, String> retrieveYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList();
        map.put("", "Year");
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("months")
    public Map<String, String> retrieveMonthsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(12);
        map.put("", "Month");
        for (String month : list) {
            map.put(month, month);
        }
        return map;
    }

    @ModelAttribute("days")
    public Map<String, String> retrieveDaysAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(31);
        map.put("", "Day");
        for (String day : list) {
            map.put(day, day);
        }
        return map;
    }

    @ModelAttribute("idTypes")
    public Map<String, String> retrieveIdTypesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("", "What type of external ID?");
        for (WorkExternalIdentifierType type : WorkExternalIdentifierType.values()) {
            map.put(type.value(), type.description());
        }
        return map;
    }

    @ModelAttribute("roles")
    public Map<String, String> retrieveRolesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("", "What was your role?");
        for (ContributorRole contributorRole : ContributorRole.values()) {
            map.put(contributorRole.value(), StringUtils.capitalize(contributorRole.value().replaceAll("[-_]", " ")));
        }
        return map;
    }

    @ModelAttribute("sequences")
    public Map<String, String> retrieveSequencesAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        for (SequenceType sequenceType : SequenceType.values()) {
            map.put(sequenceType.value(), StringUtils.capitalize(sequenceType.value().replaceAll("[-]", " ")));
        }
        return map;
    }


    @RequestMapping
    public ModelAndView viewWorkspace(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults) {
        
        ModelAndView mav = new ModelAndView("workspace");
        mav.addObject("showPrivacy", true);

        OrcidProfile profile = getCurrentUserAndRefreshIfNecessary().getEffectiveProfile();
        getCurrentUser().setEffectiveProfile(profile);
        List<CurrentWork> currentWorks = getCurrentWorksFromProfile(profile);
        if (currentWorks != null && !currentWorks.isEmpty()) {
            mav.addObject("currentWorks", currentWorks);
        }
        mav.addObject("profile", profile);
        mav.addObject("baseUri",getBaseUri());
        mav.addObject("baseUriHttp",getBaseUriHttp());
        return mav;
    }

    /**
     * Retrieve all external identifiers as a json string
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/externalIdentifiers.json", method = RequestMethod.GET)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifiers getExternalIdentifiersJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        org.orcid.pojo.ExternalIdentifiers externalIdentifiers = new org.orcid.pojo.ExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers((List<org.orcid.pojo.ExternalIdentifier>) (Object) currentProfile.getOrcidBio().getExternalIdentifiers()
                .getExternalIdentifier());
        return externalIdentifiers;
    }

    @RequestMapping(value = "/sourceGrantReadWizard.json", method = RequestMethod.GET)
    public @ResponseBody
    ThirdPartyRedirect getSourceGrantReadWizard() {
        ThirdPartyRedirect tpr = new ThirdPartyRedirect();

        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        if (currentProfile.getOrcidHistory().getSource() == null)
            return tpr;
        SourceOrcid sourceOrcid = currentProfile.getOrcidHistory().getSource().getSourceOrcid();
        String sourcStr = sourceOrcid.getValue();
        List<OrcidClient> orcidClients = thirdPartyImportManager.findOrcidClientsWithPredefinedOauthScopeReadAccess();
        for (OrcidClient orcidClient : orcidClients) {
            if (sourcStr.equals(orcidClient.getClientId())) {
                RedirectUri ru = orcidClient.getRedirectUris().getRedirectUri().get(0);
                String redirect = getBaseUri() + "/oauth/authorize?client_id=" + orcidClient.getClientId() + "&response_type=code&scope=" + ru.getScopeAsSingleString()
                        + "&redirect_uri=" + ru.getValue();
                tpr.setUrl(redirect);
                tpr.setDisplayName(orcidClient.getDisplayName());
                tpr.setShortDescription(orcidClient.getShortDescription());
                return tpr;
            }
        }
        return tpr;
    }

    /**
     * Updates the list of external identifiers assigned to a user
     * */
    @RequestMapping(value = "/externalIdentifiers.json", method = RequestMethod.DELETE)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifier removeExternalIdentifierJson(HttpServletRequest request, @RequestBody org.orcid.pojo.ExternalIdentifier externalIdentifier) {
        List<String> errors = new ArrayList<String>();

        // If the orcid is blank, add an error
        if (externalIdentifier.getOrcid() == null || StringUtils.isBlank(externalIdentifier.getOrcid().getValue())) {
            errors.add(getMessage("ExternalIdentifier.orcid"));
        }

        // If the external identifier is blank, add an error
        if (externalIdentifier.getExternalIdReference() == null || StringUtils.isBlank(externalIdentifier.getExternalIdReference().getContent())) {
            errors.add(getMessage("ExternalIdentifier.externalIdReference"));
        }
        // Set errors to the external 
        externalIdentifier.setErrors(errors);

        if (errors.isEmpty()) {
            //Get cached profile
            OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
            ExternalIdentifiers externalIdentifiers = currentProfile.getOrcidBio().getExternalIdentifiers();
            List<ExternalIdentifier> externalIdentifiersList = externalIdentifiers.getExternalIdentifier();
            Iterator<ExternalIdentifier> externalIdentifierIterator = externalIdentifiersList.iterator();
            //Remove external identifier from the cached profile
            while (externalIdentifierIterator.hasNext()) {
                ExternalIdentifier existingExternalIdentifier = externalIdentifierIterator.next();
                if (existingExternalIdentifier.equals(externalIdentifier)) {
                    externalIdentifierIterator.remove();
                }
            }
            //Update cached profile
            currentProfile.getOrcidBio().setExternalIdentifiers(externalIdentifiers);
            //Remove external identifier
            externalIdentifierManager.removeExternalIdentifier(externalIdentifier.getOrcid().getValue(), externalIdentifier.getExternalIdReference().getContent());
        }

        return externalIdentifier;
    }

    /**
     * Removes a work from a profile
     * */
    @RequestMapping(value = "/works.json", method = RequestMethod.DELETE)
    public @ResponseBody
    Work removeWorkJson(HttpServletRequest request, @RequestBody Work work) {
        OrcidWork delWork = work.toOrcidWork();
         
        //Get cached profile
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
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
    public @ResponseBody List<Work> getWorkJson(HttpServletRequest request, @RequestParam(value = "workIds") String workIdsStr) {
        List<Work> workList = new ArrayList<>();
        Work work = null;
        String[] workIds = workIdsStr.split(",");
        
        if(workIds != null){
            HashMap<String,Work> worksMap = (HashMap<String,Work>)request.getSession().getAttribute(WORKS_MAP);
            // this should never happen, but just in case.
            if (worksMap == null) {
                createWorksIdList(request);
                worksMap = (HashMap<String,Work>)request.getSession().getAttribute(WORKS_MAP);
            }
            for (String workId: workIds) {
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
        wTypeText.setRequired(true);
        w.setWorkType(wTypeText);
        
        Date d = new Date();
        w.setPublicationDate(d);
        
        WorkExternalIdentifier wdi = new WorkExternalIdentifier();
        Text wdiT = new Text();
        Text wdiType = new Text();
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
        contr.setContributorRole(rText);
        
        Text sText= new Text();
        contr.setContributorSequence(sText);
        contrList.add(contr);
        w.setContributors(contrList);
        
        Text disText= new Text();
        w.setShortDescription(disText);
        
        OrcidProfile profile = getCurrentUser().getEffectiveProfile();
        Visibility v = Visibility.valueOf(profile.getOrcidInternal().getPreferences().getWorkVisibilityDefault().getValue());
        w.setVisibility(v);
        
        return w;
    }

    
    /**
     * Returns a blank work
     * */
    @RequestMapping(value = "/work.json", method = RequestMethod.POST)
    public @ResponseBody
    Work postWork(HttpServletRequest request,  @RequestBody Work work) {
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
        for (Contributor c:work.getContributors()) {
            copyErrors(c.getContributorRole(), work);
            copyErrors(c.getContributorSequence(), work);
        }
        
        for (WorkExternalIdentifier wId:work.getWorkExternalIdentifiers()) {
            copyErrors(wId.getWorkExternalIdentifierId(), work);
            copyErrors(wId.getWorkExternalIdentifierType(), work);
        }
        
        if (work.getErrors().size() == 0) {
            OrcidWork newOw = work.toOrcidWork();
            newOw.setPutCode("-1"); // put codes of -1 override new works visibility filtering settings.
            // Why do we have to save all the works?
            OrcidProfile profile = getCurrentUser().getEffectiveProfile();
            if (profile.getOrcidActivities() == null) 
                profile.setOrcidActivities(new OrcidActivities());
            if (profile.getOrcidActivities().getOrcidWorks() ==null)
                profile.getOrcidActivities().setOrcidWorks(new OrcidWorks());
            List<OrcidWork> owList = profile.getOrcidActivities().getOrcidWorks().getOrcidWork();
            owList.add(newOw);
            profile.getOrcidActivities().getOrcidWorks().setOrcidWork(owList);
            OrcidProfile updatedProfile = orcidProfileManager.updateOrcidWorks(profile);
            getCurrentUser().setEffectiveProfile(updatedProfile);
        }
        
        return work;
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
        if (work.getWorkTitle().getSubtitle().getValue() != null 
                && work.getWorkTitle().getSubtitle().getValue().length() > 1000) {
            setError(work.getWorkTitle().getSubtitle(), "manualWork.length_less_1000");
        }
        return work;
    }


    @RequestMapping(value = "/work/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workUrlValidate(@RequestBody Work work) {
        work.getUrl().setErrors(new ArrayList<String>());
        if (work.getUrl().getValue() != null 
                && work.getUrl().getValue().length() > 350) {
            setError(work.getUrl(), "manualWork.length_less_350");
        }
        return work;
    }


    @RequestMapping(value = "/work/descriptionValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workdescriptionValidate(@RequestBody Work work) {
        work.getShortDescription().setErrors(new ArrayList<String>());
        if (work.getShortDescription().getValue() != null 
                && work.getShortDescription().getValue().length() > 5000) {
            setError(work.getShortDescription(), "manualWork.length_less_5000");
        }
        return work;
    }
    
    @RequestMapping(value = {"/work/roleValidate.json","/work/sequenceValidate.json"}, method = RequestMethod.POST)
    public @ResponseBody
    Work workRoleCreditedValidate(@RequestBody Work work) {
        for (Contributor c:work.getContributors()) {
            c.getContributorSequence().setErrors(new ArrayList<String>());
            c.getContributorRole().setErrors(new ArrayList<String>());
            boolean emptyRole = c.getContributorRole()==null || c.getContributorRole().getValue()==null || c.getContributorRole().getValue().trim().isEmpty();
            boolean emptySequence = c.getContributorSequence()==null || c.getContributorSequence().getValue()==null || c.getContributorSequence().getValue().trim().isEmpty();
            if (emptyRole && !emptySequence) {
                setError(c.getContributorRole(), "NotBlank.currentWorkContributors.role");
            }
            if (emptySequence && !emptyRole) {
                setError(c.getContributorSequence(), "NotBlank.currentWorkContributors.sequence");
            }
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
        for (WorkExternalIdentifier wId:work.getWorkExternalIdentifiers()) {
            wId.getWorkExternalIdentifierId().setErrors(new ArrayList<String>());
            wId.getWorkExternalIdentifierType().setErrors(new ArrayList<String>());
            // if has id type must be specified 
            if (wId.getWorkExternalIdentifierId().getValue() != null
                    && !wId.getWorkExternalIdentifierId().getValue().trim().equals("")
                    && (wId.getWorkExternalIdentifierType().getValue() == null
                        || wId.getWorkExternalIdentifierType().getValue().equals(""))) {
                setError(wId.getWorkExternalIdentifierType(), "NotBlank.currentWorkExternalIds.idType"); 
            } else if (wId.getWorkExternalIdentifierId().getValue() != null 
                    && wId.getWorkExternalIdentifierId().getValue().length() > 2084) {
                setError(wId.getWorkExternalIdentifierId(), "manualWork.length_less_2084");
            }
            // if type is set a id must set
            if (wId.getWorkExternalIdentifierType().getValue() != null
                    && !wId.getWorkExternalIdentifierType().getValue().trim().equals("")
                    && (wId.getWorkExternalIdentifierId().getValue() == null
                         || wId.getWorkExternalIdentifierId().getValue().trim().equals(""))) {
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
        
        // Citations must have a type
        if (work.getCitation().getCitationType() == null 
                || work.getCitation().getCitationType().getValue() == null
                || work.getCitation().getCitationType().getValue().trim().equals("")) {
            setError(work.getCitation().getCitationType(), "NotBlank.manualWork.citationType");
        } else if (!work.getCitation().getCitationType().getValue().trim().equals(CitationType.FORMATTED_UNSPECIFIED.value())) {
            // citation should not be blank if citation type is set 
            if (work.getCitation().getCitation() == null || 
                     work.getCitation().getCitation().getValue().trim().equals("")) {
                setError(work.getCitation().getCitation(), "NotBlank.manualWork.citation");
            }
            
            // if bibtext must be valid
            if (work.getCitation().getCitationType().getValue().equals(CitationType.BIBTEX.value())) {
                try {
                    BibtexUtils.validate(HtmlUtils.htmlUnescape(work.getCitation().getCitationType().getValue()));
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
        //Get cached profile
        List<String> workIds = createWorksIdList(request);
        return workIds;
    }

    /**
     * created a work id list and sorts a map associated with the list in
     * in the session
     *
     */
    private List<String> createWorksIdList(HttpServletRequest request) {
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
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
     * List works associated with a profile
     * */
    @RequestMapping(value = "/profileWork.json", method = RequestMethod.PUT)
    public @ResponseBody
    Work updateProfileWorkJson(HttpServletRequest request, @RequestBody Work work) {
        //Get cached profile
        OrcidWork ow = work.toOrcidWork();
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        OrcidWorks orcidWorks = currentProfile.getOrcidActivities() == null ? null : currentProfile.getOrcidActivities().getOrcidWorks();
        if (orcidWorks != null) {
            List<OrcidWork> orcidWorksList = orcidWorks.getOrcidWork();
            if (orcidWorksList != null) {
                for (OrcidWork orcidWork : orcidWorksList) {
                    //If the put codes are equal, we know that they are the same work
                    if (orcidWork.getPutCode().equals(ow.getPutCode())) {
                        //Update the privacy of the work
                        profileWorkManager.updateWork(currentProfile.getOrcid().getValue(), ow.getPutCode(), ow.getVisibility());
                    }
                }
            }
        }
        return work;
    }
}