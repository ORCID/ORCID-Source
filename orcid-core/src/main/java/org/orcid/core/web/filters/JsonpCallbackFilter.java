/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.web.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Adds jsonp callbacks Follow spring tutorial
 * http://jpgmr.wordpress.com/2010/07
 * /28/tutorial-implementing-a-servlet-filter-for
 * -jsonp-callback-with-springs-delegatingfilterproxy/
 * 
 * @author Robert Peters (rcpeters)
 * 
 */

public class JsonpCallbackFilter extends OncePerRequestFilter {

    private static Log log = LogFactory.getLog(JsonpCallbackFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        @SuppressWarnings("unchecked")
        Map<String, String[]> parms = httpRequest.getParameterMap();

        if (parms.containsKey("callback")) {
            if (log.isDebugEnabled())
                log.debug("Wrapping response with JSONP callback '" + parms.get("callback")[0] + "'");

            HttpServletRequestWrapper requestWrapper = new AcceptHeaderRequestWrapper(httpRequest, "application/json");

            OutputStream out = httpResponse.getOutputStream();

            GenericResponseWrapper responseWrapper = new GenericResponseWrapper(httpResponse);

            filterChain.doFilter(requestWrapper, responseWrapper);

            out.write(new String(parms.get("callback")[0] + "(").getBytes());
            out.write(responseWrapper.getData());
            out.write(new String(");").getBytes());

            responseWrapper.setContentType("text/javascript;charset=UTF-8");

            out.close();
        } else {
            filterChain.doFilter(request, response);
        }
    }

}