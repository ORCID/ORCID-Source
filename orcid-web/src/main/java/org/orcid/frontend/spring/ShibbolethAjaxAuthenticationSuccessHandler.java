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

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.OrcidBadRequestException;
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
    
    private static final String[] POSSIBLE_REMOTE_USER_HEADERS = new String[] { "persistent-id", "targeted-id" };
    
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    	Map<String, String> headers = new HashMap<String, String>();
    	
    	Enumeration<String> headerNames = request.getHeaderNames();
    	while (headerNames.hasMoreElements()) {
    		String key = (String) headerNames.nextElement();
    		String value = request.getHeader(key);
    		headers.put(key, value);
    	}
    	checkEnabled();
        String providerUserId = retrieveRemoteUser(headers);
        String providerId = headers.get(SHIB_IDENTITY_PROVIDER_HEADER);
        UserconnectionEntity userConnectionEntity = userConnectionDao.findByProviderIdAndProviderUserId(providerUserId, providerId);
        if (userConnectionEntity == null) {
        	userConnectionEntity = new UserconnectionEntity();
		    String randomId = Long.toString(new Random(Calendar.getInstance().getTimeInMillis()).nextLong());
		    UserconnectionPK pk = new UserconnectionPK(randomId, providerId, providerUserId);
		    OrcidProfile profile = getRealProfile();
		    userConnectionEntity.setOrcid(profile.getOrcidIdentifier().getPath());
		    userConnectionEntity.setProfileurl(profile.getOrcidIdentifier().getUri());
		    userConnectionEntity.setDisplayname(retrieveDisplayName(headers));
		    userConnectionEntity.setRank(1);
		    userConnectionEntity.setId(pk);
		    userConnectionEntity.setLinked(true);
		    userConnectionEntity.setLastLogin(new Timestamp(new Date().getTime()));
		    userConnectionDao.persist(userConnectionEntity);
        }
    	
        String targetUrl = getTargetUrl(request, response, authentication);
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");        
    }
    
    private void checkEnabled() {
        if (!enabled) {
            throw new FeatureDisabledException();
        }
    }

    private String retrieveDisplayName(Map<String, String> headers) {
        String eppn = headers.get("eppn");
        if (StringUtils.isNotBlank(eppn)) {
            return eppn;
        }
        String displayName = headers.get("displayName");
        if (StringUtils.isNotBlank(displayName)) {
            return displayName;
        }
        String givenName = headers.get("givenName");
        String sn = headers.get("sn");
        String combinedNames = StringUtils.join(new String[] { givenName, sn }, ' ');
        if (StringUtils.isNotBlank(combinedNames)) {
            return combinedNames;
        }
        String remoteUser = retrieveRemoteUser(headers);
        if (StringUtils.isNotBlank(remoteUser)) {
            return remoteUser.substring(remoteUser.lastIndexOf("!"));
        }
        throw new OrcidBadRequestException("Couldn't find any user display name headers");
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
