package org.orcid.api.common.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.togglz.Features;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.core.utils.OrcidStringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class DefaultApiVersionFilter extends OncePerRequestFilter {

    private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d.*?)/");

    private static final List<String> IGNORE_LIST = Arrays.asList("/resources/", "/search/", "/oauth/token", OrcidApiConstants.EXPERIMENTAL_RDF_V1 + "/", "/static/", "/openapi.json");

    private static final String WEBHOOK_PATH_REGEX = "^/" + OrcidStringUtils.ORCID_STRING + "/webhook/.+";

    public static final Pattern webhookPattern = Pattern.compile(WEBHOOK_PATH_REGEX);

    @Resource
    protected OrcidUrlManager orcidUrlManager;

    protected Features feature;
    
    protected Boolean isPublicApi;

    public void setFeature(Features f) {
        this.feature = f;
    }
    
    public void setIsPublicApi(Boolean api) {
        this.isPublicApi = api;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        System.out.println("1 " + httpRequest.getContextPath());
        System.out.println("2 " + httpRequest.getPathInfo());
        System.out.println("3 " + httpRequest.getPathTranslated());
        System.out.println("4 " + httpRequest.getQueryString());
        System.out.println("5 " + httpRequest.getRequestURI());
        System.out.println("6 " + httpRequest.getRequestURL());
        System.out.println("7 " + httpRequest.getServletPath());        
        String path = httpRequest.getPathInfo();
        if (IGNORE_LIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
        } else if (webhookPattern.matcher(path).matches()) {
            filterChain.doFilter(request, response);
        } else {
            Matcher matcher = VERSION_PATTERN.matcher(path);
            String version = null;
            if (matcher.lookingAt()) {
                version = matcher.group(1);
            }

            if (PojoUtil.isEmpty(version)) {
                if (isLODButNotJSONLD(request.getHeader("Accept"))) {
                    String redirectUri = orcidUrlManager.getPubBaseUrl() + OrcidApiConstants.EXPERIMENTAL_RDF_V1 + path;
                    response.sendRedirect(redirectUri);
                } else {
                    String baseUrl = isPublicApi ? orcidUrlManager.getPubBaseUrl() : orcidUrlManager.getApiBaseUrl();
                    if (feature == null || feature.isActive()) {
                        String redirectUri = baseUrl + "/v3.0" + path;
                        response.sendRedirect(redirectUri);
                    } else {
                        String redirectUri = baseUrl + "/v2.0" + path;
                        response.sendRedirect(redirectUri);
                    }
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
    
    private boolean isLODButNotJSONLD(String accept) {
        if (accept == null)
            return false;
        return (accept.contains("n3") || accept.contains("rdf") || accept.contains("n-triples") || accept.contains("turtle"));
    }
}
