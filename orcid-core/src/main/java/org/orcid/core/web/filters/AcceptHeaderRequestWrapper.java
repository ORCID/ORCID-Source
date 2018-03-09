package org.orcid.core.web.filters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * 
 * @author Will Simpson
 * 
 */
public class AcceptHeaderRequestWrapper extends HttpServletRequestWrapper {

    private String accepts;

    public AcceptHeaderRequestWrapper(HttpServletRequest request, String accepts) {
        super(request);
        String contentType = request.getHeader("Content-Type");
        if (accepts == null && contentType != null)
            this.accepts = contentType;
        else
            this.accepts = accepts;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getHeaders(String name) {
        if ("accept".equalsIgnoreCase(name)) {
            List<String> acceptsList = new ArrayList<>();
            acceptsList.add(accepts);
            return Collections.enumeration(acceptsList);
        }
        return super.getHeaders(name);
    }
}
