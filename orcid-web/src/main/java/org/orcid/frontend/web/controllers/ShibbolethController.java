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
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.utils.JsonUtils;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.frontend.web.util.RemoteUser;
import org.orcid.persistence.dao.IdentityProviderDao;
import org.orcid.persistence.dao.UserConnectionDao;
import org.orcid.persistence.jpa.entities.UserConnectionStatus;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.pojo.HeaderCheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethController.class);

    private static final String SEPARATOR = ";";

    private static final Pattern ATTRIBUTE_SEPARATOR_PATTERN = Pattern.compile("(?<!\\\\)" + SEPARATOR);

    private static final Pattern ESCAPED_SEPARATOR_PATTERN = Pattern.compile("\\\\" + SEPARATOR);

    @Resource
    private UserConnectionDao userConnectionDao;

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private IdentityProviderManager identityProviderManager;

    @Resource
    private IdentityProviderDao identityProviderDao;

    @Resource
    private InstitutionalSignInManager institutionalSignInManager;

    @RequestMapping(value = { "/signin" }, method = RequestMethod.GET)
    public ModelAndView signinHandler(HttpServletRequest request, HttpServletResponse response, @RequestHeader Map<String, String> headers, ModelAndView mav) {
        LOGGER.info("Headers for shibboleth sign in: {}", headers);
        checkEnabled();
        mav.setViewName("social_link_signin");
        String shibIdentityProvider = headers.get(InstitutionalSignInManager.SHIB_IDENTITY_PROVIDER_HEADER);
        mav.addObject("providerId", shibIdentityProvider);
        String displayName = retrieveDisplayName(headers);
        mav.addObject("accountId", displayName);
        RemoteUser remoteUser = retrieveRemoteUser(headers);
        if (remoteUser == null) {
            LOGGER.info("Failed federated log in for {}", shibIdentityProvider);
            identityProviderDao.incrementFailedCount(shibIdentityProvider);
            mav.addObject("unsupportedInstitution", true);
            mav.addObject("institutionContactEmail", identityProviderManager.retrieveContactEmailByProviderid(shibIdentityProvider));
            return mav;
        }

        // Check if the Shibboleth user is already linked to an ORCID account.
        // If so sign them in automatically.
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserIdAndIdType(remoteUser.getUserId(), shibIdentityProvider,
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
                token.setDetails(new WebAuthenticationDetails(request));
                Authentication authentication = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                userConnectionEntity.setLastLogin(new Date());
                userConnectionDao.merge(userConnectionEntity);
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

    public static RemoteUser retrieveRemoteUser(Map<String, String> headers) {
        for (String possibleHeader : InstitutionalSignInManager.POSSIBLE_REMOTE_USER_HEADERS) {
            String userId = extractFirst(headers.get(possibleHeader));
            if (userId != null) {
                return new RemoteUser(userId, possibleHeader);
            }
        }
        return null;
    }

    public static String retrieveDisplayName(Map<String, String> headers) {
        String eppn = extractFirst(headers.get(InstitutionalSignInManager.EPPN_HEADER));
        if (StringUtils.isNotBlank(eppn)) {
            return eppn;
        }
        String displayName = extractFirst(headers.get(InstitutionalSignInManager.DISPLAY_NAME_HEADER));
        if (StringUtils.isNotBlank(displayName)) {
            return displayName;
        }
        String givenName = extractFirst(headers.get(InstitutionalSignInManager.GIVEN_NAME_HEADER));
        String sn = extractFirst(headers.get(InstitutionalSignInManager.SN_HEADER));
        String combinedNames = StringUtils.join(new String[] { givenName, sn }, ' ');
        if (StringUtils.isNotBlank(combinedNames)) {
            return combinedNames;
        }
        RemoteUser remoteUser = retrieveRemoteUser(headers);
        if (remoteUser != null) {
            String remoteUserId = remoteUser.getUserId();
            if (StringUtils.isNotBlank(remoteUserId)) {
                int indexOfBang = remoteUserId.lastIndexOf("!");
                if (indexOfBang != -1) {
                    return remoteUserId.substring(indexOfBang);
                } else {
                    return remoteUserId;
                }
            }
        }
        throw new OrcidBadRequestException("Couldn't find any user display name headers");
    }

    /**
     * Shibboleth SP combines multiple values by concatenating, using semicolon
     * as the separator (the escape character is '\'). Mutliple values will be
     * provided, even if it is actually the same attribute in mace and oid
     * format.
     * 
     * @param headerValue
     * @return the first attribute value
     */
    private static String extractFirst(String headerValue) {
        if (headerValue == null) {
            return null;
        }
        String[] values = ATTRIBUTE_SEPARATOR_PATTERN.split(headerValue);
        return values.length > 0 ? ESCAPED_SEPARATOR_PATTERN.matcher(values[0]).replaceAll(SEPARATOR) : "";
    }

}
