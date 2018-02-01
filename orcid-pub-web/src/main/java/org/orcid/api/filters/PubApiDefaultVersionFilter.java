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
package org.orcid.api.filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.togglz.Features;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.web.filter.OncePerRequestFilter;

public class PubApiDefaultVersionFilter extends OncePerRequestFilter {

    private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d.*?)/");

    @Resource
    protected OrcidUrlManager orcidUrlManager;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getServletPath();
        if (path.startsWith("/search/") || path.startsWith("/oauth/token") || path.startsWith("/experimental_rdf_v1/") || path.startsWith("/static/identifierTypes.xsl")) {
            filterChain.doFilter(request, response);
        } else {
            Matcher matcher = VERSION_PATTERN.matcher(path);
            String version = null;
            if (matcher.lookingAt()) {
                version = matcher.group(1);
            }

            if (PojoUtil.isEmpty(version)) {
                if (Features.PUB_API_2_0_BY_DEFAULT.isActive()) {
                    String baseUrl = orcidUrlManager.getPubBaseUrl();                
                    String redirectUri = baseUrl + "/v2.0" + path;
                    response.sendRedirect(redirectUri);
                } else {
                    filterChain.doFilter(request, response);
                }                                
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
