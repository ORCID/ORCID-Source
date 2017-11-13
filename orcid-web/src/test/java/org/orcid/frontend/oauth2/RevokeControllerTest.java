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
package org.orcid.frontend.oauth2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;

public class RevokeControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    private RevokeController revokeController = new RevokeController();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        revokeController.setOrcidOauth2TokenDetailService(orcidOauth2TokenDetailService);
        revokeController.setOrcidSecurityManager(orcidSecurityManager);
        when(request.getParameter("token")).thenReturn("token-value");
        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn("client-id");
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setClientDetailsId("client-id");
        token.setTokenValue("token-value");
        token.setRefreshTokenValue("refresh-token-value");
        token.setTokenDisabled(false);
        when(orcidOauth2TokenDetailService.findNonDisabledByTokenValue("token-value")).thenReturn(token);
        when(orcidOauth2TokenDetailService.findByRefreshTokenValue("refresh-token-value")).thenReturn(token);
    }

    @Test
    public void noClientIdTest() {
        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn(null);
        try {
            revokeController.revoke(request);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Unable to validate client credentials", e.getMessage());
        } catch (Exception e) {
            fail();
        }

        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn("");
        try {
            revokeController.revoke(request);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Unable to validate client credentials", e.getMessage());
        } catch (Exception e) {
            fail();
        }

        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn("client-id");
        try {
            revokeController.revoke(request);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void noTokenTest() {
        when(request.getParameter("token")).thenReturn(null);
        try {
            revokeController.revoke(request);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Please provide the token to be param", e.getMessage());
        } catch (Exception e) {
            fail();
        }

        when(request.getParameter("token")).thenReturn("");
        try {
            revokeController.revoke(request);
            fail();
        } catch (IllegalArgumentException e) {
            assertEquals("Please provide the token to be param", e.getMessage());
        } catch (Exception e) {
            fail();
        }

        when(request.getParameter("token")).thenReturn("token-value");
        try {
            revokeController.revoke(request);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void notOwnerTest() {
        when(orcidSecurityManager.getClientIdFromAPIRequest()).thenReturn("other-client-id");
        revokeController.revoke(request);

        verify(orcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("token-value");
        verify(orcidOauth2TokenDetailService, times(0)).findByRefreshTokenValue(anyString());
        verify(orcidOauth2TokenDetailService, times(0)).disableAccessToken(anyString());
    }

    @Test
    public void tokenAlreadyDisabledOrNonExistingTest() {
        when(request.getParameter("token")).thenReturn("disabled-or-unexisting");
        revokeController.revoke(request);
        verify(orcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("disabled-or-unexisting");
        verify(orcidOauth2TokenDetailService, times(1)).findByRefreshTokenValue("disabled-or-unexisting");
        verify(orcidOauth2TokenDetailService, times(0)).disableAccessToken(anyString());
    }

    @Test
    public void disableByTokenTest() {
        when(request.getParameter("token")).thenReturn("token-value");
        revokeController.revoke(request);
        verify(orcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("token-value");
        verify(orcidOauth2TokenDetailService, times(0)).findByRefreshTokenValue(anyString());
        verify(orcidOauth2TokenDetailService, times(1)).disableAccessToken("token-value");
    }

    @Test
    public void disableByRefreshTokenTest() {
        when(request.getParameter("token")).thenReturn("refrsh-token-value");
        revokeController.revoke(request);
        verify(orcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("refrsh-token-value");
        verify(orcidOauth2TokenDetailService, times(1)).findByRefreshTokenValue("refrsh-token-value");
        verify(orcidOauth2TokenDetailService, times(1)).disableAccessToken("refrsh-token-value");
    }
}
