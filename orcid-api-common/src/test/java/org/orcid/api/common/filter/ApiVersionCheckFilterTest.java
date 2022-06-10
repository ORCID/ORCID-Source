package org.orcid.api.common.filter;

import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

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

import jakarta.ws.rs.core.SecurityContext;

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

    @Test(expected = OrcidBadRequestException.class)
    public void apiV2BlockHttpTest() throws IOException {
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
        filter.filter(request);
    }

    @Test
    public void apiV2HeaderTest() throws IOException {
        ContainerRequest containerRequest = buildContainerRequest(URI.create("https://localhost:8443/orcid-api-web/"),
                URI.create("https://localhost:8443/orcid-api-web/v2.0_rc1/0000-0001-7510-9252/activities"), "POST");
        containerRequest.getHeaders().add("X-Forwarded-Proto", "https");
        ApiVersionCheckFilter filter = getApiVersionCheckFilter("https");
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
        } catch (OrcidBadRequestException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch (OrcidBadRequestException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch (OrcidBadRequestException e) {

        } catch (Exception e) {
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
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
        } catch (Exception e) {
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
        } catch (OrcidBadRequestException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch (OrcidBadRequestException e) {

        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("http");
            filter.filter(containerRequest);
            fail();
        } catch (OrcidBadRequestException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void api2_0VersionTest() {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/v2.0/0000-0001-7510-9252/activities");

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "POST");
            containerRequest.getHeaders().add("X-Forwarded-Proto", "https");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("https");
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "PUT");
            containerRequest.getHeaders().add("X-Forwarded-Proto", "https");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("https");
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "DELETE");
            containerRequest.getHeaders().add("X-Forwarded-Proto", "https");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("https");
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }

        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "GET");
            containerRequest.getHeaders().add("X-Forwarded-Proto", "https");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("https");
            filter.filter(containerRequest);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void apiOauthTokenTest() {
        URI baseUri = URI.create("http://localhost:8443/orcid-api-web/");
        URI requestUri = URI.create("http://localhost:8443/orcid-api-web/oauth/token");
        try {
            ContainerRequest containerRequest = buildContainerRequest(baseUri, requestUri, "POST");
            containerRequest.getHeaders().add("X-Forwarded-Proto", "https");
            ApiVersionCheckFilter filter = getApiVersionCheckFilter("https");
        } catch (Exception e) {
            fail();
        }
    }

    private ApiVersionCheckFilter getApiVersionCheckFilter(String scheme) {
        LocaleManager localeManager = Mockito.mock(LocaleManager.class);
        Mockito.when(localeManager.resolveMessage(Matchers.anyString())).thenReturn("error message");
        Mockito.when(localeManager.resolveMessage(Matchers.anyString(), Matchers.any())).thenReturn("error message");
        MockHttpServletRequest mockReq = new MockHttpServletRequest();

        if (!PojoUtil.isEmpty(scheme)) {
            mockReq.setScheme(scheme);
        }

        return new ApiVersionCheckFilter(localeManager, mockReq);
    }
}