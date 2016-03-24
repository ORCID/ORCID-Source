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
package org.orcid.frontend.spring;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.frontend.web.controllers.ShibbolethController;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.persistence.jpa.entities.UserconnectionEntity;
import org.orcid.persistence.jpa.entities.UserconnectionPK;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

public class ShibbolethAjaxAuthenticationSuccessHandler extends AjaxAuthenticationSuccessHandlerBase {

    private static final String SHIB_IDENTITY_PROVIDER_HEADER = "shib-identity-provider";

    @Value("${org.orcid.shibboleth.enabled:false}")
    private boolean enabled;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        linkShibbolethAccount(request, response);
        String targetUrl = getTargetUrl(request, response, authentication);
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");
    }

    public void linkShibbolethAccount(HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> headers = new HashMap<String, String>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        checkEnabled();
        String providerUserId = ShibbolethController.retrieveRemoteUser(headers);
        String providerId = headers.get(SHIB_IDENTITY_PROVIDER_HEADER);
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserId(providerUserId, providerId);
        if (userConnectionEntity == null) {
            userConnectionEntity = new UserconnectionEntity();
            String randomId = Long.toString(new Random(Calendar.getInstance().getTimeInMillis()).nextLong());
            UserconnectionPK pk = new UserconnectionPK(randomId, providerId, providerUserId);
            OrcidProfile profile = getRealProfile();
            userConnectionEntity.setOrcid(profile.getOrcidIdentifier().getPath());
            userConnectionEntity.setProfileurl(profile.getOrcidIdentifier().getUri());
            userConnectionEntity.setDisplayname(ShibbolethController.retrieveDisplayName(headers));
            userConnectionEntity.setRank(1);
            userConnectionEntity.setId(pk);
            userConnectionEntity.setLinked(true);
            userConnectionEntity.setLastLogin(new Timestamp(new Date().getTime()));
            userConnectionDao.persist(userConnectionEntity);
        }
    }

    private void checkEnabled() {
        if (!enabled) {
            throw new FeatureDisabledException();
        }
    }

}
