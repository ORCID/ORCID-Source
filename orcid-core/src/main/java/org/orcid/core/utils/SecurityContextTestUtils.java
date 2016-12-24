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
package org.orcid.core.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.orcid.core.oauth.OrcidOAuth2Authentication;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.provider.OAuth2Request;

public class SecurityContextTestUtils {

	public final static String DEFAULT_CLIENT_ID = "APP-5555555555555555"; 
	
    static public void setUpSecurityContext() {
        setUpSecurityContext(ScopePathType.ORCID_WORKS_CREATE);
    }

    static public void setUpSecurityContext(ScopePathType... scopePathTypes) {
        setUpSecurityContext("4444-4444-4444-4441", scopePathTypes);
    }
       
    static public void setUpSecurityContext(String userOrcid, ScopePathType... scopePathTypes) {
        setUpSecurityContext(userOrcid, DEFAULT_CLIENT_ID, scopePathTypes);
    }
    
    static public void setUpSecurityContext(String userOrcid, String clientId, ScopePathType... scopePathTypes) {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        OrcidOAuth2Authentication mockedAuthentication = mock(OrcidOAuth2Authentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        ProfileEntity userProfileEntity = new ProfileEntity(userOrcid);
        when(mockedAuthentication.getPrincipal()).thenReturn(userProfileEntity);
        Authentication userAuthentication = mock(Authentication.class);
        when(userAuthentication.getPrincipal()).thenReturn(userProfileEntity);
        when(mockedAuthentication.getUserAuthentication()).thenReturn(userAuthentication);
        Set<String> scopes = new HashSet<String>();
        if (scopePathTypes != null) {
            for (ScopePathType scopePathType : scopePathTypes) {
                scopes.add(scopePathType.value());
            }
        }
        OAuth2Request authorizationRequest = new OAuth2Request(Collections.<String, String> emptyMap(), clientId,
                Collections.<GrantedAuthority> emptyList(), true, scopes, Collections.<String> emptySet(), null, Collections.<String> emptySet(),
                Collections.<String, Serializable> emptyMap());
        when(mockedAuthentication.getOAuth2Request()).thenReturn(authorizationRequest);
        when(mockedAuthentication.isAuthenticated()).thenReturn(true);
    }

    static public void setUpSecurityContextForClientOnly() {
        setUpSecurityContextForClientOnly("APP-5555555555555555");
    }

    static public void setUpSecurityContextForGroupIdClientOnly() {
        Set<String> scopes = new HashSet<String>();
        scopes.add(ScopePathType.GROUP_ID_RECORD_READ.value());
        scopes.add(ScopePathType.GROUP_ID_RECORD_UPDATE.value());
        setUpSecurityContextForClientOnly("APP-5555555555555555", scopes);
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
        OAuth2Request authorizationRequest = new OAuth2Request(Collections.<String, String> emptyMap(), clientId, Collections.<GrantedAuthority> emptyList(), true,
                scopes, Collections.<String> emptySet(), null, Collections.<String> emptySet(), Collections.<String, Serializable> emptyMap());
        when(mockedAuthentication.getOAuth2Request()).thenReturn(authorizationRequest);
        when(mockedAuthentication.isAuthenticated()).thenReturn(true);
        when(mockedAuthentication.getName()).thenReturn(clientId);
    }

    static public void setUpSecurityContextForAnonymous() {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        AnonymousAuthenticationToken anonToken = new AnonymousAuthenticationToken("testKey", "testToken", authorities);
        securityContext.setAuthentication(anonToken);
        SecurityContextHolder.setContext(securityContext);
    }

    static public void clearSecurityContext() {
        SecurityContextHolder.setContext(new SecurityContextImpl());
    }

}
