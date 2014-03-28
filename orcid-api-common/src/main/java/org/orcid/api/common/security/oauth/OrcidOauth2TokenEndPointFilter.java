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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 08/05/2012
 */
public class OrcidOauth2TokenEndPointFilter extends ClientCredentialsTokenEndpointFilter {

    private final static String PUBLIC_ROLE = "ROLE_PUBLIC";
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getMethod().equals(RequestMethod.GET.name())) {
            String message = "Method GET is not supported for token requests. POST IS supported, "
                    + "but BASIC authentication is the preferred method of authenticating clients.";
            InvalidRequestException ire = new InvalidRequestException(message);
            throw new MethodNotAllowedException(message, ire);
        }
        
        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");

        // If the request is already authenticated we can assume that this filter is not needed
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
                return authentication;
        }
        
        if (clientId == null) {
                throw new BadCredentialsException("No client credentials presented");
        }

        if (clientSecret == null) {
                clientSecret = "";
        }

        clientId = clientId.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId,
                        clientSecret);

        authentication = this.getAuthenticationManager().authenticate(authRequest); 
                        
        if(authentication != null){
            for(GrantedAuthority auth : authentication.getAuthorities()) {
                if(PUBLIC_ROLE.equals(auth.getAuthority())){
                    String message = "Public members are not allowed to use the Members API";
                    InvalidRequestException ire = new InvalidRequestException(message);
                    throw new MethodNotAllowedException(message, ire);
                }
            }
        }
        
        return authentication;
    }

}
