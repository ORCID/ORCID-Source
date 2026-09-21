package org.orcid.api.filters;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.api.rate_limit.PapiRateLimitRedisClient;
import org.orcid.core.togglz.Features;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.togglz.junit.TogglzRule;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ApiRateLimitFilterTest {

    private ApiRateLimitFilter apiRateLimitFilter;

    @Mock
    private FilterChain filterChainMock;

    @Mock
    private PapiRateLimitRedisClient papiRateLimitRedisMock;

    private MockHttpServletRequest httpServletRequestMock;

    private MockHttpServletResponse httpServletResponseMock;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allEnabled(Features.class);

    @Before
    public void setUp() throws ServletException {
        MockitoAnnotations.initMocks(this);
        apiRateLimitFilter = new ApiRateLimitFilter();
        httpServletRequestMock = new MockHttpServletRequest();
        httpServletResponseMock = new MockHttpServletResponse();

        ReflectionTestUtils.setField(apiRateLimitFilter, "papiRedisClient", papiRateLimitRedisMock);
        ReflectionTestUtils.setField(apiRateLimitFilter, "anonymousRequestLimit", 10000);
        ReflectionTestUtils.setField(apiRateLimitFilter, "knownRequestLimit", 40000);
        ReflectionTestUtils.setField(apiRateLimitFilter, "papiWhiteSpaceSeparatedWhiteList", "127.0.0.1");
        ReflectionTestUtils.setField(apiRateLimitFilter, "papiClientIdWhiteSpaceSeparatedWhiteList", "");
        ReflectionTestUtils.setField(apiRateLimitFilter, "papiReferrerWhiteSpaceSeparatedWhiteList", "");
        ReflectionTestUtils.setField(apiRateLimitFilter, "papiCidrRangeWhiteSpaceSeparatedWhiteList", "10.0.0.0/8");
        apiRateLimitFilter.afterPropertiesSet();
    }

    private void setRateLimitingEnabled(boolean enabled) {
        ReflectionTestUtils.setField(apiRateLimitFilter, "enableRateLimiting", enabled);
    }

    @Test
    public void doFilterInternal_rateLimitingDisabledTest() throws ServletException, IOException {
        setRateLimitingEnabled(false);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(filterChainMock, times(1)).doFilter(eq(httpServletRequestMock), eq(httpServletResponseMock));

        verify(papiRateLimitRedisMock, never()).getDailyLimitsForClient(anyString(), any());
        verify(papiRateLimitRedisMock, never()).setTodayLimitsForClient(anyString(), any());
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_X_FORWARDED_FOR_header_Test() throws ServletException, IOException {
        String ip = "127.0.0.2";

        setRateLimitingEnabled(true);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(null);
        httpServletRequestMock.addHeader("X-FORWARDED-FOR", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_X_REAL_IP_header_Test() throws ServletException, IOException {
        String ip = "127.0.0.2";

        setRateLimitingEnabled(true);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, never()).getDailyLimitsForClient(anyString(), any());
        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_annonymousRequest_newEntry_whitelisted_IP_Test() throws ServletException, IOException {
        String ip = "127.0.0.1";

        setRateLimitingEnabled(true);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, never()).setTodayLimitsForClient(eq(ip), any());
    }

    @Test
    public void doFilterInternal_annonymousRequest_existingEntryTest() throws ServletException, IOException, JSONException {
        String ip = "127.0.0.2";
        JSONObject dailyLimitsObj = new JSONObject();
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_DATE_CREATED, System.currentTimeMillis());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_IS_ANONYMOUS, true);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_DATE, LocalDate.now().toString());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_CLIENT, ip);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_COUNT, 1);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_LAST_MODIFIED, System.currentTimeMillis());

        setRateLimitingEnabled(true);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(dailyLimitsObj);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_clientRequest_newEntryTest() throws ServletException, IOException {
        String ip = "127.0.0.2";

        httpServletRequestMock.addHeader("Authorization", "TEST_TOKEN");
        setRateLimitingEnabled(true);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_clientRequest_existingEntryTest() throws ServletException, IOException, JSONException {
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
        setRateLimitingEnabled(true);

        when(papiRateLimitRedisMock.getDailyLimitsForClient(eq(clientId), any())).thenReturn(dailyLimitsObj);
        httpServletRequestMock.addHeader("X-REAL-IP", ip);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(anyString(), any(JSONObject.class));
    }

    @Test
    public void doFilterInternal_checkLimitReachedTest() throws ServletException, IOException, JSONException {
        String ip = "127.0.0.2";

        JSONObject dailyLimitsObj = new JSONObject();
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_DATE_CREATED, System.currentTimeMillis());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_IS_ANONYMOUS, true);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_DATE, LocalDate.now().toString());
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_CLIENT, ip);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_REQUEST_COUNT, 100000001L);
        dailyLimitsObj.put(PapiRateLimitRedisClient.KEY_LAST_MODIFIED, System.currentTimeMillis());

        setRateLimitingEnabled(true);

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
        String ip_in_cidr = "10.0.0.0";

        setRateLimitingEnabled(true);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip_in_cidr))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip_in_cidr);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, never()).setTodayLimitsForClient(eq(ip_in_cidr), any());
    }

    @Test
    public void doFilterInternal_annonymousRequest_not_whitelisted_cidr_IP_Test() throws ServletException, IOException {
        String ip_not_cidr = "20.0.0.0";

        setRateLimitingEnabled(true);

        when(papiRateLimitRedisMock.getTodayDailyLimitsForClient(eq(ip_not_cidr))).thenReturn(null);
        httpServletRequestMock.addHeader("X-REAL-IP", ip_not_cidr);

        apiRateLimitFilter.doFilterInternal(httpServletRequestMock, httpServletResponseMock, filterChainMock);

        verify(papiRateLimitRedisMock, times(1)).setTodayLimitsForClient(eq(ip_not_cidr), any(JSONObject.class));
    }
}
