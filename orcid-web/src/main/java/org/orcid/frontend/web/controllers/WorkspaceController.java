/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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
import java.util.Currency;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.manager.OtherNameManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.ProfileKeywordManager;
import org.orcid.core.manager.ResearcherUrlManager;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.frontend.web.util.NumberList;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.FundingContributorRole;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.record_v2.CitationType;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.Role;
import org.orcid.jaxb.model.record_v2.WorkCategory;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.ThirdPartyRedirect;
import org.orcid.pojo.ajaxForm.ExternalIdentifierForm;
import org.orcid.pojo.ajaxForm.ExternalIdentifiersForm;
import org.orcid.pojo.ajaxForm.KeywordForm;
import org.orcid.pojo.ajaxForm.KeywordsForm;
import org.orcid.pojo.ajaxForm.OtherNameForm;
import org.orcid.pojo.ajaxForm.OtherNamesForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.ajaxForm.WebsiteForm;
import org.orcid.pojo.ajaxForm.WebsitesForm;
import org.orcid.utils.FunctionsOverCollections;
import org.orcid.utils.solr.entities.OrgDisambiguatedSolrDocument;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

/**
 * @author Will Simpson
 */
@Controller("workspaceController")
public class WorkspaceController extends BaseWorkspaceController {

    @Resource
    private ThirdPartyLinkManager thirdPartyLinkManager;

    @Resource
    private ExternalIdentifierManager externalIdentifierManager;    
    
    @Resource
    private ProfileKeywordManager profileKeywordManager;
    
    @Resource
    private OtherNameManager otherNameManager;

    @Resource
    private Jpa2JaxbAdapter jpa2JaxbAdapter;

    @Resource
    private Jaxb2JpaAdapter jaxb2JpaAdapter;

    @Resource
    private WorkManager workManager;
    
    @Resource
    private ResearcherUrlManager researcherUrlManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource(name = "profileEntityManager")
    private ProfileEntityManager profileEntityManager;
    
    @Resource
    private IdentifierTypeManager identifierTypeManager;
    
    private long getLastModifiedTime(String orcid) {        
        return profileEntityManager.getLastModified(orcid);
    }
    
