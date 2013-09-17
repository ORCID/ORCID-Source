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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.CountryManager;
import org.orcid.core.manager.CrossRefManager;
import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.core.manager.SponsorManager;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.frontend.web.util.FunctionsOverCollections;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.OrcidWork;
import org.orcid.jaxb.model.message.OrcidWorks;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.persistence.jpa.entities.CountryIsoEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 22/02/2012
 */
public class BaseWorkspaceController extends BaseController {

    protected static final String WORKS_RESULTS_ATTRIBUTE = "works_results_attribute";

    protected static final String PUBLIC_WORKS_RESULTS_ATTRIBUTE = "public_works_results_attribute";

    @Resource
    protected SponsorManager sponsorManager;

    @Resource
    protected CountryManager countryManager;

    @Resource
    protected CrossRefManager crossRefManager;

    @Resource
    protected SecurityQuestionManager securityQuestionManager;

    @Resource(name = "visibilityFilter")
    protected VisibilityFilter visibilityFilter;

    /**
     * Use {@link #retrieveIsoCountries()} instead.
     */

    @ModelAttribute("countries")
    @Deprecated
    public Map<String, String> retrieveCountries() {
        Map<String, String> countriesWithId = new LinkedHashMap<String, String>();
        List<String> countries = countryManager.retrieveCountries();
        countriesWithId.put("", "Select a country");
        for (String countryName : countries) {
            countriesWithId.put(countryName, countryName);
        }
        return countriesWithId;
    }

    @ModelAttribute("isoCountries")
    public Map<String, String> retrieveIsoCountries() {
        Map<String, String> dbCountries = countryManager.retrieveCountriesAndIsoCodes(); 
        Map<String, String> countries = new LinkedHashMap<String, String>();        
        
        for(String key : dbCountries.keySet()){            
            countries.put(key, getMessage(buildInternationalizationKey(CountryIsoEntity.class, key)));
        }
                
        return FunctionsOverCollections.sortMapsByValues(countries);
    }

    @ModelAttribute("emailVisibilities")
    public Map<String, String> getEmailVisibilities() {
        Map<String, String> emailVisibilities = new HashMap<String, String>();
        emailVisibilities.put(Visibility.REGISTERED_ONLY.value(), "Registered users only");
        emailVisibilities.put(Visibility.LIMITED.value(), "Trusted users only");
        emailVisibilities.put(Visibility.PUBLIC.value(), "Publicly available (not recommended)");
        return emailVisibilities;
    }

    @ModelAttribute("allDates")
    public Map<String, String> getAllDates() {
        Map<String, String> map = new LinkedHashMap<String, String>();
        List<String> list = YearsList.createList();
        map.put("", "Select date");
        for (String year : list) {
            map.put(year, year);
        }
        return map;
    }

    protected List<CurrentWork> getCurrentWorksFromProfile(OrcidProfile profile) {
        List<CurrentWork> currentWorks = new ArrayList<CurrentWork>();
        OrcidWorks orcidWorks = (profile != null && profile.getOrcidActivities() != null) ? profile.getOrcidActivities().getOrcidWorks() : null;
        if (orcidWorks != null && orcidWorks.getOrcidWork() != null && !orcidWorks.getOrcidWork().isEmpty()) {
            List<OrcidWork> works = orcidWorks.getOrcidWork();
            for (OrcidWork orcidWork : works) {
                currentWorks.add(new CurrentWork(orcidWork));
            }
        }
        return currentWorks;
    }

}
