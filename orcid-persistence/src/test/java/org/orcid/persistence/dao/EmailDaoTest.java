package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
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
        assertFalse(emailDao.emailExists("teddybass@semantico.com"));
        assertTrue(emailDao.emailExists("8f649f6d12203f020ee26467547432add46bc5395ff8dd72242fa2d7aa4fc04a"));        
        assertFalse(emailDao.emailExists("8f649f6d12203f020ee26467547432add46bc5395ff8dd72242fa2d7aaWRONG"));
        assertTrue(emailDao.emailExists("ecdc2c6aef7aa5aa4012b9e5f262de2214c9b0e3f3b0201da0eeebc7531ae018"));        
    }
    
    @Test
    public void testRemovePrimaryEmail() {
        String primaryEmail = "angel1@montenegro.com";
        String primaryEmailHash = "2965a4115f2639b43e5feb78adddfff52e5324813b6c12b4e25ebdac052f72df";
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Not the owner
        emailDao.removeEmail("4444-4444-4444-4443", primaryEmail, true);
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Don't delete if it is primary
        emailDao.removeEmail("4444-4444-4444-4444", primaryEmail, false);
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Right owner and delete even if it is primary
        emailDao.removeEmail("4444-4444-4444-4444", primaryEmail, true);
        assertFalse(emailDao.emailExists(primaryEmailHash));
    }
    
    @Test
    public void testRemovePrimaryEmailCaseSensitive() {    	    	
    	String primaryEmail = "spike@milligan.com";
    	String primaryEmailHash = "fa755fdf4ba30ea92bbbd382f4787526d162110cd83192a3bec180e6a09396b5";
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Not the owner
        emailDao.removeEmail("4444-4444-4444-4443", primaryEmail, true);
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Don't delete if it is primary
        emailDao.removeEmail("4444-4444-4444-4441", primaryEmail, false);
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Right owner and delete even if it is primary
        emailDao.removeEmail("4444-4444-4444-4441", primaryEmail, true);
        assertFalse(emailDao.emailExists(primaryEmailHash));
    }
    
    @Test
    public void testRemoveNonPrimaryEmail() {
        String nonPrimaryEmail = "limited_0000-0000-0000-0003@test.orcid.org";
        String emailHash = "71d1e18acf189e7b14e486a53691cef30249a3aedfd5b4c988b1754eb179e6b9";
        //Not the owner
        emailDao.removeEmail("4444-4444-4444-4443", nonPrimaryEmail, false);        
        assertTrue(emailDao.emailExists(emailHash));
        //Remove only if it is not primary
        emailDao.removeEmail("0000-0000-0000-0003", nonPrimaryEmail, false);
        assertFalse(emailDao.emailExists(emailHash));
    }
    
    @Test
    public void testRemoveNonPrimaryEmailCaseSensitive() {
    	String nonPrimaryEmail = "TeDdYbAsS@semantico.com";
    	String nonPrimaryEmailHash = "2c5cb98057d742ca06eff946aa12a2eb3f9a383159ec85097d8a525fc260cbe7";
    	assertTrue(emailDao.emailExists(nonPrimaryEmailHash));
    	//Not the owner    	
        emailDao.removeEmail("0000-0000-0000-0003", nonPrimaryEmail, false);        
        assertTrue(emailDao.emailExists(nonPrimaryEmailHash));
        //Remove only if it is not primary
        emailDao.removeEmail("4444-4444-4444-4443", nonPrimaryEmail, false);
        assertFalse(emailDao.emailExists(nonPrimaryEmailHash));
    }
    
    @Test 
    public void testVerify() {
    	EmailEntity email = emailDao.findByEmail("teddybass2@semantico.com");
    	assertNotNull(email);
    	assertFalse(email.getVerified());
    	emailDao.verifyEmail("teddybass2@semantico.com");
    	
    	email = emailDao.findByEmail("teddybass2@semantico.com");
    	assertNotNull(email);
    	assertTrue(email.getVerified());
    }
    
    @Test 
    public void testVerifyCaseSensitive() {
    	EmailEntity email = emailDao.findByEmail("teddybass3public@semantico.com");
    	assertNotNull(email);
    	assertFalse(email.getVerified());
    	emailDao.verifyEmail("TeDdYbAsS3PuBlIc@semantico.com");
    	
    	email = emailDao.findByEmail("teddybass3public@semantico.com");
    	assertNotNull(email);
    	assertTrue(email.getVerified());
    }
    
    @Test
    public void testFindPrimaryEmail() {
        EmailEntity email = emailDao.findPrimaryEmail("0000-0000-0000-0003");
        assertNotNull(email);
        assertEquals("public_0000-0000-0000-0003@test.orcid.org", email.getEmail());
        
        email = emailDao.findPrimaryEmail("0000-0000-0000-0004");
        assertNotNull(email);
        assertEquals("private_0000-0000-0000-0004@test.orcid.org", email.getEmail());
    }    
}
