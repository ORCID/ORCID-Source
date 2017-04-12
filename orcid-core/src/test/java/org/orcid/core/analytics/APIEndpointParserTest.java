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
package org.orcid.core.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URI;

import javax.ws.rs.core.HttpHeaders;

import org.junit.Test;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;

public class APIEndpointParserTest {
    
    @Test
    public void testAPIEndpointParserWithApiVersionAndOrcid() {
        ContainerRequest request = getRequest("https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321/works");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v2.0", parser.getApiVersion());
        assertEquals("works", parser.getCategory());
    }
    
    @Test
    public void testAPIEndpointParserWithApiVersionWithoutOrcid() {
        ContainerRequest request = getRequest("https://localhost:8443/orcid-api-web/v1.2/orcid-profile");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v1.2", parser.getApiVersion());
        assertEquals("orcid-profile", parser.getCategory());
    }
    
    @Test
    public void testAPIEndpointParserWithoutApiVersionOrOrcid() {
        ContainerRequest request = getRequest("https://localhost:8443/orcid-api-web/oauth/token");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("oauth", parser.getCategory());
    }
    
    @Test
    public void testAPIEndpointParserWithoutApiVersionWithOrcid() {
        ContainerRequest request = getRequest("https://localhost:8443/orcid-api-web/1234-4321-1234-4321/orcid-bio");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryV2() {
        ContainerRequest request = getRequest("https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v2.0", parser.getApiVersion());
        assertEquals("record", parser.getCategory());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryV1() {
        ContainerRequest request = getRequest("https://localhost:8443/orcid-api-web/v1.2/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v1.2", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryOrVersion() {
        ContainerRequest request = getRequest("https://localhost:8443/orcid-api-web/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
    }    
    
    private ContainerRequest getRequest(String url) {
        InBoundHeaders headers = new InBoundHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/xml");
        headers.add(HttpHeaders.USER_AGENT, "blah");
        headers.add("X-FORWARDED-FOR", "37.14.150.83");
        return new ContainerRequest(new WebApplicationImpl(), "POST", URI.create("https://localhost:8443/orcid-api-web/"),
                URI.create(url), headers, null);
    }

}
