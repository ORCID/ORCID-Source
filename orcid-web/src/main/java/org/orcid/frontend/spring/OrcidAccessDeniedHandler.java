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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

public class OrcidAccessDeniedHandler extends AccessDeniedHandlerImpl {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidAccessDeniedHandler.class);
    
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException,
            ServletException {
        
        if(accessDeniedException != null) {
            if(InvalidCsrfTokenException.class.isAssignableFrom(accessDeniedException.getClass())) {
                String sessionId = request.getRequestedSessionId();
                String path = request.getRequestURL().toString();
                LOGGER.error("InvalidCsrfTokenException for session {} and path {}", new Object[]{sessionId, path});                
            } else if(MissingCsrfTokenException.class.isAssignableFrom(accessDeniedException.getClass())) {
                String sessionId = request.getRequestedSessionId();
                String path = request.getRequestURL().toString();
                LOGGER.error("MissingCsrfTokenException for session {} and path {}", new Object[]{sessionId, path});
            }
        }
        
        super.handle(request, response, accessDeniedException);
    }
}
