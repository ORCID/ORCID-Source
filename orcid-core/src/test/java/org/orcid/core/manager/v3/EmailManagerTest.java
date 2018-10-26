package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Email;
import org.orcid.jaxb.model.v3.rc2.record.Emails;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.TargetProxyHelper;
import org.springframework.mock.web.MockHttpServletRequest;

public class EmailManagerTest extends BaseTest {
    private static final String ORCID = "0000-0000-0000-0003";
    
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
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
    }
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @After
    public void after() throws JAXBException {
        TargetProxyHelper.injectIntoProxy(emailManager, "sourceManager", sourceManager);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }        
    
    @Test
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
        
        emailManager.addEmail(new MockHttpServletRequest(), ORCID, email);
        
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        Mockito.verify(mockEmailDao).addEmail(eq(ORCID), eq(emailAddress), captor.capture(), eq(Visibility.PUBLIC.name()), eq(ORCID), isNull());
        String hashValue = captor.getValue();
        
        assertNotEquals(hashValue, encryptionManager.sha256Hash(emailAddress));
        assertEquals(hashValue, encryptionManager.sha256Hash(emailAddress.toLowerCase()));
       
        TargetProxyHelper.injectIntoProxy(emailManager, "emailDao", emailDao);        
    }
    
    @Test
    public void reactivateOrCreateTest() {
        String otherOrcid = "0000-0000-0000-0002";
        String orcid = "0000-0000-0000-0003";
        String email = "public_0000-0000-0000-0003@test.orcid.org";
        String hash = "public_0000-0000-0000-0003@test.orcid.org";
        EmailEntity e = new EmailEntity();
        e.setEmail(email);
        e.setId(hash);
        e.setProfile(new ProfileEntity("0000-0000-0000-0003"));
        TargetProxyHelper.injectIntoProxy(emailManager, "emailDao", mockEmailDao);
        
        // Test merging
        when(mockEmailDao.find(hash)).thenReturn(e);
        emailManager.reactivateOrCreate(orcid, email, hash, Visibility.PUBLIC);
        
        ArgumentCaptor<EmailEntity> captor = ArgumentCaptor.forClass(EmailEntity.class);
        Mockito.verify(mockEmailDao).merge(captor.capture());
        
        EmailEntity merged = captor.getValue();
        assertNotNull(merged);
        assertFalse(merged.getPrimary());
        assertEquals(orcid, merged.getProfile().getId());
        assertEquals(email, merged.getEmail());
        assertEquals(hash, merged.getId());
        assertEquals(Visibility.PUBLIC.name(), merged.getVisibility());
        
        // Test creating
        emailManager.reactivateOrCreate(orcid, "new@email.com", "new@email.com", Visibility.PUBLIC);
        Mockito.verify(mockEmailDao).addEmail(eq(orcid), eq("new@email.com"), eq("new@email.com"), eq(Visibility.PUBLIC.name()), eq(orcid), eq(null));
        
        // Test belong to other record
        try {
            emailManager.reactivateOrCreate(otherOrcid, email, hash, Visibility.PUBLIC);            
            fail();
        } catch (IllegalArgumentException iae) {
            
        }
        
        TargetProxyHelper.injectIntoProxy(emailManager, "emailDao", emailDao);
    }
}
