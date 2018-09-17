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
import org.orcid.core.BaseTest;
import org.orcid.core.manager.v3.read_only.EmailManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.record.Email;
import org.orcid.jaxb.model.v3.rc1.record.Emails;

public class EmailManagerReadOnlyTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
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
        assertFalse(emailManagerReadOnly.haveAnyEmailVerified("0000-0000-0000-0001"));
        assertFalse(emailManagerReadOnly.haveAnyEmailVerified("4444-4444-4444-4445"));
    }
    
    @Test
    public void getEmailsTest() {
        Emails emails = emailManagerReadOnly.getEmails("0000-0000-0000-0003");
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(5, emails.getEmails().size());
        boolean found1 = false, found2 = false, found3 = false, found4=false, found5 = false;
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
            } else {
                fail("Invalid email found: " + email.getEmail());
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        assertTrue(found5);
    }
    
    @Test
    public void getPublicEmailsTest() {
        Emails emails = emailManagerReadOnly.getPublicEmails("0000-0000-0000-0003");
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(1, emails.getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", emails.getEmails().get(0).getEmail());
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
}
