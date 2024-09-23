package org.orcid.core.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Date;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.orcid.jaxb.model.common_v2.OrcidType;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;

public class OrcidUserDetailsServiceTest {

    OrcidUserDetailsServiceImpl service = new OrcidUserDetailsServiceImpl();

    @Mock
    private ProfileDao profileDao;

    @Mock
    private EmailDao emailDao;
    
    @Mock
    protected EmailManagerReadOnly emailManagerReadOnly;
    
    @Mock
    private OrcidSecurityManager securityMgr;

    private static final String ORCID = "0000-0000-0000-0000";
    private static final String EMAIL = "email@test.orcid.org";

    private static EmailEntity email;
    private static ProfileEntity profile;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(service, "profileDao", profileDao);
        TargetProxyHelper.injectIntoProxy(service, "emailDao", emailDao);
        TargetProxyHelper.injectIntoProxy(service, "securityMgr", securityMgr);
        TargetProxyHelper.injectIntoProxy(service, "emailManagerReadOnly", emailManagerReadOnly);
        
        when(profileDao.find(anyString())).thenReturn(null);
        when(profileDao.find(ORCID)).thenReturn(getProfileEntity());
        
        EmailEntity emailEntity = new EmailEntity();
        emailEntity.setEmail(EMAIL);
        emailEntity.setOrcid(ORCID);
        
        Email email = new Email();
        email.setEmail(EMAIL);
        
        when(emailManagerReadOnly.findOrcidIdByEmail(anyString())).thenReturn(null);
        when(emailManagerReadOnly.findOrcidIdByEmail(EMAIL)).thenReturn(ORCID);
        when(emailManagerReadOnly.findPrimaryEmail(ORCID)).thenReturn(email);   
        
        when(emailDao.findByEmail(anyString())).thenReturn(null);
        when(emailDao.findByEmail(EMAIL)).thenReturn(getEmailEntity(ORCID));
        when(emailDao.findPrimaryEmail(ORCID)).thenReturn(getEmailEntity(ORCID));
    }

    @Test
    public void loadUserByUsername_EmailTest() {
        UserDetails details = service.loadUserByUsername(ORCID);
        assertNotNull(details);
        assertEquals(1, details.getAuthorities().size());
        assertTrue(details.getAuthorities().contains(OrcidWebRole.ROLE_USER));
        assertEquals(ORCID, details.getUsername());
        assertEquals("PWD", details.getPassword());
    }

    @Test
    public void loadUserByUsername_OrcidIdTest() {
        UserDetails details = service.loadUserByUsername(EMAIL);
        assertNotNull(details);
        assertEquals(1, details.getAuthorities().size());
        assertTrue(details.getAuthorities().contains(OrcidWebRole.ROLE_USER));
        assertEquals(ORCID, details.getUsername());
        assertEquals("PWD", details.getPassword());
    }

    @Test
    public void loadUserByUsername_ClientTest() {
        profile.setOrcidType(OrcidType.CLIENT.name());
        try {
            service.loadUserByUsername(ORCID);
            fail();
        } catch (InvalidUserTypeException e) {

        }
        profile.setOrcidType(OrcidType.USER.name());
    }

    @Test
    public void loadUserByUsername_UncalimedTest() {
        profile.setClaimed(false);
        try {
            service.loadUserByUsername(ORCID);
            fail();
        } catch (UnclaimedProfileExistsException e) {

        }
        profile.setClaimed(true);
    }

    @Test
    public void loadUserByUsername_DeprecatedTest() {
        profile.setPrimaryRecord(new ProfileEntity());
        try {
            service.loadUserByUsername(ORCID);
            fail();
        } catch (DeprecatedProfileException e) {

        }
        profile.setPrimaryRecord(null);
    }

    @Test
    public void loadUserByUsername_DeactivatedTest() {
        profile.setDeactivationDate(new Date());
        try {
            service.loadUserByUsername(ORCID);
            fail();
        } catch (DisabledException e) {

        }
        profile.setDeactivationDate(null);
    }

    @Test
    public void loadUserByProfileTest() {
        UserDetails details = service.loadUserByProfile(getProfileEntity());
        assertNotNull(details);
        assertEquals(1, details.getAuthorities().size());
        assertTrue(details.getAuthorities().contains(OrcidWebRole.ROLE_USER));
        assertEquals(ORCID, details.getUsername());
        assertEquals("PWD", details.getPassword());
    }

    @Test
    public void loadUserByProfile_ClientTest() {
        profile.setOrcidType(OrcidType.CLIENT.name());
        try {
            service.loadUserByProfile(profile);
            fail();
        } catch (InvalidUserTypeException e) {

        }
        profile.setOrcidType(OrcidType.USER.name());
    }

    @Test
    public void loadUserByProfile_UncalimedTest() {
        profile.setClaimed(false);
        try {
            service.loadUserByProfile(profile);
            fail();
        } catch (UnclaimedProfileExistsException e) {

        }
        profile.setClaimed(true);
    }

    @Test
    public void loadUserByProfile_DeprecatedTest() {
        profile.setPrimaryRecord(new ProfileEntity());
        try {
            service.loadUserByProfile(profile);
            fail();
        } catch (DeprecatedProfileException e) {

        }
        profile.setPrimaryRecord(null);
    }

    @Test
    public void loadUserByProfile_DeactivatedTest() {
        profile.setDeactivationDate(new Date());
        try {
            service.loadUserByProfile(profile);
            fail();
        } catch (DisabledException e) {

        }
        profile.setDeactivationDate(null);
    }
    
    @Test
    public void loadUserByProfile_MoreThanOnePrimaryAvailable() {
        String email = "the_new_primary@test.orcid.org";
        when(emailDao.findPrimaryEmail(anyString())).thenThrow(NonUniqueResultException.class);
        when(emailDao.findNewestPrimaryEmail(anyString())).thenReturn(email);
        
        OrcidProfileUserDetails opud = service.loadUserByProfile(profile);
        
        Mockito.verify(emailDao, Mockito.times(1)).updatePrimary(Mockito.matches(ORCID), Mockito.matches(email));  
                        
        assertNotNull(opud);
        opud.getUsername();
        assertEquals(ORCID, opud.getUsername());
    }

    @Test
    public void loadUserByProfile_NoPrimaryAvailable() {
        String email = "the_new_primary@test.orcid.org";
        when(emailDao.findPrimaryEmail(anyString())).thenThrow(NoResultException.class);
        when(emailDao.findNewestVerifiedOrNewestEmail(anyString())).thenReturn(email);
        
        OrcidProfileUserDetails opud = service.loadUserByProfile(profile);
        
        Mockito.verify(emailDao, Mockito.times(1)).updatePrimary(Mockito.matches(ORCID), Mockito.matches(email));     
        
        assertNotNull(opud);
        opud.getUsername();
        assertEquals(ORCID, opud.getUsername());
    }
    
    private EmailEntity getEmailEntity(String orcid) {
        if (email != null) {
            return email;
        }

        email = new EmailEntity();
        email.setEmail(EMAIL);
        email.setOrcid(orcid);
        email.setVerified(true);
        return email;
    }

    private ProfileEntity getProfileEntity() {
        if (profile != null) {
            return profile;
        }
        profile = new ProfileEntity();
        profile.setId(ORCID);
        profile.setOrcidType(OrcidType.USER.name());
        profile.setClaimed(true);
        profile.setEncryptedPassword("PWD");
        return profile;
    }
}
