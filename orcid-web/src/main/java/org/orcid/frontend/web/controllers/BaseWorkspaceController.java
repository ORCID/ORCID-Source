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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.CountryManager;
import org.orcid.core.manager.CrossRefManager;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.core.manager.SecurityQuestionManager;
import org.orcid.core.manager.SponsorManager;
import org.orcid.core.security.visibility.filter.VisibilityFilter;
import org.orcid.frontend.web.util.YearsList;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * @author Declan Newman (declan) Date: 22/02/2012
 */
public class BaseWorkspaceController extends BaseController {

    protected static final String WORKS_RESULTS_ATTRIBUTE = "works_results_attribute";

    protected static final String PUBLIC_WORKS_RESULTS_ATTRIBUTE = "public_works_results_attribute";

    protected static final String ORCID_ID_HASH = "orcid_hash";

    @Resource
    protected SponsorManager sponsorManager;

    @Resource
    private EncryptionManager encryptionManager;

    @Resource
    protected CountryManager countryManager;

    @Resource
    protected CrossRefManager crossRefManager;

    @Resource
    private LocaleManager localeManager;

    @Resource
    protected SecurityQuestionManager securityQuestionManager;

    @Resource
    private ProfileEntityManager profileEntityManager;

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
        Locale locale = localeManager.getLocale();
        return localeManager.getCountries(locale);
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

    @ModelAttribute("orcidIdHash")
    String getOrcidHash(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        String hash = session != null ? (String) session.getAttribute(ORCID_ID_HASH) : null;
        if (!PojoUtil.isEmpty(hash)) {
            return hash;
        }
        hash = profileEntityManager.getOrcidHash(getEffectiveUserOrcid());
        if (session != null) {
            request.getSession().setAttribute(ORCID_ID_HASH, hash);
        }
        return hash;
    }

    public String getCountryName(OrcidProfile profile) {
        return getCountryName(profile, false);
    }

    public String getCountryName(OrcidProfile profile, boolean checkVisibility) {
        if (profile.getOrcidBio() != null && profile.getOrcidBio().getContactDetails() != null && profile.getOrcidBio().getContactDetails().getAddress() != null
                && profile.getOrcidBio().getContactDetails().getAddress().getCountry() != null) {
            if (checkVisibility) {
                if (!Visibility.PUBLIC.equals(profile.getOrcidBio().getContactDetails().getAddress().getCountry().getVisibility()))
                    return null;
            }
            Map<String, String> countries = retrieveIsoCountries();
            return countries.get(profile.getOrcidBio().getContactDetails().getAddress().getCountry().getValue().value());
        }
        return null;
    }

    public String getcountryName(String Iso3166Country) {
        Map<String, String> countries = retrieveIsoCountries();
        return countries.get(Iso3166Country);
    }
    
}
