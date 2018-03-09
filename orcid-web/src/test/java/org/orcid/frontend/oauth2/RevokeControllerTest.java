package org.orcid.frontend.oauth2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.oauth.OrcidOauth2TokenDetailService;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;

public class RevokeControllerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private OrcidOauth2TokenDetailService mockOrcidOauth2TokenDetailService;

    @Resource
    private OrcidOauth2TokenDetailService orcidOauth2TokenDetailService;
    
    private RevokeController revokeController = new RevokeController();

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        revokeController.setOrcidOauth2TokenDetailService(mockOrcidOauth2TokenDetailService);
        when(request.getParameter("token")).thenReturn("token-value");
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setClientDetailsId("client-id");
        token.setTokenValue("token-value");
        token.setRefreshTokenValue("refresh-token-value");
        token.setTokenDisabled(false);
        when(mockOrcidOauth2TokenDetailService.findNonDisabledByTokenValue("token-value")).thenReturn(token);
        when(mockOrcidOauth2TokenDetailService.findByRefreshTokenValue("refresh-token-value")).thenReturn(token);
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("client-id");
    }
    
    @After
    public void after() {
        revokeController.setOrcidOauth2TokenDetailService(orcidOauth2TokenDetailService);
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
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("other-client-id");
        
        revokeController.revoke(request);

        verify(mockOrcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("token-value");
        verify(mockOrcidOauth2TokenDetailService, times(0)).findByRefreshTokenValue(anyString());
        verify(mockOrcidOauth2TokenDetailService, times(0)).revokeAccessToken(anyString());
    }

    @Test
    public void tokenAlreadyDisabledOrNonExistingTest() {
        when(request.getParameter("token")).thenReturn("disabled-or-unexisting");
        
        revokeController.revoke(request);
        
        verify(mockOrcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("disabled-or-unexisting");
        verify(mockOrcidOauth2TokenDetailService, times(1)).findByRefreshTokenValue("disabled-or-unexisting");
        verify(mockOrcidOauth2TokenDetailService, times(0)).revokeAccessToken(anyString());
    }

    @Test
    public void disableByTokenTest() {
        when(request.getParameter("token")).thenReturn("token-value");
        
        revokeController.revoke(request);
        
        verify(mockOrcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("token-value");
        verify(mockOrcidOauth2TokenDetailService, times(0)).findByRefreshTokenValue(anyString());
        verify(mockOrcidOauth2TokenDetailService, times(1)).revokeAccessToken("token-value");
    }

    @Test
    public void disableByRefreshTokenTest() {
        when(request.getParameter("token")).thenReturn("refresh-token-value");
        
        revokeController.revoke(request);
        
        verify(mockOrcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("refresh-token-value");
        verify(mockOrcidOauth2TokenDetailService, times(1)).findByRefreshTokenValue("refresh-token-value");
        verify(mockOrcidOauth2TokenDetailService, times(1)).revokeAccessToken("token-value");
    }
    
    @Test
    public void refreshTokenAlreadyDisabledTest() {
        OrcidOauth2TokenDetail token = new OrcidOauth2TokenDetail();
        token.setClientDetailsId("client-id");
        token.setTokenValue("token-value");
        token.setRefreshTokenValue("refresh-token-value");
        token.setTokenDisabled(true);
        when(mockOrcidOauth2TokenDetailService.findNonDisabledByTokenValue("token-value")).thenReturn(token);
        when(mockOrcidOauth2TokenDetailService.findByRefreshTokenValue("refresh-token-value")).thenReturn(token);
        when(request.getParameter("token")).thenReturn("refresh-token-value");
        
        revokeController.revoke(request);
        
        verify(mockOrcidOauth2TokenDetailService, times(1)).findNonDisabledByTokenValue("refresh-token-value");
        verify(mockOrcidOauth2TokenDetailService, times(1)).findByRefreshTokenValue("refresh-token-value");
        // It should not call the disable function since it is already disabled
        verify(mockOrcidOauth2TokenDetailService, times(0)).revokeAccessToken("refresh-token-value");
    }
}
