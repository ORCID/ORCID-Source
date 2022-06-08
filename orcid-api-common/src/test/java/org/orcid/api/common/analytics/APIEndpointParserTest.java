package org.orcid.api.common.analytics;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.HttpHeaders;

import org.glassfish.jersey.uri.UriComponent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.api.common.analytics.APIEndpointParser;

import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.server.impl.application.WebApplicationImpl;
import com.sun.jersey.spi.container.ContainerRequest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.PathSegment;

public class APIEndpointParserTest {
    
    @Test
    public void testAPIEndpointParserWithApiVersionAndOrcid() {
        List<PathSegment> segments = getPathSegments("https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321/works");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertEquals("v2.0", parser.getApiVersion());
        assertEquals("works", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserWithApiVersionWithoutOrcid() {
        List<PathSegment> segments = getPathSegments("https://localhost:8443/orcid-api-web/v1.2/orcid-profile");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertEquals("v1.2", parser.getApiVersion());
        assertEquals("orcid-profile", parser.getCategory());
        assertNull(parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserWithoutApiVersionOrOrcid() {
        List<PathSegment> segments = getPathSegments("https://localhost:8443/orcid-api-web/oauth/token");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("oauth", parser.getCategory());
        assertNull(parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserWithoutApiVersionWithOrcid() {
        List<PathSegment> segments = getPathSegments("https://localhost:8443/orcid-api-web/1234-4321-1234-4321/orcid-bio");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryV2() {
        List<PathSegment> segments = getPathSegments("https://localhost:8443/orcid-api-web/v2.0/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertEquals("v2.0", parser.getApiVersion());
        assertEquals("record", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryV3() {
        List<PathSegment> segments = getPathSegments("https://localhost:8443/orcid-api-web/v3.0_rc1/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertEquals("v3.0_rc1", parser.getApiVersion());
        assertEquals("record", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryV1() {
        List<PathSegment> segments = getPathSegments("https://localhost:8443/orcid-api-web/v1.2/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertEquals("v1.2", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testAPIEndpointParserNoCategoryOrVersion() {
        List<PathSegment> segments = getPathSegments("https://localhost:8443/orcid-api-web/1234-4321-1234-4321");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertNotNull(parser.getApiVersion());
        assertEquals("", parser.getApiVersion());
        assertEquals("orcid-bio", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }    
    
    @Test
    public void testViewOrcidWorksOldVersion() {
        List<PathSegment> segments = getPathSegments("http://api.qa.orcid.org/orcid-api-web/v1.2/1234-4321-1234-4321/orcid-works");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertNotNull(parser.getApiVersion());
        assertEquals("v1.2", parser.getApiVersion());
        assertEquals("orcid-works", parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }
    
    @Test
    public void testInvalidEventCategory() {
        List<PathSegment> segments = getPathSegments("http://api.qa.orcid.org/orcid-api-web/v2.0/1234-4321-1234-4321/erm");
        APIEndpointParser parser = new APIEndpointParser(segments);
        assertNotNull(parser.getApiVersion());
        assertEquals("v2.0", parser.getApiVersion());
        assertEquals(APIEndpointParser.INVALID_URL_CATEGORY, parser.getCategory());
        assertEquals("1234-4321-1234-4321", parser.getOrcidId());
    }       
    
    private List<PathSegment> getPathSegments(String uri) {
        return Collections.unmodifiableList(UriComponent.decodePath(uri, true));
    }
}
