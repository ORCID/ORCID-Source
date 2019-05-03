package org.orcid.core.web.filters;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Robert Peters (rcpeters)
 * 
 */

public class CorsFilterWeb extends OncePerRequestFilter {

    @Resource
    CrossDomainWebManger crossDomainWebManger;

    @Value("${org.orcid.core.baseUri}")
    private String baseUri;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Pattern p = Pattern.compile("^/public/.*|^/userStatus\\.json");
        Matcher m = p.matcher(OrcidUrlManager.getPathWithoutContextPath(request));
        // Allow CORS for all paths from Angular frontend only if we are in local dev env
        // All other envs allow CORS only if request is userStatus.json or /public/*
        if (baseUri.equals("https://localhost:8443/orcid-web") | baseUri.equals("https://localhost") | m.matches()) {
            if (crossDomainWebManger.allowed(request)) {
                String origin = request.getHeader("origin");
                if (PojoUtil.isEmpty(origin)) {
                    response.addHeader("Access-Control-Allow-Origin", "*");
                } else {
                    response.addHeader("Access-Control-Allow-Origin", origin);
                    response.addHeader("Access-Control-Allow-Credentials", "true");
                }

                if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
                    // CORS "pre-flight" request
                    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type,Accept,x-csrf-token");
                }
            }

        }

        filterChain.doFilter(request, response);
    }

}