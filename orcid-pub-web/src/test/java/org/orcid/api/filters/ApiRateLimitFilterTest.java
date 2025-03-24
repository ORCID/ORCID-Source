package org.orcid.api.filters;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.api.rate_limit.PapiRateLimitRedisClient;
import org.orcid.core.oauth.service.OrcidTokenStore;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.TargetProxyHelper;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDate;

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
    private PapiRateLimitRedisClient papiRateLimitRedisMock;

    MockHttpServletRequest httpServletRequestMock = new MockHttpServletRequest();

    MockHttpServletResponse httpServletResponseMock = new MockHttpServletResponse();

    @Test
    public void doFilterInternal_rateLimitingDisabledTest() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", false);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(filterChainMock, times(1)).doFilter(eq(httpServletRequestMock), eq(httpServletResponseMock));
        verify(orcidTokenStoreMock, never()).readClientId(anyString());

        verify(papiRateLimitRedisMock, never()).getDailyLimitsForClient(anyString(), any());
        verify(papiRateLimitRedisMock, never()).setTodayLimitsForClient(anyString(), any());
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_X_FORWARDED_FOR_header_Test() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(null);
        httpServletRequestMock.addHeader("X-FORWARDED-FOR", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_X_REAL_IP_header_Test() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());

        verify(papiRateLimitRedisMock, never()).getDailyLimitsForClient(anyString(), any());
        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_whitelisted_IP_Test() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.1";

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitRedisMock, never()).setTodayLimitsForClient(eq(ip), any());
    }

    @Test
    public void doFilterInternal_annonymousRequest_existingEntryTest() throws ServletException, IOException, JSONException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";
        JSONObject dailyLimitsObj = new JSONObject();
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_DATE_CREATED, System.currentTimeMillis());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_IS_ANONYMOUS, true);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_DATE, LocalDate.now().toString());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_CLIENT, ip);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_COUNT, 1);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_LAST_MODIFIED, System.currentTimeMillis());

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(dailyLimitsObj);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));

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
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_clientRequest_existingEntryTest() throws ServletException, IOException, JSONException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";
        String clientId = "clientId1";

        JSONObject dailyLimitsObj = new JSONObject();
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_DATE_CREATED, System.currentTimeMillis());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_IS_ANONYMOUS, true);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_DATE, LocalDate.now().toString());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_CLIENT, clientId);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_COUNT, 100L);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_LAST_MODIFIED, System.currentTimeMillis());

        httpServletRequestMock.addHeader("Authorization", "TEST_TOKEN");
        when(orcidTokenStoreMock.readClientId(eq("TEST_TOKEN"))).thenReturn(clientId);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getDailyLimitsForClient(eq(clientId), any())).thenReturn(dailyLimitsObj);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_checkLimitReachedTest() throws ServletException, IOException, JSONException {
        MockitoAnnotations.initMocks(this);
        String ip = "127.0.0.2";

        JSONObject dailyLimitsObj = new JSONObject();
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_DATE_CREATED, System.currentTimeMillis());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_IS_ANONYMOUS, true);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_DATE, LocalDate.now().toString());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_CLIENT, ip);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_COUNT, 100000001L);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_LAST_MODIFIED, System.currentTimeMillis());

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(dailyLimitsObj);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        assertEquals(429, httpServletResponseMock.getStatus());
        String content = httpServletResponseMock.getContentAsString();
        assertEquals(
                "Too Many Requests. You have exceeded the daily quota for anonymous usage of this API. \nYou can increase your daily quota by registering for and using Public API client credentials (https://info.orcid.org/documentation/integration-guide/registering-a-public-api-client/)",
                content);
    }

    @Test
    public void doFilterInternal_annonymousRequest_whitelisted_cidr_IP_Test() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip_in_cidr = "10.0.0.0";

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip_in_cidr))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip_in_cidr);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitRedisMock, never()).setTodayLimitsForClient(eq(ip_in_cidr), any());
    }

    @Test
    public void doFilterInternal_annonymousRequest_not_whitelisted_cidr_IP_Test() throws ServletException, IOException {
        MockitoAnnotations.initMocks(this);
        String ip_not_cidr = "20.0.0.0";

        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "enableRateLimiting", true);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "orcidTokenStore", orcidTokenStoreMock);
        TargetProxyHelper.injectIntoProxy(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip_not_cidr))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip_not_cidr);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(orcidTokenStoreMock, never()).readClientId(anyString());
        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(eq(ip_not_cidr), any(JSONObject.class));
    }
}
