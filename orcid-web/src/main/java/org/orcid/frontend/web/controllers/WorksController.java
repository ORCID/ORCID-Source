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
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.UrlValidator;
import org.hibernate.validator.constraints.impl.URLValidator;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ActivityCacheManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.core.manager.WorkExternalIdentifierManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContributorAttributes;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.OrcidActivities;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.PublicationDate;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.WorkCategory;
import org.orcid.jaxb.model.message.WorkContributors;
import org.orcid.jaxb.model.message.WorkType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileWorkEntity;
import org.orcid.persistence.jpa.entities.PublicationDateEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.persistence.jpa.entities.custom.MinimizedWorkEntity;
import org.orcid.pojo.ajaxForm.Citation;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitle;
import org.orcid.pojo.ajaxForm.Work;
import org.orcid.pojo.ajaxForm.WorkExternalIdentifier;
import org.orcid.pojo.ajaxForm.WorkTitle;
import org.orcid.utils.FunctionsOverCollections;
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

    private static final Pattern LANGUAGE_CODE = Pattern.compile("([a-zA-Z]{2})(_[a-zA-Z]{2}){0,2}");

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
    private WorkExternalIdentifierManager workExternalIdentifierManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    private ActivityCacheManager cacheManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;

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
            profileWorkManager.removeWork(currentProfile.getOrcidIdentifier().getPath(), work.getPutCode().getValue());
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
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
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
                // Set country name
                if (!PojoUtil.isEmpty(work.getCountryCode())) {
                    Text countryName = Text.valueOf(countries.get(work.getCountryCode().getValue()));
                    work.setCountryName(countryName);
                }
                // Set language name
                if (!PojoUtil.isEmpty(work.getLanguageCode())) {
                    Text languageName = Text.valueOf(languages.get(work.getLanguageCode().getValue()));
                    work.setLanguageName(languageName);
                }
                // Set translated title language name
                if (!(work.getWorkTitle() == null) && !(work.getWorkTitle().getTranslatedTitle() == null)
                        && !StringUtils.isEmpty(work.getWorkTitle().getTranslatedTitle().getLanguageCode())) {
                    String languageName = languages.get(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
                    work.getWorkTitle().getTranslatedTitle().setLanguageName(languageName);
                }

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
        TranslatedTitle tt = new TranslatedTitle();
        tt.setContent(new String());
        tt.setLanguageCode(new String());
        tt.setLanguageName(new String());
        wt.setTranslatedTitle(tt);
        w.setWorkTitle(wt);

        // work journal title
        Text jt = new Text();
        jt.setRequired(false);
        w.setJournalTitle(jt);

        // set citation text and type
        Citation c = new Citation();
        Text ctText = new Text();
        ctText.setValue(CitationType.FORMATTED_UNSPECIFIED.value());
        c.setCitationType(ctText);
        Text cText = new Text();
        c.setCitation(cText);
        w.setCitation(c);

        Text wCategoryText = new Text();
        wCategoryText.setValue("");
        wCategoryText.setRequired(true);
        w.setWorkCategory(wCategoryText);

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
        w.setVisibility(profile.getOrcidInternal().getPreferences().getActivitiesVisibilityDefault().getValue());

        // Language code
        Text lc = new Text();
        lc.setRequired(false);
        w.setLanguageCode(lc);
        Text ln = new Text();
        ln.setRequired(false);
        w.setLanguageName(ln);

        w.setCountryCode(new Text());
        w.setCountryName(new Text());

        return w;
    }

    /**
     * Returns a blank work
     * */
    @RequestMapping(value = "/getWorkInfo.json", method = RequestMethod.GET)
    public @ResponseBody
    Work getWorkInfo(@RequestParam(value = "workId") String workId) {
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        if (StringUtils.isEmpty(workId))
            return null;

        ProfileWorkEntity profileWork = profileWorkManager.getProfileWork(this.getCurrentUserOrcid(), workId);

        if (profileWork != null) {

            OrcidWork orcidWork = jpa2JaxbAdapter.getOrcidWork(profileWork);

            if (orcidWork != null) {
                Work work = Work.valueOf(orcidWork);
                // Set country name
                if (!PojoUtil.isEmpty(work.getCountryCode())) {
                    Text countryName = Text.valueOf(countries.get(work.getCountryCode().getValue()));
                    work.setCountryName(countryName);
                }
                // Set language name
                if (!PojoUtil.isEmpty(work.getLanguageCode())) {
                    Text languageName = Text.valueOf(languages.get(work.getLanguageCode().getValue()));
                    work.setLanguageName(languageName);
                }
                // Set translated title language name
                if (!(work.getWorkTitle().getTranslatedTitle() == null) && !StringUtils.isEmpty(work.getWorkTitle().getTranslatedTitle().getLanguageCode())) {
                    String languageName = languages.get(work.getWorkTitle().getTranslatedTitle().getLanguageCode());
                    work.getWorkTitle().getTranslatedTitle().setLanguageName(languageName);
                }

                // If the work source is the user himself, fill the work source
                // name
                if (!PojoUtil.isEmpty(work.getWorkSource()) && profileWork.getProfile().getId().equals(work.getWorkSource().getValue())) {
                    List<Contributor> contributors = work.getContributors();
                    if (work.getContributors() != null) {
                        for (Contributor contributor : contributors) {
                            if (!PojoUtil.isEmpty(contributor.getContributorRole()) || !PojoUtil.isEmpty(contributor.getContributorSequence())) {
                                ProfileEntity profile = profileWork.getProfile();
                                String creditNameString = cacheManager.getCreditName(profile);
                                Text creditName = Text.valueOf(creditNameString);
                                contributor.setCreditName(creditName);
                            }
                        }
                    }
                }

                return work;
            }
        }
        return null;
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
        workWorkTitleTranslatedTitleValidate(work);
        workdescriptionValidate(work);
        workWorkCategoryValidate(work);
        workWorkTypeValidate(work);
        workWorkExternalIdentifiersValidate(work);
        workUrlValidate(work);
        workJournalTitleValidate(work);
        workLanguageCodeValidate(work);

        copyErrors(work.getCitation().getCitation(), work);
        copyErrors(work.getCitation().getCitationType(), work);
        copyErrors(work.getWorkTitle().getTitle(), work);
        copyErrors(work.getWorkTitle().getTranslatedTitle(), work);
        copyErrors(work.getShortDescription(), work);
        copyErrors(work.getWorkTitle().getSubtitle(), work);
        copyErrors(work.getWorkType(), work);
        copyErrors(work.getUrl(), work);
        copyErrors(work.getJournalTitle(), work);

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

            // Set the credit name to the work

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
            profileWorkManager.addProfileWork(currentProfile.getOrcidIdentifier().getPath(), workEntity.getId(), newOw.getVisibility(), getRealUserOrcid());

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
     * Returns a map containing the language code and name for each language
     * supported.
     * 
     * @return A map of the form [language_code, language_name] containing all
     *         supported languages
     * */
    @RequestMapping(value = "/languages.json", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getLanguageMap(HttpServletRequest request) {
        return lm.buildLanguageMap(localeManager.getLocale(), false);
    }

    /**
     * Returns a map containing the iso country code and the name for each
     * country.\
     * 
     * @return A map of the form [iso_code, country_name] containing all
     *         existing countries.
     * */
    @RequestMapping(value = "/countries.json", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, String> getCountriesMap(HttpServletRequest request) {
        return retrieveIsoCountries();
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
        workEntity.setTitle(orcidWork.getWorkTitle().getTitle().getContent().trim());
        workEntity.setJournalTitle(orcidWork.getJournalTitle() != null ? orcidWork.getJournalTitle().getContent() : null);
        workEntity.setWorkType(orcidWork.getWorkType());
        workEntity.setWorkUrl(orcidWork.getUrl().getValue());
        workEntity.setLanguageCode(StringUtils.isEmpty(orcidWork.getLanguageCode()) ? null : orcidWork.getLanguageCode());

        TranslatedTitle translatedTitle = TranslatedTitle.valueOf(orcidWork.getWorkTitle().getTranslatedTitle());

        if (translatedTitle != null) {
            workEntity.setTranslatedTitle(translatedTitle.getContent());
            workEntity.setTranslatedTitleLanguageCode(translatedTitle.getLanguageCode());
        }

        WorkContributors workContributors = orcidWork.getWorkContributors();
        if (workContributors != null) {
            for (org.orcid.jaxb.model.message.Contributor workContributor : workContributors.getContributor()) {
                workContributor.setCreditName(new CreditName(getEffectiveProfile().getOrcidBio().getPersonalDetails().retrievePublicDisplayName()));
            }
            workEntity.setContributorsJson(JsonUtils.convertToJsonString(workContributors));
        }

        if (orcidWork.getCountry() != null)
            workEntity.setIso2Country(orcidWork.getCountry().getValue());

        return workEntity;
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

    @RequestMapping(value = "/work/workTitle/translatedTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workWorkTitleTranslatedTitleValidate(@RequestBody Work work) {
        work.getWorkTitle().getTranslatedTitle().setErrors(new ArrayList<String>());

        if (work.getWorkTitle().getTranslatedTitle() != null) {

            String content = work.getWorkTitle().getTranslatedTitle().getContent();
            String code = work.getWorkTitle().getTranslatedTitle().getLanguageCode();

            if (!StringUtils.isEmpty(content)) {
                if (!StringUtils.isEmpty(code)) {
                    if (!LANGUAGE_CODE.matcher(work.getWorkTitle().getTranslatedTitle().getLanguageCode()).matches()) {
                        setError(work.getWorkTitle().getTranslatedTitle(), "manualWork.invalid_language_code");
                    }
                } else {
                    setError(work.getWorkTitle().getTranslatedTitle(), "manualWork.empty_code");
                }
                if (content.length() > 1000) {
                    setError(work.getWorkTitle().getTranslatedTitle(), "manualWork.length_less_1000");
                }
            } else {
                if (!StringUtils.isEmpty(code)) {
                    setError(work.getWorkTitle().getTranslatedTitle(), "manualWork.empty_translation");
                }
            }
        }
        return work;
    }

    @RequestMapping(value = "/work/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workUrlValidate(@RequestBody Work work) {
        work.getUrl().setErrors(new ArrayList<String>());
        if (work.getUrl().getValue() != null) {
           // trim if required
           if (!work.getUrl().getValue().equals(work.getUrl().getValue().trim())) 
               work.getUrl().setValue(work.getUrl().getValue().trim());
           
           // check length
           if (work.getUrl().getValue().length() > 350)
              setError(work.getUrl(), "manualWork.length_less_350");
           
           // add protocall if missing
           String[] schemes = {"http","https", "ftp"}; // DEFAULT schemes = "http", "https", "ftp"
           UrlValidator urlValidator = new UrlValidator(schemes);
           if (!urlValidator.isValid(work.getUrl().getValue()))
              work.getUrl().setValue("http://" + work.getUrl().getValue());   
           
           // test validity again
           if (!urlValidator.isValid(work.getUrl().getValue()))
               setError(work.getUrl(), "manualWork.invalid_url");
        }
        return work;
    }

    @RequestMapping(value = "/work/journalTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workJournalTitleValidate(@RequestBody Work work) {
        work.getJournalTitle().setErrors(new ArrayList<String>());
        if (work.getJournalTitle().getValue() != null && work.getJournalTitle().getValue().length() > 1000) {
            setError(work.getJournalTitle(), "manualWork.length_less_1000");
        }
        return work;
    }

    @RequestMapping(value = "/work/languageCodeValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workLanguageCodeValidate(@RequestBody Work work) {
        work.getLanguageCode().setErrors(new ArrayList<String>());
        if (work.getLanguageCode().getValue() != null) {
            if (!LANGUAGE_CODE.matcher(work.getLanguageCode().getValue()).matches())
                setError(work.getLanguageCode(), "manualWork.invalid_language_code");
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

    @RequestMapping(value = "/work/workCategoryValidate.json", method = RequestMethod.POST)
    public @ResponseBody
    Work workWorkCategoryValidate(@RequestBody Work work) {
        work.getWorkCategory().setErrors(new ArrayList<String>());
        if (work.getWorkCategory().getValue() == null || work.getWorkCategory().getValue().trim().length() == 0) {
            setError(work.getWorkCategory(), "NotBlank.manualWork.workCategory");
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
            if (PojoUtil.isEmpty(work.getCitation().getCitation())) {
                setError(work.getCitation().getCitation(), "NotBlank.manualWork.citation");
            }

        }

        return work;
    }

    /**
     * List works ids associated with a profile
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
        java.util.Date lastModified = currentProfile.getOrcidHistory().getLastModifiedDate().getValue().toGregorianCalendar().getTime();
        List<MinimizedWorkEntity> works = workManager.findWorks(currentProfile.getOrcidIdentifier().getPath(), lastModified);
        HashMap<String, Work> worksMap = new HashMap<String, Work>();
        List<String> workIds = new ArrayList<String>();
        if (works != null) {
            for (MinimizedWorkEntity work : works) {
                try {
                    worksMap.put(String.valueOf(work.getId()), Work.valueOf(work));
                    workIds.add(String.valueOf(work.getId()));
                } catch (Exception e) {
                    LOGGER.error("ProfileWork failed to parse as Work. Put code" + work.getId());
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
                        profileWorkManager.updateWork(currentProfile.getOrcidIdentifier().getPath(), ow.getPutCode(), ow.getVisibility());
                    }
                }
            }
        }
        return work;
    }

    /**
     * Return a list of work types based on the work category provided as a
     * parameter
     * 
     * @param workCategoryName
     * @return a map containing the list of types associated with that type and
     *         his localized name
     * */
    @RequestMapping(value = "/loadWorkTypes.json", method = RequestMethod.POST)
    public @ResponseBody
    Map<String, String> retriveWorkSubtypesAsMap(@RequestParam(value = "workCategory") String workCategoryName) {
        Map<String, String> workTypes = new LinkedHashMap<String, String>();
        WorkCategory workCategory = WorkCategory.fromValue(workCategoryName);

        for (WorkType workType : workCategory.getSubTypes()) {
            // Dont put work type UNDEFINED
            if (!workType.equals(WorkType.UNDEFINED) && !workType.equals(WorkType.OTHER))
                workTypes.put(workType.value(), getMessage(buildInternationalizationKey(WorkType.class, workType.value())));
            else if (workType.equals(WorkType.OTHER))
                // OTHER will be at the bottom of the list, so, put a custom
                // message that will move other at bottom
                workTypes.put(Work.OTHER_AT_BOTTOM, getMessage(buildInternationalizationKey(WorkType.class, workType.value())));
        }

        return FunctionsOverCollections.sortMapsByValues(workTypes);
    }
}