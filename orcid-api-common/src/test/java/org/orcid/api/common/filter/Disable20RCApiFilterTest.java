package org.orcid.api.common.filter;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
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
import org.orcid.core.togglz.Features;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.togglz.junit.TogglzRule;

public class Disable20RCApiFilterTest {
    private static final String LOCALHOST_URL = "https://localhost:8443";
    private static final String ORCID_URL = "https://orcid.org";    
    private static final String CONTEXT_PATH = "/orcid-pub-web";
    private static final String PATH = "/0000-0000-0000-0000/record";
    
    @InjectMocks
    Disable20RCApiFilter filter;
    
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
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(request.getHeaderNames()).thenReturn(new Vector<String>().elements());
        when(request.getLocales()).thenReturn(new Vector<Locale>().elements());
        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>());
        // default version
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("");
        
        // Disable all features by default
        togglzRule.disableAll();
    }
    
    @Test
    public void filterFeatureEnabledLocalHostRootPathTest() throws ServletException, IOException {
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = LOCALHOST_URL + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledLocalHostV20PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = LOCALHOST_URL + "/v2.0" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledLocalHostV21PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = LOCALHOST_URL + "/v2.1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledLocalHostV30PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = LOCALHOST_URL + "/v3.0" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledLocalHostV30rc1PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0_rc1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = LOCALHOST_URL + "/v3.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void filterFeatureEnabledLocalHostV30rc2PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0_rc2");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = LOCALHOST_URL + "/v3.0_rc2" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidRootPathTest() throws ServletException, IOException {
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV20PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v2.0" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV21PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v2.1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV30PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v3.0" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV30rc1PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0_rc1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v3.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(0, response.getStatus());
        verify(chain, times(1)).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void filterFeatureEnabledOrcidV30_rc2PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("3.0_rc2");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v3.0_rc2" + PATH;
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
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc1");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v2.0_rc1" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response).sendRedirect(ORCID_URL + "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledOrcidV20r2PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc2");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v2.0_rc2" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response).sendRedirect(ORCID_URL + "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledOrcidV20rc3PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc3");
        when(request.getContextPath()).thenReturn("");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v2.0_rc3" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response).sendRedirect(ORCID_URL + "/v2.0" + PATH);
    }
    
    @Test
    public void filterFeatureEnabledOrcidV20rc4PathTest() throws ServletException, IOException {
        when(request.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME)).thenReturn("2.0_rc4");
        togglzRule.enable(Features.V2_DISABLE_RELEASE_CANDIDATES);
        String requestUri = ORCID_URL + "/v2.0_rc4" + PATH;
        when(request.getRequestURI()).thenReturn(requestUri);        
        filter.doFilter(request, response, chain);        
        assertEquals(HttpServletResponse.SC_MOVED_PERMANENTLY, response.getStatus());
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
        verify(response).sendRedirect(ORCID_URL + "/v2.0" + PATH);
    }
}
