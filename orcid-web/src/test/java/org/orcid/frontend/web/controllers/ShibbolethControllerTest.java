package org.orcid.frontend.web.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.InstitutionalSignInManager;
import org.orcid.pojo.OAuthSigninData;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml", "classpath:orcid-persistence-context.xml",
        "classpath:statistics-core-context.xml" })
public class ShibbolethControllerTest {

    @Resource(name = "shibbolethController")
    ShibbolethController shibbolethController;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

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

}
