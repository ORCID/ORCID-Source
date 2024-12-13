package org.orcid.api.filters;

import org.apache.commons.math3.stat.inference.TestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.oauth.service.OrcidTokenStore;
import org.orcid.persistence.dao.PublicApiDailyRateLimitDao;
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

    MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();

    MockHttpServletResponse mockHttpServletResponse = new MockHttpServletResponse();

    @Test
    public void enableRateLimitingDisabledTest() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", false);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        apiRateLimitFilter.doFilterInternal(mockHttpServletRequest, mockHttpServletResponse, filterChainMock);
        verify(filterChainMock, times(1)).doFilter(eq(mockHttpServletRequest), eq(mockHttpServletResponse));
        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitingDaoMock, never()).findByIpAddressAndRequestDate(anyString(), any());
        verify(papiRateLimitingDaoMock, never()).persist(any());
    }
}
