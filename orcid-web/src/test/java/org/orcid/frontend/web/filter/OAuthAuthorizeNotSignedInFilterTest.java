package org.orcid.frontend.web.filter;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
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
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.togglz.Features;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.togglz.junit.TogglzRule;

public class OAuthAuthorizeNotSignedInFilterTest {

    @InjectMocks
    OAuthAuthorizeNotSignedInFilter oaFilter;

    @Mock
    OrcidUrlManager orcidUrlManager;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    HttpSession session;

    @Mock
    FilterChain chain;

    @Mock
    SecurityContext context;

    @Mock
    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken;

    @Mock
    OrcidProfileUserDetails orcidProfileUserDetails;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);
    
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(orcidUrlManager.getBaseUrl()).thenReturn("http://test.com");
        when(request.getHeaderNames()).thenReturn(new Vector<String>().elements());
        when(request.getLocales()).thenReturn(new Vector<Locale>().elements());
        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>());
        when(request.getScheme()).thenReturn("i hate you with all my heart spring mvc");
        when(request.getRequestURL()).thenReturn(new StringBuffer("really, we should break up"));
        
        // Disable all features by default
        togglzRule.disableAll();
    }

    @Test
    public void nullSession() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession(false)).thenReturn(null);

        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response).sendRedirect("http://test.com/signin?oauth&test_param=param");
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void noSecurityContext() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);

        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response).sendRedirect("http://test.com/signin?oauth&test_param=param");
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void noAuthentication() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(context);

        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response).sendRedirect("http://test.com/signin?oauth&test_param=param");
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void hasOrcidProfileUserDetails() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession()).thenReturn(session);
        when(usernamePasswordAuthenticationToken.getDetails()).thenReturn(new OrcidProfileUserDetails());
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(context);
        when(context.getAuthentication()).thenReturn(usernamePasswordAuthenticationToken);
        when(usernamePasswordAuthenticationToken.getDetails()).thenReturn(orcidProfileUserDetails);

        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response, never()).sendRedirect(Mockito.anyString());
        verify(chain).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void notUriOauthAuthorize() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/signin?oauth");
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession(false)).thenReturn(null);

        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response, never()).sendRedirect(Mockito.anyString());
        verify(chain).doFilter(Mockito.any(), Mockito.any());
    }
    
    @Test
    public void oauth2ScreensFeatureEnabledTest() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(context);

        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);        
        
        verify(response).sendRedirect("http://test.com/signin?oauth&test_param=param");
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void oauth2ScreensFeatureFlagUsedTest() throws IOException, ServletException {
        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        when(request.getQueryString()).thenReturn("test_param=param&" + OrcidOauth2Constants.OAUTH_2SCREENS);
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(context);
        
        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);
        
        verify(response).sendRedirect("http://test.com/signin?oauth&test_param=param&" + OrcidOauth2Constants.OAUTH_2SCREENS);
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
    }
}
