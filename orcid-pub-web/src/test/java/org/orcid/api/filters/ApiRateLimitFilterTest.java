package org.orcid.api.filters;

import org.apache.commons.math3.stat.inference.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.oauth.service.OrcidTokenStore;
import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;
import org.orcid.persistence.jpa.entities.PublicApiDailyRateLimitEntity;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
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
    public void doFilterInternal_annonymousRequest_newEntryTest() throws ServletException, IOException {
        String ip = "127.0.0.1";
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", false);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);

        PublicApiDailyRateLimitEntity e = new PublicApiDailyRateLimitEntity();
        e.setId(1000L);
        e.setIpAddress(ip);
        e.setRequestCount(100L);

        when(papiRateLimitingDaoMock.findByIpAddressAndRequestDate(eq(ip), any())).thenReturn(e);
        httpServletRequestMock.setAttribute("X-FORWARDED-FOR", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitingDaoMock, times(1)).updatePublicApiDailyRateLimit(any(), eq(false));
    }

    @Test
    public void doFilterInternal_annonymousRequest_existingEntryTest() throws ServletException, IOException {
       fail();
    }

    @Test
    public void doFilterInternal_clientRequest_newEntryTest() throws ServletException, IOException {
        fail();
    }

    @Test
    public void doFilterInternal_clientRequest_existingEntryTest() throws ServletException, IOException {
        fail();
    }
}