package org.orcid.frontend.oauth2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.oauth.authorizationServer.AuthorizationServerUtil;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

public class RevokeControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private AuthorizationServerUtil authorizationServerUtil;

    @InjectMocks
    private RevokeController revokeController;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testRevokeWithClientIdAndSecret() throws Exception {
        String clientId = "APP-1234567890123456";
        String clientSecret = "some-secret";
        String token = "some-token";

        SecurityContextTestUtils.setUpSecurityContextForClientOnly(clientId);
        
        when(request.getParameter("token")).thenReturn(token);
        when(request.getParameter("client_secret")).thenReturn(clientSecret);
        when(request.getHeader("Authorization")).thenReturn(null);

        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.getEntity()).thenReturn("Success");

        when(authorizationServerUtil.forwardTokenRevocationRequest(clientId, clientSecret, token)).thenReturn(mockResponse);

        ResponseEntity<?> result = revokeController.revoke(request);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Success", result.getBody());

        verify(authorizationServerUtil).forwardTokenRevocationRequest(clientId, clientSecret, token);
    }

    @Test
    public void testRevokeWithBasicAuth() throws Exception {
        String authorization = "Basic Y2xpZW50LWlkOnNlY3JldA==";
        String token = "some-token";

        when(request.getParameter("token")).thenReturn(token);
        when(request.getHeader("Authorization")).thenReturn(authorization);

        Response mockResponse = mock(Response.class);
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockResponse.getEntity()).thenReturn("Success");

        when(authorizationServerUtil.forwardTokenRevocationRequest(authorization, token)).thenReturn(mockResponse);

        ResponseEntity<?> result = revokeController.revoke(request);

        assertNotNull(result);
        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Success", result.getBody());

        verify(authorizationServerUtil).forwardTokenRevocationRequest(authorization, token);
    }

    @Test
    public void testRevokeNoToken() throws Exception {
        when(request.getParameter("token")).thenReturn(null);
        try {
            revokeController.revoke(request);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Please provide the token to be param", e.getMessage());
        }

        when(request.getParameter("token")).thenReturn("");
        try {
            revokeController.revoke(request);
            fail("Should have thrown IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Please provide the token to be param", e.getMessage());
        }
    }
}
