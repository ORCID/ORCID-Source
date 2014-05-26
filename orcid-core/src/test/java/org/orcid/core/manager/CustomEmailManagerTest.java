/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.persistence.jpa.entities.CustomEmailEntity;
import org.orcid.persistence.jpa.entities.EmailType;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class CustomEmailManagerTest extends BaseTest {

    @Resource
    CustomEmailManager customEmailManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(
                Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml"),
                null);
    }

    @Before
    public void before() {
        assertNotNull(customEmailManager);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(
                Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"),
                null);
    }

    @Test
    public void testGetCustomEmails() {
        List<CustomEmailEntity> customEmails = customEmailManager.getCustomEmails("4444-4444-4444-4441");
        assertNotNull(customEmails);
        assertEquals(1, customEmails.size());
        CustomEmailEntity customEmail = customEmails.get(0);
        assertNotNull(customEmail);
        assertEquals("This is the content", customEmail.getContent());
        assertEquals(EmailType.CLAIM, customEmail.getEmailType());
        assertEquals("angel.montenegro.jimenez@gmail.com", customEmail.getSender());
        assertEquals("This is the subject", customEmail.getSubject());
        assertTrue(customEmail.isHtml());
    }

    @Test
    @Transactional
    public void testAddAmendCustomEmail() {
        assertTrue(customEmailManager.createCustomEmail("4444-4444-4444-4441", EmailType.AMEND, "angel.montenegro.jimenez@gmail.com", "Amend subject", "Amend content",
                false));
        List<CustomEmailEntity> customEmails = customEmailManager.getCustomEmails("4444-4444-4444-4441");
        assertNotNull(customEmails);
        assertEquals(2, customEmails.size());
        boolean amend = false, claim = false;
        for (CustomEmailEntity customEmail : customEmails) {
            if (EmailType.AMEND.equals(customEmail.getEmailType())) {
                assertEquals("Amend subject", customEmail.getSubject());
                assertEquals("angel.montenegro.jimenez@gmail.com", customEmail.getSender());
                assertEquals("Amend content", customEmail.getContent());
                assertFalse(customEmail.isHtml());
                amend = true;
            } else {
                assertEquals("This is the content", customEmail.getContent());
                assertEquals(EmailType.CLAIM, customEmail.getEmailType());
                assertEquals("angel.montenegro.jimenez@gmail.com", customEmail.getSender());
                assertEquals("This is the subject", customEmail.getSubject());
                claim = true;
            }
        }

        assertTrue(amend);
        assertTrue(claim);
    }

    @Test    
    public void testUpdateCustomEmail() {
        // Check old values
        List<CustomEmailEntity> customEmails = customEmailManager.getCustomEmails("4444-4444-4444-4441");
        assertNotNull(customEmails);
        assertEquals(1, customEmails.size());
        CustomEmailEntity customEmail = customEmails.get(0);
        assertNotNull(customEmail);
        assertEquals("This is the content", customEmail.getContent());
        assertEquals(EmailType.CLAIM, customEmail.getEmailType());
        assertEquals("angel.montenegro.jimenez@gmail.com", customEmail.getSender());
        assertEquals("This is the subject", customEmail.getSubject());
        assertTrue(customEmail.isHtml());
        // Update
        customEmailManager.updateCustomEmail("4444-4444-4444-4441", EmailType.CLAIM, "updated@sender.com", "updated subject", "updated content", false);
        // Check new values
        customEmails = customEmailManager.getCustomEmails("4444-4444-4444-4441");
        assertNotNull(customEmails);
        assertEquals(1, customEmails.size());
        customEmail = customEmails.get(0);
        assertNotNull(customEmail);
        assertEquals("updated content", customEmail.getContent());
        assertEquals(EmailType.CLAIM, customEmail.getEmailType());
        assertEquals("updated@sender.com", customEmail.getSender());
        assertEquals("updated subject", customEmail.getSubject());
        assertFalse(customEmail.isHtml());
    }

    @Test
    @Transactional
    public void testDeleteCustomEmail() {
        // Check old values
        List<CustomEmailEntity> customEmails = customEmailManager.getCustomEmails("4444-4444-4444-4441");
        assertNotNull(customEmails);
        assertEquals(1, customEmails.size());        
        // Delete
        customEmailManager.deleteCustomEmail("4444-4444-4444-4441", EmailType.CLAIM);
        // Check it is now empty
        customEmails = customEmailManager.getCustomEmails("4444-4444-4444-4441");
        assertNotNull(customEmails);
        assertEquals(0, customEmails.size());
    }

}
