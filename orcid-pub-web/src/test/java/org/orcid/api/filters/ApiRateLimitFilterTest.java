package org.orcid.api.filters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.oauth.service.OrcidTokenStore;
import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;
import org.orcid.persistence.jpa.entities.PublicApiDailyRateLimitEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-t1-web-context.xml" })
public class ApiRateLimitFilterTest {

    @Resource
    public ApiRateLimitFilter apiRateLimitFilter;

    @Mock
    private FilterChain filterChainMock;

    @Mock
    private OrcidTokenStore orcidTokenStoreMock;

    @Mock
    private PublicApiDailyRateLimitDao papiRateLimitingDaoMock;

    MockHttpServletRequest httpServletRequestMock = new MockHttpServletRequest();

    MockHttpServletResponse httpServletResponseMock = new MockHttpServletResponse();

    @Test
    public void doFilterInternal_rateLimitingDisabledTest() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", false);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(filterChainMock, times(1)).doFilter(eq(httpServletRequestMock), eq(httpServletResponseMock));
        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitingDaoMock, never()).findByIpAddressAndRequestDate(anyString(), any());
        verify(papiRateLimitingDaoMock, never()).persist(any());
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_X_FORWARDED_FOR_header_Test() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRateLimitingDao", papiRateLimitingDaoMock);

        when(papiRateLimitingDaoMock.findByIpAddressAndRequestDate(eq(ip), any())).thenReturn(null);
        httpServletRequestMock.addHeader("X-FORWARDED-FOR", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitingDaoMock, never()).updatePublicApiDailyRateLimit(any(PublicApiDailyRateLimitEntity.class), anyBoolean());
        verify(papiRateLimitingDaoMock, times(1)).persist(any(PublicApiDailyRateLimitEntity.class));
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_X_REAL_IP_header_Test() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRateLimitingDao", papiRateLimitingDaoMock);

        when(papiRateLimitingDaoMock.findByIpAddressAndRequestDate(eq(ip), any())).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitingDaoMock, never()).updatePublicApiDailyRateLimit(any(PublicApiDailyRateLimitEntity.class), anyBoolean());
        verify(papiRateLimitingDaoMock, times(1)).persist(any(PublicApiDailyRateLimitEntity.class));
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_whitelisted_IP_Test() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.1";

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRateLimitingDao", papiRateLimitingDaoMock);

        when(papiRateLimitingDaoMock.findByIpAddressAndRequestDate(eq(ip), any())).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitingDaoMock, never()).updatePublicApiDailyRateLimit(any(PublicApiDailyRateLimitEntity.class), anyBoolean());
        verify(papiRateLimitingDaoMock, never()).persist(any(PublicApiDailyRateLimitEntity.class));
    }

    @Test
    public void doFilterInternal_annonymousRequest_existingEntryTest() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";
        PublicApiDailyRateLimitEntity e = new PublicApiDailyRateLimitEntity();
        e.setId(1000L);
        e.setIpAddress(ip);
        e.setRequestCount(100L);

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRateLimitingDao", papiRateLimitingDaoMock);

        when(papiRateLimitingDaoMock.findByIpAddressAndRequestDate(eq(ip), any())).thenReturn(e);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitingDaoMock, times(1)).updatePublicApiDailyRateLimit(any(PublicApiDailyRateLimitEntity.class), eq(false));
        verify(papiRateLimitingDaoMock, never()).persist(any(PublicApiDailyRateLimitEntity.class));
    }

    @Test
    public void doFilterInternal_clientRequest_newEntryTest() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";
        String clientId = "clientId1";

        httpServletRequestMock.addHeader("Authorization", "TEST_TOKEN");
        when(orcidTokenStoreMock.readClientId(eq("TEST_TOKEN"))).thenReturn(clientId);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRateLimitingDao", papiRateLimitingDaoMock);

        when(papiRateLimitingDaoMock.findByClientIdAndRequestDate(eq(ip), any())).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitingDaoMock, never()).updatePublicApiDailyRateLimit(any(PublicApiDailyRateLimitEntity.class), anyBoolean());
        verify(papiRateLimitingDaoMock, times(1)).persist(any(PublicApiDailyRateLimitEntity.class));
    }

    @Test
    public void doFilterInternal_clientRequest_existingEntryTest() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";
        String clientId = "clientId1";

        PublicApiDailyRateLimitEntity e = new PublicApiDailyRateLimitEntity();
        e.setId(1000L);
        e.setIpAddress(ip);
        e.setRequestCount(100L);

        httpServletRequestMock.addHeader("Authorization", "TEST_TOKEN");
        when(orcidTokenStoreMock.readClientId(eq("TEST_TOKEN"))).thenReturn(clientId);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRateLimitingDao", papiRateLimitingDaoMock);

        when(papiRateLimitingDaoMock.findByClientIdAndRequestDate(eq(clientId), any())).thenReturn(e);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitingDaoMock, times(1)).updatePublicApiDailyRateLimit(any(PublicApiDailyRateLimitEntity.class), eq(true));
        verify(papiRateLimitingDaoMock, never()).persist(any(PublicApiDailyRateLimitEntity.class));
    }

    @Test
    public void doFilterInternal_checkLimitReachedTest() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";

        PublicApiDailyRateLimitEntity e = new PublicApiDailyRateLimitEntity();
        e.setId(1000L);
        e.setIpAddress(ip);
        e.setRequestCount(10001L);

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRateLimitingDao", papiRateLimitingDaoMock);

        when(papiRateLimitingDaoMock.findByIpAddressAndRequestDate(eq(ip), any())).thenReturn(e);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        assertEquals(429, httpServletResponseMock.getStatus());
        String content = httpServletResponseMock.getContentAsString();
        assertEquals("Too Many Requests. You have exceeded the daily quota for anonymous usage of this API. \\nYou can increase your daily quota by registering for and using Public API client credentials (https://info.orcid.org/documentation/integration-guide/registering-a-public-api-client/)", content);
    }
}