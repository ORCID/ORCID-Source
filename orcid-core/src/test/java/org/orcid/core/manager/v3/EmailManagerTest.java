package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.when;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.EncryptionManager;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;
import org.orcid.utils.OrcidStringUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailManagerTest extends BaseTest {
    private static final String ORCID = "0000-0000-0000-0003";
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");

    @Resource(name = "emailManagerV3")
    private EmailManager emailManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private EmailDao emailDao;    
    
    @Resource(name = "encryptionManager")
    private EncryptionManager encryptionManager;
    
    @Mock
    private SourceManager mockSourceManager;
    
    @Mock
    private EmailDao mockEmailDao;
    
    @Before
    public void before() throws JAXBException {
        MockitoAnnotations.initMocks(this);
        TargetProxyHelper.injectIntoProxy(emailManager, "sourceManager", mockSourceManager);
        SourceEntity source = new SourceEntity();
        source.setSourceProfile(new ProfileEntity(ORCID));
        when(mockSourceManager.retrieveActiveSourceEntity()).thenReturn(source);
        //Set the default manager and dao
        ReflectionTestUtils.setField(emailManager, "emailDao", emailDao);
    }
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @After
    public void after() throws JAXBException {
        ReflectionTestUtils.setField(emailManager, "sourceManager", sourceManager);
        ReflectionTestUtils.setField(emailManager, "emailDao", emailDao);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }        
    
    @Test(expected = IllegalArgumentException.class)
    public void removeEmailTest() {
        assertTrue(emailManager.emailExists("billie@holiday.com"));
        emailManager.removeEmail("4444-4444-4444-4446", "billie@holiday.com");
        //Now it should be gone
        assertFalse(emailManager.emailExists("billie@holiday.com"));
    }
    
    @Test
    public void moveEmailToOtherAccountTest() {
        String email = "public@email.com";
        String from = "4444-4444-4444-4441";        
        String to = "4444-4444-4444-4499";
        
        ProfileEntity destinationBefore = profileDao.find(to);
        Date beforeLastModified = destinationBefore.getLastModified();
        
        Map<String, String> map = emailManager.findOricdIdsByCommaSeparatedEmails(email);
        assertNotNull(map);
        assertEquals(from, map.get(email));
        emailManager.moveEmailToOtherAccount(email, from, to);
        
        ProfileEntity destinationAfter = profileDao.find(to);
        Date afterLastModified = destinationAfter.getLastModified();
        
        assertFalse(beforeLastModified.equals(afterLastModified));
        assertTrue(afterLastModified.getTime() > beforeLastModified.getTime());
        
        //Assert the email was moved
        map = emailManager.findOricdIdsByCommaSeparatedEmails(email);
        assertNotNull(map);
        assertEquals(to, map.get(email));
        
        //Assert the email is not anymore in the from record
        Emails emails = emailManager.getEmails(from);
        for(Email e : emails.getEmails()) {
            assertFalse(email.equals(e.getEmail()));        
        }
        
        //Assert the email belongs to the to record
        emails = emailManager.getEmails(to);
        boolean found = false;
        for(Email e : emails.getEmails()) {
            if(email.equals(e.getEmail())) {
                found = true;
            }        
        }
        
        assertTrue(found);
    }
    
    @Test
    public void verifySetCurrentAndPrimaryTest() {
        String email = "public_0000-0000-0000-0004@test.orcid.org";
        String orcid = "0000-0000-0000-0004";
        Emails emails = emailManager.getEmails(orcid);
        Email element = null;
        for(Email e : emails.getEmails()) {
            if(email.equals(e.getEmail())) {
                element = e;
                break;
            }
        }
        
        assertNotNull(element);
        assertFalse(element.isCurrent());
        assertFalse(element.isPrimary());
        assertFalse(element.isVerified());
        XMLGregorianCalendar dateCreated = element.getCreatedDate().getValue();
        XMLGregorianCalendar lastModified = element.getLastModifiedDate().getValue();
        
        emailManager.verifySetCurrentAndPrimary(orcid, email);
        
        emails = emailManager.getEmails(orcid);
        element = null;
        for(Email e : emails.getEmails()) {
            if(email.equals(e.getEmail())) {
                element = e;
                break;
            }
        }
        
        assertNotNull(element);
        assertTrue(element.isCurrent());
        assertTrue(element.isPrimary());
        assertTrue(element.isVerified());

        XMLGregorianCalendar newDateCreated = element.getCreatedDate().getValue();
        XMLGregorianCalendar newLastModified = element.getLastModifiedDate().getValue();
        assertEquals(dateCreated, newDateCreated);
        assertTrue(newLastModified.getMillisecond() > lastModified.getMillisecond());
    }
    
    @Test
    public void verifyPrimaryEmailTest() {
        assertFalse(emailManager.isPrimaryEmailVerified("0000-0000-0000-0004"));
        emailManager.verifyPrimaryEmail("0000-0000-0000-0004");
        assertTrue(emailManager.isPrimaryEmailVerified("0000-0000-0000-0004"));
    }
    
    @Test
    public void addEmailTest() throws NoSuchAlgorithmException {
        TargetProxyHelper.injectIntoProxy(emailManager, "emailDao", mockEmailDao);
        String emailAddress = "TeSt@email.com";
        
        Email email = new Email();
        email.setEmail(emailAddress);
        email.setPrimary(false);
        email.setVisibility(Visibility.PUBLIC);
        
        emailManager.addEmail(ORCID, email);
        
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockEmailDao).addEmail(eq(ORCID), eq(emailAddress), captor.capture(), eq(Visibility.PUBLIC.name()), eq(ORCID), isNull());
        String hashValue = captor.getValue();
        
        assertNotEquals(hashValue, encryptionManager.sha256Hash(emailAddress));
        assertNotEquals(hashValue, encryptionManager.sha256Hash("   " + emailAddress + "   "));
        assertNotEquals(hashValue, encryptionManager.sha256Hash(("   " + emailAddress + "   ").toLowerCase()));
        assertEquals(hashValue, encryptionManager.sha256Hash(("   " + emailAddress + "   ").trim().toLowerCase()));
        assertEquals(hashValue, encryptionManager.getEmailHash(emailAddress));
        assertEquals(hashValue, encryptionManager.getEmailHash("   " + emailAddress + "   "));
        assertEquals(hashValue, encryptionManager.getEmailHash("test@email.com"));
        assertEquals(hashValue, encryptionManager.getEmailHash("TEST@EMAIL.COM"));
        assertEquals(hashValue, encryptionManager.getEmailHash("tEsT@EmAiL.CoM"));       
    }
    
    @Test
    public void reactivateOrCreateTest() {
        String otherOrcid = "0000-0000-0000-0002";
        String orcid = "0000-0000-0000-0003";
        String email = "pUbLiC_0000-0000-0000-0003@test.orcid.org";
        String hash = encryptionManager.getEmailHash(email);
        EmailEntity e = new EmailEntity();
        e.setEmail(email);
        e.setId(hash);
        e.setOrcid("0000-0000-0000-0003");
        TargetProxyHelper.injectIntoProxy(emailManager, "emailDao", mockEmailDao);
        
        // Test merging
        when(mockEmailDao.find(hash)).thenReturn(e);
        emailManager.reactivateOrCreate(orcid, email, Visibility.PUBLIC);
        
        ArgumentCaptor<EmailEntity> captor = ArgumentCaptor.forClass(EmailEntity.class);
        Mockito.verify(mockEmailDao).merge(captor.capture());
        
        EmailEntity merged = captor.getValue();
        assertNotNull(merged);
        assertFalse(merged.getPrimary());
        assertEquals(orcid, merged.getOrcid());
        assertEquals(email, merged.getEmail());
        assertEquals(hash, merged.getId());
        assertEquals(Visibility.PUBLIC.name(), merged.getVisibility());
        
        // Test creating
        String newEmail = "NEW@email.com";
        emailManager.reactivateOrCreate(orcid, newEmail, Visibility.PUBLIC);
        Mockito.verify(mockEmailDao).addEmail(eq(orcid), eq(newEmail), eq(encryptionManager.getEmailHash(newEmail)), eq(Visibility.PUBLIC.name()), eq(orcid), eq(null));
        
        // Test belong to other record
        try {
            emailManager.reactivateOrCreate(otherOrcid, email, Visibility.PUBLIC);
            fail();
        } catch (IllegalArgumentException iae) {
            
        }
    }
    
    @Test
    public void testEditPrimaryEmail() throws IllegalAccessException {
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);
        
        EmailEntity primaryEmailEntity = new EmailEntity();
        primaryEmailEntity.setEmail("original");
        primaryEmailEntity.setPrimary(Boolean.TRUE);
        primaryEmailEntity.setVerified(Boolean.TRUE);
        primaryEmailEntity.setVisibility("PRIVATE");
        primaryEmailEntity.setId("some-email-hash");        
        
        Mockito.when(mockEmailDao.findByEmail(Mockito.eq("original"))).thenReturn(primaryEmailEntity);
        
        emailManager.editEmail("orcid", "original", "edited", new MockHttpServletRequest());
        ArgumentCaptor<EmailEntity> captor = ArgumentCaptor.forClass(EmailEntity.class);
        Mockito.verify(mockEmailDao).persist(captor.capture());
        Mockito.verify(mockEmailDao).remove(Mockito.eq("some-email-hash"));
        
        EmailEntity mergedEntity = captor.getValue();
        assertEquals("edited", mergedEntity.getEmail());
        assertTrue(mergedEntity.getPrimary());
        assertFalse(mergedEntity.getVerified());
        assertEquals("PRIVATE", mergedEntity.getVisibility());
    }
    
    @Test
    public void testEditSecondaryEmail() throws IllegalAccessException {       
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);
        EmailEntity primaryEmailEntity = new EmailEntity();
        primaryEmailEntity.setEmail("original");
        primaryEmailEntity.setCurrent(true);
        primaryEmailEntity.setPrimary(Boolean.FALSE);
        primaryEmailEntity.setVerified(Boolean.TRUE);
        primaryEmailEntity.setVisibility("PRIVATE");
        primaryEmailEntity.setId("some-email-hash");        
        
        Mockito.when(mockEmailDao.findByEmail(Mockito.eq("original"))).thenReturn(primaryEmailEntity);
        
        emailManager.editEmail("orcid", "original", "edited", new MockHttpServletRequest());
        ArgumentCaptor<EmailEntity> captor = ArgumentCaptor.forClass(EmailEntity.class);
        Mockito.verify(mockEmailDao).persist(captor.capture());
        Mockito.verify(mockEmailDao).remove(Mockito.eq("some-email-hash"));
        
        EmailEntity mergedEntity = captor.getValue();
        assertEquals("edited", mergedEntity.getEmail());
        assertFalse(mergedEntity.getPrimary());
        assertFalse(mergedEntity.getVerified());
        assertEquals("PRIVATE", mergedEntity.getVisibility());
    }
    
    @Test
    public void testEditPrimaryEmailNoAddressChange() throws IllegalAccessException {
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);
        
        EmailEntity primaryEmailEntity = new EmailEntity();
        primaryEmailEntity.setEmail("original");
        primaryEmailEntity.setPrimary(Boolean.TRUE);
        primaryEmailEntity.setVerified(Boolean.TRUE);
        primaryEmailEntity.setVisibility("PRIVATE");
        primaryEmailEntity.setId("some-email-hash");        
        
        Mockito.when(mockEmailDao.findByEmail(Mockito.eq("email"))).thenReturn(primaryEmailEntity);
        
        emailManager.editEmail("orcid", "email", "email", new MockHttpServletRequest());
        ArgumentCaptor<EmailEntity> captor = ArgumentCaptor.forClass(EmailEntity.class);
        Mockito.verify(mockEmailDao).persist(captor.capture());
        Mockito.verify(mockEmailDao).remove(Mockito.eq("some-email-hash"));
        
        EmailEntity mergedEntity = captor.getValue();
        assertEquals("email", mergedEntity.getEmail());
        assertTrue(mergedEntity.getPrimary());
        assertFalse(mergedEntity.getVerified());
        assertEquals("PRIVATE", mergedEntity.getVisibility());
    }
    
    @Test
    public void addEmailRemovesSpaceCharsTest() throws NoSuchAlgorithmException {
        TargetProxyHelper.injectIntoProxy(emailManager, "emailDao", mockEmailDao);
      
        char[] chars = { ' ', '\n', '\t', '\u00a0', '\u0020', '\u1680', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008',
                '\u2009', '\u200a', '\u202f', '\u205f', '\u3000' };

        for (char c : chars) {
            long now = System.currentTimeMillis();
            String emailAddress = now + "test" + c + "@email.com";
            String filteredEmailAddress = OrcidStringUtils.filterEmailAddress(emailAddress);
            
            Email email = new Email();
            email.setEmail(emailAddress);
            email.setPrimary(false);
            email.setVisibility(Visibility.PUBLIC);
            
            emailManager.addEmail(ORCID, email);
            
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(mockEmailDao).addEmail(eq(ORCID), eq(filteredEmailAddress), captor.capture(), eq(Visibility.PUBLIC.name()), eq(ORCID), isNull());
            String hashValue = captor.getValue();
            
            assertNotEquals(hashValue, encryptionManager.sha256Hash(emailAddress));
            assertEquals(hashValue, encryptionManager.getEmailHash(filteredEmailAddress));
            Mockito.reset(mockEmailDao);
        }
    }
    
    @Test
    public void editEmailRemovesSpaceCharsTest() throws IllegalAccessException {
        char[] chars = { ' ', '\n', '\t', '\u00a0', '\u0020', '\u1680', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008',
                '\u2009', '\u200a', '\u202f', '\u205f', '\u3000' };
        
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);
        
        EmailEntity primaryEmailEntity = new EmailEntity();
        primaryEmailEntity.setEmail("original");
        primaryEmailEntity.setPrimary(Boolean.TRUE);
        primaryEmailEntity.setVerified(Boolean.TRUE);
        primaryEmailEntity.setVisibility("PRIVATE");
        primaryEmailEntity.setId("some-email-hash");
        
        Mockito.when(mockEmailDao.findByEmail(Mockito.eq("original"))).thenReturn(primaryEmailEntity);
        
        for(char c : chars) {
            String email = c + "test" + c + "@test" + c + ".com";
            String filteredEmail = "test@test.com";
        
            emailManager.editEmail("orcid", "original", email, new MockHttpServletRequest());
            ArgumentCaptor<EmailEntity> captor = ArgumentCaptor.forClass(EmailEntity.class);
            Mockito.verify(mockEmailDao, atLeastOnce()).persist(captor.capture());
            Mockito.verify(mockEmailDao, atLeastOnce()).remove(Mockito.eq("some-email-hash"));
            
            EmailEntity entity = captor.getValue();
            assertEquals(filteredEmail, entity.getEmail());
            assertEquals(encryptionManager.getEmailHash(filteredEmail), entity.getId());
        }        
    }
    
    @Test
    public void reactivateOrCreateRemovesSpaceChars_ReactivateTest() throws IllegalAccessException {
        char[] chars = { ' ', '\n', '\t', '\u00a0', '\u0020', '\u1680', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008',
                '\u2009', '\u200a', '\u202f', '\u205f', '\u3000' };        
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);
        
        EmailEntity primaryEmailEntity = new EmailEntity();
        primaryEmailEntity.setEmail("original");
        primaryEmailEntity.setPrimary(Boolean.TRUE);
        primaryEmailEntity.setVerified(Boolean.TRUE);
        primaryEmailEntity.setVisibility("PRIVATE");
        primaryEmailEntity.setId("some-email-hash");
        primaryEmailEntity.setOrcid(ORCID);
        
        Mockito.when(mockEmailDao.find(Mockito.anyString())).thenReturn(primaryEmailEntity);
        
        for(char c : chars) {
            String rand = RandomStringUtils.randomAlphanumeric(10);
            String email =  rand + c + "test" + c + "@test" + c + ".com";
            String filteredEmail = rand + "test@test.com";
        
            emailManager.reactivateOrCreate(ORCID, email, Visibility.PUBLIC);
            ArgumentCaptor<EmailEntity> captor = ArgumentCaptor.forClass(EmailEntity.class);
            Mockito.verify(mockEmailDao, atLeastOnce()).merge(captor.capture());
            
            EmailEntity entity = captor.getValue();
            assertEquals(filteredEmail, entity.getEmail());
            assertEquals("some-email-hash", entity.getId());            
        }  
    }
    
    @Test
    public void reactivateOrCreateRemovesSpaceChars_CreateTest() {
        char[] chars = { ' ', '\n', '\t', '\u00a0', '\u0020', '\u1680', '\u2000', '\u2001', '\u2002', '\u2003', '\u2004', '\u2005', '\u2006', '\u2007', '\u2008',
                '\u2009', '\u200a', '\u202f', '\u205f', '\u3000' };
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);
        
        Mockito.when(mockEmailDao.find(Mockito.anyString())).thenReturn(null);
        
        for(char c : chars) {
            String rand = RandomStringUtils.randomAlphanumeric(10);
            String email =  rand + c + "test" + c + "@test" + c + ".com";
            String filteredEmail = rand + "test@test.com";
            String emailHash = encryptionManager.getEmailHash(filteredEmail);
            
            emailManager.reactivateOrCreate(ORCID, email, Visibility.PUBLIC);
            ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
            Mockito.verify(mockEmailDao, atLeastOnce()).addEmail(eq(ORCID), captor.capture(), eq(emailHash), eq(Visibility.PUBLIC.name()), eq(ORCID), eq(null));
            
            String emailUsed = captor.getValue();
            assertEquals(filteredEmail, emailUsed);
        }  
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmailPrimaryEmailPrimaryEmail() {
        EmailDao mockEmailDao = Mockito.mock(EmailDao.class);
        EmailDao original = (EmailDao) ReflectionTestUtils.getField(emailManager, "emailDao");
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);

        Mockito.when(mockEmailDao.isPrimaryEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"))).thenReturn(true);
        
        emailManager.removeEmail("orcid", "email@email.com");
        
        ReflectionTestUtils.setField(emailManager, "emailDao", original);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveEmailOnlyEmail() {
        EmailDao mockEmailDao = Mockito.mock(EmailDao.class);
        EmailDao original = (EmailDao) ReflectionTestUtils.getField(emailManager, "emailDao");
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);

        Mockito.when(mockEmailDao.isPrimaryEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"))).thenReturn(false);
        Mockito.when(mockEmailDao.findByOrcid(Mockito.eq("orcid"), Mockito.anyLong())).thenReturn(Arrays.asList(getEmailEntity("email@email.com")));
        
        emailManager.removeEmail("orcid", "email@email.com");
        
        ReflectionTestUtils.setField(emailManager, "emailDao", original);
    }
    
    @Test
    public void testRemoveEmail() {
        EmailDao mockEmailDao = Mockito.mock(EmailDao.class);
        EmailDao original = (EmailDao) ReflectionTestUtils.getField(emailManager, "emailDao");
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);

        Mockito.when(mockEmailDao.isPrimaryEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"))).thenReturn(false);
        Mockito.when(mockEmailDao.findByOrcid(Mockito.eq("orcid"), Mockito.anyLong())).thenReturn(Arrays.asList(getEmailEntity("email@email.com"), getEmailEntity("another@email.com")));
        Mockito.doNothing().when(mockEmailDao).removeEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"));
        
        emailManager.removeEmail("orcid", "email@email.com");
        
        Mockito.verify(mockEmailDao, Mockito.times(1)).removeEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"));
        
        ReflectionTestUtils.setField(emailManager, "emailDao", original);
    }
    
    private EmailEntity getEmailEntity(String email) {
        EmailEntity entity = new EmailEntity();
        entity.setEmail(email);
        return entity;
    }
    
    @Test
    public void testRemoveUnclaimedEmail() {
        EmailDao mockEmailDao = Mockito.mock(EmailDao.class);
        ProfileDao mockProfileDao = Mockito.mock(ProfileDao.class);
        EmailDao emailDao = (EmailDao) ReflectionTestUtils.getField(emailManager, "emailDao");
        ProfileDao profileDao = (ProfileDao) ReflectionTestUtils.getField(emailManager, "profileDao");
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);
        ReflectionTestUtils.setField(emailManager, "profileDao", mockProfileDao);

        Mockito.when(mockProfileDao.find(Mockito.eq("orcid"))).thenReturn(getUnclaimedProfile("orcid"));
        Mockito.doNothing().when(mockEmailDao).removeEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"));
        
        emailManager.removeUnclaimedEmail("orcid", "email@email.com");
        
        Mockito.verify(mockEmailDao, Mockito.times(1)).removeEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"));
        
        ReflectionTestUtils.setField(emailManager, "emailDao", emailDao);
        ReflectionTestUtils.setField(emailManager, "profileDao", profileDao);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveUnclaimedEmailForClaimedProfile() {
        EmailDao mockEmailDao = Mockito.mock(EmailDao.class);
        ProfileDao mockProfileDao = Mockito.mock(ProfileDao.class);
        EmailDao emailDao = (EmailDao) ReflectionTestUtils.getField(emailManager, "emailDao");
        ProfileDao profileDao = (ProfileDao) ReflectionTestUtils.getField(emailManager, "profileDao");
        ReflectionTestUtils.setField(emailManager, "emailDao", mockEmailDao);
        ReflectionTestUtils.setField(emailManager, "profileDao", mockProfileDao);

        Mockito.when(mockProfileDao.find(Mockito.eq("orcid"))).thenReturn(getClaimedProfile("orcid"));
        Mockito.doNothing().when(mockEmailDao).removeEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"));
        
        emailManager.removeUnclaimedEmail("orcid", "email@email.com");
        
        Mockito.verify(mockEmailDao, Mockito.never()).removeEmail(Mockito.eq("orcid"), Mockito.eq("email@email.com"));
        
        ReflectionTestUtils.setField(emailManager, "emailDao", emailDao);
        ReflectionTestUtils.setField(emailManager, "profileDao", profileDao);
    }

    private ProfileEntity getClaimedProfile(String orcid) {
        ProfileEntity unclaimed = new ProfileEntity();
        unclaimed.setId(orcid);
        unclaimed.setClaimed(Boolean.TRUE);
        return unclaimed;
    }

    private ProfileEntity getUnclaimedProfile(String orcid) {
        ProfileEntity unclaimed = new ProfileEntity();
        unclaimed.setId(orcid);
        unclaimed.setClaimed(Boolean.FALSE);
        return unclaimed;
    }

  

    
}
