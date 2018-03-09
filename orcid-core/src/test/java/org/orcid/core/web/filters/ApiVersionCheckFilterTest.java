package org.orcid.core.web.filters;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.pojo.ajaxForm.PojoUtil;
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
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/0000-0001-7510-9252/activities");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "PUT", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "DELETE", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
                
        ContainerRequest containerRequest = new ContainerRequest(webApp, "GET", baseUri, requestUri, headers, inputStream);
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
        filter.filter(containerRequest);        
    }
    
    @Test
    public void webhooksShouldWorkWithoutVersionTest() {
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/0000-0001-7510-9252/webhook/http://test.orcid.org");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "PUT", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "DELETE", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void invalidWebhooksShouldNotWork() {
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/0000-0001-7510-9252/webhook/");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);    
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "PUT", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "DELETE", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch(OrcidBadRequestException e) {
            
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void api1_2VersionTest() {
        WebApplication webApp = Mockito.mock(WebApplication.class, Mockito.RETURNS_MOCKS);
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v1.2/0000-0001-7510-9252/activities");
        InBoundHeaders headers = new InBoundHeaders();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[0]);
        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "POST", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "PUT", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "DELETE", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);            
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = new ContainerRequest(webApp, "GET", baseUri, requestUri, headers, inputStream);
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
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