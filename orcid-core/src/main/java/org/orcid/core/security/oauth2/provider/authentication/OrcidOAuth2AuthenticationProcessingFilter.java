package org.orcid.core.security.oauth2.provider.authentication;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.oauth.service.OrcidRandomValueTokenServicesImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.authentication.BearerTokenExtractor;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetailsSource;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationProcessingFilter;
import org.springframework.security.oauth2.provider.authentication.TokenExtractor;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class OrcidOAuth2AuthenticationProcessingFilter extends OAuth2AuthenticationProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidOAuth2AuthenticationProcessingFilter.class);

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource = new OAuth2AuthenticationDetailsSource();
    
    private TokenExtractor tokenExtractor = new BearerTokenExtractor();

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        final boolean debug = LOGGER.isDebugEnabled();
        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        try {

            Authentication authentication = tokenExtractor.extract(request);

            if (authentication == null) {
                if (isAuthenticated()) {
                    if (debug) {
                        LOGGER.debug("Clearing security context.");
                    }
                    SecurityContextHolder.clearContext();
                }
                if (debug) {
                    LOGGER.debug("No token in request, will continue chain.");
                }
            } else {
                request.setAttribute(OAuth2AuthenticationDetails.ACCESS_TOKEN_VALUE, authentication.getPrincipal());
                if (authentication instanceof AbstractAuthenticationToken) {
                    AbstractAuthenticationToken needsDetails = (AbstractAuthenticationToken) authentication;
                    needsDetails.setDetails(authenticationDetailsSource.buildDetails(request));
                }
                Authentication authResult = authenticationManager.authenticate(authentication);

                if (debug) {
                    LOGGER.debug("Authentication success: " + authResult);
                }

                eventPublisher.publishAuthenticationSuccess(authResult);
                SecurityContextHolder.getContext().setAuthentication(authResult);

            }
        } catch (OAuth2Exception failed) {
            SecurityContextHolder.clearContext();

            if (debug) {
                LOGGER.debug("Authentication request failed: " + failed);
            }
            eventPublisher.publishAuthenticationFailure(new BadCredentialsException(failed.getMessage(), failed),
                    new PreAuthenticatedAuthenticationToken("access-token", "N/A"));

            authenticationEntryPoint.commence(request, response, new InsufficientAuthenticationException(failed.getMessage(), failed));

            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return true;
    }

}
