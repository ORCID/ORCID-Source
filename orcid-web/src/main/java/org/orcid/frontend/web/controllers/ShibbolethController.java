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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
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

    private static final String[] POSSIBLE_REMOTE_USER_HEADERS = new String[] { "persistent-id", "targeted-id" };

    private static final String SHIB_IDENTITY_PROVIDER_HEADER = "shib-identity-provider";

    private static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethController.class);

    @Value("${org.orcid.shibboleth.enabled:false}")
    private boolean enabled;

    @Resource
    private UserConnectionDao userConnectionDao;

    @Resource
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, @RequestHeader Map<String, String> headers, ModelAndView mav) {
        checkEnabled();
        String remoteUser = retrieveRemoteUser(headers);
        String shibIdentityProvider = headers.get(SHIB_IDENTITY_PROVIDER_HEADER);
        // Check if the Shibboleth user is already linked to an ORCID account.
        // If so sign them in automatically.
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserId(remoteUser, shibIdentityProvider);
        if (userConnectionEntity != null) {
            try {
                PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userConnectionEntity.getOrcid(), remoteUser);
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
            mav.addObject("remoteUserHeader", StringUtils.join(POSSIBLE_REMOTE_USER_HEADERS, "/"));
            mav.addObject("remoteUser", remoteUser);
        }
        return mav;
    }

    @RequestMapping(value = { "/link" }, method = RequestMethod.GET)
    public ModelAndView linkHandler(@RequestHeader() Map<String, String> headers, ModelAndView mav) {
        checkEnabled();
        String providerUserId = retrieveRemoteUser(headers);
        String providerId = headers.get(SHIB_IDENTITY_PROVIDER_HEADER);
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserId(providerUserId, providerId);
        if (userConnectionEntity != null) {
            return new ModelAndView("redirect:/my-orcid");
        }
        userConnectionEntity = new UserconnectionEntity();
        String randomId = Long.toString(new Random(Calendar.getInstance().getTimeInMillis()).nextLong());
        UserconnectionPK pk = new UserconnectionPK(randomId, providerId, providerUserId);
        OrcidProfile profile = getRealProfile();
        userConnectionEntity.setEmail(providerUserId);
        userConnectionEntity.setOrcid(profile.getOrcidIdentifier().getPath());
        userConnectionEntity.setProfileurl(profile.getOrcidIdentifier().getUri());
        userConnectionEntity.setDisplayname(profile.getOrcidBio().getPersonalDetails().getGivenNames().getContent());
        userConnectionEntity.setRank(1);
        userConnectionEntity.setId(pk);
        userConnectionEntity.setLinked(true);
        userConnectionEntity.setLastLogin(new Timestamp(new Date().getTime()));
        userConnectionDao.persist(userConnectionEntity);
        mav.setViewName("shib_link_complete");
        mav.addObject("remoteUser", providerUserId);
        return mav;
    }

    private void checkEnabled() {
        if (!enabled) {
            throw new FeatureDisabledException();
        }
    }

    private String retrieveRemoteUser(Map<String, String> headers) {
        for (String possibleHeader : POSSIBLE_REMOTE_USER_HEADERS) {
            String userId = headers.get(possibleHeader);
            if (userId != null) {
                return userId;
            }
        }
        throw new OrcidBadRequestException("Couldn't find remote user header");
    }

}
