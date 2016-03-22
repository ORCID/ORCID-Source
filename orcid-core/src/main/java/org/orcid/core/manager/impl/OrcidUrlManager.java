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
package org.orcid.core.manager.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;

public class OrcidUrlManager {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidUrlManager.class);

    static Pattern fileNamePattern = Pattern.compile("https{0,1}:\\/\\/[^\\/]*(.*){0,1}");

    static String PROTOCALL_PATTREN = "http[s]{0,1}:\\/\\/";

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Value("${org.orcid.core.pubBaseUri}")
    private String pubBaseUrl;

    @Value("${org.orcid.core.apiBaseUri}")
    private String apiBaseUrl;

    @Value("${org.orcid.core.internalApiBaseUri}")
    private String internalApiBaseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getBaseDomainRmProtocall() {
        return getBaseUrl().replaceAll(PROTOCALL_PATTREN, "");
    }

    public String getBaseUriHttp() {
        return this.baseUrl.replace("https", "http").replace(":8443", ":8080");
    }

    @ModelAttribute("basePath")
    public String getBasePath() {
        Matcher fileNameMatcher = fileNamePattern.matcher(getBaseUrl());
        if (!fileNameMatcher.find())
            return "/";
        return fileNameMatcher.group(1) + "/";
    }

    /**
     * 
     * @return the path, without additional trailing slash
     */
    @ModelAttribute("apiPath")
    public String getApiPath() {
        Matcher fileNameMatcher = fileNamePattern.matcher(getApiBaseUrl());
        if (!fileNameMatcher.find())
            return "/";
        return fileNameMatcher.group(1);
    }

    /**
     * 
     * @return the path, without additional trailing slash
     */
    @ModelAttribute("internalApiPath")
    public String getInternalApiPath() {
        Matcher fileNameMatcher = fileNamePattern.matcher(getInternalApiBaseUrl());
        if (!fileNameMatcher.find())
            return "/";
        return fileNameMatcher.group(1);
    }

    /**
     * 
     * @return the path, without additional trailing slash
     */
    @ModelAttribute("pubPath")
    public String getPubPath() {
        Matcher fileNameMatcher = fileNamePattern.matcher(this.getPubBaseUrl());
        if (!fileNameMatcher.find())
            return "/";
        return fileNameMatcher.group(1);
    }

    public String getBaseHost() {
        try {
            return new URI(this.baseUrl).getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem parsing base URI: " + this.baseUrl, e);
        }
    }

    public String getApiHostWithPort() {
        try {
            URI uri = new URI(this.apiBaseUrl);
            if (uri.getPort() >= 0)
                return uri.getHost() + ":" + uri.getPort();
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem parsing base URI: " + this.apiBaseUrl, e);
        }
    }

    public String getInternalApiHostWithPort() {
        try {
            URI uri = new URI(this.internalApiBaseUrl);
            if (uri.getPort() >= 0)
                return uri.getHost() + ":" + uri.getPort();
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem parsing base URI: " + this.apiBaseUrl, e);
        }
    }

    public String getPubHostWithPort() {
        try {
            URI uri = new URI(this.pubBaseUrl);
            if (uri.getPort() >= 0)
                return uri.getHost() + ":" + uri.getPort();
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Problem parsing base URI: " + this.pubBaseUrl, e);
        }
    }

    public String getPubBaseUrl() {
        return pubBaseUrl;
    }

    public void setPubBaseUrl(String pubBaseUrl) {
        this.pubBaseUrl = pubBaseUrl;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public String getInternalApiBaseUrl() {
        return internalApiBaseUrl;
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getServerStringWithContextPath(HttpServletRequest request) {
        String scheme = getscheme(request);

        StringBuilder sb = new StringBuilder();

        if (scheme.equals("https"))
            sb.append(getBaseUrl());
        else
            sb.append(getBaseUriHttp());
        return sb.toString();
    }

    public static String getscheme(HttpServletRequest request) {
        String forwardedProto = request.getHeader("X-Forwarded-Proto");
        String scheme = forwardedProto != null ? forwardedProto : request.getScheme();
        if (scheme == null) 
            LOGGER.error("WHAT THE HELL is going on? Request scheme is null.", request);
        return scheme.toLowerCase();
    }
    
    public static boolean isSecure(HttpServletRequest request) {
        if (OrcidUrlManager.getscheme(request).equals("https")) {
          return true;  
        }
        return false;
    }

    public static String getPathWithoutContextPath(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

}
