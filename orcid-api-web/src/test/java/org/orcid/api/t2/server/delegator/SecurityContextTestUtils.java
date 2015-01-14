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
package org.orcid.api.t2.server.delegator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.provider.AuthorizationRequest;

public class SecurityContextTestUtils {

    static public void setUpSecurityContext() {
        setUpSecurityContext(ScopePathType.ORCID_WORKS_CREATE);
    }

    static public void setUpSecurityContext(ScopePathType... scopePathTypes) {
        setUpSecurityContext("4444-4444-4444-4441", scopePathTypes);
    }

    static public void setUpSecurityContext(String userOrcid, ScopePathType... scopePathTypes) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        OrcidOAuth2Authentication mockedAuthentication = mock(OrcidOAuth2Authentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        when(mockedAuthentication.getPrincipal()).thenReturn(new ProfileEntity(userOrcid));
        AuthorizationRequest authorizationRequest = mock(AuthorizationRequest.class);
        Set<String> scopes = new HashSet<String>();
        for (ScopePathType scopePathType : scopePathTypes) {
            scopes.add(scopePathType.value());
        }
        when(authorizationRequest.getClientId()).thenReturn("APP-5555555555555555");
        when(authorizationRequest.getScope()).thenReturn(scopes);
        when(mockedAuthentication.getAuthorizationRequest()).thenReturn(authorizationRequest);
    }

    static public void setUpSecurityContextForClientOnly() {
        setUpSecurityContextForClientOnly("APP-5555555555555555");
    }

    static public void setUpSecurityContextForClientOnly(String clientId) {
        Set<String> scopes = new HashSet<String>();
        scopes.add(ScopePathType.ORCID_PROFILE_CREATE.value());
        setUpSecurityContextForClientOnly(clientId, scopes);
    }

    static public void setUpSecurityContextForClientOnly(String clientId, ScopePathType... scopePathTypes) {
        Set<String> scopes = new HashSet<String>();
        for (ScopePathType scope : scopePathTypes) {
            scopes.add(scope.value());
        }
        setUpSecurityContextForClientOnly(clientId, scopes);
    }

    static public void setUpSecurityContextForClientOnly(String clientId, Set<String> scopes) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        OrcidOAuth2Authentication mockedAuthentication = mock(OrcidOAuth2Authentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        when(mockedAuthentication.getPrincipal()).thenReturn(new ProfileEntity(clientId));
        when(mockedAuthentication.isClientOnly()).thenReturn(true);
        AuthorizationRequest authorizationRequest = mock(AuthorizationRequest.class);
        when(authorizationRequest.getClientId()).thenReturn(clientId);
        when(authorizationRequest.getScope()).thenReturn(scopes);
        when(mockedAuthentication.getAuthorizationRequest()).thenReturn(authorizationRequest);
    }

}
