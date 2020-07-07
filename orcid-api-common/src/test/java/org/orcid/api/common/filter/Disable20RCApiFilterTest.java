package org.orcid.api.common.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.togglz.Features;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.test.TargetProxyHelper;
import org.togglz.junit.TogglzRule;

public class Disable20RCApiFilterTest {
    private static final String LOCALHOST_URL = "https://localhost:8443";
    private static final String ORCID_URL = "https://orcid.org";    
    private static final String CONTEXT_PATH = "/orcid-pub-web";
    private static final String PATH = "/0000-0000-0000-0000/record";
    
    @InjectMocks
    Disable20RCApiFilter filter;
    
    @Mock
    LocaleManager localeManager;
    
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    HttpSession session;

    @Mock
    FilterChain chain;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void setup() throws FileNotFoundException, IOException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(filter, "localeManager", localeManager);
        when(request.getHeaderNames()).thenReturn(new Vector<String>().elements());
        when(request.getLocales()).thenReturn(new Vector<Locale>().elements());
        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>());
        when(request.getHeader("Accept")).thenReturn("json");
        when(response.getWriter()).thenReturn(new PrintWriter(new OutputStreamWriter(new ByteArrayOutputStream(1024))));
        // default version
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("");
        when(request.getContextPath()).thenReturn(CONTEXT_PATH);
        
        when(localeManager.resolveMessage(Mockito.anyString())).then(new Answer<String>() {

            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return invocation.getArgument(0);
            }
            
        });
        // Disable all features by default
        togglzRule.disableAll();
    }
    
    @Test
    public void filterFeatureEnabledLocalHostRootPathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + PATH));
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledLocalHostV20PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledLocalHostV21PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.1" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledLocalHostV30PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v3.0" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v3.0" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledLocalHostV30rc1PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v3.0_rc1" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0_rc1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v3.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void filterFeatureEnabledLocalHostV30rc2PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v3.0_rc2" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0_rc2");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v3.0_rc2" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidRootPathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + PATH));
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV20PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV21PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.1" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV30PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v3.0" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v3.0" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV30rc1PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v3.0_rc1" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0_rc1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v3.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV30_rc2PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v3.0_rc2" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0_rc2");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v3.0_rc2" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    /**
     * For calls to any other env, redirect uri should not return the context path
     * */
    @Test
    public void filterFeatureEnabledOrcidV20rc1PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0_rc1" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response, times(1)).setHeader("Location", "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledOrcidV20r2PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0_rc2" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc2");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc2" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response, times(1)).setHeader("Location", "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledOrcidV20rc3PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0_rc3" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc3");        
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc3" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response, times(1)).setHeader("Location", "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledOrcidV20rc4PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0_rc4" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc4");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc4" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response, times(1)).setHeader("Location", "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledLocalhostV20rc1PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0_rc1" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response, times(1)).setHeader("Location", LOCALHOST_URL + CONTEXT_PATH + "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledLocalhostV20r2PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0_rc2" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc2");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc2" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response, times(1)).setHeader("Location", LOCALHOST_URL + CONTEXT_PATH + "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledLocalhostV20rc3PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0_rc3" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc3");        
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc3" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response, times(1)).setHeader("Location", LOCALHOST_URL + CONTEXT_PATH + "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledLocalhostV20rc4PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0_rc4" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc4");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc4" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, times(1)).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response, times(1)).setHeader("Location", LOCALHOST_URL + CONTEXT_PATH + "/v2.0" + PATH);
    }
    
    /**
     * Feature disabled
     * */
    @Test
    public void filterFeatureDisabledOrcidV20rc1PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0_rc1" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc1");
        togglzRule.disable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
        verify(response, never()).setHeader(Mockito.matches("Location"), Mockito.anyString());
    }
    
    @Test
    public void filterFeatureDisabledOrcidV20r2PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0_rc2" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc2");
        togglzRule.disable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc2" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
        verify(response, never()).setHeader(Mockito.matches("Location"), Mockito.anyString());
    }
    
    @Test
    public void filterFeatureDisableOrcidV20rc3PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0_rc3" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc3");        
        togglzRule.disable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc3" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
        verify(response, never()).setHeader(Mockito.matches("Location"), Mockito.anyString());
    }
    
    @Test
    public void filterFeatureDisableOrcidV20rc4PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(ORCID_URL + CONTEXT_PATH + "/v2.0_rc4" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc4");
        togglzRule.disable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc4" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
        verify(response, never()).setHeader(Mockito.matches("Location"), Mockito.anyString());
    }
    
    @Test
    public void filterFeatureDisableLocalhostV20rc1PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0_rc1" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc1");
        togglzRule.disable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
        verify(response, never()).setHeader(Mockito.matches("Location"), Mockito.anyString());
    }
    
    @Test
    public void filterFeatureDisableLocalhostV20r2PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0_rc2" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc2");
        togglzRule.disable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc2" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
        verify(response, never()).setHeader(Mockito.matches("Location"), Mockito.anyString());
    }
    
    @Test
    public void filterFeatureDisableLocalhostV20rc3PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0_rc3" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc3");        
        togglzRule.disable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc3" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
        verify(response, never()).setHeader(Mockito.matches("Location"), Mockito.anyString());
    }
    
    @Test
    public void filterFeatureDisableLocalhostV20rc4PathTest() throws ServletException, IOException {
        when(request.getRequestURL()).thenReturn(new StringBuffer(LOCALHOST_URL + CONTEXT_PATH + "/v2.0_rc4" + PATH));
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc4");
        togglzRule.disable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = CONTEXT_PATH + "/v2.0_rc4" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        verify(response, never()).setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
        verify(response, never()).setHeader(Mockito.matches("Location"), Mockito.anyString());
    }
}
