/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.filter;

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

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;

import static org.mockito.Mockito.*;

public class OAuthAuthorizeNotSignedInFilterTest {

    @Test
    @Ignore
    public void nullSession() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        setUpRequestForExpectedSpringFoo(request);
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession(false)).thenReturn(null);

        OAuthAuthorizeNotSignedInFilter oaFilter = new OAuthAuthorizeNotSignedInFilter();
        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response).sendRedirect("http://test.com/oauth/signin?test_param=param");
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
    }

    public void setUpRequestForExpectedSpringFoo(HttpServletRequest request) {
        when(request.getHeaderNames()).thenReturn(new Vector<String>().elements());
        when(request.getLocales()).thenReturn(new Vector<Locale>().elements());
        when(request.getParameterMap()).thenReturn(new HashMap<String, String[]>());
        when(request.getScheme()).thenReturn("i hate you with all my heart spring mvc");
        when(request.getRequestURL()).thenReturn(new StringBuffer("really, we should break up"));
    }

    @Test
    @Ignore
    public void noSecurityContext() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        setUpRequestForExpectedSpringFoo(request);
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);

        OAuthAuthorizeNotSignedInFilter oaFilter = new OAuthAuthorizeNotSignedInFilter();
        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response).sendRedirect("http://test.com/oauth/signin?test_param=param");
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    @Ignore
    public void noAuthentication() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        SecurityContext context = mock(SecurityContext.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        setUpRequestForExpectedSpringFoo(request);
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession()).thenReturn(session);
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(context);

        OAuthAuthorizeNotSignedInFilter oaFilter = new OAuthAuthorizeNotSignedInFilter();
        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response).sendRedirect("http://test.com/oauth/signin?test_param=param");
        verify(chain, never()).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void hasOrcidProfileUserDetails() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);
        SecurityContext context = mock(SecurityContext.class);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = mock(UsernamePasswordAuthenticationToken.class);
        OrcidProfileUserDetails orcidProfileUserDetails = mock(OrcidProfileUserDetails.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/authorize");
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession()).thenReturn(session);
        when(usernamePasswordAuthenticationToken.getPrincipal()).thenReturn(new OrcidProfileUserDetails());
        when(request.getSession(false)).thenReturn(session);
        when(session.getAttribute("SPRING_SECURITY_CONTEXT")).thenReturn(context);
        when(context.getAuthentication()).thenReturn(usernamePasswordAuthenticationToken);
        when(usernamePasswordAuthenticationToken.getPrincipal()).thenReturn(orcidProfileUserDetails);

        OAuthAuthorizeNotSignedInFilter oaFilter = new OAuthAuthorizeNotSignedInFilter();
        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response, never()).sendRedirect(Mockito.anyString());
        verify(chain).doFilter(Mockito.any(), Mockito.any());
    }

    @Test
    public void notUriOauthAuthorize() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getContextPath()).thenReturn("http://test.com");
        when(request.getRequestURI()).thenReturn("http://test.com/oauth/signin");
        when(request.getQueryString()).thenReturn("test_param=param");
        when(request.getSession(false)).thenReturn(null);

        OAuthAuthorizeNotSignedInFilter oaFilter = new OAuthAuthorizeNotSignedInFilter();
        oaFilter.doFilter((ServletRequest) request, (ServletResponse) response, chain);

        verify(response, never()).sendRedirect(Mockito.anyString());
        verify(chain).doFilter(Mockito.any(), Mockito.any());
    }

}
