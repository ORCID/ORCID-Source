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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Copyright 2011-2012 ORCID
 * 
 * Controller for delegate permissions that have been granted TO the current
 * user
 * 
 * @author Will Simpson
 */
@Controller("manageDelegatorsController")
@RequestMapping(value = { "/delegators" })
public class ManageDelegatorsController extends BaseWorkspaceController {

    @RequestMapping
    public ModelAndView manageDelegators() {
        ModelAndView mav = new ModelAndView("manage_delegators");
        OrcidProfile profile = getEffectiveProfile();
        mav.addObject("profile", profile);
        return mav;
    }

    /**
     * Search DB for disambiguated affiliations to suggest to user
     */
    @RequestMapping(value = "/search/{query}", method = RequestMethod.GET)
    public @ResponseBody
    List<Map<String, String>> searchDisambiguated(@PathVariable("query") String query, @RequestParam(value = "limit") int limit) {
        List<Map<String, String>> datums = new ArrayList<>();
        Locale locale = new Locale(getLocale());
        query = query.toLowerCase(locale);
        for (DelegationDetails delegationDetails : getEffectiveProfile().getOrcidBio().getDelegation().getGivenPermissionBy().getDelegationDetails()) {
            DelegateSummary delegateSummary = delegationDetails.getDelegateSummary();
            String creditName = delegateSummary.getCreditName().getContent().toLowerCase(locale);
            String orcid = delegateSummary.getOrcidIdentifier().getUri();
            if (creditName.contains(query) || orcid.contains(query)) {
                Map<String, String> datum = createDatumFromOrgDisambiguated(delegationDetails);
                datums.add(datum);
            }
        }
        return datums;
    }

    private Map<String, String> createDatumFromOrgDisambiguated(DelegationDetails delegationDetails) {
        Map<String, String> datum = new HashMap<>();
        datum.put("value", delegationDetails.getDelegateSummary().getCreditName().getContent());
        datum.put("orcid", delegationDetails.getDelegateSummary().getOrcidIdentifier().getPath());
        return datum;
    }

}
