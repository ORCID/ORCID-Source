package org.orcid.frontend.web.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.IdentifierTypeManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ExternalIdentifierManager;
import org.orcid.core.manager.v3.OtherNameManager;
import org.orcid.core.manager.v3.ProfileKeywordManager;
import org.orcid.core.manager.v3.ResearcherUrlManager;
import org.orcid.core.manager.v3.WorkManager;
import org.orcid.core.utils.FunctionsOverCollections;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.frontend.web.util.ThirdPartyLinkManager;
import org.orcid.jaxb.model.common.CitationType;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.FundingContributorRole;
import org.orcid.jaxb.model.message.FundingType;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.v3.release.record.Keywords;
import org.orcid.jaxb.model.v3.release.record.OtherNames;
import org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.release.record.ResearcherUrls;
import org.orcid.persistence.constants.SiteConstants;
import org.orcid.persistence.jpa.entities.ProfileEntity;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
//import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

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
    
    @RequestMapping(value = { "/workspace/retrieve-funding-import-wizards.json" }, method = RequestMethod.GET)
    public @ResponseBody List<ImportWizzardClientForm> retrieveFundingImportWizards() {
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

    @ModelAttribute("citationTypes")
    public Map<String, String> retrieveTypesAsMap() {
        Map<String, String> citationTypes = new LinkedHashMap<String, String>();

        for (CitationType citationType : CitationType.values()) {
            citationTypes.put(citationType.value(), getMessage(new StringBuffer("org.orcid.jaxb.model.record.CitationType.").append(citationType.value()).toString()));
        }

        return FunctionsOverCollections.sortMapsByValues(citationTypes);
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

    @RequestMapping(value = { "/my-orcid3", "/my-orcid", "/workspace" }, method = RequestMethod.GET)
    public ModelAndView viewWorkspace3(HttpServletRequest request, HttpServletResponse response, @RequestParam(value = "page", defaultValue = "1") int pageNo, @RequestParam(value = "maxResults", defaultValue = "200") int maxResults, @RequestParam(value = "orcid", defaultValue = "") String orcid) throws ServletException, IOException {
       
        if (!orcid.equals(getCurrentUserOrcid())){
            String redirectUrl = request.getRequestURL().toString();
            redirectUrl += "?orcid="+getCurrentUserOrcid();
            if (request.getQueryString() != null && orcid.equals("")){
                redirectUrl += "&"+request.getQueryString();
            }
            response.sendRedirect(redirectUrl);
            
        }

        return new ModelAndView("workspace_v3");
    }

    @RequestMapping(value = "/my-orcid/keywordsForms.json", method = RequestMethod.GET)
    public @ResponseBody
    KeywordsForm getKeywordsFormJson(HttpServletRequest request) {     
        Keywords keywords = profileKeywordManager.getKeywords(getCurrentUserOrcid());        
        KeywordsForm form = KeywordsForm.valueOf(keywords);
        
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            org.orcid.jaxb.model.v3.release.common.Visibility defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = Visibility.valueOf(defaultVis);
            form.setVisibility(v);
        }
        
        return form;
    }
    
    @RequestMapping(value = "/my-orcid/keywordsForms.json", method = RequestMethod.POST)
    public @ResponseBody
    KeywordsForm setKeywordsFormJson(HttpServletRequest request, @RequestBody KeywordsForm kf)  {
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
                
                k.setErrors(new ArrayList<String>());
                if (k.getContent() != null && k.getContent().length() >= 100)
                    setError(k, "Pattern.registrationForm.keywords");

                //Validate visibility is not null
                validateVisibility(k);
                
                copyErrors(k, kf);
                copyErrors(k.getVisibility(), kf);
            }

            if (kf.getErrors().size()>0) {
                return kf;   
            }
            
            Keywords updatedKeywords = kf.toKeywords();
            KeywordsForm keywordsForm = KeywordsForm.valueOf(profileKeywordManager.getKeywords(getCurrentUserOrcid()));
            Collections.reverse(keywordsForm.getKeywords());
            if (!keywordsForm.compare(kf)) {
                profileKeywordManager.updateKeywords(getCurrentUserOrcid(), updatedKeywords);
            }
        }
        return kf;
    }
    
    @RequestMapping(value = "/my-orcid/otherNamesForms.json", method = RequestMethod.GET)
    public @ResponseBody
    OtherNamesForm getOtherNamesFormJson(HttpServletRequest request)  {
        OtherNames otherNames = otherNameManager.getOtherNames(getCurrentUserOrcid());                
        OtherNamesForm form = OtherNamesForm.valueOf(otherNames);
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            org.orcid.jaxb.model.v3.release.common.Visibility defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
            Visibility v = Visibility.valueOf(defaultVis);
            form.setVisibility(v);
        }
        return form;
    }
    
    @RequestMapping(value = "/my-orcid/otherNamesForms.json", method = RequestMethod.POST)
    public @ResponseBody
    OtherNamesForm setOtherNamesFormJson(@RequestBody OtherNamesForm onf) {
        onf.setErrors(new ArrayList<String>());        
        if(onf != null) {
            Iterator<OtherNameForm> it = onf.getOtherNames().iterator();
            while(it.hasNext()) {
                OtherNameForm form = it.next();
                if(PojoUtil.isEmpty(form.getContent())) {
                   it.remove();
                   continue;
                } 

                form.setErrors(new ArrayList<String>());
                if (form.getContent() != null && form.getContent().length() >= 255)
                    setError(form, "Pattern.registrationForm.otherName");
                
                //Validate visibility is not null
                validateVisibility(form);
                
                copyErrors(form, onf);
                copyErrors(form.getVisibility(), onf);
            }
                    
            if (onf.getErrors().size()>0) {
                return onf;   
            }
            
            OtherNames otherNames = onf.toOtherNames();
            OtherNamesForm otherNamesForm = OtherNamesForm.valueOf(otherNameManager.getOtherNames(getCurrentUserOrcid()));
            Collections.reverse(otherNamesForm.getOtherNames());
            if (!otherNamesForm.compare(onf)) {
                otherNameManager.updateOtherNames(getEffectiveUserOrcid(), otherNames);
            }
        }

        return onf;
    }
    
    /**
     * Retrieve all external identifiers as a json string
     * */
    @RequestMapping(value = "/my-orcid/websitesForms.json", method = RequestMethod.GET)
    public @ResponseBody
    WebsitesForm getWebsitesFormJson(HttpServletRequest request)  {
        ResearcherUrls rUrls = researcherUrlManager.getResearcherUrls(getCurrentUserOrcid());                 
        WebsitesForm form = WebsitesForm.valueOf(rUrls);
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            org.orcid.jaxb.model.v3.release.common.Visibility defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
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
    WebsitesForm setWebsitesFormJson(HttpServletRequest request, @RequestBody WebsitesForm ws)  {
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
            WebsitesForm websitesForm = WebsitesForm.valueOf(researcherUrlManager.getResearcherUrls(getCurrentUserOrcid()));
            Collections.reverse(websitesForm.getWebsites());
            if (!websitesForm.compare(ws)) {
                researcherUrlManager.updateResearcherUrls(getCurrentUserOrcid(), rUrls);
            }
        }
        
        return ws;
    }
    
    /**
     * Retrieve all external identifiers as a json string
     * */    
    @RequestMapping(value = "/my-orcid/externalIdentifiers.json", method = RequestMethod.GET)
    public @ResponseBody
    ExternalIdentifiersForm getExternalIdentifiersJson(HttpServletRequest request) {
        PersonExternalIdentifiers extIds = externalIdentifierManager.getExternalIdentifiers(getCurrentUserOrcid()); 
        ExternalIdentifiersForm form = ExternalIdentifiersForm.valueOf(extIds);
        //Set the default visibility
        ProfileEntity profile = profileEntityCacheManager.retrieve(getCurrentUserOrcid());
        if(profile != null && profile.getActivitiesVisibilityDefault() != null) {
            org.orcid.jaxb.model.v3.release.common.Visibility defaultVis = org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(profile.getActivitiesVisibilityDefault());
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
        ExternalIdentifiersForm eif = ExternalIdentifiersForm.valueOf(externalIdentifierManager.getExternalIdentifiers(getCurrentUserOrcid()));
        Collections.reverse(eif.getExternalIdentifiers());
        if (!eif.compare(externalIdentifiersForm)) {
            externalIdentifierManager.updateExternalIdentifiers(getCurrentUserOrcid(), externalIdentifiers);
        }
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
    
    @RequestMapping(value = "/countryNamesToCountryCodes.json", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getCountryNamesToCountryCodesMap() {
        Locale locale = localeManager.getLocale();
        if (locale.getLanguage().equals(new Locale("lr").getLanguage())|| locale.getLanguage().equals(new Locale("rl").getLanguage()) || locale.getLanguage().equals(new Locale("xx").getLanguage())) {
            locale = new Locale("en");
        }
        Map<String, String> countryMap = localeManager.getCountries(locale);
        Map<String, String> mapInversed = countryMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        
        return mapInversed;
    }
        
}
