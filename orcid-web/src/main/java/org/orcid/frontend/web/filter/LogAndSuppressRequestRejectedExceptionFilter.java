package org.orcid.frontend.web.filter;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.filter.GenericFilterBean;

import com.google.common.net.HttpHeaders;

/**
 * 
 * @author lizkrznarich
 * 
 */
public class LogAndSuppressRequestRejectedExceptionFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(LogAndSuppressRequestRejectedExceptionFilter.class);

    @Override
    public void destroy() {
        // Do nothing
    }
    
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        try {
            chain.doFilter(req, res);
        } catch (RequestRejectedException e) {
            HttpServletRequest request = (HttpServletRequest) req;
            HttpServletResponse response = (HttpServletResponse) res;
            LOGGER.warn(
                        "request_rejected: remote={}, user_agent={}, request_url={}" +
                        request.getRemoteHost() + 
                        request.getHeader(HttpHeaders.USER_AGENT) +
                        request.getRequestURL() + 
                        e
                );

            //response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    @Override
    public void init(FilterConfig arg0) throws ServletException {
        // Do nothing
    }
}
