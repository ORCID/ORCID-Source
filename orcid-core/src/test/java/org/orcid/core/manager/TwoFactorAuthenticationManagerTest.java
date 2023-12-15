package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.orcid.core.exception.UserAlreadyUsing2FAException;
import org.orcid.core.manager.impl.TwoFactorAuthenticationManagerImpl;
import org.orcid.core.manager.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.dao.ProfileEventDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileEventEntity;

public class TwoFactorAuthenticationManagerTest {
    
    @Mock
    private EncryptionManager encryptionManager;

    @Mock
    private EmailManagerReadOnly emailManagerReadOnly;

    @Mock
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Mock
    private BackupCodeManager backupCodeManager;
    
    @Mock
    private ProfileDao profileDao;
    
    @Mock
    private ProfileEventDao profileEventDao;
    
    @InjectMocks
    private TwoFactorAuthenticationManagerImpl twoFactorAuthenticationManager;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        doNothing().when(profileDao).disable2FA(anyString());
        doNothing().when(profileDao).enable2FA(anyString());
        doNothing().when(profileEventDao).persist(any(ProfileEventEntity.class));
        doNothing().when(backupCodeManager).removeUnusedBackupCodes(anyString());
        when(backupCodeManager.createBackupCodes(anyString())).thenReturn(List.of("some", "backup", "codes"));
    }
    
    @Test
    public void testUserUsing2FA() {
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(getNon2FAProfile());
        assertFalse(twoFactorAuthenticationManager.userUsing2FA("orcid"));
        
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(get2FAProfile());
        assertTrue(twoFactorAuthenticationManager.userUsing2FA("orcid"));
    }
    
    @Test
    public void testGetQRCode() throws UnsupportedEncodingException {
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(getNon2FAProfile());
        when(encryptionManager.encryptForInternalUse(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        doNothing().when(profileDao).update2FASecret(anyString(), anyString());
        when(emailManagerReadOnly.findPrimaryEmail(anyString())).thenReturn(getEmail());
        
        String qrCodeUrl = twoFactorAuthenticationManager.getQRCode("orcid");
        assertNotNull(qrCodeUrl);

        assertNotNull(qrCodeUrl);
        assertTrue(qrCodeUrl.contains("x@orcid.org"));
        assertTrue(qrCodeUrl.contains("otpauth://"));
    }
    
    @Test(expected = UserAlreadyUsing2FAException.class)
    public void testGetQRCodeUserAlreadyUsing2FA() {
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(get2FAProfile());
        twoFactorAuthenticationManager.getQRCode("orcid");
    }
    
    @Test
    public void testEnable2FA() {
        List<String> backupCodes = twoFactorAuthenticationManager.enable2FA("orcid");
        verify(profileDao).enable2FA(anyString());
        verify(profileEventDao).persist(any(ProfileEventEntity.class));
        assertNotNull(backupCodes);
        assertEquals(3, backupCodes.size());
    }
    
    @Test
    public void testDisable2FA() {
        twoFactorAuthenticationManager.disable2FA("orcid");
        verify(profileDao).disable2FA(anyString());
        verify(profileEventDao).persist(any(ProfileEventEntity.class));
        verify(backupCodeManager).removeUnusedBackupCodes(anyString());
    }
    
    @Test
    public void testAdminDisable2FA() {
        twoFactorAuthenticationManager.adminDisable2FA("orcid", "adminOrcid");
        verify(profileDao).disable2FA(anyString());
        verify(profileEventDao).persist(any(ProfileEventEntity.class));
        verify(backupCodeManager).removeUnusedBackupCodes(anyString());
    }
    
    @Test
    public void testGetSecret() {
        when(encryptionManager.decryptForInternalUse(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(get2FAProfile());
        assertEquals("secret", twoFactorAuthenticationManager.getSecret("orcid"));
    }
    
    @Test
    public void testVerificationCodeIsValid() throws InterruptedException {
        String secret = Base32.random();
        
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setUsing2FA(true);
        profileEntity.setSecretFor2FA(secret);
        
        when(profileEntityCacheManager.retrieve(anyString())).thenReturn(profileEntity);
        when(encryptionManager.decryptForInternalUse(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return (String) args[0];
            }
        });
        
        Totp totp = new Totp(secret);
        String code = totp.now();
        assertTrue(twoFactorAuthenticationManager.verificationCodeIsValid(code, "orcid"));
    }
    
    private ProfileEntity getNon2FAProfile() {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setUsing2FA(false);
        return profileEntity;
    }
    
    private ProfileEntity get2FAProfile() {
        ProfileEntity profileEntity = new ProfileEntity();
        profileEntity.setUsing2FA(true);
        profileEntity.setSecretFor2FA("secret");
        return profileEntity;
    }
    
    private Email getEmail() {
        Email email = new Email();
        email.setEmail("x@orcid.org");
        return email;
    }

}
