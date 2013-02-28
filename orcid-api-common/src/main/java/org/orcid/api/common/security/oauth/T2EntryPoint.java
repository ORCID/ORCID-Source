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
package org.orcid.api.common.security.oauth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.error.AbstractOAuth2SecurityExceptionHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.WebAttributes;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 11/04/2012
 */
public class T2EntryPoint extends AbstractOAuth2SecurityExceptionHandler implements AuthenticationEntryPoint {

    private String realmName;
    private String typeName;

    public T2EntryPoint() {
    }

    public T2EntryPoint(String realmName, String typeName) {
        this.realmName = realmName;
        this.typeName = typeName;
    }

    /**
     * Commences an authentication scheme.
     * <p/>
     * <code>ExceptionTranslationFilter</code> will populate the
     * <code>HttpSession</code> attribute named
     * <code>AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY</code>
     * with the requested target URL before calling this method.
     * <p/>
     * Implementations should modify the headers on the
     * <code>ServletResponse</code> as necessary to commence the authentication
     * process.
     * 
     * @param request
     *            that resulted in an <code>AuthenticationException</code>
     * @param response
     *            so that the user agent can begin authentication
     * @param authException
     *            that caused the invocation
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        OAuth2Exception oAuth2Exception = addAuthenticateHeader(request, response);
        if (oAuth2Exception != null) {
            doHandle(request, response, oAuth2Exception);
        } else {
            doHandle(request, response, authException);
        }
    }

    private OAuth2Exception addAuthenticateHeader(HttpServletRequest request, HttpServletResponse response) {

        StringBuilder builder = new StringBuilder(String.format("%s realm=\"%s\"", typeName, realmName));

        Object authException = request.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        OAuth2Exception oauth2Exception = null;

        if (authException instanceof OAuth2Exception) {
            oauth2Exception = (OAuth2Exception) authException;
            builder.append(", " + oauth2Exception.getSummary());
        }

        if (!response.containsHeader("WWW-Authenticate")) {
            response.addHeader("WWW-Authenticate", builder.toString());
        }
        return oauth2Exception;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
