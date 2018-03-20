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
        String version = checkVersion(httpRequest);
        checkSection(httpRequest, version);
        filterChain.doFilter(request, response);
    }

    private String checkVersion(HttpServletRequest httpRequest) {
        String path = httpRequest.getServletPath();
        Matcher matcher = VERSION_PATTERN.matcher(path);
        String version = null;
        if (matcher.lookingAt()) {
            version = matcher.group(1);
            httpRequest.setAttribute(API_VERSION_REQUEST_ATTRIBUTE_NAME, version);
        }
        return version;
    }

    private void checkSection(HttpServletRequest httpRequest, String version) {
        String path = httpRequest.getServletPath();
        Matcher notifcationsMatcher = NOTIFICATIONS_PATTERN.matcher(path);
        ApiSection section = ApiSection.V1;
        if (notifcationsMatcher.find()) {
            section = ApiSection.NOTIFICATIONS;
        } else if (version != null && version.startsWith("2.")) {
            section = ApiSection.V2;
        } else if (version != null && version.startsWith("3.")) {
            section = ApiSection.V3;
        }
        httpRequest.setAttribute(API_SECTION_REQUEST_ATTRIBUTE_NAME, section);
    }

}