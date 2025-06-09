package org.orcid.frontend.web.controllers;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.MissingGroupableExternalIDException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.*;
import org.orcid.core.manager.v3.read_only.WorkManagerReadOnly;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.activities.ExternalIdentifierFundedByHelper;
import org.orcid.core.utils.v3.ContributorUtils;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.frontend.web.pagination.Page;
import org.orcid.frontend.web.pagination.WorksPaginator;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.release.record.Work;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.GroupedWorks;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.PIDResolutionResult;
import org.orcid.pojo.WorkExtended;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.*;
import org.orcid.pojo.grouping.WorkGroup;
import org.orcid.pojo.grouping.WorkGroupingSuggestion;
import org.orcid.pojo.grouping.WorkGroupingSuggestions;
import org.orcid.pojo.grouping.WorkGroupingSuggestionsCount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Pattern;

/**
 * @author rcpeters
 */
@Controller("worksController")
@RequestMapping(value = { "/works" })
public class WorksController extends BaseWorkspaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorksController.class);
    
    private static final Pattern LANGUAGE_CODE = Pattern.compile("([a-zA-Z]{2})(_[a-zA-Z]{2}){0,2}");
    private static final String PAGE_SIZE_DEFAULT = "50";

    @Resource(name = "workManagerV3")
    private WorkManager workManager;

    @Resource(name = "workManagerReadOnlyV3")
    private WorkManagerReadOnly workManagerReadOnly;

    @Resource
    private WorksPaginator worksPaginator;

    @Resource
    private IdentifierTypeManager identifierTypeManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "activityManagerV3")
    private ActivityManager activityManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;

    @Resource(name = "profileEntityManagerV3")
    private ProfileEntityManager profileEntityManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "bibtexManagerV3")
    private BibtexManager bibtexManager;

    @Resource(name = "groupingSuggestionManagerV3")
    private GroupingSuggestionManager groupingSuggestionManager;

    @Resource
    PIDResolverService resolverService;

    @Resource(name = "contributorUtilsV3")
    private ContributorUtils contributorUtils;

    @Value("${org.orcid.core.work.contributors.ui.max:50}")
    private int maxContributorsForUI;

    @RequestMapping(value = "/{workIdsStr}", method = RequestMethod.DELETE)
    public @ResponseBody ArrayList<Long> removeWork(@PathVariable("workIdsStr") String workIdsStr) {
        List<String> workIds = Arrays.asList(workIdsStr.split(","));
        // Get cached profile
        ArrayList<Long> workIdLs = new ArrayList<Long>();
        if (workIds != null) {
            for (String workId : workIds) {
                workIdLs.add(new Long(workId));
            }
            workManager.removeWorks(getEffectiveUserOrcid(), workIdLs);
        }
        return workIdLs;
    }

    @RequestMapping(value = "/group/{workIdsStr}", method = RequestMethod.POST)
    public @ResponseBody GroupedWorks groupWorks(@PathVariable("workIdsStr") String workIdsStr) {
        List<Long> workIds = Arrays.stream(workIdsStr.split(",")).mapToLong(n -> Long.parseLong(n)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        GroupedWorks groupedWorks = new GroupedWorks();

        try {
            workManager.createNewWorkGroup(workIds, getCurrentUserOrcid());
        } catch (MissingGroupableExternalIDException e) {
            groupedWorks.getErrors().add(getMessage("groups.merge.no_groupable_external_ids"));
            return groupedWorks;
        }
        groupedWorks.setWorkIds(workIds);
        return groupedWorks;
    }

    @RequestMapping(value = "/updateToMaxDisplay.json", method = RequestMethod.GET)
    public @ResponseBody boolean updateToMaxDisplay(HttpServletRequest request, @RequestParam(value = "putCode") Long putCode) {
        String orcid = getEffectiveUserOrcid();
        return workManager.updateToMaxDisplay(orcid, putCode);
    }

    /**
     * Returns a blank work
     */
    @RequestMapping(value = "/work.json", method = RequestMethod.GET)
    public @ResponseBody WorkForm getWork(HttpServletRequest request) {
        WorkForm w = new WorkForm();
        initializeFields(w);
        return w;
    }

    @RequestMapping(value = "/groupingSuggestions.json", method = RequestMethod.GET)
    public @ResponseBody WorkGroupingSuggestions getGroupingSuggestions() {
        WorkGroupingSuggestions suggestions = groupingSuggestionManager.getGroupingSuggestions(getCurrentUserOrcid());
        return suggestions;
    }
    
    @RequestMapping(value = "/groupingSuggestionsCount.json", method = RequestMethod.GET)
    public @ResponseBody WorkGroupingSuggestionsCount getGroupingSuggestionsCount() {
        return groupingSuggestionManager.getGroupingSuggestionCount(getCurrentUserOrcid());
    }

    @RequestMapping(value = "/rejectGroupingSuggestion.json", method = RequestMethod.POST)
    public @ResponseBody Boolean declineGroupingSuggestion(@RequestBody WorkGroupingSuggestion suggestion) {
        groupingSuggestionManager.markGroupingSuggestionAsRejected(suggestion);
        return true;
    }
    
    @RequestMapping(value = "/acceptGroupingSuggestion.json", method = RequestMethod.POST)
    public @ResponseBody GroupedWorks acceptGroupingSuggestion(@RequestBody WorkGroupingSuggestion suggestion) {
        GroupedWorks grouped = new GroupedWorks();
        grouped.setWorkIds(suggestion.getPutCodes());
        try {
            groupingSuggestionManager.markGroupingSuggestionAsAccepted(suggestion);
            return grouped;
        } catch (MissingGroupableExternalIDException e) {
            grouped.getErrors().add(getMessage("groups.merge.no_groupable_external_ids"));
            return grouped;
        }
    }

    private void initializeFields(WorkForm w) {
        if (w.getVisibility() == null) {
            ProfileEntity profile = profileEntityCacheManager.retrieve(getEffectiveUserOrcid());
            org.orcid.jaxb.model.v3.release.common.Visibility defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = profile.getActivitiesVisibilityDefault() == null ? Visibility.valueOf(OrcidVisibilityDefaults.WORKS_DEFAULT.getVisibility())
                    : Visibility.valueOf(defaultVis);
            w.setVisibility(v);
        }

        if (w.getTitle() == null) {
            w.setTitle(new Text());
        }

        if (w.getSubtitle() == null) {
            w.setSubtitle(new Text());
        }

        if (w.getTranslatedTitle() == null) {
            TranslatedTitleForm tt = new TranslatedTitleForm();
            tt.setContent(new String());
            tt.setLanguageCode(new String());
            tt.setLanguageName(new String());
            w.setTranslatedTitle(tt);
        }

        if (PojoUtil.isEmpty(w.getJournalTitle())) {
            Text jt = new Text();
            jt.setRequired(false);
            w.setJournalTitle(jt);
        }

        if (PojoUtil.isEmpty(w.getWorkType())) {
            Text wTypeText = new Text();
            wTypeText.setValue(new String());
            wTypeText.setRequired(true);
            w.setWorkType(wTypeText);
        }

        initializePublicationDate(w);

        if (w.getCitation() == null) {
            w.setCitation(new Citation());
            w.getCitation().setCitationType(new Text());
            w.getCitation().setCitation(new Text());
        }

        if (w.getWorkExternalIdentifiers() == null || w.getWorkExternalIdentifiers().isEmpty()) {
            ActivityExternalIdentifier wei = new ActivityExternalIdentifier();
            Text wdiType = new Text();
            wdiType.setValue(new String());
            wei.setUrl(new Text());
            wei.setExternalIdentifierId(new Text());
            wei.setExternalIdentifierType(wdiType);
            wei.setRelationship(Text.valueOf(Relationship.SELF.value()));
            List<ActivityExternalIdentifier> wdiL = new ArrayList<ActivityExternalIdentifier>();
            wdiL.add(wei);
            w.setWorkExternalIdentifiers(wdiL);
        }

        if (PojoUtil.isEmpty(w.getUrl())) {
            w.setUrl(new Text());
        }

        if (w.getContributors() == null || w.getContributors().isEmpty()) {
            List<Contributor> contrList = new ArrayList<Contributor>();
            w.setContributors(contrList);
        }

        if (PojoUtil.isEmpty(w.getShortDescription())) {
            w.setShortDescription(new Text());
        }

        if (PojoUtil.isEmpty(w.getLanguageCode())) {
            Text lc = new Text();
            lc.setRequired(false);
            w.setLanguageCode(lc);
        }

        if (PojoUtil.isEmpty(w.getLanguageName())) {
            Text ln = new Text();
            ln.setRequired(false);
            w.setLanguageName(ln);
        }

        if (PojoUtil.isEmpty(w.getCountryCode())) {
            w.setCountryCode(new Text());
        }

        if (PojoUtil.isEmpty(w.getCountryName())) {
            w.setCountryName(new Text());
        }

    }

    private void initializePublicationDate(WorkForm w) {
        if (w.getPublicationDate() == null) {
            Date d = new Date();
            d.setDay(new String());
            d.setMonth(new String());
            d.setYear(new String());
            w.setPublicationDate(d);
        }
    }

    /**
     * Returns a blank work
     */
    @RequestMapping(value = "/getWorkInfo.json", method = RequestMethod.GET)
    public @ResponseBody WorkForm getWorkInfo(@RequestParam(value = "workId") Long workId) {
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        if (workId == null)
            return null;

        WorkExtended work = workManager.getWorkExtended(this.getEffectiveUserOrcid(), workId);
        WorkForm workForm = WorkForm.valueOf(work, maxContributorsForUI);
        
        if (workForm != null) {
            if (workForm.getPublicationDate() == null) {
                initializePublicationDate(workForm);
            } else {
                if (workForm.getPublicationDate().getDay() == null) {
                    workForm.getPublicationDate().setDay(new String());
                }
                if (workForm.getPublicationDate().getMonth() == null) {
                    workForm.getPublicationDate().setMonth(new String());
                }
                if (workForm.getPublicationDate().getYear() == null) {
                    workForm.getPublicationDate().setYear(new String());
                }
            }

            if (workForm.getShortDescription() == null) {
                workForm.setShortDescription(new Text());
            }

            if (workForm.getUrl() == null) {
                workForm.setUrl(new Text());
            }

            if (workForm.getJournalTitle() == null) {
                workForm.setJournalTitle(new Text());
            }

            if (workForm.getLanguageCode() == null) {
                workForm.setLanguageCode(new Text());
            }

            if (workForm.getLanguageCode() == null) {
                workForm.setLanguageCode(new Text());
            }

            if (workForm.getLanguageName() == null) {
                workForm.setLanguageName(new Text());
            }

            if (workForm.getCitation() == null) {
                workForm.setCitation(new Citation());
                workForm.getCitation().setCitationType(new Text());
                workForm.getCitation().setCitation(new Text());
            }

            if (workForm.getSubtitle() == null) {
                workForm.setSubtitle(new Text());
            }

            if (workForm.getTranslatedTitle() == null) {
                workForm.setTranslatedTitle(new TranslatedTitleForm());
            }

            if (workForm.getCountryCode() == null) {
                workForm.setCountryCode(new Text());
            }

            if (workForm.getCountryName() == null) {
                workForm.setCountryName(new Text());
            }

            if (workForm.getJournalTitle() == null) {
                workForm.setJournalTitle(new Text());
            }

            // Set country name
            if (!PojoUtil.isEmpty(workForm.getCountryCode())) {
                Text countryName = Text.valueOf(countries.get(workForm.getCountryCode().getValue()));
                workForm.setCountryName(countryName);
            }
            // Set language name
            if (!PojoUtil.isEmpty(workForm.getLanguageCode())) {
                Text languageName = Text.valueOf(languages.get(workForm.getLanguageCode().getValue()));
                workForm.setLanguageName(languageName);
            }
            // Set translated title language name
            if (!(workForm.getTranslatedTitle() == null) && !StringUtils.isEmpty(workForm.getTranslatedTitle().getLanguageCode())) {
                String languageName = languages.get(workForm.getTranslatedTitle().getLanguageCode());
                workForm.getTranslatedTitle().setLanguageName(languageName);
            }

            if (workForm.getContributorsGroupedByOrcid() != null) {
                contributorUtils.filterContributorsGroupedByOrcidPrivateData(workForm.getContributorsGroupedByOrcid(), maxContributorsForUI);
            }            

            return workForm;
        }

        return null;
    }

    @RequestMapping(value = "/worksInfo/{putCodesStr}", method = RequestMethod.GET)
    public @ResponseBody List<WorkForm> getWorksInfo(@PathVariable("putCodesStr") String putCodesStr) {
        Map<String, String> countries = retrieveIsoCountries();
        Map<String, String> languages = lm.buildLanguageMap(localeManager.getLocale(), false);
        if (putCodesStr == null)
            return null;

        ArrayList<Long> putCodes = new ArrayList<Long>();
        for (String workId : putCodesStr.split(","))
            putCodes.add(new Long(workId));
        List<WorkSummary> works = workManager.getWorksSummaryList(this.getEffectiveUserOrcid(), putCodes);
        List<WorkForm> workFormList = new ArrayList<>();

        if (works.size() > 0) {
            works.forEach(workSummary -> {
                WorkForm workForm = WorkForm.valueOf(workSummary);
                if (workForm.getPublicationDate() == null) {
                    initializePublicationDate(workForm);
                } else {
                    if (workForm.getPublicationDate().getDay() == null) {
                        workForm.getPublicationDate().setDay(new String());
                    }
                    if (workForm.getPublicationDate().getMonth() == null) {
                        workForm.getPublicationDate().setMonth(new String());
                    }
                    if (workForm.getPublicationDate().getYear() == null) {
                        workForm.getPublicationDate().setYear(new String());
                    }
                }

                if (workForm.getShortDescription() == null) {
                    workForm.setShortDescription(new Text());
                }

                if (workForm.getUrl() == null) {
                    workForm.setUrl(new Text());
                }

                if (workForm.getJournalTitle() == null) {
                    workForm.setJournalTitle(new Text());
                }

                if (workForm.getLanguageCode() == null) {
                    workForm.setLanguageCode(new Text());
                }

                if (workForm.getLanguageCode() == null) {
                    workForm.setLanguageCode(new Text());
                }

                if (workForm.getLanguageName() == null) {
                    workForm.setLanguageName(new Text());
                }

                if (workForm.getCitation() == null) {
                    workForm.setCitation(new Citation());
                    workForm.getCitation().setCitationType(new Text());
                    workForm.getCitation().setCitation(new Text());
                }

                if (workForm.getSubtitle() == null) {
                    workForm.setSubtitle(new Text());
                }

                if (workForm.getTranslatedTitle() == null) {
                    workForm.setTranslatedTitle(new TranslatedTitleForm());
                }

                if (workForm.getCountryCode() == null) {
                    workForm.setCountryCode(new Text());
                }

                if (workForm.getCountryName() == null) {
                    workForm.setCountryName(new Text());
                }

                if (workForm.getJournalTitle() == null) {
                    workForm.setJournalTitle(new Text());
                }

                // Set country name
                if (!PojoUtil.isEmpty(workForm.getCountryCode())) {
                    Text countryName = Text.valueOf(countries.get(workForm.getCountryCode().getValue()));
                    workForm.setCountryName(countryName);
                }
                // Set language name
                if (!PojoUtil.isEmpty(workForm.getLanguageCode())) {
                    Text languageName = Text.valueOf(languages.get(workForm.getLanguageCode().getValue()));
                    workForm.setLanguageName(languageName);
                }
                // Set translated title language name
                if (!(workForm.getTranslatedTitle() == null) && !StringUtils.isEmpty(workForm.getTranslatedTitle().getLanguageCode())) {
                    String languageName = languages.get(workForm.getTranslatedTitle().getLanguageCode());
                    workForm.getTranslatedTitle().setLanguageName(languageName);
                }

                if (workForm.getContributors() != null) {
                    for (Contributor contributor : workForm.getContributors()) {
                        if (!PojoUtil.isEmpty(contributor.getOrcid())) {
                            String contributorOrcid = contributor.getOrcid().getValue();
                            if (profileEntityManager.orcidExists(contributorOrcid)) {
                                // contributor is an ORCID user - visibility of
                                // user's name in record must be taken into
                                // account
                                String publicContributorCreditName = activityManager.getPublicCreditName(contributorOrcid);
                                contributor.setCreditName(Text.valueOf(publicContributorCreditName));
                            }
                        }
                    }
                }
                workFormList.add(workForm);
            });
            return workFormList;
        }

        return null;
    }    
    
    /**
     * Creates a new work
     * 
     * @throws Exception
     */
    @RequestMapping(value = "/work.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm postWork(HttpServletRequest request, @RequestBody WorkForm workForm,
            @RequestParam(value = "isBibtex", required = false, defaultValue = "false") Boolean isBibtex) throws Exception {
        validateWork(workForm);
        if (workForm.getErrors().size() == 0) {
            removeEmptyExternalIdentifiers(workForm);
            if (workForm.getPutCode() != null)
                updateWork(workForm);
            else
                addWork(workForm);
        }
        return workForm;
    }

    private void removeEmptyExternalIdentifiers(WorkForm workForm) {
        if (workForm != null) {
            if (workForm.getWorkExternalIdentifiers() != null && !workForm.getWorkExternalIdentifiers().isEmpty()) {
                List<ActivityExternalIdentifier> cleanExtIds = new ArrayList<ActivityExternalIdentifier>();
                for (ActivityExternalIdentifier wExtId : workForm.getWorkExternalIdentifiers()) {
                    if (!PojoUtil.isEmpty(wExtId.getExternalIdentifierType())) {
                        if (!PojoUtil.isEmpty(wExtId.getExternalIdentifierId())) {
                            cleanExtIds.add(wExtId);
                        }
                    }
                }
                workForm.setWorkExternalIdentifiers(cleanExtIds);
            }
        }
    }

    private void addWork(WorkForm workForm) {
        Work newWork = workForm.toWork();
        newWork.setPutCode(null);

        // Create work
        newWork = workManager.createWork(getEffectiveUserOrcid(), workForm);
        
        // Set the id in the work to be returned
        Long workId = newWork.getPutCode();
        workForm.setPutCode(Text.valueOf(workId));
    }

    private void updateWork(WorkForm workForm) throws Exception {
        // Get current profile
        String userOrcid = getEffectiveUserOrcid();
        if (!userOrcid.equals(workForm.getSource())) {
            throw new Exception(getMessage("web.orcid.activity_incorrectsource.exception"));
        }

        workManager.updateWork(userOrcid, workForm);        
    }

    @RequestMapping(value = "/worksValidate.json", method = RequestMethod.POST)
    public @ResponseBody List<WorkForm> validatesWork(@RequestBody List<WorkForm> works) {
        for (WorkForm work : works)
            validateWork(work);
        return works;
    }

    @RequestMapping(value = "/workValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm validateWork(@RequestBody WorkForm work) {
        work.setErrors(new ArrayList<String>());

        if (work.getCitation() != null) {
            workCitationValidate(work);
            copyErrors(work.getCitation().getCitationType(), work);
            copyErrors(work.getCitation().getCitation(), work);
        }
        workTitleValidate(work);
        copyErrors(work.getTitle(), work);
        if (work.getSubtitle() != null) {
            workSubtitleValidate(work);
            copyErrors(work.getSubtitle(), work);
        }
        if (work.getTranslatedTitle() != null) {
            workTranslatedTitleValidate(work);
            copyErrors(work.getTranslatedTitle(), work);
        }
        // allowed to be null
        if (work.getShortDescription() != null) {
            workdescriptionValidate(work);
            copyErrors(work.getShortDescription(), work);
        }

        workWorkTypeValidate(work);
        copyErrors(work.getWorkType(), work);
        if (work.getWorkExternalIdentifiers() != null) {
            workWorkExternalIdentifiersValidate(work);
            for (ActivityExternalIdentifier wId : work.getWorkExternalIdentifiers()) {
                copyErrors(wId.getExternalIdentifierId(), work);
                copyErrors(wId.getExternalIdentifierType(), work);
                copyErrors(wId.getRelationship(), work);
            }
        }

        if (work.getUrl() != null) {
            workUrlValidate(work);
            copyErrors(work.getUrl(), work);
        }

        if (work.getJournalTitle() != null) {
            workJournalTitleValidate(work);
            copyErrors(work.getJournalTitle(), work);
        }

        if (work.getPublicationDate() != null) {
            workPublicationDateValidate(work);
            copyErrors(work.getPublicationDate(), work);
        }

        // allowed to be null
        if (work.getLanguageCode() != null) {
            workLanguageCodeValidate(work);
            copyErrors(work.getLanguageCode(), work);
        }

        // null
        if (work.getPutCode() != null) {
            validateWorkId(work);
        }
        return work;
    }

    @RequestMapping(value = "/work/titleValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workTitleValidate(@RequestBody WorkForm work) {
        work.getTitle().setErrors(new ArrayList<String>());
        if (work.getTitle().getValue() == null || work.getTitle().getValue().trim().length() == 0) {
            setError(work.getTitle(), "common.title.not_blank");
        } else {
            if (work.getTitle().getValue().trim().length() > 1000) {
                setError(work.getTitle(), "common.length_less_1000");
            }
        }
        return work;
    }

    @RequestMapping(value = "/work/subtitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workSubtitleValidate(@RequestBody WorkForm work) {

        work.getSubtitle().setErrors(new ArrayList<String>());
        if (work.getSubtitle().getValue() != null && work.getSubtitle().getValue().length() > 1000) {
            setError(work.getSubtitle(), "common.length_less_1000");
        }
        return work;
    }

    @RequestMapping(value = "/work/translatedTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workTranslatedTitleValidate(@RequestBody WorkForm work) {
        work.getTranslatedTitle().setErrors(new ArrayList<String>());

        String content = work.getTranslatedTitle() == null ? null : work.getTranslatedTitle().getContent();
        String code = work.getTranslatedTitle() == null ? null : work.getTranslatedTitle().getLanguageCode();

        if (!StringUtils.isEmpty(content)) {
            if (!StringUtils.isEmpty(code)) {
                if (!LANGUAGE_CODE.matcher(work.getTranslatedTitle().getLanguageCode()).matches()) {
                    setError(work.getTranslatedTitle(), "manualWork.invalid_language_code");
                }
            } else {
                setError(work.getTranslatedTitle(), "manualWork.empty_code");
            }
            if (content.length() > 1000) {
                setError(work.getTranslatedTitle(), "common.length_less_1000");
            }
        } else {
            if (!StringUtils.isEmpty(code)) {
                setError(work.getTranslatedTitle(), "manualWork.empty_translation");
            }
        }
        return work;
    }

    @RequestMapping(value = "/work/urlValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workUrlValidate(@RequestBody WorkForm work) {
        validateUrl(work.getUrl());
        return work;
    }

    @RequestMapping(value = "/work/journalTitleValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workJournalTitleValidate(@RequestBody WorkForm work) {
        work.getJournalTitle().setErrors(new ArrayList<String>());
        if (work.getJournalTitle().getValue() != null && work.getJournalTitle().getValue().length() > 1000) {
            setError(work.getJournalTitle(), "common.length_less_1000");
        }
        return work;
    }

    @RequestMapping(value = "/work/publicationDateValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workPublicationDateValidate(@RequestBody WorkForm work) {
        work.getPublicationDate().setErrors(new ArrayList<String>());
        if ((PojoUtil.isEmpty(work.getPublicationDate().getYear())
                && (!PojoUtil.isEmpty(work.getPublicationDate().getMonth()) || !PojoUtil.isEmpty(work.getPublicationDate().getDay())))
                || (!PojoUtil.isEmpty(work.getPublicationDate().getYear()) && PojoUtil.isEmpty(work.getPublicationDate().getMonth())
                        && !PojoUtil.isEmpty(work.getPublicationDate().getDay()))) {
            setError(work.getPublicationDate(), "common.dates.invalid");
        }
        return work;
    }

    @RequestMapping(value = "/work/languageCodeValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workLanguageCodeValidate(@RequestBody WorkForm work) {
        work.getLanguageCode().setErrors(new ArrayList<String>());
        if (work.getLanguageCode().getValue() != null) {
            if (!LANGUAGE_CODE.matcher(work.getLanguageCode().getValue()).matches())
                setError(work.getLanguageCode(), "manualWork.invalid_language_code");
        }
        return work;
    }

    @RequestMapping(value = "/work/descriptionValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workdescriptionValidate(@RequestBody WorkForm work) {
        work.getShortDescription().setErrors(new ArrayList<String>());
        if (work.getShortDescription().getValue() != null && work.getShortDescription().getValue().length() > 5000) {
            setError(work.getShortDescription(), "manualWork.length_less_5000");
        }
        return work;
    }

    @RequestMapping(value = "/work/workTypeValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workWorkTypeValidate(@RequestBody WorkForm work) {
        if (work.getWorkType() == null) {
            work.setWorkType(new Text());
        } else {
            work.getWorkType().setErrors(new ArrayList<String>());
        }
        if (work.getWorkType().getValue() == null || work.getWorkType().getValue().trim().length() == 0) {
            setError(work.getWorkType(), "NotBlank.manualWork.workType");
        }

        return work;
    }

    @RequestMapping(value = "/work/workExternalIdentifiersValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workWorkExternalIdentifiersValidate(@RequestBody WorkForm work) {
        ActivityExternalIdentifier lastVersionOfIdentifier = null;
        Boolean hasSelfOfIdentifier = false;
        for (ActivityExternalIdentifier wId : work.getWorkExternalIdentifiers()) {
            if (wId.getExternalIdentifierId() == null)
                wId.setExternalIdentifierId(new Text());
            if (wId.getExternalIdentifierType() == null)
                wId.setExternalIdentifierType(new Text());
            wId.getExternalIdentifierId().setErrors(new ArrayList<String>());
            wId.getExternalIdentifierType().setErrors(new ArrayList<String>());
            if (wId.getRelationship() != null)
                wId.getRelationship().setErrors(new ArrayList<String>());
            // if has id type must be specified
            if (wId.getExternalIdentifierId().getValue() != null && !wId.getExternalIdentifierId().getValue().trim().equals("")
                    && (wId.getExternalIdentifierType().getValue() == null || wId.getExternalIdentifierType().getValue().equals(""))) {
                setError(wId.getExternalIdentifierType(), "NotBlank.currentWorkExternalIds.idType");
            } else if (wId.getExternalIdentifierId().getValue() != null && wId.getExternalIdentifierId().getValue().length() > 2084) {
                setError(wId.getExternalIdentifierId(), "manualWork.length_less_2084");
            }
            // if type is set a id must set
            if (wId.getExternalIdentifierType().getValue() != null && !wId.getExternalIdentifierType().getValue().trim().equals("")
                    && (wId.getExternalIdentifierId().getValue() == null || wId.getExternalIdentifierId().getValue().trim().equals(""))) {
                setError(wId.getExternalIdentifierId(), "NotBlank.currentWorkExternalIds.id");
            }

            // check type is valid
            Map<String, IdentifierType> types = identifierTypeManager.fetchIdentifierTypesByAPITypeName(getLocale());
            if (wId.getExternalIdentifierType().getValue() != null && !wId.getExternalIdentifierType().getValue().trim().isEmpty()
                    && !types.keySet().contains(wId.getExternalIdentifierType().getValue())) {
                setError(wId.getExternalIdentifierType(), "manualWork.id_invalid");
            }

            if (wId.getUrl() != null)
                validateUrl(wId.getUrl());

            if (wId.getRelationship() != null) {
                if (Relationship.VERSION_OF.value().equals(wId.getRelationship().getValue())) {
                    lastVersionOfIdentifier = wId;
                }

                if (Relationship.SELF.value().equals(wId.getRelationship().getValue())) {
                    hasSelfOfIdentifier = true;
                }
                
                if (Relationship.FUNDED_BY.value().equals(wId.getRelationship().getValue())) {
                    if(!ExternalIdentifierFundedByHelper.isExtIdTypeAllowedForFundedBy(wId.getExternalIdentifierType().getValue())) {
                        setError(wId.getExternalIdentifierType(), "manualWork.ext_ids.funded_by");   
                    }
                }
            }
            
        }

        if (lastVersionOfIdentifier != null && !hasSelfOfIdentifier) {
            setError(lastVersionOfIdentifier.getRelationship(), "manualWork.ext_ids.self_required");
        }

        return work;
    }

    @RequestMapping(value = "/work/citationValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workCitationValidate(@RequestBody WorkForm work) {
        if (work.getCitation().getCitation() == null) {
            work.getCitation().setCitation(Text.valueOf(StringUtils.EMPTY));
        }
        if (work.getCitation().getCitationType() == null) {
            work.getCitation().setCitationType(Text.valueOf(StringUtils.EMPTY));
        }
        work.getCitation().getCitation().setErrors(new ArrayList<String>());
        work.getCitation().getCitationType().setErrors(new ArrayList<String>());

        // Citations must have a type if citation text has a value
        if (PojoUtil.isEmpty(work.getCitation().getCitationType()) && !PojoUtil.isEmpty(work.getCitation().getCitation())) {
            setError(work.getCitation().getCitationType(), "NotBlank.manualWork.citationType");
        } else if (!PojoUtil.isEmpty(work.getCitation().getCitationType())) {
            // citation should not be blank if citation type is set
            if (PojoUtil.isEmpty(work.getCitation().getCitation())) {
                setError(work.getCitation().getCitation(), "NotBlank.manualWork.citation");
            }

        }
        return work;
    }

    public WorkForm validateWorkId(WorkForm work) {
        List<Work> works = workManager.findWorks(getEffectiveUserOrcid());
        if (works == null || works.isEmpty()) {
            setError(work, "manual_work_form_contents.edit_work.invalid_id");
        } else if (PojoUtil.isEmpty(work.getPutCode())) {
            setError(work, "manual_work_form_contents.edit_work.undefined_id");
        } else {
            boolean exists = false;
            Long putCode = Long.valueOf(work.getPutCode().getValue());
            for (Work existingWork : works) {
                if (existingWork.getPutCode().equals(putCode)) {
                    exists = true;
                    break;
                }
            }

            if (!exists)
                setError(work, "manual_work_form_contents.edit_work.invalid_id");
        }

        return work;
    }

    @RequestMapping(value = "/worksPage.json", method = RequestMethod.GET)
    public @ResponseBody Page<WorkGroup> getWorkGroupsJson(@RequestParam(value="pageSize", defaultValue = PAGE_SIZE_DEFAULT) int pageSize, @RequestParam("offset") int offset, @RequestParam("sort") String sort,
            @RequestParam("sortAsc") boolean sortAsc) {
        String orcid = getEffectiveUserOrcid();
        return worksPaginator.getWorksPage(orcid, offset, pageSize, false, sort, sortAsc);
    }

    @RequestMapping(value = "/worksExtendedPage.json", method = RequestMethod.GET)
    public @ResponseBody Page<WorkGroup> getWorksExtendedGroupsJson(@RequestParam(value="pageSize", defaultValue = PAGE_SIZE_DEFAULT) int pageSize, @RequestParam("offset") int offset, @RequestParam("sort") String sort,
                                                                           @RequestParam("sortAsc") boolean sortAsc) {
        String orcid = getEffectiveUserOrcid();
        return worksPaginator.getWorksExtendedPage(orcid, offset, pageSize, false, sort, sortAsc);
    }

    @RequestMapping(value = "/allWorks.json", method = RequestMethod.GET)
    public @ResponseBody Page<WorkGroup> getAllWorkGroupsJson(@RequestParam("sort") String sort, @RequestParam("sortAsc") boolean sortAsc) {
        String orcid = getEffectiveUserOrcid();
        return worksPaginator.getAllWorks(orcid, false, sort, sortAsc);
    }

    @RequestMapping(value = "/refreshWorks.json", method = RequestMethod.GET)
    public @ResponseBody Page<WorkGroup> refreshWorkGroupsJson(@RequestParam("limit") int limit, @RequestParam("sort") String sort,
            @RequestParam("sortAsc") boolean sortAsc) {
        String orcid = getEffectiveUserOrcid();
        return worksPaginator.refreshWorks(orcid, limit, sort, sortAsc);
    }

    /**
     * updates visibility of works
     */
    @RequestMapping(value = "/{workIdsStr}/visibility/{visibilityStr}", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Long> updateVisibility(@PathVariable("workIdsStr") String workIdsStr, @PathVariable("visibilityStr") String visibilityStr) {
        // make sure this is a users work
        String orcid = getEffectiveUserOrcid();
        ArrayList<Long> workIds = new ArrayList<Long>();
        for (String workId : workIdsStr.split(","))
            workIds.add(new Long(workId));
        workManager.updateVisibilities(orcid, workIds, org.orcid.jaxb.model.v3.release.common.Visibility.fromValue(visibilityStr));
        return workIds;
    }

    /**
     * Returns the works in bibtex format. This format encodes everything it can
     * using latex encoding and leaves the rest as UTF-8 Note, you must
     * specifically set the encoding to UTF-8 in the produces argument to
     * enforce the encoding over the wire.
     */
    @RequestMapping(value = "/works.bib", method = RequestMethod.GET, produces = "text/plain; charset=utf-8")
    public @ResponseBody String fetchBibtex() {
        return bibtexManager.generateBibtexReferenceList(getEffectiveUserOrcid());
    }  
    
    @RequestMapping(value = "/export/bibtex", method = RequestMethod.GET, produces = "text/plain; charset=utf-8")
    public @ResponseBody String exportAsBibtex(@RequestParam("workIdsStr") String workIdsStr) {
        List<Long> workIds = Arrays.stream(workIdsStr.split(",")).mapToLong(n -> Long.parseLong(n)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);        
        return bibtexManager.generateBibtexReferenceList(getEffectiveUserOrcid(), workIds);
    } 

    /**
     * Search DB for id types to suggest to user if list empty, suggest the top
     * ten.
     */
    @RequestMapping(value = "/idTypes.json", method = RequestMethod.GET)
    public @ResponseBody List<Map<String, String>> searchExternalIDTypes(@RequestParam("query") String query) {
        List<Map<String, String>> datums = new ArrayList<>();

        // fetch results
        List<IdentifierType> types;
        if (query == null || query.trim().isEmpty()) {
            types = identifierTypeManager.fetchDefaultIdentifierTypes(getLocale());
        } else {
            types = identifierTypeManager.queryByPrefix(query, getLocale());
        }

        // format for output
        for (IdentifierType t : types) {
            if (IdentifierType.PRIMARY_USE_WORK.equals(t.getPrimaryUse())) {
                Map<String, String> datum1 = new HashMap<String, String>();
                datum1.put("name", t.getName());
                datum1.put("description", t.getDescription());
                datum1.put("resolutionPrefix", t.getResolutionPrefix());
                datums.add(datum1);
            }
        }

        return datums;
    }

    /**
     * Attempts to resolve an identifier to a landing page.
     * 
     * Do it real time (on exit)
     * 
     * @param type
     * @param value
     * @return "resolved" if it can be resolved, "resolvableType" if we
     *         attempted to resolve it.
     */
    // TODO: move to PIDController.
    @RequestMapping(value = "/id/{type}", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public @ResponseBody PIDResolutionResult checkIdResolution(@PathVariable("type") String type, @RequestParam("value") String value) {
        try {
            return resolverService.resolve(type, value);
        } catch (IllegalArgumentException iae) {
            return new PIDResolutionResult(false, true, false, null);
        }
    }

    @RequestMapping(value = "/resolve/{type}", method = RequestMethod.GET)
    public @ResponseBody WorkForm fetchWorkData(@PathVariable("type") String type, @RequestParam("value") String value) {
        Work w = resolverService.resolveMetadata(type, value);
        if (w == null) {
            return null;
        }
        WorkForm workForm = WorkForm.valueOf(w, maxContributorsForUI);
        initializeFields (workForm);
        validateWork(workForm);
        return workForm;
    }
}
