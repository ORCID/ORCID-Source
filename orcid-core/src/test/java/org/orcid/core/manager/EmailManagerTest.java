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
package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.record_rc3.Email;
import org.orcid.jaxb.model.record_rc3.Emails;

public class EmailManagerTest extends BaseTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
            "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/RecordNameEntityData.xml");

    @Resource
    private EmailManager emailManager;

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
        assertTrue(emailManager.emailExists("spike@milligan.com"));
        assertTrue(emailManager.emailExists("1@deprecate.com"));
        assertTrue(emailManager.emailExists("4444-4444-4444-4498@milligan.com"));
        assertTrue(emailManager.emailExists("MiXeD@cASe.com"));
        assertTrue(emailManager.emailExists("public_0000-0000-0000-0001@test.orcid.org"));
        
        assertFalse(emailManager.emailExists("test@test.com"));
        assertFalse(emailManager.emailExists("0000-0000-0000-0001@test.orcid.org"));
        assertFalse(emailManager.emailExists("public_0000-0000-0000-0001@test.orcid"));
    }
    
    @Test
    public void isPrimaryEmailVerifiedTest() {
        assertFalse(emailManager.isPrimaryEmailVerified("4444-4444-4444-4443"));
        assertFalse(emailManager.isPrimaryEmailVerified("4444-4444-4444-4445"));
        assertFalse(emailManager.isPrimaryEmailVerified("0000-0000-0000-0001"));
        assertTrue(emailManager.isPrimaryEmailVerified("0000-0000-0000-0003"));
        assertTrue(emailManager.isPrimaryEmailVerified("4444-4444-4444-4499"));
    }
    
    @Test
    public void haveAnyEmailVerifiedTest() {
        assertTrue(emailManager.haveAnyEmailVerified("0000-0000-0000-0003"));
        assertTrue(emailManager.haveAnyEmailVerified("4444-4444-4444-4442"));
        assertFalse(emailManager.haveAnyEmailVerified("0000-0000-0000-0001"));
        assertFalse(emailManager.haveAnyEmailVerified("4444-4444-4444-4443"));
    }
    
    @Test
    public void getEmailsTest() {
        Emails emails = emailManager.getEmails("0000-0000-0000-0003", System.currentTimeMillis());
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
        Emails emails = emailManager.getPublicEmails("0000-0000-0000-0003", System.currentTimeMillis());
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(1, emails.getEmails().size());
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", emails.getEmails().get(0).getEmail());
    }
    
    @Test
    public void removeEmailTest() {
        assertTrue(emailManager.emailExists("angel1@montenegro.com"));
        emailManager.removeEmail("4444-4444-4444-4444", "angel1@montenegro.com");
        assertFalse(emailManager.emailExists("angel1@montenegro.com"));
    }
    
    @Test
    public void removeEmailIfPrimaryTest() {
        assertTrue(emailManager.emailExists("billie@holiday.com"));
        emailManager.removeEmail("4444-4444-4444-4446", "billie@holiday.com", false);
        //Should not be removed yet
        assertTrue(emailManager.emailExists("billie@holiday.com"));
        emailManager.removeEmail("4444-4444-4444-4446", "billie@holiday.com", true);
        //Now it should be gone
        assertFalse(emailManager.emailExists("billie@holiday.com"));
    }
    
    @Test
    public void moveEmailToOtherAccountTest() {
        fail();
    }
    
    @Test
    public void verifySetCurrentAndPrimaryTest() {
        fail();
    }
    
    @Test
    public void verifyEmailTest() {
        fail();
    }
    
    @Test
    public void verifyPrimaryEmailTest() {
        fail();
    }
}
