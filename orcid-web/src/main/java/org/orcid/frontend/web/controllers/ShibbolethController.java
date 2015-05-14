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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.persistence.dao.ShibbolethAccountDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ShibbolethAccountEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author Will Simpson
 *
 */
@Controller
@RequestMapping("/shibboleth")
public class ShibbolethController extends BaseController {

    private static final String REMOTE_USER_HEADER = "eppn";

    private static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethController.class);

    @Value("${org.orcid.shibboleth.enabled:false}")
    private boolean enabled;

    @Resource
    private ShibbolethAccountDao shibbolethAccountDao;

    @Resource
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, @RequestHeader(REMOTE_USER_HEADER) String remoteUser,
            @RequestHeader("Shib-Identity-Provider") String shibIdentityProvider, ModelAndView mav) {
        checkEnabled();
        // Check if the Shibboleth user is already linked to an ORCID account.
        // If so sign them in automatically.
        ShibbolethAccountEntity shibbolethAccountEntity = shibbolethAccountDao.findByRemoteUserAndShibIdentityProvider(remoteUser, shibIdentityProvider);
        if (shibbolethAccountEntity != null) {
            ProfileEntity profileEntity = shibbolethAccountEntity.getProfile();
            try {
                PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(profileEntity.getId(), remoteUser);
                token.setDetails(new WebAuthenticationDetails(request));
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException e) {
                // this should never happen
                SecurityContextHolder.getContext().setAuthentication(null);
                LOGGER.warn("User {0} should have been logged-in via Shibboleth, but was unable to due to a problem", remoteUser, e);
            }
            return new ModelAndView("redirect:/my-orcid");
        } else {
            // To avoid confusion, force the user to login to ORCID again
            logoutCurrentUser();
            mav.setViewName("shib_link_signin");
            mav.addObject("remoteUserHeader", REMOTE_USER_HEADER);
            mav.addObject("remoteUser", remoteUser);
        }
        return mav;
    }

    @RequestMapping(value = { "/link" }, method = RequestMethod.GET)
    public ModelAndView linkHandler(@RequestHeader(REMOTE_USER_HEADER) String remoteUser, @RequestHeader("Shib-Identity-Provider") String shibIdentityProvider,
            ModelAndView mav) {
        checkEnabled();
        ShibbolethAccountEntity shibbolethAccountEntity = shibbolethAccountDao.findByRemoteUserAndShibIdentityProvider(remoteUser, shibIdentityProvider);
        if (shibbolethAccountEntity != null) {
            return new ModelAndView("redirect:/my-orcid");
        }
        shibbolethAccountEntity = new ShibbolethAccountEntity();
        shibbolethAccountEntity.setRemoteUser(remoteUser);
        shibbolethAccountEntity.setShibIdentityProvider(shibIdentityProvider);
        shibbolethAccountEntity.setProfile(new ProfileEntity(getCurrentUserOrcid()));
        shibbolethAccountDao.persist(shibbolethAccountEntity);
        mav.setViewName("shib_link_complete");
        mav.addObject("remoteUser", remoteUser);
        return mav;
    }

    private void checkEnabled() {
        if (!enabled) {
            throw new FeatureDisabledException();
        }
    }

}
