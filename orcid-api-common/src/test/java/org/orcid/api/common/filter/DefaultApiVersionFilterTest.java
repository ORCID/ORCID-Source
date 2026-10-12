package org.orcid.api.common.filter;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpStatus;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.togglz.Features;
import org.springframework.test.util.ReflectionTestUtils;
import org.togglz.junit.TogglzRule;

public class DefaultApiVersionFilterTest {

    private static final String API_BASE_URL = "https://api.orcid.org";
    private static final String PUBLIC_BASE_URL = "https://pub.orcid.org";

    private DefaultApiVersionFilter filter;
    private OrcidUrlManager orcidUrlManager;
    private LocaleManager localeManager;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void setUp() throws Exception {
        filter = new DefaultApiVersionFilter();
        orcidUrlManager = mock(OrcidUrlManager.class);
        localeManager = mock(LocaleManager.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);

        filter.orcidUrlManager = orcidUrlManager;
        filter.setIsPublicApi(false);
        ReflectionTestUtils.setField(filter, "localeManager", localeManager);

        when(orcidUrlManager.getApiBaseUrl()).thenReturn(API_BASE_URL);
        when(orcidUrlManager.getPubBaseUrl()).thenReturn(PUBLIC_BASE_URL);
        when(localeManager.resolveMessage(ArgumentMatchers.anyString(), ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.getArgument(1).toString() + " message");
    }

    @Test
    public void shouldContinueChainForIgnoredPath() throws Exception {
        when(request.getServletPath()).thenReturn("/resources/openapi.yaml");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldContinueChainForWebhookPath() throws Exception {
        when(request.getServletPath()).thenReturn("/0000-0001-2345-6789/webhook/https://example.org/hook");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendRedirect(ArgumentMatchers.anyString());
    }

    @Test
    public void shouldRedirectUnversionedRequestToV3WhenFeatureIsUnset() throws Exception {
        when(request.getServletPath()).thenReturn("/0000-0001-2345-6789/person");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendRedirect(API_BASE_URL + "/v3.0/0000-0001-2345-6789/person");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void shouldRedirectUnversionedRequestToV2WhenFeatureIsInactive() throws Exception {
        filter.setFeature(Features.EVENTS);
        when(request.getServletPath()).thenReturn("/0000-0001-2345-6789/person");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendRedirect(API_BASE_URL + "/v2.0/0000-0001-2345-6789/person");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void shouldRedirectLodRequestToExperimentalRdfEndpoint() throws Exception {
        when(request.getServletPath()).thenReturn("/0000-0001-2345-6789/person");
        when(request.getHeader("Accept")).thenReturn("text/turtle");

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendRedirect(PUBLIC_BASE_URL + OrcidApiConstants.EXPERIMENTAL_RDF_V1 + "/0000-0001-2345-6789/person");
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    public void shouldNormalizeUnsupportedVersionAndWriteJsonMessage() throws Exception {
        StringWriter body = new StringWriter();
        when(request.getServletPath()).thenReturn("/v3.1/0000-0001-2345-6789/person");
        when(request.getHeader("Accept")).thenReturn("application/json");
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.PERMANENT_REDIRECT_308);
        verify(response).setHeader("Location", API_BASE_URL + "/v3.0/0000-0001-2345-6789/person");
        assertTrue(body.toString().contains("9056"));
        assertTrue(body.toString().contains("2.0 message"));
    }

    @Test
    public void shouldNormalizeUnsupportedVersionAndWriteXmlMessage() throws Exception {
        StringWriter body = new StringWriter();
        when(request.getServletPath()).thenReturn("/v2.0_rc1/0000-0001-2345-6789/person");
        when(request.getHeader("Accept")).thenReturn("application/xml");
        when(response.getWriter()).thenReturn(new PrintWriter(body));

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpStatus.PERMANENT_REDIRECT_308);
        verify(response).setHeader("Location", API_BASE_URL + "/v2.0/0000-0001-2345-6789/person");
        assertTrue(body.toString().contains("9056"));
        assertTrue(body.toString().contains("2.0 message"));
    }
}
