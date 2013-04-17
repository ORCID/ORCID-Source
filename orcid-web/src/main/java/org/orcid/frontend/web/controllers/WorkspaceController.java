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
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.pojo.ExternalIdentifiers;
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

    @ModelAttribute("thirdPartiesForImport")
    public List<OrcidClient> retrieveThirdPartiesForImport() {
        return thirdPartyImportManager.findOrcidClientsWithPredefinedOauthScopeForImport();
    }

    @RequestMapping
    public ModelAndView viewWorkspace(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults) {
        ModelAndView mav = new ModelAndView("workspace");
        mav.addObject("showPrivacy", true);

        OrcidProfile profile = orcidProfileManager.retrieveOrcidProfile(getCurrentUserOrcid());
        getCurrentUser().setEffectiveProfile(profile);
        List<CurrentWork> currentWorks = getCurrentWorksFromProfile(profile);
        if (currentWorks != null && !currentWorks.isEmpty()) {
            mav.addObject("currentWorks", currentWorks);
        }
        mav.addObject("profile", profile);
        return mav;
    }

    @RequestMapping(value = { "/public", "/preview" })
    public ModelAndView preview(HttpServletRequest request, @RequestParam(value = "page", defaultValue = "1") int pageNo,
            @RequestParam(value = "maxResults", defaultValue = "200") int maxResults) {
        ModelAndView mav = new ModelAndView("public_profile");
        mav.addObject("isPreview", true);

        OrcidProfile profile = orcidProfileManager.retrievePublicOrcidProfile(getCurrentUserOrcid());
        request.getSession().removeAttribute(PUBLIC_WORKS_RESULTS_ATTRIBUTE);
        List<CurrentWork> currentWorks = getCurrentWorksFromProfile(profile);
        if (currentWorks != null && !currentWorks.isEmpty()) {
            mav.addObject("currentWorks", currentWorks);
        }
        mav.addObject("profile", profile);
        return mav;
    }

    /**
     * Retrieve all external identifiers as a json string
     * */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/externalIdentifiers.json", method = RequestMethod.GET)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifiers getExternalIdentifiersJson(HttpServletRequest request) throws NoSuchRequestHandlingMethodException {
        OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
        ExternalIdentifiers externalIdentifiers = new org.orcid.pojo.ExternalIdentifiers();
        externalIdentifiers.setExternalIdentifiers((List<org.orcid.pojo.ExternalIdentifier>) (Object) currentProfile.getOrcidBio().getExternalIdentifiers()
                .getExternalIdentifier());
        return externalIdentifiers;
    }

    /**
     * Updates the list of external identifiers assigned to a user
     * */
    @RequestMapping(value = "/externalIdentifiers.json", method = RequestMethod.POST)
    public @ResponseBody
    org.orcid.pojo.ExternalIdentifiers removeExternalIdentifierJson(HttpServletRequest request, @RequestBody org.orcid.pojo.ExternalIdentifiers externalIdentifiers) {
        List<String> allErrors = new ArrayList<String>();

        // clear errors
        externalIdentifiers.setErrors(new ArrayList<String>());

        // We should never found this kind of errors
        for (org.orcid.pojo.ExternalIdentifier externalIdentifier : externalIdentifiers.getExternalIdentifiers()) {
            List<String> externalIdentifierErrors = new ArrayList<String>();

            // If the orcid is blank, add an error
            if (externalIdentifier.getOrcid() == null || StringUtils.isBlank(externalIdentifier.getOrcid().getValue())) {
                allErrors.add(getMessage("ExternalIdentifier.orcid"));
                externalIdentifierErrors.add(getMessage("ExternalIdentifier.orcid"));
            }

            // If the external identifier is blank, add an error
            if (externalIdentifier.getExternalIdReference() == null || StringUtils.isBlank(externalIdentifier.getExternalIdReference().getContent())) {
                allErrors.add(getMessage("ExternalIdentifier.externalIdReference"));
                externalIdentifierErrors.add(getMessage("ExternalIdentifier.externalIdReference"));
            }

            // Add errors to the external identifier
            externalIdentifier.setErrors(externalIdentifierErrors);
        }

        if (allErrors.isEmpty()) {
            OrcidProfile currentProfile = getCurrentUser().getEffectiveProfile();
            currentProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().clear();
            currentProfile.getOrcidBio().getExternalIdentifiers().getExternalIdentifier().addAll(externalIdentifiers.getExternalIdentifiers());
            orcidProfileManager.updateOrcidProfile(currentProfile);
        }

        return externalIdentifiers;
    }
}
