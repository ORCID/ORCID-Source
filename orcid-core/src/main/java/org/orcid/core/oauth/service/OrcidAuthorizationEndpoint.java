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
package org.orcid.core.oauth.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.web.HttpSessionRequiredException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

public class OrcidAuthorizationEndpoint extends AuthorizationEndpoint {

    @Override
    @ExceptionHandler(HttpSessionRequiredException.class)
    public ModelAndView handleHttpSessionRequiredException(HttpSessionRequiredException e, ServletWebRequest webRequest) throws Exception {
        return new ModelAndView("redirect:" + buildRedirectUri(webRequest).toString());
    }

    private URI buildRedirectUri(ServletWebRequest webRequest) throws URISyntaxException {
        String[] referers = webRequest.getHeaderValues("referer");
        if (referers != null && referers.length > 0) {
            return new URI(referers[0]);
        }
        String uri = "/session-expired";
        String contextPath = webRequest.getContextPath();
        if (contextPath != null) {
            uri = contextPath + uri;
        }
        return new URI(uri);
    }

}
