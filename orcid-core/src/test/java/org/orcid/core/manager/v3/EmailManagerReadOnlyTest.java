package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.v3.release.record.Email;
import org.orcid.jaxb.model.v3.release.record.Emails;
import org.orcid.persistence.dao.EmailDao;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.springframework.test.util.ReflectionTestUtils;

public class EmailManagerReadOnlyTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");

    @Resource(name = "emailManagerReadOnlyV3")
    private EmailManagerReadOnly emailManagerReadOnly;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }
    
    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles);
    }
    
    @Test
    public void emailExistsTest() {
        assertTrue(emailManagerReadOnly.emailExists("spike@milligan.com"));
        assertTrue(emailManagerReadOnly.emailExists("1@deprecate.com"));
        assertTrue(emailManagerReadOnly.emailExists("4444-4444-4444-4498@milligan.com"));
        assertTrue(emailManagerReadOnly.emailExists("MiXeD@cASe.com"));
        assertTrue(emailManagerReadOnly.emailExists("public_0000-0000-0000-0001@test.orcid.org"));
        assertTrue(emailManagerReadOnly.emailExists("test@test.com"));
        
        assertFalse(emailManagerReadOnly.emailExists("0000-0000-0000-0001@test.orcid.org"));
        assertFalse(emailManagerReadOnly.emailExists("public_0000-0000-0000-0001@test.orcid"));
    }
    
    @Test
    public void isPrimaryEmailVerifiedTest() {        
        assertFalse(emailManagerReadOnly.isPrimaryEmailVerified("4444-4444-4444-4445"));
        assertFalse(emailManagerReadOnly.isPrimaryEmailVerified("0000-0000-0000-0001"));
        assertTrue(emailManagerReadOnly.isPrimaryEmailVerified("0000-0000-0000-0003"));
        assertTrue(emailManagerReadOnly.isPrimaryEmailVerified("4444-4444-4444-4499"));
    }
    
    @Test
    public void haveAnyEmailVerifiedTest() {
        assertTrue(emailManagerReadOnly.haveAnyEmailVerified("0000-0000-0000-0003"));
        assertTrue(emailManagerReadOnly.haveAnyEmailVerified("4444-4444-4444-4442"));
        assertTrue(emailManagerReadOnly.haveAnyEmailVerified("0000-0000-0000-0001"));
        assertFalse(emailManagerReadOnly.haveAnyEmailVerified("4444-4444-4444-4445"));
    }
    
    @Test
    public void getEmailsTest() {
        Emails emails = emailManagerReadOnly.getEmails("0000-0000-0000-0003");
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(6, emails.getEmails().size());
        boolean found1 = false, found2 = false, found3 = false, found4=false, found5 = false, found6 = false;
        for(Email email : emails.getEmails()) {
            if(email.getEmail().equals("public_0000-0000-0000-0003@test.orcid.org")) {
                found1 = true;
            } else if(email.getEmail().equals("limited_0000-0000-0000-0003@test.orcid.org")) {
                found2 = true;
            } else if(email.getEmail().equals("private_0000-0000-0000-0003@test.orcid.org")) {
                found3 = true;
            } else if(email.getEmail().equals("self_limited_0000-0000-0000-0003@test.orcid.org")) {
                found4 = true;
            } else if(email.getEmail().equals("self_private_0000-0000-0000-0003@test.orcid.org")) {
                found5 = true;
            } else if(email.getEmail().equals("public_0000-0000-0000-0003@orcid.org")) {
                found6 = true;
            } else {
                fail("Invalid email found: " + email.getEmail());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
        assertTrue(found6);
    }
    
    @Test
    public void getPublicEmailsTest() {
        Emails emails = emailManagerReadOnly.getPublicEmails("0000-0000-0000-0003");
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(2, emails.getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", emails.getEmails().get(0).getEmail());
        assertEquals("public_0000-0000-0000-0003@orcid.org", emails.getEmails().get(1).getEmail());
    }       
    
    @Test
    public void findOrcidIdByEmail() {
        assertEquals("0000-0000-0000-0003", emailManagerReadOnly.findOrcidIdByEmail("public_0000-0000-0000-0003@test.orcid.org"));
        assertEquals("0000-0000-0000-0003", emailManagerReadOnly.findOrcidIdByEmail("PUBLIC_0000-0000-0000-0003@TEST.ORCID.ORG"));
        assertEquals("0000-0000-0000-0003", emailManagerReadOnly.findOrcidIdByEmail("PuBlIc_0000-0000-0000-0003@test.orcid.org"));
        
        assertEquals("0000-0000-0000-0004", emailManagerReadOnly.findOrcidIdByEmail("public_0000-0000-0000-0004@test.orcid.org"));
        assertEquals("0000-0000-0000-0004", emailManagerReadOnly.findOrcidIdByEmail("PUBLIC_0000-0000-0000-0004@TEST.ORCID.ORG"));
        assertEquals("0000-0000-0000-0004", emailManagerReadOnly.findOrcidIdByEmail("PuBlIc_0000-0000-0000-0004@test.orcid.org"));
        
        try {
            assertNull(emailManagerReadOnly.findOrcidIdByEmail("fail@test.orcid.org"));
            fail();
        } catch(NoResultException n) {
            
        } catch(Exception e) {
            fail();
        }
    }

    @Test
    public void testFindOrcidByVerifiedEmail() {
        assertEquals("0000-0000-0000-0003", emailManagerReadOnly.findOrcidByVerifiedEmail("public_0000-0000-0000-0003@test.orcid.org"));
        assertEquals("0000-0000-0000-0003", emailManagerReadOnly.findOrcidByVerifiedEmail("PUBLIC_0000-0000-0000-0003@TEST.ORCID.ORG"));
        assertEquals("0000-0000-0000-0003", emailManagerReadOnly.findOrcidByVerifiedEmail("PuBlIc_0000-0000-0000-0003@test.orcid.org"));

        try {
            assertNull(emailManagerReadOnly.findOrcidByVerifiedEmail("public_0000-0000-0000-0004@test.orcid.org"));
            assertNull(emailManagerReadOnly.findOrcidByVerifiedEmail("fail@test.orcid.org"));
            fail("Should throw an exception if no email is founded");
        } catch(NoResultException e) {

        } catch(Exception e) {
            fail();
        }
    }
    
    @Test
    public void testIsUsersOnlyEmailMultipleEmails() {
        EmailDao mockEmailDao = Mockito.mock(EmailDao.class);
        EmailDao original = (EmailDao) ReflectionTestUtils.getField(emailManagerReadOnly, "emailDao");
        ReflectionTestUtils.setField(emailManagerReadOnly, "emailDao", mockEmailDao);
        Mockito.when(mockEmailDao.findByOrcid(Mockito.eq("orcid"), Mockito.anyLong())).thenReturn(Arrays.asList(getEmailEntity("email@email.com"), getEmailEntity("something@email.com")));
        assertFalse(emailManagerReadOnly.isUsersOnlyEmail("orcid", "email@email.com"));
        ReflectionTestUtils.setField(emailManagerReadOnly, "emailDao", original);
    }
    
    @Test
    public void testIsUsersOnlyEmailNoMatch() {
        EmailDao mockEmailDao = Mockito.mock(EmailDao.class);
        EmailDao original = (EmailDao) ReflectionTestUtils.getField(emailManagerReadOnly, "emailDao");
        ReflectionTestUtils.setField(emailManagerReadOnly, "emailDao", mockEmailDao);
        Mockito.when(mockEmailDao.findByOrcid(Mockito.eq("orcid"), Mockito.anyLong())).thenReturn(Arrays.asList(getEmailEntity("email@email.com")));
        assertFalse(emailManagerReadOnly.isUsersOnlyEmail("orcid", "erm@email.com"));
        ReflectionTestUtils.setField(emailManagerReadOnly, "emailDao", original);
    }
    
    @Test
    public void testIsUsersOnlyEmail() {
        EmailDao mockEmailDao = Mockito.mock(EmailDao.class);
        EmailDao original = (EmailDao) ReflectionTestUtils.getField(emailManagerReadOnly, "emailDao");
        ReflectionTestUtils.setField(emailManagerReadOnly, "emailDao", mockEmailDao);
        Mockito.when(mockEmailDao.findByOrcid(Mockito.eq("orcid"), Mockito.anyLong())).thenReturn(Arrays.asList(getEmailEntity("email@email.com")));
        assertTrue(emailManagerReadOnly.isUsersOnlyEmail("orcid", "email@email.com"));
        ReflectionTestUtils.setField(emailManagerReadOnly, "emailDao", original);
    }

    private EmailEntity getEmailEntity(String email) {
        EmailEntity entity = new EmailEntity();
        entity.setEmail(email);
        return entity;
    }

    
    
}
