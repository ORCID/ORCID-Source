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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Robert Peters (rcpeters)
 * 
 */

public class CorsFilter extends OncePerRequestFilter {

    @Value("${org.orcid.security.cors.allowed_domains:orcid.org}")
    private String allowedDomains;
    
    private static List<String> domainsRegex;    

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        allowed(request);

        if(allowed(request)) {
            response.addHeader("Access-Control-Allow-Origin", "*");
            if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
                // CORS "pre-flight" request
                response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type, Accept");
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean allowed(HttpServletRequest request) throws MalformedURLException {
        URL url = new URL(request.getRequestURL().toString());
        String domain = url.getHost();
        
        if (validateDomain(domain)) {
            return true;
        }
        
        return false;
    }

    private boolean validateDomain(String domain) {
        for (String allowedDomain : getAllowedDomainsRegex()) {
            if (domain.matches(allowedDomain)) {
                return true;
            }
        }
        return false;
    }    
    
    private List<String> getAllowedDomainsRegex() {
        if (domainsRegex == null) {
            domainsRegex = new ArrayList<String>();
            for (String allowedDomain : allowedDomains.split(",")) {
                String regex = transformPatternIntoRegex(allowedDomain);
                domainsRegex.add(regex);
            }
        }

        return domainsRegex;
    }

    private String transformPatternIntoRegex(String domainPattern) {
        String result = domainPattern.replace(".", "\\.");
        result = domainPattern.replace("*", ".+");
        return result;
    }
}