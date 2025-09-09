package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.api.common.oauth.OrcidClientCredentialEndPointDelegator;
import org.orcid.core.exception.LockedException;
import org.orcid.core.oauth.OAuthError;
import org.orcid.core.togglz.Features;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.togglz.junit.TogglzRule;

import java.io.IOException;
import java.net.URISyntaxException;

public class OauthGenericCallsControllerTest {

    @Mock
    private OrcidClientCredentialEndPointDelegator orcidClientCredentialEndPointDelegator;

    @InjectMocks
    private OauthGenericCallsController controller;

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testObtainOauth2TokenPost() throws IOException, URISyntaxException, InterruptedException {
        when(orcidClientCredentialEndPointDelegator.obtainOauth2Token(isNull(), any())).thenReturn(
                Response.ok("some-success-entity").build());
        ResponseEntity<?> responseEntity = controller.obtainOauth2TokenPost(new MockHttpServletRequest());
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertEquals("some-success-entity", responseEntity.getBody());
    }

    @Test
    public void testObtainOauth2TokenPostLockedClient() throws IOException, URISyntaxException, InterruptedException {
        when(orcidClientCredentialEndPointDelegator.obtainOauth2Token(isNull(), any())).thenThrow(
                new LockedException("Client is locked"));
        ResponseEntity<?> responseEntity = controller.obtainOauth2TokenPost(new MockHttpServletRequest());
        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getBody());
        assertTrue(responseEntity.getBody() instanceof OAuthError);

        OAuthError error = (OAuthError) responseEntity.getBody();
        assertEquals(OAuthError.UNAUTHORIZED_CLIENT, error.getError());
        assertEquals("Client is locked", error.getErrorDescription());
    }

}
