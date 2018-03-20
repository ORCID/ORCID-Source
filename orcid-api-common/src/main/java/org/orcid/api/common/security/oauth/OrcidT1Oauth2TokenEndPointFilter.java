package org.orcid.api.common.security.oauth;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.locale.LocaleManager;
import org.orcid.core.security.MethodNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.provider.client.ClientCredentialsTokenEndpointFilter;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author Angel Montenegro (amontenegro) Date: 27/03/2014
 */
public class OrcidT1Oauth2TokenEndPointFilter extends ClientCredentialsTokenEndpointFilter {

    @Resource
    private LocaleManager localeManager;
	
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidT1Oauth2TokenEndPointFilter.class);

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (request.getMethod().equals(RequestMethod.GET.name())) {
            InvalidRequestException ire = new InvalidRequestException(localeManager.resolveMessage("apiError.token_request_callmethod.exception"));
            throw new MethodNotAllowedException(localeManager.resolveMessage("apiError.token_request_callmethod.exception"), ire);
        }

        String clientId = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");

        LOGGER.info("About to attempt authentication: clientId={}", clientId);

        // If the request is already authenticated we can assume that this
        // filter is not needed
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            LOGGER.info("Already got authentication in security context holder: principal={}, name={}", authentication.getPrincipal(), authentication.getName());
            return authentication;
        }

        if (clientId == null) {
            throw new BadCredentialsException(localeManager.resolveMessage("apiError.client_credentials.exception"));
        }

        if (clientSecret == null) {
            clientSecret = "";
        }

        clientId = clientId.trim();
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(clientId, clientSecret);

        Authentication authenticationResult = this.getAuthenticationManager().authenticate(authRequest);
        if (authenticationResult != null) {
            LOGGER.info("Got authentication result: principal={}, name={}", authenticationResult.getPrincipal(), authenticationResult.getName());
        }
        return authenticationResult;
    }

}