package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.manager.IdentityProviderManager;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.core.manager.UserConnectionManager;
import org.orcid.core.manager.impl.InstitutionalSignInManagerImpl;
import org.orcid.frontend.web.exception.FeatureDisabledException;
import org.orcid.pojo.OAuthSigninData;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * The two header cases below assert the Shibboleth attribute-separator rule
 * ("first-name-1; first-name-2" yields "first-name-1"), which lives in
 * {@link InstitutionalSignInManagerImpl#retrieveFirstName(Map)} and not in the
 * controller. A mocked manager would make both assertions tautologies, so the
 * real implementation is wired in here: the three header readers it exposes are
 * pure functions of the header map and touch none of its collaborators. Its
 * constructor is given an unreachable DiscoFeed URL, exactly as
 * org.orcid.core.manager.InstitutionalSignInManagerTest does, because it
 * swallows the resulting IOException.
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class ShibbolethControllerTest {

    private ShibbolethController shibbolethController;

    private InstitutionalSignInManager institutionalSignInManager;

    @Mock
    private UserConnectionManager userConnectionManager;

    @Mock
    private IdentityProviderManager identityProviderManager;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

    @Before
    public void before() {
        shibbolethController = new ShibbolethController();
        institutionalSignInManager = new InstitutionalSignInManagerImpl("http://localhost:1/dummy-disco-feed");

        ReflectionTestUtils.setField(shibbolethController, "institutionalSignInManager", institutionalSignInManager);
        ReflectionTestUtils.setField(shibbolethController, "userConnectionManager", userConnectionManager);
        ReflectionTestUtils.setField(shibbolethController, "identityProviderManager", identityProviderManager);

        // No existing link for this remote user, which is the branch that copies
        // the names off the manager into the signin data.
        when(userConnectionManager.findByProviderIdAndProviderUserIdAndIdType(anyString(), anyString(), anyString())).thenReturn(null);

        when(servletRequest.getLocale()).thenReturn(Locale.ENGLISH);

        HttpSession session = mock(HttpSession.class);
        when(servletRequest.getSession()).thenReturn(session);
    }

    @Test
    public void testNameHeaders() throws UnsupportedEncodingException {
        Map<String, String> headers = new HashMap<>();
        headers.put(InstitutionalSignInManager.SHIB_IDENTITY_PROVIDER_HEADER, "idp-entity-id");
        headers.put("persistent-id", "user-id");
        headers.put(InstitutionalSignInManager.GIVEN_NAME_HEADER, "first-name-1");
        headers.put(InstitutionalSignInManager.SN_HEADER, "last-name-1");
        shibbolethController.setShibbolethEnabled(true);
        OAuthSigninData data = shibbolethController.getSigninData(headers);
        assertEquals("idp-entity-id", data.getProviderId());
        assertEquals("first-name-1", data.getFirstName());
        assertEquals("last-name-1", data.getLastName());
    }

    @Test
    public void testDuplicateNameHeaders() throws UnsupportedEncodingException {
        Map<String, String> headers = new HashMap<>();
        headers.put(InstitutionalSignInManager.SHIB_IDENTITY_PROVIDER_HEADER, "idp-entity-id");
        headers.put("persistent-id", "user-id");
        headers.put(InstitutionalSignInManager.GIVEN_NAME_HEADER, "first-name-1; first-name-2");
        headers.put(InstitutionalSignInManager.SN_HEADER, "last-name-1; last-name-2");
        shibbolethController.setShibbolethEnabled(true);
        OAuthSigninData data = shibbolethController.getSigninData(headers);
        assertEquals("idp-entity-id", data.getProviderId());
        assertEquals("first-name-1", data.getFirstName());
        assertEquals("last-name-1", data.getLastName());
    }

    @Test
    public void unsupportedInstitutionIsReportedWithAContactEmail() throws UnsupportedEncodingException {
        Map<String, String> headers = new HashMap<>();
        headers.put(InstitutionalSignInManager.SHIB_IDENTITY_PROVIDER_HEADER, "idp-entity-id");
        // No remote-user header at all, so retrieveRemoteUser returns null.
        when(identityProviderManager.retrieveContactEmailByProviderid("idp-entity-id")).thenReturn("help@institution.edu");
        shibbolethController.setShibbolethEnabled(true);

        OAuthSigninData data = shibbolethController.getSigninData(headers);

        assertEquals("idp-entity-id", data.getProviderId());
        assertTrue(data.isUnsupportedInstitution());
        assertEquals("help@institution.edu", data.getInstitutionContactEmail());
        assertNull(data.getFirstName());
    }

    @Test(expected = FeatureDisabledException.class)
    public void getSigninDataIsRefusedWhenShibbolethIsDisabled() throws UnsupportedEncodingException {
        shibbolethController.setShibbolethEnabled(false);
        shibbolethController.getSigninData(new HashMap<String, String>());
    }
}
