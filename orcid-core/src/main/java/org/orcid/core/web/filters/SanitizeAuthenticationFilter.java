/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 
 * @author Angel Montenegro
 * 
 */

public class SanitizeAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        CustomHeadersHttpServletRequest customRequest = new CustomHeadersHttpServletRequest(request);
        
        Enumeration<String> originalHeaders = request.getHeaderNames();
        if(originalHeaders != null) {
            while(originalHeaders.hasMoreElements()) {
                String header = originalHeaders.nextElement();
                if(!PojoUtil.isEmpty(header)) {
                    if(header.equals("authorization")) {
                        String value = request.getHeader(header);
                        if(!PojoUtil.isEmpty(value) && !value.trim().equals("bearer")) {
                            customRequest.addHeader(header, value);
                        }
                    } else {
                        customRequest.addHeader(header, request.getHeader(header));
                    }
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}

class CustomHeadersHttpServletRequest extends HttpServletRequestWrapper {

    private Map<String, List<String>> headers = new HashMap<String, List<String>>();
    
    public CustomHeadersHttpServletRequest(HttpServletRequest request) {
        super(request);        
    }
    
    @Override
    public String getHeader(String name) {
        if (headers.containsKey(name)) {
            return headers.get(name).get(0);
        }

        return null;
    }
    
    /**
     * The default behavior of this method is to return getHeaders(String name)
     * on the wrapped request object.
     */
    public Enumeration<String> getHeaders(String name) {
        if(headers.containsKey(name)) {
            return Collections.enumeration(headers.keySet());
        } 
        return Collections.emptyEnumeration();
    }  
    
    public void addHeader(String header, String value) {
        List<String> list = headers.get(header);
        if (list == null) {
            list = new ArrayList<String>();
            headers.put(header, list);
        }
        list.add(value);
    }        
    
    @Override
    public Enumeration<String> getHeaderNames() {
        List<String> list = new ArrayList<String>();
        list.addAll(headers.keySet());
        return Collections.enumeration(list);
    }
}