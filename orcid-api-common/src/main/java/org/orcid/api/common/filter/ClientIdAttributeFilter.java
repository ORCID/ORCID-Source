package org.orcid.api.common.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.orcid.core.manager.v3.SourceManager;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Will Simpson
 *
 */
public class ClientIdAttributeFilter extends OncePerRequestFilter {

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    private static final String CLIENT_ID_REQUEST_ATTRIBUTE = "clientId";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        filterChain.doFilter(request, response);
        String clientId = sourceManager.retrieveActiveSourceId();
        request.setAttribute(CLIENT_ID_REQUEST_ATTRIBUTE, clientId);
    }

}
