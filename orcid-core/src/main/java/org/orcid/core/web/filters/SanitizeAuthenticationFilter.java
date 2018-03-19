package org.orcid.core.web.filters;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

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
        filterChain.doFilter(customRequest, response);        
    }
}

/**
 * Thanks! http://stackoverflow.com/questions/15585755/setting-http-header-in-delegatingfilterproxy
 * */
class CustomHeadersHttpServletRequest extends HttpServletRequestWrapper {

    public CustomHeadersHttpServletRequest(HttpServletRequest request) {
        super(request);        
    }
    
    @Override
    public String getHeader(String name) {
        HttpServletRequest req = (HttpServletRequest)this.getRequest();
        if(name.equalsIgnoreCase("authorization")) {
            String authorization = req.getHeader(name);
            if(PojoUtil.isEmpty(authorization) || authorization.trim().compareToIgnoreCase("bearer") == 0){
                return null;
            } 
            return authorization;
        }
        
        return req.getHeader(name);
    }
    
    public Enumeration<String> getHeaders(String name) {
        HttpServletRequest req = (HttpServletRequest)this.getRequest();
        if(name.equalsIgnoreCase("authorization")) {
            List<String> headers = new ArrayList<String>();
            Enumeration<String> existingHeaders = req.getHeaders(name);            
            if(existingHeaders != null){
                while(existingHeaders.hasMoreElements()) {
                    String existingHeader = existingHeaders.nextElement();
                    if(!PojoUtil.isEmpty(existingHeader) && !(existingHeader.trim().compareToIgnoreCase("bearer") == 0)){
                        headers.add(existingHeader);
                    }
                }
            }
            return Collections.enumeration(headers);
        }
        return req.getHeaders(name);
    }          
}