    @RequestMapping(value = { "/workspace/retrieve-work-impor-wizards.json" }, method = RequestMethod.GET)
    public @ResponseBody List<OrcidClient> retrieveWorkImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeWorksImport();
    }

    @ModelAttribute("fundingImportWizards")
    public List<OrcidClient> retrieveFundingImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeFundingImport();
    }
    
    @RequestMapping(value = { "/workspace/retrieve-peer-review-import-wizards.json" }, method = RequestMethod.GET)
    public @ResponseBody List<OrcidClient> retrievePeerReviewImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopePeerReviewImport();
    }

    @ModelAttribute("affiliationTypes")
    public Map<String, String> retrieveAffiliationTypesAsMap() {
        Map<String, String> affiliationTypes = new LinkedHashMap<String, String>();
        for (AffiliationType affiliationType : AffiliationType.values()) {
            affiliationTypes.put(affiliationType.value(), getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.AffiliationType.class, affiliationType.value())));
        }
        return FunctionsOverCollections.sortMapsByValues(affiliationTypes);
    }

    @ModelAttribute("fundingTypes")
    public Map<String, String> retrieveFundingTypesAsMap() {
        Map<String, String> grantTypes = new LinkedHashMap<String, String>();
        for (FundingType fundingType : FundingType.values()) {
            grantTypes.put(fundingType.value(), getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.FundingType.class, fundingType.value())));
        }
        return FunctionsOverCollections.sortMapsByValues(grantTypes);
    }

    @ModelAttribute("currencyCodeTypes")
    public Map<String, String> retrieveCurrencyCodesTypesAsMap() {
        Map<String, String> currencyCodeTypes = new LinkedHashMap<String, String>();
        //Add an empty one
        currencyCodeTypes.put("", "");
        for (Currency currency : Currency.getAvailableCurrencies()) {
            currencyCodeTypes.put(currency.getCurrencyCode(), currency.getCurrencyCode());
        }
        return FunctionsOverCollections.sortMapsByValues(currencyCodeTypes);
    }

    @ModelAttribute("affiliationLongDescriptionTypes")
    public Map<String, String> retrieveAffiliationLongDescriptionTypesAsMap() {
        Map<String, String> organizationTypes = new LinkedHashMap<String, String>();
        for (AffiliationType organizationType : AffiliationType.values()) {
            organizationTypes.put(organizationType.value(), getMessage(AffiliationType.class.getName() + '.' + "longDescription" + '.' + organizationType.value()));
        }
        return FunctionsOverCollections.sortMapsByValues(organizationTypes);
    }

    @ModelAttribute("workCategories")
    public Map<String, String> retrieveWorkCategoriesAsMap() {
        Map<String, String> workCategories = new LinkedHashMap<String, String>();

        for (WorkCategory workCategory : WorkCategory.values()) {
            workCategories.put(workCategory.value(), getMessage(new StringBuffer("org.orcid.jaxb.model.record.WorkCategory.").append(workCategory.value()).toString()));
        }

        return workCategories;
    }

    @ModelAttribute("citationTypes")
    public Map<String, String> retrieveTypesAsMap() {
        Map<String, String> citationTypes = new LinkedHashMap<String, String>();

        for (CitationType citationType : CitationType.values()) {
            citationTypes.put(citationType.value(), getMessage(new StringBuffer("org.orcid.jaxb.model.record.CitationType.").append(citationType.value()).toString()));
        }

        return FunctionsOverCollections.sortMapsByValues(citationTypes);
    }

    @ModelAttribute("years")
    public Map<String, String> retrieveYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList();
        map.put("", getMessage("select.item.year"));
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("fundingYears")
    public Map<String, String> retrieveFundingYearsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList(10);
        map.put("", getMessage("select.item.year"));
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    @ModelAttribute("months")
    public Map<String, String> retrieveMonthsAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(12);
        map.put("", getMessage("select.item.month"));
        for (String month : list) {
            map.put(month, month);
        }
        return map;
    }

    @ModelAttribute("days")
    public Map<String, String> retrieveDaysAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = NumberList.createList(31);
        map.put("", getMessage("select.item.day"));
        for (String day : list) {
            map.put(day, day);
        }
        return map;
    }

    /**
     * Generate a map with ID types. The map is different from the rest, because
     * it will be ordered in the form: value -> key, to keep the map alpha
     * ordered in UI.
     * */
    @ModelAttribute("idTypes")
    public Map<String, String> retrieveIdTypesAsMap() {
        
        Map<String,String> map = new TreeMap<String,String>();
            Map<String,IdentifierType> types = identifierTypeManager.fetchIdentifierTypesByAPITypeName(getLocale());
            for (String type : types.keySet()) {
                try{
                    map.put(types.get(type).getDescription(), type);
                }catch (NoSuchMessageException e){
                    //we will skip these from UI for now.
                    //map.put(type, type);                    
                }
            }
            return FunctionsOverCollections.sortMapsByValues(map);
    }

    @ModelAttribute("roles")
    public Map<String, String> retrieveRolesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();
        try{
            for (ContributorRole contributorRole : ContributorRole.values()) {
                map.put(contributorRole.value(), getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.ContributorRole.class, contributorRole.value())));
            }
            return FunctionsOverCollections.sortMapsByValues(map);
        }catch(Exception e){
            e.printStackTrace();
        }
        return map;
    }

    @ModelAttribute("fundingRoles")
    public Map<String, String> retrieveFundingRolesAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();

        for (FundingContributorRole contributorRole : FundingContributorRole.values()) {
            map.put(contributorRole.value(), getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.FundingContributorRole.class, contributorRole.value())));
        }
        return map;
    }

    @ModelAttribute("sequences")
    public Map<String, String> retrieveSequencesAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();

        for (SequenceType sequenceType : SequenceType.values()) {
            map.put(sequenceType.value(), getMessage(buildInternationalizationKey(org.orcid.jaxb.model.message.SequenceType.class, sequenceType.value())));
        }

        return FunctionsOverCollections.sortMapsByValues(map);
    }

    @ModelAttribute("languages")
    public Map<String, String> retrieveLocalesAsMap() {
        return lm.getLanguagesMap(localeManager.getLocale());
    }

    @ModelAttribute("peerReviewRoles")
    public Map<String, String> retrievePeerReviewRolesAsMap() {
        Map<String, String> peerReviewRoles = new LinkedHashMap<String, String>();
        for (Role role : Role.values()) {
            peerReviewRoles.put(role.value(), getMessage(new StringBuffer("org.orcid.jaxb.model.record.Role.").append(role.name()).toString()));
        }
        return FunctionsOverCollections.sortMapsByValues(peerReviewRoles);
    }
    
    @ModelAttribute("peerReviewTypes")
    public Map<String, String> retrievePeerReviewTypesAsMap() {
        Map<String, String> peerReviewTypes = new LinkedHashMap<String, String>();
        for (PeerReviewType type : PeerReviewType.values()) {
            peerReviewTypes.put(type.value(), getMessage(new StringBuffer("org.orcid.jaxb.model.record.PeerReviewType.").append(type.name()).toString()));
        }
        return FunctionsOverCollections.sortMapsByValues(peerReviewTypes);
    }
    
    @ModelAttribute("workTypes")
    public Map<String, String> retrieveWorkTypesAsMap() {
        Map<String, String> types = new LinkedHashMap<String, String>();
        for (WorkType type : WorkType.values()) {
            types.put(type.value(), getMessage(new StringBuffer("org.orcid.jaxb.model.record.WorkType.").append(type.value()).toString()));
        }
        return FunctionsOverCollections.sortMapsByValues(types);
    }
    
    @RequestMapping(value = {"/my-orcid3","/my-orcid", "/workspace"}, method = RequestMethod.GET)
    public ModelAndView viewWorkspace3(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults) {
        ModelAndView mav = new ModelAndView("workspace_v3");
        mav.addObject("showPrivacy", true);

        mav.addObject("currentLocaleKey", localeManager.getLocale().toString());
        mav.addObject("sendEmailFrequencies", retrieveEmailFrequenciesAsMap());
        mav.addObject("currentLocaleValue", lm.buildLanguageValue(localeManager.getLocale(), localeManager.getLocale()));
        return mav;
    }

    @RequestMapping(value = "/my-orcid/keywordsForms.json", method = RequestMethod.GET)
    public @ResponseBody
    KeywordsForm getKeywordsFormJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {     
        long lastModifiedTime = getLastModifiedTime(getCurrentUserOrcid());
        Keywords keywords = profileKeywordManager.getKeywords(getCurrentUserOrcid(), lastModifiedTime);        
        KeywordsForm form = KeywordsForm.valueOf(keywords);
        
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            form.setVisibility(Visibility.valueOf(profile.getActivitiesVisibilityDefault()));
        }
        
        return form;
    }
    
    @RequestMapping(value = "/my-orcid/keywordsForms.json", method = RequestMethod.POST)
    public @ResponseBody
    KeywordsForm setKeywordsFormJson(HttpServletRequest request, @RequestBody KeywordsForm kf) throws NoSuchRequestHandlingMethodException {
        kf.setErrors(new ArrayList<String>());              
        ProfileEntity profile = profileEntityCacheManager.retrieve(getEffectiveUserOrcid());
        Visibility defaultVisibility = Visibility.valueOf(profile.getActivitiesVisibilityDefault());        
        if(kf != null) {
            Iterator<KeywordForm> it = kf.getKeywords().iterator();            
            while (it.hasNext()) {
                KeywordForm k = it.next();
                if(!PojoUtil.isEmpty(k.getContent())) {
                    if (k.getContent().length() > SiteConstants.KEYWORD_MAX_LENGTH) {
                        k.setContent(k.getContent().substring(0,SiteConstants.KEYWORD_MAX_LENGTH));
                    }                    
                } else {
                    it.remove();
                } 
                
                //Set default visibility in case it is null
                if(k.getVisibility() == null || k.getVisibility().getVisibility() == null) {
                    k.setVisibility(defaultVisibility);
                }
                
                copyErrors(k, kf);
            }

            if (kf.getErrors().size()>0) {
                return kf;   
            }
            
            Keywords updatedKeywords = kf.toKeywords();                        
            profileKeywordManager.updateKeywords(getCurrentUserOrcid(), updatedKeywords);            
        }
        return kf;
    }
    
    @RequestMapping(value = "/my-orcid/otherNamesForms.json", method = RequestMethod.GET)
    public @ResponseBody
    OtherNamesForm getOtherNamesFormJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        long lastModifiedTime = getLastModifiedTime(getCurrentUserOrcid());
        OtherNames otherNames = otherNameManager.getOtherNames(getCurrentUserOrcid(), lastModifiedTime);                
        OtherNamesForm form = OtherNamesForm.valueOf(otherNames);
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            form.setVisibility(Visibility.valueOf(profile.getActivitiesVisibilityDefault()));
        }
        return form;
    }
    
    @RequestMapping(value = "/my-orcid/otherNamesForms.json", method = RequestMethod.POST)
    public @ResponseBody
    OtherNamesForm setOtherNamesFormJson(@RequestBody OtherNamesForm onf) throws NoSuchRequestHandlingMethodException {
        onf.setErrors(new ArrayList<String>());        
        ProfileEntity profile = profileEntityCacheManager.retrieve(getEffectiveUserOrcid());
        Visibility defaultVisibility = Visibility.valueOf(profile.getActivitiesVisibilityDefault());        
        if(onf != null) {
            Iterator<OtherNameForm> it = onf.getOtherNames().iterator();
            while(it.hasNext()) {
                OtherNameForm form = it.next();
                if(PojoUtil.isEmpty(form.getContent())) {
                   it.remove();
                   continue;
                } 
                if(form.getContent().length() > SiteConstants.MAX_LENGTH_255) {
                    form.setContent(form.getContent().substring(0, SiteConstants.MAX_LENGTH_255));
                }
                //Set default visibility in case it is null
                if(form.getVisibility() == null || form.getVisibility().getVisibility() == null) {
                    form.setVisibility(defaultVisibility);
                }                
                copyErrors(form, onf);
            }
                    
            if (onf.getErrors().size()>0) {
                return onf;   
            }
            
            OtherNames otherNames = onf.toOtherNames();                
            otherNameManager.updateOtherNames(getEffectiveUserOrcid(), otherNames);            
        }

        return onf;
    }
    
    /**
     * Retrieve all external identifiers as a json string
     * */
    @RequestMapping(value = "/my-orcid/websitesForms.json", method = RequestMethod.GET)
    public @ResponseBody
    WebsitesForm getWebsitesFormJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        ResearcherUrls rUrls = researcherUrlManager.getResearcherUrls(getCurrentUserOrcid(), getLastModifiedTime(getCurrentUserOrcid()));                 
        WebsitesForm form = WebsitesForm.valueOf(rUrls);
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            form.setVisibility(Visibility.valueOf(profile.getActivitiesVisibilityDefault()));
        }
        return form;
    }
    
    /**
     * Retrieve all external identifiers as a json string
     * */
    @RequestMapping(value = "/my-orcid/websitesForms.json", method = RequestMethod.POST)
    public @ResponseBody
    WebsitesForm setWebsitesFormJson(HttpServletRequest request, @RequestBody WebsitesForm ws) throws NoSuchRequestHandlingMethodException {
        ws.setErrors(new ArrayList<String>());
        ProfileEntity profile = profileEntityCacheManager.retrieve(getEffectiveUserOrcid());
        Visibility defaultVisibility = Visibility.valueOf(profile.getActivitiesVisibilityDefault());
        if(ws != null) {
            Set<String> existingUrls = new HashSet<String>();
            for (WebsiteForm w : ws.getWebsites()) {
                //Clean old errors
                w.setErrors(new ArrayList<String>());
                
                //Validate
                if(!validateUrl(w.getUrl())) {
                    w.getErrors().add(getMessage("common.invalid_url"));                
                }
                if(isLongerThan(w.getUrlName(), SiteConstants.URL_MAX_LENGTH)) {
                    w.getErrors().add(getMessage("manualWork.length_less_X"));
                }         
                
                //Check there are no duplicates
                if(existingUrls.contains(w.getUrl())) {
                    w.getErrors().add(getMessage("researcher_url.error.duplicated", w.getUrl()));
                } else {
                    existingUrls.add(w.getUrl());
                }
                //Set default visibility in case it is null
                if(w.getVisibility() == null || w.getVisibility().getVisibility() == null) {
                    w.setVisibility(defaultVisibility);
                } 
                copyErrors(w, ws);
            }   
            
            if (ws.getErrors().size()>0) {
                return ws;   
            }
            
            ResearcherUrls rUrls = ws.toResearcherUrls();
            researcherUrlManager.updateResearcherUrls(getCurrentUserOrcid(), rUrls);            
        }
        
        return ws;
    }
    
    /**
     * Retrieve all external identifiers as a json string
     * */    
    @RequestMapping(value = "/my-orcid/externalIdentifiers.json", method = RequestMethod.GET)
    public @ResponseBody
    ExternalIdentifiersForm getExternalIdentifiersJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        PersonExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(getCurrentUserOrcid(), getLastModifiedTime(getCurrentUserOrcid()));        
        return ExternalIdentifiersForm.valueOf(extIds);
    }

    /**
     * Delete an external identifier
     * */
    @RequestMapping(value = "/my-orcid/externalIdentifiers.json", method = RequestMethod.DELETE)
    public @ResponseBody
    ExternalIdentifierForm removeExternalIdentifierJson(HttpServletRequest request, @RequestBody ExternalIdentifierForm externalIdentifier) {
        if(externalIdentifier != null && externalIdentifier.getPutCode() != null) {
            externalIdentifierManager.deleteExternalIdentifier(getCurrentUserOrcid(), Long.valueOf(externalIdentifier.getPutCode()), false);
        }       
        return externalIdentifier;
    }
    
    /**
     * Update the visibility of the given external identifeirs
     * */
    @RequestMapping(value = "/my-orcid/externalIdentifiers.json", method = RequestMethod.POST)
    public @ResponseBody
    ExternalIdentifiersForm updateExternalIdentifierJson(HttpServletRequest request, @RequestBody ExternalIdentifiersForm externalIdentifiersForm) {        
        PersonExternalIdentifiers externalIdentifiers = externalIdentifiersForm.toPersonExternalIdentifiers();
        externalIdentifiers = externalIdentifierManager.updateExternalIdentifiers(getCurrentUserOrcid(), externalIdentifiers);
        return externalIdentifiersForm;
    }
    
    @RequestMapping(value = "/my-orcid/sourceGrantReadWizard.json", method = RequestMethod.GET)
    public @ResponseBody
    ThirdPartyRedirect getSourceGrantReadWizard() {
        ThirdPartyRedirect tpr = new ThirdPartyRedirect();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getEffectiveUserOrcid());        
        if(profile.getSource() == null || profile.getSource().getSourceId() == null) {
            return tpr;
        }        
        String sourcStr = profile.getSource().getSourceId();     
        // Check that the cache is up to date
        evictThirdPartyLinkManagerCacheIfNeeded();
        // Get list of clients
        List<OrcidClient> orcidClients = thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeReadAccess();
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
     * Reads the latest cache version from database, compare it against the
     * local version; if they are different, evicts all caches.
     * 
     * @return true if the local cache version is different than the one on
     *         database
     * */
    private boolean evictThirdPartyLinkManagerCacheIfNeeded() {
        long currentCachedVersion = thirdPartyLinkManager.getLocalCacheVersion();
        long dbCacheVersion = thirdPartyLinkManager.getDatabaseCacheVersion();
        if (currentCachedVersion < dbCacheVersion) {
            // If version changed, evict the cache
            thirdPartyLinkManager.evictAll();
            return true;
        }
        return false;
    }    
}