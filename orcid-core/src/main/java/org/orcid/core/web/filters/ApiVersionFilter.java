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
package org.orcid.core.web.filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.version.ApiSection;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Will Simpson
 * 
 */

public class ApiVersionFilter extends OncePerRequestFilter {

    public static final String API_VERSION_REQUEST_ATTRIBUTE_NAME = "apiVersion";

    public static final String API_SECTION_REQUEST_ATTRIBUTE_NAME = "apiSection";

    private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d.*?)/");

    private static final Pattern NOTIFICATIONS_PATTERN = Pattern.compile("/notifications/");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        checkVersion(httpRequest);
        checkSection(httpRequest);
        filterChain.doFilter(request, response);
    }

    private void checkVersion(HttpServletRequest httpRequest) {
        String path = httpRequest.getServletPath();
        Matcher matcher = VERSION_PATTERN.matcher(path);
        if (matcher.lookingAt()) {
            String version = matcher.group(1);
            httpRequest.setAttribute(API_VERSION_REQUEST_ATTRIBUTE_NAME, version);
        }
    }

    private void checkSection(HttpServletRequest httpRequest) {
        String path = httpRequest.getServletPath();
        Matcher matcher = NOTIFICATIONS_PATTERN.matcher(path);
        ApiSection section = ApiSection.DEFAULT;
        if (matcher.find()) {
            section = ApiSection.NOTIFICATIONS;
        }
        httpRequest.setAttribute(API_SECTION_REQUEST_ATTRIBUTE_NAME, section);
    }

}