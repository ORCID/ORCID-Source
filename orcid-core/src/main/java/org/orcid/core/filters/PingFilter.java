package org.orcid.core.filters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;

public class PingFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(PingFilter.class);
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            LOG.warn("PING HIT");
            request.setAttribute("skipAccessLog", true);
            request.setAttribute("isMonitoring", true);
            response.setStatus(200);
            response.setContentType("application/json");
            response.getWriter().write("{\"tomcatUp\":true}");
            return;
        }
}
