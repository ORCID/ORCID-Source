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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.orcid.core.manager.ClientDetailsManager;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller("oauthConfirmAccessController")
@RequestMapping(value = "/oauth", method = RequestMethod.GET)
public class OauthConfirmAccessController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OauthConfirmAccessController.class);

    private static final String JUST_REGISTERED = "justRegistered";
    @Resource
    private OrcidProfileManager orcidProfileManager;
    @Resource
    private ClientDetailsManager clientDetailsManager;

    @RequestMapping(value = "/confirm_access", method = RequestMethod.GET)
    public ModelAndView loginGetHandler(HttpServletRequest request, ModelAndView mav, @RequestParam("client_id") String clientId, @RequestParam("scope") String scope) {
        OrcidProfile clientProfile = orcidProfileManager.retrieveOrcidProfile(clientId);
        Boolean justRegistered = (Boolean) request.getSession().getAttribute(JUST_REGISTERED);
        if (justRegistered != null) {
            request.getSession().removeAttribute(JUST_REGISTERED);
            mav.addObject(JUST_REGISTERED, justRegistered);
        }
        String clientName = "";
        String clientDescription = "";
        String clientGroupName = "";
        String clientWebsite = "";

        ClientDetailsEntity clientDetails = clientDetailsManager.find(clientId);
        clientName = clientDetails.getClientName() == null ? "" : clientDetails.getClientName();
        clientDescription = clientDetails.getClientDescription() == null ? "" : clientDetails.getClientDescription();
        clientWebsite = clientDetails.getClientWebsite() == null ? "" : clientDetails.getClientWebsite();

        if (clientProfile.getOrcidInternal() != null && clientProfile.getOrcidInternal().getGroupOrcidIdentifier() != null
                && StringUtils.isNotBlank(clientProfile.getOrcidInternal().getGroupOrcidIdentifier().getPath())) {
            String client_group_id = clientProfile.getOrcidInternal().getGroupOrcidIdentifier().getPath();
            OrcidProfile clientGroupProfile = orcidProfileManager.retrieveOrcidProfile(client_group_id);
            if (clientGroupProfile.getOrcidBio() != null && clientGroupProfile.getOrcidBio().getPersonalDetails() != null
                    && clientGroupProfile.getOrcidBio().getPersonalDetails().getCreditName() != null)
                clientGroupName = clientGroupProfile.getOrcidBio().getPersonalDetails().getCreditName().getContent();
        }

        // If the group name is empty, use the same as the client name, since it
        // should be a SSO user
        if (StringUtils.isBlank(clientGroupName)) {
            clientGroupName = clientName;
        }

        mav.addObject("client_name", clientName);
        mav.addObject("client_description", clientDescription);
        mav.addObject("client_group_name", clientGroupName);
        mav.addObject("client_website", clientWebsite);
        mav.addObject("clientProfile", clientProfile);
        mav.addObject("scopes", ScopePathType.getScopesFromSpaceSeparatedString(scope));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        mav.addObject("auth", authentication);
        mav.setViewName("confirm-oauth-access");
        mav.addObject("hideUserVoiceScript", true);
        mav.addObject("profile", getEffectiveProfile());
        return mav;
    }

}
