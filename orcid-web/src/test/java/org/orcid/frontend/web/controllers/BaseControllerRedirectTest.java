package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.orcid.core.manager.impl.OrcidUrlManager;

public class BaseControllerRedirectTest {

    private static final String BASE_URI = "https://dev.orcid.org";

    private OrcidUrlManager orcidUrlManager;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private HttpSession session;
    private TestBaseController controller;

    @Before
    public void setUp() {
        orcidUrlManager = mock(OrcidUrlManager.class);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        session = mock(HttpSession.class);

        when(request.getSession()).thenReturn(session);
        when(request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_2SCREENS)).thenReturn(null);

        controller = new TestBaseController(orcidUrlManager);
    }

    @Test
    public void baseControllerKeepsSavedRequestForRegistration() {
        when(orcidUrlManager.determineFullTargetUrlFromSavedRequest(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(BASE_URI + "/signin");

        String redirect = controller.callCalculateRedirectUrl(request, response, true, false, null);

        assertEquals(BASE_URI + "/signin", redirect);
    }

    @Test
    public void successfulThirdPartySigninRedirectsToThirdPartyCompletedWhenSavedRequestIsSignin() {
        when(orcidUrlManager.determineFullTargetUrlFromSavedRequest(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(BASE_URI + "/signin");

        String redirect = controller.callCalculateRedirectUrl(request, response, false, false, "shibboleth");

        assertEquals(BASE_URI + "/my-orcid/third-party-signin-completed", redirect);
    }

    @Test
    public void successfulSocialSigninRedirectsToThirdPartyCompletedWhenSavedRequestIsSignin() {
        when(orcidUrlManager.determineFullTargetUrlFromSavedRequest(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenReturn(BASE_URI + "/signin");

        String redirect = controller.callCalculateRedirectUrl(request, response, false, false, "social");

        assertEquals(BASE_URI + "/my-orcid/third-party-signin-completed", redirect);
    }

    @Test
    public void oauthScreensRequestStillRedirectsToAuthorizeForThirdPartySignin() {
        when(request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_2SCREENS)).thenReturn(Boolean.TRUE);
        when(request.getSession().getAttribute(OrcidOauth2Constants.OAUTH_QUERY_STRING)).thenReturn("client_id=APP-123&prompt=login");
        when(orcidUrlManager.getBaseUrl()).thenReturn(BASE_URI);

        String redirect = controller.callCalculateRedirectUrl(request, response, false, false, "shibboleth");

        assertEquals(BASE_URI + "/oauth/authorize/third-party-signin-completed?client_id=APP-123", redirect);
    }

    private static class TestBaseController extends BaseController {
        TestBaseController(OrcidUrlManager orcidUrlManager) {
            this.orcidUrlManager = orcidUrlManager;
        }

        @Override
        public String getBaseUri() {
            return BASE_URI;
        }

        public String callCalculateRedirectUrl(HttpServletRequest request, HttpServletResponse response, boolean justRegistered, boolean avoidOauthRedirect,
                String thirdPartyLogin) {
            return calculateRedirectUrl(request, response, justRegistered, avoidOauthRedirect, thirdPartyLogin);
        }
    }
}