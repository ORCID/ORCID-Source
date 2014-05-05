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

import org.orcid.core.security.MethodNotAllowedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 2013-2014 ORCID
 * 
 * @author Angel Montenegro (amontenegro) Date: 27/03/2014
 */
public class OrcidT1Oauth2TokenEndPointFilter extends ClientCredentialsTokenEndpointFilter {
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getMethod().equals(RequestMethod.GET.name())) {
            String message = "Method GET is not supported for token requests. POST IS supported, "
                    + "but BASIC authentication is the preferred method of authenticating clients.";
            InvalidRequestException ire = new InvalidRequestException(message);
            throw new MethodNotAllowedException(message, ire);
        }
        
        return super.attemptAuthentication(request, response);
    }
}