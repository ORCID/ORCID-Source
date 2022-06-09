package org.orcid.api.common.filter;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.mock.web.MockHttpServletRequest;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.SecurityContext;

import org.orcid.api.common.filter.*;

public class ApiVersionCheckFilterTest {

    @Mock
    private SecurityContext mockSecurityContext;
    
    @Mock
    private PropertiesDelegate mockPropertiesDelegate;
    
    private ContainerRequest request;
    
    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        request = new ContainerRequest(URI.create("https://localhost:8443/orcid-api-web/"),
                URI.create("https://localhost:8443/orcid-api-web/v2.0_rc1/0000-0001-7510-9252/activities"), "GET", mockSecurityContext, mockPropertiesDelegate, null);
    }
    
    private ContainerRequest buildContainerRequest(URI uri1, URI uri2, String httpMethod) {
        return new ContainerRequest(uri1, uri2, httpMethod, mockSecurityContext, mockPropertiesDelegate, null);
    }
    
    @Test
    public void apiV2SchemeTest() throws IOException {
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("https");
        filter.filter(request);
    }
    
    @Test(expected=OrcidBadRequestException.class)
    public void apiV2BlockHttpTest() throws IOException {
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
        filter.filter(request);
    }
    
    @Test
    public void apiV2HeaderTest() throws IOException {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setAttribute("X-Forwarded-Proto", "https");
        OrcidHttpServletRequestWrapper requestWrapper = new OrcidHttpServletRequestWrapper(mockReq);
        ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
        filter.filter(request);
    }
    
    @Test
    public void apiDefaultVersionTest() throws IOException {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/0000-0001-7510-9252/activities");
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "POST");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
                
        ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "GET");
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
        filter.filter(containerRequest);        
    }
    
    @Test
    public void webhooksShouldWorkWithoutVersionTest() {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/0000-0001-7510-9252/webhook/http://test.orcid.org");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "POST");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void invalidWebhooksShouldNotWork() {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/0000-0001-7510-9252/webhook/");
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "POST");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);    
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void api1_1VersionTest() {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v1.1/0000-0001-7510-9252/activities");
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "POST");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch (OrcidBadRequestException e) {
            // We expect this
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch (OrcidBadRequestException e) {
            // We expect this
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch (OrcidBadRequestException e) {
            // We expect this
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "GET");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
        } catch (OrcidBadRequestException e) {
            // We expect this
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void api1_2VersionTest() {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v1.2/0000-0001-7510-9252/activities");
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "POST");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch (OrcidBadRequestException e) {
            // We expect this
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch (OrcidBadRequestException e) {
            // We expect this
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch (OrcidBadRequestException e) {
            // We expect this
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "GET");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
        } catch (OrcidBadRequestException e) {
            // We expect this
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void api2_0_rc2VersionTest() {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setAttribute("X-Forwarded-Proto", "https");
        OrcidHttpServletRequestWrapper requestWrapper = new OrcidHttpServletRequestWrapper(mockReq);                
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v2.0_rc2/0000-0001-7510-9252/activities");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "PUT", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "DELETE", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "GET", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }
    }
       
    @Test
    public void api2_0_rc3VersionTest() {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setAttribute("X-Forwarded-Proto", "https");
        OrcidHttpServletRequestWrapper requestWrapper = new OrcidHttpServletRequestWrapper(mockReq);                
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v2.0_rc3/0000-0001-7510-9252/activities");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "PUT", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "DELETE", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "GET", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void api2_0_rc4VersionTest() {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setAttribute("X-Forwarded-Proto", "https");
        OrcidHttpServletRequestWrapper requestWrapper = new OrcidHttpServletRequestWrapper(mockReq);                
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v2.0_rc4/0000-0001-7510-9252/activities");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "PUT", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "DELETE", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "GET", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }
    }
        
    @Test
    public void api2_0VersionTest() {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setAttribute("X-Forwarded-Proto", "https");
        OrcidHttpServletRequestWrapper requestWrapper = new OrcidHttpServletRequestWrapper(mockReq);                
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v2.0/0000-0001-7510-9252/activities");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "PUT", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "DELETE", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "GET", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }
    }
    
    @Test
    public void apiOauthTokenTest() {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setAttribute("X-Forwarded-Proto", "https");
        OrcidHttpServletRequestWrapper requestWrapper = new OrcidHttpServletRequestWrapper(mockReq);                
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/oauth/token");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }
    }
    
    private ApiVersionCheckFilter getApiVersionCheckFilter(String scheme) {
        LocaleManager localeManager = Mockito.mock(LocaleManager.class);
        Mockito.when(localeManager.resolveMessage(Matchers.anyString())).thenReturn("error message");
        Mockito.when(localeManager.resolveMessage(Matchers.anyString(), Matchers.any())).thenReturn("error message");
        MockHttpServletRequest mockReq = new MockHttpServletRequest();        
        
        if(!PojoUtil.isEmpty(scheme)) {
            mockReq.setScheme(scheme);
        }
        
        return new ApiVersionCheckFilter(localeManager, mockReq);
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