package org.orcid.core.oauth;

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
import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response.Status;

import org.junit.Test;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.orcid.core.security.aop.LockedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;

public class OAuthErrorUtilsTest {

    @Test
    public void testGetOAuthErrorForLockedException() {
        OAuthError error = OAuthErrorUtils.getOAuthError(new LockedException("message here"));
        assertEquals(OAuthError.UNAUTHORIZED_CLIENT, error.getError());
        assertEquals(Status.BAD_REQUEST, error.getResponseStatus());
        assertEquals("message here", error.getErrorDescription());
    }
    
    @Test
    public void testGetOAuthErrorForUnsupportedGrantTypeException() {
        OAuthError error = OAuthErrorUtils.getOAuthError(new UnsupportedGrantTypeException("message here"));
        assertEquals(OAuthError.UNSUPPORTED_GRANT_TYPE, error.getError());
        assertEquals(Status.BAD_REQUEST, error.getResponseStatus());
        assertEquals("message here", error.getErrorDescription());
    }
    
    @Test
    public void testGetOAuthErrorForOrcidInvalidScopeException() {
        OAuthError error = OAuthErrorUtils.getOAuthError(new OrcidInvalidScopeException("message here"));
        assertEquals(OAuthError.INVALID_SCOPE, error.getError());
        assertEquals(Status.BAD_REQUEST, error.getResponseStatus());
        assertEquals("message here", error.getErrorDescription());
    }
    
    @Test
    public void testGetOAuthErrorForInsufficientAuthenticationException() {
        OAuthError error = OAuthErrorUtils.getOAuthError(new InsufficientAuthenticationException("message here"));
        assertEquals(OAuthError.UNAUTHORIZED_CLIENT, error.getError());
        assertEquals(Status.UNAUTHORIZED, error.getResponseStatus());
        assertEquals("message here", error.getErrorDescription());
    }
    
    @Test
    public void testGetOAuthErrorForIllegalArgumentException() {
        OAuthError error = OAuthErrorUtils.getOAuthError(new IllegalArgumentException("message here"));
        assertEquals(OAuthError.INVALID_REQUEST, error.getError());
        assertEquals(Status.BAD_REQUEST, error.getResponseStatus());
        assertEquals("message here", error.getErrorDescription());
    }
    
    @Test
    public void testGetOAuthErrorForInternalServerError() {
        OAuthError error = OAuthErrorUtils.getOAuthError(new RuntimeException("message here"));
        assertEquals(OAuthError.SERVER_ERROR, error.getError());
        assertEquals(Status.INTERNAL_SERVER_ERROR, error.getResponseStatus());
        assertEquals("message here", error.getErrorDescription());
    }
    
    @Test
    public void testGetOAuthErrorForDeactivatedException() {
        OAuthError error = OAuthErrorUtils.getOAuthError(new DeactivatedException("message here"));
        assertEquals(OAuthError.UNAUTHORIZED_CLIENT, error.getError());
        assertEquals(Status.BAD_REQUEST, error.getResponseStatus());
        assertEquals("message here", error.getErrorDescription());
    }
    
    @Test
    public void testGetOAuthErrorForOrcidDeprecatedException() {
        OAuthError error = OAuthErrorUtils.getOAuthError(new OrcidDeprecatedException());
        assertEquals(OAuthError.UNAUTHORIZED_CLIENT, error.getError());
        assertEquals(Status.BAD_REQUEST, error.getResponseStatus());
    }
}
