package org.orcid.core.utils;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.*;

import org.orcid.core.oauth.OrcidBearerTokenAuthentication;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

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
        OrcidBearerTokenAuthentication mockedAuthentication = mock(OrcidBearerTokenAuthentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        when(mockedAuthentication.getPrincipal()).thenReturn(clientId);
        when(mockedAuthentication.getName()).thenReturn(clientId);
        when(mockedAuthentication.getClientId()).thenReturn(clientId);
        when(mockedAuthentication.getUserOrcid()).thenReturn(userOrcid);
        Set<String> scopes = new HashSet<String>();
        if (scopePathTypes != null) {
            for (ScopePathType scopePathType : scopePathTypes) {
                scopes.add(scopePathType.value());
            }
        }
        when(mockedAuthentication.getScopes()).thenReturn(scopes);
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
        OrcidBearerTokenAuthentication mockedAuthentication = mock(OrcidBearerTokenAuthentication.class);
        securityContext.setAuthentication(mockedAuthentication);
        SecurityContextHolder.setContext(securityContext);
        when(mockedAuthentication.getPrincipal()).thenReturn(clientId);
        when(mockedAuthentication.getName()).thenReturn(clientId);
        when(mockedAuthentication.getClientId()).thenReturn(clientId);
        when(mockedAuthentication.getScopes()).thenReturn(scopes);
    }

    static public void setUpSecurityContextForAnonymous() {
        SecurityContextImpl securityContext = new SecurityContextImpl();
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
        AnonymousAuthenticationToken anonToken = new AnonymousAuthenticationToken("testKey", "testToken", authorities);
        securityContext.setAuthentication(anonToken);
        SecurityContextHolder.setContext(securityContext);
    }

    static public void setupSecurityContextForWebUser(String userId, String email) {
        UserDetails details = new User(userId, email, List.of());
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, "password");
        auth.setDetails(details);
        SecurityContextImpl securityContext = new SecurityContextImpl();
        securityContext.setAuthentication(auth);
        SecurityContextHolder.setContext(securityContext);
    }

    static public void setUpSecurityContextForGroupIdClientOnly() {
        Set<String> scopes = new HashSet<String>();
        scopes.add(ScopePathType.GROUP_ID_RECORD_READ.value());
        scopes.add(ScopePathType.GROUP_ID_RECORD_UPDATE.value());
        setUpSecurityContextForClientOnly("APP-5555555555555555", scopes);
    }
}