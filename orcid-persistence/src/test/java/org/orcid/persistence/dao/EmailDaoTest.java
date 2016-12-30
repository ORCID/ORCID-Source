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
package org.orcid.persistence.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.EmailEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class EmailDaoTest extends DBUnitTest {
    
    @Resource
    EmailDao emailDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Before
    public void beforeRunning() {
        assertNotNull(emailDao);
    }
    
    @Test
    public void testIsAutoDeprecateEnableForEmail() {
        //Unclaimed and the source have auto deprecate enabled
        assertTrue(emailDao.isAutoDeprecateEnableForEmail("public_0000-0000-0000-0001@test.orcid.org"));
        assertTrue(emailDao.isAutoDeprecateEnableForEmail("PUBLIC_0000-0000-0000-0001@test.orcid.org"));
        assertTrue(emailDao.isAutoDeprecateEnableForEmail("PuBlIc_0000-0000-0000-0001@test.orcid.org"));
        //Claimed
        assertFalse(emailDao.isAutoDeprecateEnableForEmail("public_0000-0000-0000-0002@test.orcid.org"));
        assertFalse(emailDao.isAutoDeprecateEnableForEmail("PUBLIC_0000-0000-0000-0002@test.orcid.org"));
        assertFalse(emailDao.isAutoDeprecateEnableForEmail("PuBlIc_0000-0000-0000-0002@test.orcid.org"));
        
        //Unclaimed but source have auto deprecate disabled
        assertFalse(emailDao.isAutoDeprecateEnableForEmail("public_0000-0000-0000-0006@test.orcid.org"));
        assertFalse(emailDao.isAutoDeprecateEnableForEmail("PUBLIC_0000-0000-0000-0006@test.orcid.org"));
        assertFalse(emailDao.isAutoDeprecateEnableForEmail("PuBlIc_0000-0000-0000-0006@test.orcid.org"));
    }
    
    @Test
    public void testEmailExists() {
        assertFalse(emailDao.emailExists("shoud@fail.com"));
        assertTrue(emailDao.emailExists("public_0000-0000-0000-0001@test.orcid.org"));
        assertTrue(emailDao.emailExists("PUBLIC_0000-0000-0000-0001@test.orcid.org"));
        assertFalse(emailDao.emailExists("0000-0000-0000-0001@test.orcid.org"));
        assertTrue(emailDao.emailExists("public_0000-0000-0000-0002@test.orcid.org"));
        assertTrue(emailDao.emailExists("PuBlIc_0000-0000-0000-0002@test.orcid.org"));
    }
    
    @Test
    public void testRemovePrimaryEmail() {
        String primaryEmail = "angel1@montenegro.com";
        assertTrue(emailDao.emailExists(primaryEmail));
        //Not the owner
        emailDao.removeEmail("4444-4444-4444-4443", primaryEmail, true);
        assertTrue(emailDao.emailExists(primaryEmail));
        //Don't delete if it is primary
        emailDao.removeEmail("4444-4444-4444-4444", primaryEmail, false);
        assertTrue(emailDao.emailExists(primaryEmail));
        //Right owner and delete even if it is primary
        emailDao.removeEmail("4444-4444-4444-4444", primaryEmail, true);
        assertFalse(emailDao.emailExists(primaryEmail));
    }
    
    @Test
    public void testRemovePrimaryEmailCaseSensitive() {    	    	
    	String primaryEmail = "LIMITED@email.com";
        assertTrue(emailDao.emailExists(primaryEmail));
        //Not the owner
        emailDao.removeEmail("4444-4444-4444-4443", primaryEmail, true);
        assertTrue(emailDao.emailExists(primaryEmail));
        //Don't delete if it is primary
        emailDao.removeEmail("4444-4444-4444-4441", primaryEmail, false);
        assertTrue(emailDao.emailExists(primaryEmail));
        //Right owner and delete even if it is primary
        emailDao.removeEmail("4444-4444-4444-4441", primaryEmail, true);
        assertFalse(emailDao.emailExists(primaryEmail));
    }
    
    @Test
    public void testRemoveNonPrimaryEmail() {
        String nonPrimaryEmail = "limited_0000-0000-0000-0003@test.orcid.org";
        //Not the owner
        emailDao.removeEmail("4444-4444-4444-4443", nonPrimaryEmail, false);        
        assertTrue(emailDao.emailExists(nonPrimaryEmail));
        //Remove only if it is not primary
        emailDao.removeEmail("0000-0000-0000-0003", nonPrimaryEmail, false);
        assertFalse(emailDao.emailExists(nonPrimaryEmail));
    }
    
    @Test
    public void testRemoveNonPrimaryEmailCaseSensitive() {
    	String nonPrimaryEmail = "TeDdYbAsS@semantico.com";
    	assertTrue(emailDao.emailExists(nonPrimaryEmail));
    	//Not the owner    	
        emailDao.removeEmail("0000-0000-0000-0003", nonPrimaryEmail, false);        
        assertTrue(emailDao.emailExists(nonPrimaryEmail));
        //Remove only if it is not primary
        emailDao.removeEmail("4444-4444-4444-4443", nonPrimaryEmail, false);
        assertFalse(emailDao.emailExists(nonPrimaryEmail));
    }
    
    @Test 
    public void testVerify() {
    	EmailEntity email = emailDao.find("teddybass2@semantico.com");
    	assertNotNull(email);
    	assertFalse(email.getVerified());
    	emailDao.verifyEmail("teddybass2@semantico.com");
    	
    	email = emailDao.find("teddybass2@semantico.com");
    	assertNotNull(email);
    	assertTrue(email.getVerified());
    }
    
    @Test 
    public void testVerifyCaseSensitive() {
    	EmailEntity email = emailDao.find("teddybass3public@semantico.com");
    	assertNotNull(email);
    	assertFalse(email.getVerified());
    	emailDao.verifyEmail("TeDdYbAsS3PuBlIc@semantico.com");
    	
    	email = emailDao.find("teddybass3public@semantico.com");
    	assertNotNull(email);
    	assertTrue(email.getVerified());
    }
}
