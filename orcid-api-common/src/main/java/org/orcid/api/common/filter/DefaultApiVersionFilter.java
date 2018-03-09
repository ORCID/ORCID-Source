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

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.togglz.Features;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class DefaultApiVersionFilter extends OncePerRequestFilter {

    private static final Pattern VERSION_PATTERN = Pattern.compile("/v(\\d.*?)/");

    private static final List<String> IGNORE_LIST = Arrays.asList("/resources/","/search/","/oauth/token","/experimental_rdf_v1/","/static/");
    
    private static final String WEBHOOK_PATH_REGEX = "^/" + OrcidStringUtils.ORCID_STRING + "/webhook/.+";
    
    public static final Pattern webhookPattern = Pattern
            .compile(WEBHOOK_PATH_REGEX);
    
    @Resource
    protected OrcidUrlManager orcidUrlManager;
    
    protected Features feature;
    
    public void setFeature(Features f) {
        this.feature = f;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getServletPath();
        if (IGNORE_LIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
        } else if(webhookPattern.matcher(path).matches()) {
            filterChain.doFilter(request, response);
        } else {
            Matcher matcher = VERSION_PATTERN.matcher(path);
            String version = null;
            if (matcher.lookingAt()) {
                version = matcher.group(1);
            }

            if (PojoUtil.isEmpty(version)) {
                if (feature.isActive()) {
                    String baseUrl = Features.PUB_API_2_0_BY_DEFAULT.equals(feature) ? orcidUrlManager.getPubBaseUrl() : orcidUrlManager.getApiBaseUrl();                
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
