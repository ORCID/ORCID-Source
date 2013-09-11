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
package org.orcid.frontend.spring;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.security.DeprecatedException;
import org.orcid.core.security.UnclaimedProfileExistsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

/*
 * Trying to make spring login for
 * http://stackoverflow.com/questions/10811623/spring-security-programatically-logging-in
 * 
 * @author Robert Peters (rcpeters)
 */
public class AjaxAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setContentType("application/json");
        PrintWriter writer = response.getWriter();
        writer.println("{");
        writer.println("\"success\": false");
        if (exception.getCause() instanceof UnclaimedProfileExistsException) {
            writer.println(",");
            writer.println("\"unclaimed\": true");
        } else if(exception.getCause() instanceof DeprecatedException){
            writer.println(",");
            writer.println("\"deprecated\": true");
            DeprecatedException exc = (DeprecatedException)exception.getCause();
            if(exc != null && exc.getPrimary() != null){
                writer.println(",");
                writer.println("\"primary\":\"" + exc.getPrimary() + "\"");
            }
        }
        writer.println("}");
    }
}
