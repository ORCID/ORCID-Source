package org.orcid.frontend.spring;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.common.manager.EventManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.persistence.jpa.entities.EventType;
import org.orcid.pojo.RemoteUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;

public class ShibbolethAjaxAuthenticationSuccessHandler extends AjaxAuthenticationSuccessHandlerBase {

    private static final String SHIB_IDENTITY_PROVIDER_HEADER = "shib-identity-provider";
    
    private static final Logger LOGGER = LoggerFactory.getLogger(ShibbolethAjaxAuthenticationSuccessHandler.class);

    @Value("${org.orcid.shibboleth.enabled:false}")
    private boolean enabled;

    @Resource
    private InstitutionalSignInManager institutionalSignInManager;

    @Autowired
    EventManager eventManager;
    
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        linkShibbolethAccount(request, response);
        String targetUrl = getTargetUrl(request, response, authentication);
        if (Features.EVENTS.isActive()) {
            eventManager.createEvent(EventType.SIGN_IN, request);
        }
        response.setContentType("application/json");
        response.getWriter().println("{\"success\": true, \"url\": \"" + targetUrl.replaceAll("^/", "") + "\"}");
    }

    public void linkShibbolethAccount(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
        Map<String, String> headers = new HashMap<String, String>();

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        LOGGER.info("Headers for shibboleth link: {}", headers);
        checkEnabled();
        RemoteUser remoteUser = institutionalSignInManager.retrieveRemoteUser(headers);
        String providerId = headers.get(SHIB_IDENTITY_PROVIDER_HEADER);
        String remoteUserId = remoteUser.getUserId();
        String idType = remoteUser.getIdType();
        String displayName = institutionalSignInManager.retrieveDisplayName(headers);
        String userOrcid = getRealUserOrcid();                
        institutionalSignInManager.createUserConnectionAndNotify(idType, remoteUserId, displayName, providerId, userOrcid, headers);
    }

    private void checkEnabled() {
        if (!enabled) {
            throw new FeatureDisabledException();
        }
    }
}
