package org.orcid.frontend.spring;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Test;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.RequestRejectedException;

public class LoggingStrictHttpFirewallTest {

    @Test
    public void testGetFirewalledRequestNormal() {
        LoggingStrictHttpFirewall firewall = new LoggingStrictHttpFirewall();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getContextPath()).thenReturn("");
        when(request.getServletPath()).thenReturn("/test");
        when(request.getMethod()).thenReturn("GET");
        
        FirewalledRequest firewalledRequest = firewall.getFirewalledRequest(request);
        assertNotNull(firewalledRequest);
    }

    @Test(expected = RequestRejectedException.class)
    public void testGetFirewalledRequestRejected() {
        LoggingStrictHttpFirewall firewall = new LoggingStrictHttpFirewall();
        HttpServletRequest request = mock(HttpServletRequest.class);
        // StrictHttpFirewall rejects semicolon by default
        when(request.getRequestURI()).thenReturn("/test;semicolon");
        when(request.getContextPath()).thenReturn("");
        when(request.getServletPath()).thenReturn("/test;semicolon");
        when(request.getMethod()).thenReturn("GET");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test;semicolon"));

        firewall.getFirewalledRequest(request);
    }
}
