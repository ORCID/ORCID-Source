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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.adapter.Jaxb2JpaAdapter;
import org.orcid.core.adapter.Jpa2JaxbAdapter;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ExternalIdentifierManager;
import org.orcid.core.manager.LoadOptions;
import org.orcid.core.manager.ProfileWorkManager;
import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.core.manager.WorkContributorManager;
import org.orcid.core.manager.WorkManager;
import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.frontend.web.util.FunctionsOverCollections;
import org.orcid.frontend.web.util.LanguagesMap;
import org.orcid.frontend.web.util.NumberList;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.clientgroup.RedirectUri;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.CitationType;
import org.orcid.jaxb.model.message.ContributorRole;
import org.orcid.jaxb.model.message.ExternalIdentifier;
import org.orcid.jaxb.model.message.ExternalIdentifiers;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.SequenceType;
import org.orcid.jaxb.model.message.SourceOrcid;
import org.orcid.jaxb.model.message.WorkCategory;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.pojo.ThirdPartyRedirect;
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

/**
 * @author Will Simpson
 */
@Controller("workspaceController")
@RequestMapping(value = { "/my-orcid", "/workspace" })
public class WorkspaceController extends BaseWorkspaceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkspaceController.class);

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
    private LocaleManager localeManager;

    @ModelAttribute("thirdPartiesForImport")
    public List<OrcidClient> retrieveThirdPartiesForImport() {
        return thirdPartyImportManager.findOrcidClientsWithPredefinedOauthScopeWorksImport();
    }

    @ModelAttribute("affiliationTypes")
    public Map<String, String> retrieveAffiliationTypesAsMap() {
        Map<String, String> affiliationTypes = new LinkedHashMap<String, String>();
        for (AffiliationType affiliationType : AffiliationType.values()) {
            affiliationTypes.put(affiliationType.value(), getMessage(buildInternationalizationKey(AffiliationType.class, affiliationType.value())));
        }
        return FunctionsOverCollections.sortMapsByValues(affiliationTypes);
    }
    
    @ModelAttribute("affiliationLongDescriptionTypes")
    public Map<String, String> retrieveAffiliationLongDescriptionTypesAsMap() {
        Map<String, String> affiliationTypes = new LinkedHashMap<String, String>();
        for (AffiliationType affiliationType : AffiliationType.values()) {
            affiliationTypes.put(affiliationType.value(), getMessage(AffiliationType.class.getName() + '.' + "longDescription" + '.' + affiliationType.value()));
        }
        return FunctionsOverCollections.sortMapsByValues(affiliationTypes);
    }

    @ModelAttribute("workCategories")
    public Map<String, String> retrieveWorkTypesAsMap() {
        Map<String, String> workCategories = new LinkedHashMap<String, String>();

        for (WorkCategory workCategory : WorkCategory.values()) {
        	workCategories.put(workCategory.value(), getMessage(buildInternationalizationKey(WorkCategory.class, workCategory.value())));
        }
        
        return FunctionsOverCollections.sortMapsByValues(workCategories);
    }        

    @ModelAttribute("citationTypes")
    public Map<String, String> retrieveTypesAsMap() {
        Map<String, String> citationTypes = new LinkedHashMap<String, String>();

        for (CitationType citationType : CitationType.values()) {
            citationTypes.put(citationType.value(), getMessage(buildInternationalizationKey(CitationType.class, citationType.value())));
        }

        return FunctionsOverCollections.sortMapsByValues(citationTypes);
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

    /**
     * Generate a map with ID types.
     * The map is different from the rest, because it will be ordered in the form: value -> key, to keep the map alpha ordered in UI. 
     * */
    @ModelAttribute("idTypes")
    public Map<String, String> retrieveIdTypesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();

        for (WorkExternalIdentifierType type : WorkExternalIdentifierType.values()) {
            map.put(getMessage(buildInternationalizationKey(WorkExternalIdentifierType.class, type.value())), type.value());
        }

        return FunctionsOverCollections.sortMapsByValues(map);
    }

    @ModelAttribute("roles")
    public Map<String, String> retrieveRolesAsMap() {
        Map<String, String> map = new TreeMap<String, String>();

        for (ContributorRole contributorRole : ContributorRole.values()) {
            map.put(contributorRole.value(), getMessage(buildInternationalizationKey(ContributorRole.class, contributorRole.value())));
        }
        return FunctionsOverCollections.sortMapsByValues(map);
    }

    @ModelAttribute("sequences")
    public Map<String, String> retrieveSequencesAsMap() {
        Map<String, String> map = new LinkedHashMap<String, String>();

        for (SequenceType sequenceType : SequenceType.values()) {
            map.put(sequenceType.value(), getMessage(buildInternationalizationKey(SequenceType.class, sequenceType.value())));
        }

        return FunctionsOverCollections.sortMapsByValues(map);
    }

    @ModelAttribute("languages")
    public Map<String, String> retrieveLocalesAsMap() {
        return LanguagesMap.getLanguagesMap(localeManager.getLocale());
    }

    @RequestMapping
    public ModelAndView viewWorkspace(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults) {

        ModelAndView mav = new ModelAndView("workspace");
        mav.addObject("showPrivacy", true);

        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid(), LoadOptions.BIO_ONLY);
        List<CurrentWork> currentWorks = getCurrentWorksFromProfile(profile);
        if (currentWorks != null && !currentWorks.isEmpty()) {
            mav.addObject("currentWorks", currentWorks);
        }
        mav.addObject("profile", profile);
        mav.addObject("currentLocaleKey", LanguagesMap.buildLanguageKey(localeManager.getLocale()));
        mav.addObject("currentLocaleValue", LanguagesMap.buildLanguageValue(localeManager.getLocale(), localeManager.getLocale()));
        return mav;
    }

    /**
     * Retrieve all external identifiers as a json string
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/externalIdentifiers.json", method = RequestMethod.GET)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifiers getExternalIdentifiersJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getEffectiveProfile();
        org.orcid.pojo.ExternalIdentifiers externalIdentifiers = new org.orcid.pojo.ExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers((List<org.orcid.pojo.ExternalIdentifier>) (Object) currentProfile.getOrcidBio().getExternalIdentifiers()
                .getExternalIdentifier());
        return externalIdentifiers;
    }

    @RequestMapping(value = "/sourceGrantReadWizard.json", method = RequestMethod.GET)
    public @ResponseBody
    ThirdPartyRedirect getSourceGrantReadWizard() {
        ThirdPartyRedirect tpr = new ThirdPartyRedirect();

        OrcidProfile currentProfile = getEffectiveProfile();
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
            // Get cached profile
            OrcidProfile currentProfile = getEffectiveProfile();
            ExternalIdentifiers externalIdentifiers = currentProfile.getOrcidBio().getExternalIdentifiers();
            List<ExternalIdentifier> externalIdentifiersList = externalIdentifiers.getExternalIdentifier();
            Iterator<ExternalIdentifier> externalIdentifierIterator = externalIdentifiersList.iterator();
            // Remove external identifier from the cached profile
            while (externalIdentifierIterator.hasNext()) {
                ExternalIdentifier existingExternalIdentifier = externalIdentifierIterator.next();
                if (existingExternalIdentifier.equals(externalIdentifier)) {
                    externalIdentifierIterator.remove();
                }
            }
            // Update cached profile
            currentProfile.getOrcidBio().setExternalIdentifiers(externalIdentifiers);
            // Remove external identifier
            externalIdentifierManager.removeExternalIdentifier(externalIdentifier.getOrcid().getValue(), externalIdentifier.getExternalIdReference().getContent());
        }

        return externalIdentifier;
    }

}