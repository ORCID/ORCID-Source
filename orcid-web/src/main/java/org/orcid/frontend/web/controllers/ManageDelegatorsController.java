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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.message.CreditName;
import org.orcid.jaxb.model.message.DelegateSummary;
import org.orcid.jaxb.model.message.Delegation;
import org.orcid.jaxb.model.message.DelegationDetails;
import org.orcid.jaxb.model.message.GivenPermissionBy;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

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

    @Resource
    private SourceManager sourceManager;

    @RequestMapping
    public ModelAndView manageDelegators() {
        ModelAndView mav = new ModelAndView("manage_delegators");
        OrcidProfile profile = getEffectiveProfile();
        mav.addObject("profile", profile);
        return mav;
    }

    @RequestMapping(value = "/delegation.json", method = RequestMethod.GET)
    public @ResponseBody
    Delegation getDelegatesJson() throws NoSuchRequestHandlingMethodException {
        OrcidProfile realProfile = getRealProfile();
        Delegation delegation = realProfile.getOrcidBio().getDelegation();
        return delegation;
    }

    @RequestMapping(value = "/delegatorsPlusMe.json", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, Object> getDelegatorsPlusMeJson() throws NoSuchRequestHandlingMethodException {
        Map<String, Object> map = new HashMap<>();
        OrcidProfile realProfile = getRealProfile();
        Delegation delegation = realProfile.getOrcidBio().getDelegation();
        GivenPermissionBy givenPermissionBy = delegation.getGivenPermissionBy();
        map.put("delegators", givenPermissionBy);
        if (sourceManager.isInDelegationMode()) {
            // Add me, so I can switch back to me
            DelegationDetails details = new DelegationDetails();
            DelegateSummary summary = new DelegateSummary();
            details.setDelegateSummary(summary);
            String displayName = realProfile.getOrcidBio().getPersonalDetails().retrieveDisplayNameIgnoringVisibility();
            summary.setCreditName(new CreditName(displayName));
            summary.setOrcidIdentifier(realProfile.getOrcidIdentifier());
            map.put("me", details);
        }
        return map;
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
        for (DelegationDetails delegationDetails : getRealProfile().getOrcidBio().getDelegation().getGivenPermissionBy().getDelegationDetails()) {
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
