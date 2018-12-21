package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ThirdPartyLinkManager;
import org.orcid.core.manager.v3.ExternalIdentifierManager;
import org.orcid.core.manager.v3.OtherNameManager;
import org.orcid.core.manager.v3.ProfileKeywordManager;
import org.orcid.core.manager.v3.ResearcherUrlManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.FundingContributorRole;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.v3.rc2.record.CitationType;
import org.orcid.jaxb.model.v3.rc2.record.Keywords;
import org.orcid.jaxb.model.v3.rc2.record.OtherNames;
import org.orcid.jaxb.model.v3.rc2.record.PeerReviewType;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc2.record.Role;
import org.orcid.jaxb.model.v3.rc2.record.WorkCategory;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.IdentifierType;
import org.orcid.pojo.ThirdPartyRedirect;
import org.orcid.pojo.ajaxForm.ExternalIdentifierForm;
import org.orcid.pojo.ajaxForm.ExternalIdentifiersForm;
import org.orcid.pojo.ajaxForm.ImportWizzardClientForm;
import org.orcid.pojo.ajaxForm.KeywordForm;
import org.orcid.pojo.ajaxForm.KeywordsForm;
import org.orcid.pojo.ajaxForm.OtherNameForm;
import org.orcid.pojo.ajaxForm.OtherNamesForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.pojo.ajaxForm.Visibility;
import org.orcid.pojo.ajaxForm.WebsiteForm;
import org.orcid.pojo.ajaxForm.WebsitesForm;
import org.orcid.utils.FunctionsOverCollections;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
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

    @Resource(name = "externalIdentifierManagerV3")
    private ExternalIdentifierManager externalIdentifierManager;    
    
    @Resource(name = "profileKeywordManagerV3")
    private ProfileKeywordManager profileKeywordManager;
    
    @Resource(name = "workManagerV3")
    private WorkManager workManager;
    
    @Resource(name = "otherNameManagerV3")
    private OtherNameManager otherNameManager;
    
    @Resource(name = "researcherUrlManagerV3")
    private ResearcherUrlManager researcherUrlManager;

    @Resource(name = "languagesMap")
    private LanguagesMap lm;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Resource
    private IdentifierTypeManager identifierTypeManager;
    
    @Resource
    private LocaleManager localeManager;
    
    @RequestMapping(value = { "/workspace/retrieve-work-import-wizards.json" }, method = RequestMethod.GET)
    public @ResponseBody List<ImportWizzardClientForm> retrieveWorkImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeWorksImport(localeManager.getLocale());        
    }

    @ModelAttribute("fundingImportWizards")
    public List<ImportWizzardClientForm> retrieveFundingImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeFundingImport(localeManager.getLocale());
    }
    
    @RequestMapping(value = { "/workspace/retrieve-peer-review-import-wizards.json" }, method = RequestMethod.GET)
    public @ResponseBody List<ImportWizzardClientForm> retrievePeerReviewImportWizards() {
        return thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopePeerReviewImport(localeManager.getLocale());
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
    
    @RequestMapping(value = { "/my-orcid3", "/my-orcid", "/workspace" }, method = RequestMethod.GET)
    public ModelAndView viewWorkspace3(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults,
            @CookieValue(value = "justRegistered", defaultValue = "false") boolean justRegistered) {
        ModelAndView mav = new ModelAndView("workspace_v3");
        mav.addObject("showPrivacy", true);
        mav.addObject("currentLocaleKey", localeManager.getLocale().toString());
        mav.addObject("sendEmailFrequencies", retrieveEmailFrequenciesAsMap());
        mav.addObject("currentLocaleValue", lm.buildLanguageValue(localeManager.getLocale(), localeManager.getLocale()));
        mav.addObject("justRegistered", justRegistered);
        Cookie justRegisteredCookie = new Cookie("justRegistered", null);
        justRegisteredCookie.setMaxAge(0);
        response.addCookie(justRegisteredCookie);
        return mav;
    }

    @RequestMapping(value = "/my-orcid/keywordsForms.json", method = RequestMethod.GET)
    public @ResponseBody
    KeywordsForm getKeywordsFormJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {     
        Keywords keywords = profileKeywordManager.getKeywords(getCurrentUserOrcid());        
        KeywordsForm form = KeywordsForm.valueOf(keywords);
        
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            org.orcid.jaxb.model.v3.rc2.common.Visibility defaultVis = org.orcid.jaxb.model.v3.rc2.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = Visibility.valueOf(defaultVis);
            form.setVisibility(v);
        }
        
        return form;
    }
    
    @RequestMapping(value = "/my-orcid/keywordsForms.json", method = RequestMethod.POST)
    public @ResponseBody
    KeywordsForm setKeywordsFormJson(HttpServletRequest request, @RequestBody KeywordsForm kf) throws NoSuchRequestHandlingMethodException {
        kf.setErrors(new ArrayList<String>());              
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
                
                //Validate visibility is not null
                validateVisibility(k);
                
                copyErrors(k, kf);
                copyErrors(k.getVisibility(), kf);
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
        OtherNames otherNames = otherNameManager.getOtherNames(getCurrentUserOrcid());                
        OtherNamesForm form = OtherNamesForm.valueOf(otherNames);
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            org.orcid.jaxb.model.v3.rc2.common.Visibility defaultVis = org.orcid.jaxb.model.v3.rc2.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = Visibility.valueOf(defaultVis);
            form.setVisibility(v);
        }
        return form;
    }
    
    @RequestMapping(value = "/my-orcid/otherNamesForms.json", method = RequestMethod.POST)
    public @ResponseBody
    OtherNamesForm setOtherNamesFormJson(@RequestBody OtherNamesForm onf) throws NoSuchRequestHandlingMethodException {
        onf.setErrors(new ArrayList<String>());        
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
                
                //Validate visibility is not null
                validateVisibility(form);
                
                copyErrors(form, onf);
                copyErrors(form.getVisibility(), onf);
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
        ResearcherUrls rUrls = researcherUrlManager.getResearcherUrls(getCurrentUserOrcid());                 
        WebsitesForm form = WebsitesForm.valueOf(rUrls);
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            org.orcid.jaxb.model.v3.rc2.common.Visibility defaultVis = org.orcid.jaxb.model.v3.rc2.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = Visibility.valueOf(defaultVis);
            form.setVisibility(v);
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
        if(ws != null) {
            Set<String> existingUrls = new HashSet<String>();
            for (WebsiteForm w : ws.getWebsites()) {
                //Clean old errors
                w.setErrors(new ArrayList<String>());
                
                // Validate url
                validateUrl(w.getUrl());
                copyErrors(w.getUrl(), w);
                
                // Validate url name
                if(isLongerThan(w.getUrlName(), SiteConstants.URL_NAME_MAX_LENGTH)) {
                    w.getErrors().add(getMessage("manualWork.length_less_X", SiteConstants.URL_NAME_MAX_LENGTH));
                }         
                
                //Check there are no duplicates
                if(existingUrls.contains(w.getUrl().getValue())) {
                    w.getErrors().add(getMessage("researcher_url.error.duplicated", w.getUrl()));
                } else {
                    existingUrls.add(w.getUrl().getValue());
                }
                //Validate visibility is not null
                validateVisibility(w);
                 
                copyErrors(w, ws);
                copyErrors(w.getUrl(), ws);
                copyErrors(w.getVisibility(), ws);
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
        PersonExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(getCurrentUserOrcid()); 
        ExternalIdentifiersForm form = ExternalIdentifiersForm.valueOf(extIds);
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            org.orcid.jaxb.model.v3.rc2.common.Visibility defaultVis = org.orcid.jaxb.model.v3.rc2.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = Visibility.valueOf(defaultVis);
            form.setVisibility(v);
        }
        return form;
        
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
        externalIdentifiersForm.setErrors(new ArrayList<String>());
        //Validate visibility is not null
        if(externalIdentifiersForm != null && externalIdentifiersForm.getExternalIdentifiers() != null) {
            for(ExternalIdentifierForm extId : externalIdentifiersForm.getExternalIdentifiers()) {
                //Validate visibility is not null
                validateVisibility(extId);
                copyErrors(extId, externalIdentifiersForm);
            }            
        }
        
        if(!externalIdentifiersForm.getErrors().isEmpty()) {
            return externalIdentifiersForm;
        }        
                        
        PersonExternalIdentifiers externalIdentifiers = externalIdentifiersForm.toPersonExternalIdentifiers();
        externalIdentifiers = externalIdentifierManager.updateExternalIdentifiers(getCurrentUserOrcid(), externalIdentifiers);
        return externalIdentifiersForm;
    }
    
    @RequestMapping(value = "/my-orcid/sourceGrantReadWizard.json", method = RequestMethod.GET)
    public @ResponseBody
    ThirdPartyRedirect getSourceGrantReadWizard() {
        ThirdPartyRedirect tpr = new ThirdPartyRedirect();
        ProfileEntity profile = profileEntityCacheManager.retrieve(getEffectiveUserOrcid());        
        if(profile.getSource() == null || SourceEntityUtils.getSourceId(profile.getSource()) == null) {
            return tpr;
        }        
        String sourcStr = SourceEntityUtils.getSourceId(profile.getSource());     
        // Check that the cache is up to date
        evictThirdPartyLinkManagerCacheIfNeeded();
        // Get list of clients
        List<ImportWizzardClientForm> clients = thirdPartyLinkManager.findOrcidClientsWithPredefinedOauthScopeReadAccess(localeManager.getLocale());
        for (ImportWizzardClientForm client : clients) {
            if (sourcStr.equals(client.getId())) {
                String redirect = getBaseUri() + "/oauth/authorize?client_id=" + client.getId() + "&response_type=code&scope=" +client.getScopes()
                        + "&redirect_uri=" + client.getRedirectUri();
                tpr.setUrl(redirect);
                tpr.setDisplayName(client.getName());
                tpr.setShortDescription(client.getDescription());
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