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

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.springframework.mock.web.MockHttpServletRequest;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;

public class ApiVersionCheckFilterTest {

    private ContainerRequest request;
    
    @Before
    public void setup() {
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        InBoundHeaders headers = new InBoundHeaders();
        headers.add("X-Forwarded-Proto", "https");
        request = new ContainerRequest(webApp, "GET", URI.create("https://localhost:8443/orcid-api-web/"),
                URI.create("https://localhost:8443/orcid-api-web/v2.0_rc1/0000-0001-7510-9252/activities"), headers, new ByteArrayInputStream(new byte[0]));
    }
    
    @Test
    public void apiV2SchemeTest() {
        
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setScheme("https");
        
        ApiVersionCheckFilter filter = new ApiVersionCheckFilter(mockReq);
        filter.filter(request);
    }
    
    @Test(expected=OrcidBadRequestException.class)
    public void apiV2BlockHttpTest() {
        LocaleManager localeManager = Mockito.mock(LocaleManager.class);
        Mockito.when(localeManager.resolveMessage("apiError.badrequest_secure_only.exception")).thenReturn("API Version 2.0 only allows HTTPS calls.");
        
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setScheme("http");
        
        ApiVersionCheckFilter filter = new ApiVersionCheckFilter(localeManager, mockReq);
        filter.filter(request);
    }
    
    @Test
    public void apiV2HeaderTest() {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setAttribute("X-Forwarded-Proto", "https");
        OrcidHttpServletRequestWrapper requestWrapper = new OrcidHttpServletRequestWrapper(mockReq);
        ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
        filter.filter(request);
    }
    
    private static class OrcidHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public OrcidHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getHeader(String name) {
            String header = super.getHeader(name);
            return header == null ? (String) super.getAttribute(name) : header;
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            List<String> headerNames = Collections.list(super.getHeaderNames());
            headerNames.addAll(Collections.list(super.getAttributeNames()));
            return Collections.enumeration(headerNames);
        }
    }
}
