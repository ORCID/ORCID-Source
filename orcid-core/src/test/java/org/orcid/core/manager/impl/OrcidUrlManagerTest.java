package org.orcid.core.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.core.constants.OrcidOauth2Constants;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidUrlManagerTest extends BaseTest {

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Test
    public void testDetermineFullTargetUrlFromSavedRequest() throws URISyntaxException {
        // Saved urls that are OK to use
        checkSame("https://orcid.org/my-orcid");
        checkSame("https://orcid.org/account");
        checkSame("https://orcid.org/account?newlogin");
        checkSame("https://orcid.org/account/confirm-deactivate-orcid/AnYR4Nd0MStrIN6YVjZ6ZXV6ejX0Iu8jXklrv24PLoXfd");
        checkSame("https://orcid.org/account/confirm-deactivate-orcid/AnYR4Nd0MStrIN6YVjZ6ZXV6ejX0Iu8jXklrv24PLoXfd?lang=en");
        checkSame("https://orcid.org/account/confirm-deactivate-orcid/clpkT3kwbVpwY2pDM1VYVjZ6ZXV6ejBjMEliVTFNQ1Q0WDRJZDJ4TTBhanVUa2U3VWdubjllTnpwckZYUk0vWQ?lang=en");
        checkSame(
                "https://orcid.org/oauth/authorize?client_id=APP-5AYWFGEWVKRWQFS3&response_type=code&scope=/orcid-profile/read-limited&redirect_uri=http://localhost:8080/orcid-web/oauth/playground");
        checkSame("https://orcid.org/verify-email/a1VGWGpmdTlPdjBHbCtCNHIxUkhST3NPUUpRQ3Q2QXpMTTVIVVl0YnFseE1OZHNLQXg2SFFRUDVHOHZMZTZRLw?lang=en");
        // Saved urls to ignore
        assertNull(determineTargetUrl("https://orcid.org/blank.gif"));
        assertNull(determineTargetUrl("https://orcid.org/oauth/custom/login.json"));
        assertNull(determineTargetUrl("https://orcid.org/shibboleth/signin/auth.json"));
        assertNull(determineTargetUrl("https://orcid.org/account/confirm-deactivate-orcid/AnYR4Nd0MStrIN6YVjZ6ZXV6ejX0Iu8jXklrv24PLoXfd/"));
        assertNull(determineTargetUrl("https://orcid.org/account/confirm-deactivate-orcid/AnYR4Nd0MStrIN6YVjZ6ZXV6ejX0Iu8jXklrv24PLoXfd/other"));
        assertNull(determineTargetUrl("https://orcid.org/account/confirm-deactivate-orcid/AnYR4Nd0MStrIN6YVjZ6ZXV6ejX0Iu8jXklrv24PLoXfd/other/1"));
    }

    @Test
    public void checkOauthRequestFirst() throws URISyntaxException {
        Pair<HttpServletRequest, HttpServletResponse> pair = setUpSavedRequest("https://orcid.org/my-orcid", true); 
        String redirectUri = orcidUrlManager.determineFullTargetUrlFromSavedRequest(pair.getLeft(), pair.getRight());
        assertEquals("https://orcid.org/originalOauthUrl", redirectUri);
        redirectUri = orcidUrlManager.determineFullTargetUrlFromSavedRequest(pair.getLeft(), pair.getRight());
        assertEquals("https://orcid.org/my-orcid", redirectUri);        
    }
    
    private void checkSame(String savedUrl) throws URISyntaxException {
        assertEquals(savedUrl, determineTargetUrl(savedUrl));
    }

    private String determineTargetUrl(String savedUrl) throws URISyntaxException {
        Pair<HttpServletRequest, HttpServletResponse> pair = setUpSavedRequest(savedUrl, false);
        return orcidUrlManager.determineFullTargetUrlFromSavedRequest(pair.getLeft(), pair.getRight());
    }

    private Pair<HttpServletRequest, HttpServletResponse> setUpSavedRequest(String savedUrl, boolean setOauthRequest) throws URISyntaxException {
        URI uri = new URI(savedUrl);
        MockHttpServletRequest savedRequest = new MockHttpServletRequest("GET", uri.getPath());
        savedRequest.setScheme(uri.getScheme());
        savedRequest.setServerName(uri.getHost());
        savedRequest.setQueryString(uri.getQuery());
        MockHttpServletResponse savedResponse = new MockHttpServletResponse();
        HttpSessionRequestCache sessionCache = new HttpSessionRequestCache();
        sessionCache.saveRequest(savedRequest, savedResponse);

        MockHttpServletRequest currentRequest = new MockHttpServletRequest();
        currentRequest.setSession(savedRequest.getSession());
        if(setOauthRequest) {
            currentRequest.setParameter("oauthRequest", String.valueOf(true));
            currentRequest.getSession().setAttribute(OrcidOauth2Constants.ORIGINAL_OAUTH_URL, "https://orcid.org/originalOauthUrl");
        }
        MockHttpServletResponse currentResponse = new MockHttpServletResponse();
        return new ImmutablePair<>(currentRequest, currentResponse);
    }      
}
