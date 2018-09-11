package org.orcid.frontend.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.OrcidWebRole;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;

public class OrcidAuthenticationProviderTest {

    @Mock
    UserCache userCacheMock;
    
    @Mock
    UserDetailsChecker userDetailsCheckerMock;
    
    @Mock
    GrantedAuthoritiesMapper grantedAuthoritiesMapperMock;
    
    @Mock
    ProfileEntityCacheManager profileEntityCacheManagerMock;
    
    @Mock
    EmailManagerReadOnly emailManagerReadOnlyMock;
    
    @Mock
    OrcidUserDetailsService orcidUserDetailsServiceMock;
    
    OrcidAuthenticationProvider orcidAuthenticationProvider = new OrcidAuthenticationProvider();
    
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        orcidAuthenticationProvider.setUserCache(userCacheMock);
        orcidAuthenticationProvider.setPreAuthenticationChecks(userDetailsCheckerMock);
        orcidAuthenticationProvider.setPostAuthenticationChecks(userDetailsCheckerMock);
        
        TargetProxyHelper.injectIntoProxy(orcidAuthenticationProvider, "profileEntityCacheManager", profileEntityCacheManagerMock);
        TargetProxyHelper.injectIntoProxy(orcidAuthenticationProvider, "emailManagerReadOnly", emailManagerReadOnlyMock);
        TargetProxyHelper.injectIntoProxy(orcidAuthenticationProvider, "orcidUserDetailsService", orcidUserDetailsServiceMock);
        
        when(userCacheMock.getUserFromCache(anyString())).then(new Answer<UserDetails>(){

            @Override
            public UserDetails answer(InvocationOnMock invocation) throws Throwable {                
                return new OrcidProfileUserDetails((String) invocation.getArgument(0), "user@email.com", "password");                
            }
            
        });
        
        when(grantedAuthoritiesMapperMock.mapAuthorities(any())).thenAnswer(new Answer<HashSet<GrantedAuthority>>(){

            @Override
            public HashSet<GrantedAuthority> answer(InvocationOnMock invocation) throws Throwable {
                HashSet<GrantedAuthority> mapped = new HashSet<GrantedAuthority>();
                mapped.add(OrcidWebRole.ROLE_USER);
                return mapped;
            }
            
        });
        
        when(orcidUserDetailsServiceMock.loadUserByProfile(any())).thenAnswer(new Answer<OrcidProfileUserDetails>() {

            @Override
            public OrcidProfileUserDetails answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity p = (ProfileEntity) invocation.getArgument(0);
                return new OrcidProfileUserDetails(p.getId(), "email", p.getEncryptedPassword());                
            }
            
        });
    }
    
    
    @Test
    public void authenticateOrcidTest() {
        String orcid = "0000-0000-0000-0000";
        String password = "password";
        ProfileEntity p = new ProfileEntity(orcid);
        p.setUsing2FA(false);
        p.setEncryptedPassword(password);
                
        when(profileEntityCacheManagerMock.retrieve(orcid)).thenReturn(p);
        
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(orcid, password);
        
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) orcidAuthenticationProvider.authenticate(authRequest);
        assertNotNull(token); 
        assertEquals(orcid, token.getPrincipal());
        assertEquals(password, token.getCredentials());
    }
    
    @Test
    public void authenticateEmailTest() {
        String email = "email@email.com";
        String orcid = "0000-0000-0000-0000";
        String password = "password";
        ProfileEntity p = new ProfileEntity(orcid);
        p.setUsing2FA(false);
        p.setEncryptedPassword(password);
        
        when(emailManagerReadOnlyMock.findOrcidIdByEmail(email)).thenReturn(orcid);
        when(profileEntityCacheManagerMock.retrieve(orcid)).thenReturn(p);
        
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(orcid, password);
        
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) orcidAuthenticationProvider.authenticate(authRequest);
        assertNotNull(token); 
        assertEquals(orcid, token.getPrincipal());
        assertEquals(password, token.getCredentials());
    }
}
