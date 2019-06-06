package org.orcid.core.manager.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.constants.OrcidOauth2Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.bind.annotation.ModelAttribute;

public class OrcidUrlManager {

    private static final String DEFAULT_APP_NAME = "default";

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidUrlManager.class);

    private static Pattern fileNamePattern = Pattern.compile("https{0,1}:\\/\\/[^\\/]*(.*){0,1}");

    private static String PROTOCALL_PATTREN = "http[s]{0,1}:\\/\\/";

    public static Pattern SAVED_REQUEST_PATTERN = Pattern
            .compile("/(my-orcid|inbox|account|account/confirm-deactivate-orcid/[^/]+|developer-tools|self-service(/[^/]+)?|manage-members|admin-actions)(\\?|$)|(oauth/(?![^?]*\\.json))");

    @Value("${org.orcid.core.baseUri}")
    private String baseUrl;

    @Value("${org.orcid.core.pubBaseUri}")
    private String pubBaseUrl;

    @Value("${org.orcid.core.apiBaseUri}")
    private String apiBaseUrl;

    @Value("${org.orcid.core.internalApiBaseUri}")
    private String internalApiBaseUrl;
    
    @Autowired(required = false)
    private ServletContext servletContext;

    public String getAppName() {
        if (servletContext != null && servletContext.getContextPath().length() > 1) {
            return servletContext.getContextPath().substring(1);
        }
        return DEFAULT_APP_NAME;
    }

    public String getAppNameSuffix() {
        String appName = getAppName();
        return "_" + appName;
    }

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

    public String getPubBaseUriHttp() {
        return this.pubBaseUrl.replace("https", "http").replace(":8443", ":8080");
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

    public String determineFullTargetUrlFromSavedRequest(HttpServletRequest request, HttpServletResponse response) {
        boolean isOauthRequest = request.getParameter("oauthRequest") == null ? false : Boolean.valueOf(request.getParameter("oauthRequest"));
        
        String url = null;
        if(isOauthRequest) {
            String queryString = (String) request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_QUERY_STRING);            
            if(queryString.startsWith("oauth&")) {
                queryString = queryString.replaceFirst("oauth&", "");
            }
            url = getBaseUrl() + "/oauth/authorize?" + queryString;
        } else {
            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
            if(savedRequest != null) {
                url = savedRequest.getRedirectUrl();
            }
            
            if (url != null) {
                String contextPath = request.getContextPath();
                // Remove the context path if it looks like we are configured to
                // run behind nginx.
                if (getBasePath().equals("/") && !contextPath.equals("/"))
                    url = url.replaceFirst(contextPath.replace("/", "\\/"), "");
                // Only allow the saved request to be used if it matches the
                // expected pattern. So, we won't redirct to blank.gif, for
                // example.
                if (!SAVED_REQUEST_PATTERN.matcher(url).find()) {
                    url = null;
                }
            }
        }
                
        return url;
    }
}
