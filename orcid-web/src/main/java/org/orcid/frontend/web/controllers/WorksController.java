package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.QueryParam;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ActivityManager;
import org.orcid.core.manager.v3.BibtexManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.security.visibility.OrcidVisibilityDefaults;
import org.orcid.core.utils.v3.identifiers.PIDResolverService;
import org.orcid.frontend.web.pagination.WorksPage;
import org.orcid.frontend.web.pagination.WorksPaginator;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.v3.rc1.record.Relationship;
import org.orcid.jaxb.model.v3.rc1.record.Work;
import org.orcid.jaxb.model.v3.rc1.record.WorkCategory;
import org.orcid.jaxb.model.v3.rc1.record.WorkType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.KeyValue;
import org.orcid.pojo.PIDResolutionResult;
import org.orcid.pojo.ajaxForm.Contributor;
import org.orcid.pojo.ajaxForm.Date;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Text;
import org.orcid.pojo.ajaxForm.TranslatedTitleForm;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.ajaxForm.ActivityExternalIdentifier;
import org.orcid.pojo.ajaxForm.WorkForm;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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

    private static final Pattern LANGUAGE_CODE = Pattern.compile("([a-zA-Z]{2})(_[a-zA-Z]{2}){0,2}");

    @Resource(name = "workManagerV3")
    private WorkManager workManager;
    
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
    
    @Resource
    PIDResolverService resolverService;

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
    public @ResponseBody List<Long> groupWorks(@PathVariable("workIdsStr") String workIdsStr) {
        List<Long> workIds = Arrays.stream(workIdsStr.split(",")).mapToLong(n -> Long.parseLong(n)).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        workManager.createNewWorkGroup(workIds, getCurrentUserOrcid());
        return workIds;
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

    private void initializeFields(WorkForm w) {
        if (w.getVisibility() == null) {
            ProfileEntity profile = profileEntityCacheManager.retrieve(getEffectiveUserOrcid());
            org.orcid.jaxb.model.v3.rc1.common.Visibility defaultVis = org.orcid.jaxb.model.v3.rc1.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = profile.getActivitiesVisibilityDefault() == null
                    ? Visibility.valueOf(OrcidVisibilityDefaults.WORKS_DEFAULT.getVisibility()) : Visibility.valueOf(defaultVis);
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

        if (PojoUtil.isEmpty(w.getWorkCategory())) {
            Text wCategoryText = new Text();
            wCategoryText.setValue(new String());
            wCategoryText.setRequired(true);
            w.setWorkCategory(wCategoryText);
        }

        if (PojoUtil.isEmpty(w.getWorkType())) {
            Text wTypeText = new Text();
            wTypeText.setValue(new String());
            wTypeText.setRequired(true);
            w.setWorkType(wTypeText);
        }

        initializePublicationDate(w);

        if (w.getWorkExternalIdentifiers() == null || w.getWorkExternalIdentifiers().isEmpty()) {
            ActivityExternalIdentifier wei = new ActivityExternalIdentifier();
            Text wdiType = new Text();
            wdiType.setValue(new String());
            wei.setWorkExternalIdentifierId(new Text());
            wei.setWorkExternalIdentifierType(wdiType);
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

        Work work = workManager.getWork(this.getEffectiveUserOrcid(), workId);

        if (work != null) {
            WorkForm workForm = WorkForm.valueOf(work);
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
                            // contributor is an ORCID user - visibility of user's name in record must be taken into account 
                            ProfileEntity profileEntity = profileEntityCacheManager.retrieve(contributorOrcid);
                            String publicContributorCreditName = activityManager.getPublicCreditName(profileEntity);
                            contributor.setCreditName(Text.valueOf(publicContributorCreditName));
                        }
                    }
                }
            }

            return workForm;
        }

        return null;
    }

    /**
     * Creates a new work
     * 
     * @throws Exception
     */
    @RequestMapping(value = "/work.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm postWork(HttpServletRequest request, @RequestBody WorkForm workForm) throws Exception {
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
                    if (!PojoUtil.isEmpty(wExtId.getWorkExternalIdentifierType())) {
                        if (!PojoUtil.isEmpty(wExtId.getWorkExternalIdentifierId())) {
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
        newWork = workManager.createWork(getEffectiveUserOrcid(), newWork, false);

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

        Work updatedWork = workForm.toWork();
        // Edit work
        workManager.updateWork(userOrcid, updatedWork, false);
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
        if (work.getWorkCategory() != null)
            workWorkCategoryValidate(work);

        workWorkTypeValidate(work);
        copyErrors(work.getWorkType(), work);
        if (work.getWorkExternalIdentifiers() != null) {
            workWorkExternalIdentifiersValidate(work);
            for (ActivityExternalIdentifier wId : work.getWorkExternalIdentifiers()) {
                copyErrors(wId.getWorkExternalIdentifierId(), work);
                copyErrors(wId.getWorkExternalIdentifierType(), work);
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

    @RequestMapping(value = "/work/workCategoryValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workWorkCategoryValidate(@RequestBody WorkForm work) {
        work.getWorkCategory().setErrors(new ArrayList<String>());
        if (work.getWorkCategory().getValue() == null || work.getWorkCategory().getValue().trim().length() == 0) {
            setError(work.getWorkCategory(), "NotBlank.manualWork.workCategory");
        }

        return work;
    }

    @RequestMapping(value = "/work/workTypeValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workWorkTypeValidate(@RequestBody WorkForm work) {
        work.getWorkType().setErrors(new ArrayList<String>());
        if (work.getWorkType().getValue() == null || work.getWorkType().getValue().trim().length() == 0) {
            setError(work.getWorkType(), "NotBlank.manualWork.workType");
        }

        return work;
    }

    @RequestMapping(value = "/work/workExternalIdentifiersValidate.json", method = RequestMethod.POST)
    public @ResponseBody WorkForm workWorkExternalIdentifiersValidate(@RequestBody WorkForm work) {
        for (ActivityExternalIdentifier wId : work.getWorkExternalIdentifiers()) {
            if (wId.getWorkExternalIdentifierId() == null)
                wId.setWorkExternalIdentifierId(new Text());
            if (wId.getWorkExternalIdentifierType() == null)
                wId.setWorkExternalIdentifierType(new Text());
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

            //check type is valid
            Map<String,IdentifierType> types = identifierTypeManager.fetchIdentifierTypesByAPITypeName(getLocale());
            if (wId.getWorkExternalIdentifierType().getValue() != null  
                    && !wId.getWorkExternalIdentifierType().getValue().trim().isEmpty() 
                    && !types.keySet().contains(wId.getWorkExternalIdentifierType().getValue())){
                setError(wId.getWorkExternalIdentifierType(), "manualWork.id_invalid");
            }
            
            if (wId.getUrl() != null)
                validateUrl(wId.getUrl());                        
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
    public @ResponseBody WorksPage getWorkGroupsJson(@RequestParam("offset") int offset, @RequestParam("sort") String sort, @RequestParam("sortAsc") boolean sortAsc) {
        String orcid = getEffectiveUserOrcid();
        return worksPaginator.getWorksPage(orcid, offset, false, sort, sortAsc);
    }
    
    @RequestMapping(value = "/allWorks.json", method = RequestMethod.GET)
    public @ResponseBody WorksPage getAllWorkGroupsJson(@RequestParam("sort") String sort, @RequestParam("sortAsc") boolean sortAsc) {
        String orcid = getEffectiveUserOrcid();
        return worksPaginator.getAllWorks(orcid, sort, sortAsc);
    }
    
    @RequestMapping(value = "/refreshWorks.json", method = RequestMethod.GET)
    public @ResponseBody WorksPage refreshWorkGroupsJson(@RequestParam("limit") int limit, @RequestParam("sort") String sort, @RequestParam("sortAsc") boolean sortAsc) {
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
        workManager.updateVisibilities(orcid, workIds, org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(visibilityStr));
        return workIds;
    }

    /**
     * Return a list of work types based on the work category provided as a
     * parameter
     * 
     * @param workCategoryName
     * @return a map containing the list of types associated with that type and
     *         his localized name
     */
    @RequestMapping(value = "/loadWorkTypes.json", method = RequestMethod.GET)
    public @ResponseBody List<KeyValue> retriveWorkTypes(@RequestParam(value = "workCategory") String workCategoryName) {
        List<KeyValue> types = new ArrayList<KeyValue>();

        WorkCategory workCategory = null;
        if (!PojoUtil.isEmpty(workCategoryName))
            workCategory = WorkCategory.fromValue(workCategoryName);
        // Get work types based on category
        if (workCategory != null) {
            for (WorkType workType : workCategory.getSubTypes()) {
                // Dont put work type UNDEFINED
                if (!workType.equals(WorkType.UNDEFINED)) {
                    types.add(new KeyValue(workType.value(), getMessage(new StringBuffer("org.orcid.jaxb.model.record.WorkType.").append(workType.value()).toString())));
                }
            }
        } else {
            // Get all work types
            for (WorkType workType : WorkType.values()) {
                // Dont put work type UNDEFINED
                if (!workType.equals(WorkType.UNDEFINED)) {
                    types.add(new KeyValue(workType.value(), getMessage(new StringBuffer("org.orcid.jaxb.model.record.WorkType.").append(workType.value()).toString())));
                }
            }
        }
        return types;
    }

    /** Returns the works in bibtex format.  
     * This format encodes everything it can using latex encoding and leaves the rest as UTF-8
     * Note, you must specifically set the encoding to UTF-8 in the produces argument to enforce the encoding over the wire.
     */
    @RequestMapping(value = "/works.bib", method = RequestMethod.GET, produces = "text/plain; charset=utf-8")
    public @ResponseBody String fetchBibtex() {
        return bibtexManager.generateBibtexReferenceList(getEffectiveUserOrcid());
    }
    
    /**
     * Search DB for id types to suggest to user
     * if list empty, suggest the top ten.
     */
    @RequestMapping(value = "/idTypes.json", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, String>> searchExternalIDTypes(@RequestParam("query") String query) {
        List<Map<String, String>> datums = new ArrayList<>();
        
        //fetch results
        List<IdentifierType> types;             
        if (query == null || query.trim().isEmpty()){
            types = identifierTypeManager.fetchDefaultIdentifierTypes(getLocale());
        } else {
            types = identifierTypeManager.queryByPrefix(query, getLocale());
        }
                
        //format for output
        for (IdentifierType t : types){
            if (IdentifierType.PRIMARY_USE_WORK.equals(t.getPrimaryUse())){
                Map<String, String> datum1 = new HashMap<String,String>();
                datum1.put("name", t.getName());
                datum1.put("description", t.getDescription());
                datum1.put("resolutionPrefix", t.getResolutionPrefix());
                datums.add(datum1);                                
            }
        }
        
        return datums;
    }
    
    /** Attempts to resolve an identifier to a landing page.
     * 
     * Do it real time (on exit)
     * 
     * @param type
     * @param value
     * @return "resolved" if it can be resolved, "resolvableType" if we attempted to resolve it.
     */
    @RequestMapping(value = "/id/{type}", method = RequestMethod.GET)
    public @ResponseBody PIDResolutionResult checkIdResolution(@PathVariable("type") String type, @RequestParam("value") String value){        
        return resolverService.resolve(type, value);
    }

}