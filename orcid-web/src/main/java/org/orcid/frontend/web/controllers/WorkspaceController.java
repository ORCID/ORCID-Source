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

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.ThirdPartyImportManager;
import org.orcid.frontend.web.forms.CurrentWork;
import org.orcid.jaxb.model.clientgroup.OrcidClient;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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

}
