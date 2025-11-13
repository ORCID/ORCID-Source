package org.orcid.api.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.orcid.core.togglz.Features;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Provider
@Component
public class PutAuthTokenActionFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutAuthTokenActionFilter.class);

    private static final String OAUTH_TOKEN_PATH = "/oauth/token";

    @Context
    private HttpServletRequest httpServletRequest;

    @Value("${org.orcid.papi.http.redirect.code:307}")
    private int httpRedirectCode;

    @Value("${org.orcid.core.baseUri}")
    private String rootLocation;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (Features.REDIRECT_PUT_TOKEN_ENDPOINT.isActive() && request.getRequestURI().contains(OAUTH_TOKEN_PATH)) {
            response.setStatus(httpRedirectCode);
            response.setHeader("Location", rootLocation + OAUTH_TOKEN_PATH);
            LOGGER.debug("Redirecting PUT token request to root");
        }
        else {
            filterChain.doFilter(request, response);
        }
    }
}
