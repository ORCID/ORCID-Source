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
package org.orcid.frontend.spring;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CustomCsrfSecurityRequestMatcherTest {

    private CustomCsrfSecurityRequestMatcher requestMatcher = new CustomCsrfSecurityRequestMatcher();

    private final String CONTEXT_PATH = "https://test.orcid.org";

    @Mock
    private HttpServletRequest request;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(request.getContextPath()).thenReturn("https://test.orcid.org");
    }

    /**
     * All HEAD requests will bypass the CSRF check, so, they will return false
     */
    @Test
    public void matches_HEAD_Test() {
        when(request.getMethod()).thenReturn("HEAD");

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }

    /**
     * All OPTIONS requests will bypass the CSRF check, so, they will return
     * false
     */
    @Test
    public void matches_OPTIONS_Test() {
        when(request.getMethod()).thenReturn("OPTIONS");

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }

    /**
     * All TRACE requests will bypass the CSRF check, so, they will return false
     */
    @Test
    public void matches_TRACE_Test() {
        when(request.getMethod()).thenReturn("TRACE");

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }

    /**
     * All GET requests will bypass the CSRF check, so, they will return false
     */
    @Test
    public void matches_GET_Test() {
        when(request.getMethod()).thenReturn("GET");

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertFalse(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }

    /**
     * All CONNECT requests should be CSRF validated so they return true, but,
     * the /userStatus.json end point which should bypass the CSRF validation
     * and hence return false
     */
    @Test
    public void matches_CONNECT_Test() {
        when(request.getMethod()).thenReturn("CONNECT");

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertTrue(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }

    /**
     * All PATCH requests should be CSRF validated so they return true, but,
     * the /userStatus.json end point which should bypass the CSRF validation
     * and hence return false
     */
    @Test
    public void matches_PATCH_Test() {
        when(request.getMethod()).thenReturn("PATCH");

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertTrue(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }

    /**
     * All DELETE requests should be CSRF validated so they return true, but,
     * the /userStatus.json end point which should bypass the CSRF validation
     * and hence return false
     */
    @Test
    public void matches_DELETE_Test() {
        when(request.getMethod()).thenReturn("DELETE");

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertTrue(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }

    /**
     * All POST requests should be CSRF validated so they return true, but,
     * the /userStatus.json end point which should bypass the CSRF validation
     * and hence return false
     */
    @Test
    public void matches_POST_Test() {
        when(request.getMethod()).thenReturn("POST");

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertTrue(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }

    /**
     * All PUT requests should be CSRF validated so they return true, but,
     * the /userStatus.json end point which should bypass the CSRF validation
     * and hence return false
     */
    @Test
    public void matches_PUT_Test() {
        when(request.getMethod()).thenReturn("PUT");

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/signin");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/affiliations/affiliations.json?affiliationIds=xxx1,xxx2,xxx3&_=random_number");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/inbox/notification-alerts.json");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever");
        assertTrue(requestMatcher.matches(request));

        // true
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/what_ever/what_ever/what_ever/userStatus.json");
        assertTrue(requestMatcher.matches(request));

        // false
        when(request.getRequestURI()).thenReturn(CONTEXT_PATH + "/userStatus.json");
        assertFalse(requestMatcher.matches(request));
    }
}
