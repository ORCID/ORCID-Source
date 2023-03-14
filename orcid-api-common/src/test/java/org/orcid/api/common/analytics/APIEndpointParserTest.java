package org.orcid.api.common.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.SecurityContext;

import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;

public class APIEndpointParserTest {
    

    @Mock
    private SecurityContext securityContext;
    @Mock
    private PropertiesDelegate propertiesDelegate;

    
    private String BASE_LOCAL_URL = "https://localhost:8443/orcid-api-web/";
    
    @Before
    public void setup() {
        securityContext = Mockito.mock(SecurityContext.class);
        propertiesDelegate = Mockito.mock(PropertiesDelegate.class);
     }
    
    @Test
    public void testAPIEndpointParserWithApiVersionAndOrcid() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL, "https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321/works");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v2.0", parser.getApiVersion());
        assertEquals("works", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserWithApiVersionWithoutOrcid() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL,"https://localhost:8443/orcid-api-web/v1.2/orcid-profile");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v1.2", parser.getApiVersion());
        assertEquals("orcid-profile", parser.getCategory());
        assertNull(parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserWithoutApiVersionOrOrcid() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL,"https://localhost:8443/orcid-api-web/oauth/token");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("oauth", parser.getCategory());
        assertNull(parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserWithoutApiVersionWithOrcid() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL,"https://localhost:8443/orcid-api-web/1234-4321-1234-4321/orcid-bio");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryV2() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL,"https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v2.0", parser.getApiVersion());
        assertEquals("record", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryV3() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL,"https://localhost:8443/orcid-api-web/v3.0_rc1/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v3.0_rc1", parser.getApiVersion());
        assertEquals("record", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryV1() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL,"https://localhost:8443/orcid-api-web/v1.2/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertEquals("v1.2", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryOrVersion() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL,"https://localhost:8443/orcid-api-web/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }    
    
    @Test
    public void testInvalidEventCategory() {
        ContainerRequest request = getRequest(BASE_LOCAL_URL,"https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321/erm");
        APIEndpointParser parser = new APIEndpointParser(request);
        assertNotNull(parser.getApiVersion());
        assertEquals("v2.0", parser.getApiVersion());
        assertEquals(APIEndpointParser.INVALID_URL_CATEGORY, parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    private ContainerRequest getRequest(String urlBase, String url) {
        ContainerRequest  request = new ContainerRequest(URI.create(urlBase), URI.create(url), "GET", securityContext, propertiesDelegate);
        request.header(HttpHeaders.CONTENT_TYPE, "application/xml");
        request.header(HttpHeaders.USER_AGENT, "blah");
        request.header("X-FORWARDED-FOR", "37.14.150.83");
        return request;
    }
    
}
