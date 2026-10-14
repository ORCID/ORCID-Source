package org.orcid.frontend.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.authorization.authentication.MFAWebAuthenticationDetails;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.ProfileEntityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.security.OrcidUserDetailsService;
import org.orcid.core.security.OrcidRoles;
import org.orcid.core.togglz.Features;
import org.orcid.frontend.web.exception.PasswordResetRequiredException;
import org.orcid.frontend.web.exception.VerificationCodeFor2FARequiredException;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.togglz.junit.TogglzRule;

public class OrcidAuthenticationProviderTest {

    private static final String ORCID = "0000-0000-0000-0001";

    private static final String PASSWORD = "password";

    @Rule
    public TogglzRule togglzRule = TogglzRule.allDisabled(Features.class);

    @Mock
    PasswordEncoder passwordEncoderMock;

    @Mock
    UserDetailsService userDetailsServiceMock;

    @Mock
    ProfileEntityManager profileEntityManagerMock;

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
        TargetProxyHelper.injectIntoProxy(orcidAuthenticationProvider, "profileEntityManager", profileEntityManagerMock);
        TargetProxyHelper.injectIntoProxy(orcidAuthenticationProvider, "lockoutWindow", 10);
        TargetProxyHelper.injectIntoProxy(orcidAuthenticationProvider, "lockoutThreshhold", 10);
        orcidAuthenticationProvider.setPasswordEncoder(passwordEncoderMock);
        orcidAuthenticationProvider.setUserDetailsService(userDetailsServiceMock);
        orcidAuthenticationProvider.setAuthoritiesMapper(grantedAuthoritiesMapperMock);
        when(passwordEncoderMock.matches(PASSWORD, "encrypted")).thenReturn(true);
        when(userDetailsServiceMock.loadUserByUsername(anyString())).thenAnswer(invocation -> new User((String) invocation.getArgument(0), "encrypted", List.of()));
        
        when(userCacheMock.getUserFromCache(anyString())).then(new Answer<UserDetails>(){

            @Override
            public UserDetails answer(InvocationOnMock invocation) throws Throwable {                
                return new User((String) invocation.getArgument(0), "encrypted", List.of());
            }
            
        });
        
        when(grantedAuthoritiesMapperMock.mapAuthorities(any())).thenAnswer(new Answer<HashSet<GrantedAuthority>>(){

            @Override
            public HashSet<GrantedAuthority> answer(InvocationOnMock invocation) throws Throwable {
                HashSet<GrantedAuthority> mapped = new HashSet<GrantedAuthority>();
                mapped.add(new SimpleGrantedAuthority(OrcidRoles.ROLE_USER.name()));
                return mapped;
            }
            
        });
        
        when(orcidUserDetailsServiceMock.loadUserByProfile(any())).thenAnswer(new Answer<UserDetails>() {

            @Override
            public UserDetails answer(InvocationOnMock invocation) throws Throwable {
                ProfileEntity p = (ProfileEntity) invocation.getArgument(0);
                return new User(p.getId(), p.getEncryptedPassword(), List.of());
            }
            
        });
    }
    
    @Test
    public void authenticatesAnUnflaggedRecordWithTheFeatureOn() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        profile(false);

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) orcidAuthenticationProvider.authenticate(signIn(PASSWORD, null));

        assertNotNull(token);
        assertEquals(ORCID, ((User) token.getPrincipal()).getUsername());
        verify(profileEntityManagerMock).isPasswordResetRequired(ORCID);
    }

    @Test
    public void refusesACorrectPasswordOnAFlaggedRecordWithTheFeatureOn() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        // The cached entity carries no date: the gate must ask the manager, which reads the primary
        profile(false);
        when(profileEntityManagerMock.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertThrows(PasswordResetRequiredException.class, () -> orcidAuthenticationProvider.authenticate(signIn(PASSWORD, null)));

        // A correct password is not a failed attempt, and no authentication is built
        verify(profileEntityManagerMock, never()).updateSigninLock(anyString(), anyInt());
        verify(profileEntityManagerMock, never()).startSigninLock(anyString());
        verify(orcidUserDetailsServiceMock, never()).loadUserByProfile(any());
    }

    @Test
    public void aCorrectPasswordOnAFlaggedRecordIsNotAFailedAttemptWithTheLockoutOn() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        togglzRule.enable(Features.ENABLE_ACCOUNT_LOCKOUT);
        // Two earlier failures, and no lock yet
        ProfileEntity p = profile(false);
        p.setSigninLockCount(2);
        when(profileEntityManagerMock.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertThrows(PasswordResetRequiredException.class, () -> orcidAuthenticationProvider.authenticate(signIn(PASSWORD, null)));

        // Cleared, as a correct password clears it for any record, and nothing is counted
        verify(profileEntityManagerMock).resetSigninLock(ORCID);
        verify(profileEntityManagerMock, never()).updateSigninLock(anyString(), anyInt());
        verify(profileEntityManagerMock, never()).startSigninLock(anyString());
    }

    @Test
    public void authenticatesAFlaggedRecordWithTheFeatureOff() {
        profile(false);
        when(profileEntityManagerMock.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertNotNull(orcidAuthenticationProvider.authenticate(signIn(PASSWORD, null)));
        verify(profileEntityManagerMock, never()).isPasswordResetRequired(anyString());
    }

    @Test
    public void refusesAFlaggedRecordBeforeAskingForA2FACode() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        profile(true);
        when(profileEntityManagerMock.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertThrows(PasswordResetRequiredException.class, () -> orcidAuthenticationProvider.authenticate(signIn(PASSWORD, null)));
    }

    @Test
    public void asksAnUnflagged2FARecordForItsCode() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        profile(true);

        assertThrows(VerificationCodeFor2FARequiredException.class, () -> orcidAuthenticationProvider.authenticate(signIn(PASSWORD, null)));
    }

    @Test
    public void aWrongPasswordOnAFlaggedRecordIsBadCredentials() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        profile(false);
        when(profileEntityManagerMock.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> orcidAuthenticationProvider.authenticate(signIn("wrong", null)));
        verify(profileEntityManagerMock, never()).isPasswordResetRequired(anyString());
    }

    @Test
    public void aLockedOutFlaggedRecordGetsBadCredentialsBeforeTheResetCheck() {
        togglzRule.enable(Features.FORCE_PASSWORD_RESET);
        ProfileEntity p = profile(false);
        p.setSigninLockCount(12);
        p.setSigninLockStart(new Date());
        when(profileEntityManagerMock.isPasswordResetRequired(ORCID)).thenReturn(true);

        assertThrows(BadCredentialsException.class, () -> orcidAuthenticationProvider.authenticate(signIn(PASSWORD, null)));
        verify(profileEntityManagerMock, never()).isPasswordResetRequired(anyString());
    }

    private ProfileEntity profile(boolean using2FA) {
        ProfileEntity p = new ProfileEntity(ORCID);
        p.setUsing2FA(using2FA);
        p.setEncryptedPassword("encrypted");
        when(profileEntityCacheManagerMock.retrieve(ORCID)).thenReturn(p);
        return p;
    }

    private UsernamePasswordAuthenticationToken signIn(String password, String verificationCode) {
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(ORCID, password);
        authRequest.setDetails(new MFAWebAuthenticationDetails("127.0.0.1", null, verificationCode, null));
        return authRequest;
    }

    /*
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
    }*/
}
