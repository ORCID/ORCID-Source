package org.orcid.api.common.filter;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.ws.rs.core.SecurityContext;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.orcid.api.common.filter.ApiVersionCheckFilter;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.mock.web.MockHttpServletRequest;

import org.glassfish.jersey.internal.PropertiesDelegate;
import org.glassfish.jersey.server.ContainerRequest;

public class ApiVersionCheckFilterTest {
    @Mock
    private ContainerRequest request;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private PropertiesDelegate propertiesDelegate;

    
    @Before
    public void setup() {
        securityContext = Mockito.mock(SecurityContext.class);
        propertiesDelegate = Mockito.mock(PropertiesDelegate.class);
        request = new ContainerRequest(URI.create("https://localhost:8443/orcid-api-web/"), URI.create("https://localhost:8443/orcid-api-web/v2.0/0000-0001-7510-9252/activities"), "GET", securityContext, propertiesDelegate);
        request.header("X-Forwarded-Proto", "https");
     }
    
    @Test
    public void apiV2SchemeTest() {
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("https");
        filter.filter(request);
    }
    
    @Test(expected=OrcidBadRequestException.class)
    public void apiV2BlockHttpTest() {
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
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
    
    @Test
    public void apiDefaultVersionTest() {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/0000-0001-7510-9252/activities");
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "POST", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "PUT", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "DELETE", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
                
        ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "GET", securityContext, propertiesDelegate);
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
        filter.filter(containerRequest);        
    }
    
    @Test
    public void webhooksShouldWorkWithoutVersionTest() {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/0000-0001-7510-9252/webhook/http://test.orcid.org");
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "POST", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "PUT", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "DELETE", securityContext, propertiesDelegate);
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
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "POST", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);    
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "PUT", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "DELETE", securityContext, propertiesDelegate);            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
        
    @Test
    public void api2_0VersionTest() {
        MockHttpServletRequest mockReq = new MockHttpServletRequest();
        mockReq.setAttribute("X-Forwarded-Proto", "https");
        OrcidHttpServletRequestWrapper requestWrapper = new OrcidHttpServletRequestWrapper(mockReq);                
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v2.0/0000-0001-7510-9252/activities");
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "POST", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "PUT", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "DELETE", securityContext, propertiesDelegate);
            ApiVersionCheckFilter filter = new ApiVersionCheckFilter(requestWrapper);
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "GET", securityContext, propertiesDelegate);
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
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/oauth/token");
        try {
            ContainerRequest containerRequest = new ContainerRequest(baseUri, requestUri, "POST", securityContext, propertiesDelegate);
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