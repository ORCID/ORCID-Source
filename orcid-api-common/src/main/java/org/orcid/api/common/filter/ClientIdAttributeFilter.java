package org.orcid.api.common.filter;

import java.io.IOException;

import javax.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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
