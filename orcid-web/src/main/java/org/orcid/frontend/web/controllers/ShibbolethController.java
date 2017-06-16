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

import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.utils.JsonUtils;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.pojo.HeaderCheckResult;
import org.orcid.pojo.RemoteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethController.class);

    @Resource
    private UserConnectionManager userConnectionManager;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Resource
    private InstitutionalSignInManager institutionalSignInManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private EmailManagerReadOnly emailManagerReadOnly;

    @RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> headers, ModelAndView mav) {
        LOGGER.info("Headers for shibboleth sign in: {}", headers);
        checkEnabled();
        mav.setViewName("social_link_signin");
        String shibIdentityProvider = headers.get(InstitutionalSignInManager.SHIB_IDENTITY_PROVIDER_HEADER);
        mav.addObject("providerId", shibIdentityProvider);
        String displayName = institutionalSignInManager.retrieveDisplayName(headers);
        mav.addObject("accountId", displayName);
        RemoteUser remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        if (remoteUser == null) {
            LOGGER.info("Failed federated log in for {}", shibIdentityProvider);
            identityProviderManager.incrementFailedCount(shibIdentityProvider);
            mav.addObject("unsupportedInstitution", true);
            mav.addObject("institutionContactEmail", identityProviderManager.retrieveContactEmailByProviderid(shibIdentityProvider));
            return mav;
        }

        // Check if the Shibboleth user is already linked to an ORCID account.
        // If so sign them in automatically.
        UserconnectionEntity userConnectionEntity = userConnectionManager.findByProviderIdAndProviderUserIdAndIdType(remoteUser.getUserId(), shibIdentityProvider,
                remoteUser.getIdType());
        if (userConnectionEntity != null) {
            LOGGER.info("Found existing user connection: {}", userConnectionEntity);
            HeaderCheckResult checkHeadersResult = institutionalSignInManager.checkHeaders(parseOriginalHeaders(userConnectionEntity.getHeadersJson()), headers);
            if (!checkHeadersResult.isSuccess()) {
                mav.addObject("headerCheckFailed", true);
                return mav;
            }
            try {
                // Check if the user has been notified
                if (!UserConnectionStatus.NOTIFIED.equals(userConnectionEntity.getConnectionSatus())) {
                    try {
                        institutionalSignInManager.sendNotification(userConnectionEntity.getOrcid(), shibIdentityProvider);
                        userConnectionEntity.setConnectionSatus(UserConnectionStatus.NOTIFIED);
                    } catch (UnsupportedEncodingException e) {
                        LOGGER.error("Unable to send institutional sign in notification to user " + userConnectionEntity.getOrcid(), e);
                    }
                }

                PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(userConnectionEntity.getOrcid(), remoteUser.getUserId());
                token.setDetails(getOrcidProfileUserDetails(userConnectionEntity.getOrcid()));
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                userConnectionEntity.setLastLogin(new Date());
                userConnectionManager.update(userConnectionEntity);
            } catch (AuthenticationException e) {
                // this should never happen
                SecurityContextHolder.getContext().setAuthentication(null);
                LOGGER.warn("User {0} should have been logged-in via Shibboleth, but was unable to due to a problem", remoteUser, e);
            }
            return new ModelAndView("redirect:" + calculateRedirectUrl(request, response));
        } else {
            // To avoid confusion, force the user to login to ORCID again
            mav.addObject("linkType", "shibboleth");
            mav.addObject("firstName",
                    (headers.get(InstitutionalSignInManager.GIVEN_NAME_HEADER) == null) ? "" : headers.get(InstitutionalSignInManager.GIVEN_NAME_HEADER));
            mav.addObject("lastName", (headers.get(InstitutionalSignInManager.SN_HEADER) == null) ? "" : headers.get(InstitutionalSignInManager.SN_HEADER));
        }
        return mav;
    }

    private OrcidProfileUserDetails getOrcidProfileUserDetails(String orcid) {
        ProfileEntity profileEntity = profileEntityCacheManager.retrieve(orcid);
        Email email = emailManagerReadOnly.findPrimaryEmail(orcid);
        return new OrcidProfileUserDetails(orcid, email.getEmail(), profileEntity.getPassword(), profileEntity.getOrcidType());
    }

    private Map<String, String> parseOriginalHeaders(String originalHeadersJson) {
        @SuppressWarnings("unchecked")
        Map<String, String> originalHeaders = originalHeadersJson != null ? JsonUtils.readObjectFromJsonString(originalHeadersJson, Map.class)
                : Collections.<String, String> emptyMap();
        return originalHeaders;
    }

    private void checkEnabled() {
        if (!isShibbolethEnabled()) {
            throw new FeatureDisabledException();
        }
    }

}
