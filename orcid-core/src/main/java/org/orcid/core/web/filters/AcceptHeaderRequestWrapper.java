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
