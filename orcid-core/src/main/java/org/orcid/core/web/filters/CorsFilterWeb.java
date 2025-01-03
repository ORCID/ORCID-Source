package org.orcid.core.web.filters;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * @author Robert Peters (rcpeters)
 */

public class CorsFilterWeb extends OncePerRequestFilter {

    private static Log log = LogFactory.getLog(CorsFilterWeb.class);

    @Resource
    CrossDomainWebManger crossDomainWebManger;

    @Value("${org.orcid.core.baseUri}")
    private String baseUri;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (crossDomainWebManger.allowed(request)) {
                String origin = request.getHeader("origin");
                response.addHeader("Access-Control-Allow-Origin", origin);
                response.addHeader("Access-Control-Allow-Credentials", "true");

                if (request.getHeader("Access-Control-Request-Method") != null && "OPTIONS".equals(request.getMethod())) {
                    // CORS "pre-flight" request
                    response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
                    response.addHeader("Access-Control-Allow-Headers", "X-Requested-With,Origin,Content-Type,Accept,Authorization,x-csrf-token,x-xsrf-token");
                    return;
                }
            }
        } catch (URISyntaxException e) {
            String origin  = request.getHeader("origin");
            String referer = request.getHeader("referer");
            log.error("Unable to process your request due an invalid URI exception, please check your origin and request headers: origin = '" + origin + "' referer = '" + referer + "'" , e);
            throw new ServletException("Unable to process your request due an invalid URI exception", e);
        }

        filterChain.doFilter(request, response);
    }

}
