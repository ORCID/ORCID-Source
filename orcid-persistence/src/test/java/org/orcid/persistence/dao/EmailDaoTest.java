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
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Before
    public void beforeRunning() {
        assertNotNull(emailDao);
    }
    
    @Test
    public void testIsAutoDeprecateEnableForEmail() {
        //Unclaimed and the source have auto deprecate enabled
        assertTrue(emailDao.isAutoDeprecateEnableForEmailUsingHash("8f649f6d12203f020ee26467547432add46bc5395ff8dd72242fa2d7aa4fc04a"));
        //Claimed
        assertFalse(emailDao.isAutoDeprecateEnableForEmailUsingHash("ecdc2c6aef7aa5aa4012b9e5f262de2214c9b0e3f3b0201da0eeebc7531ae018"));
        //Unclaimed but source have auto deprecate disabled
        assertFalse(emailDao.isAutoDeprecateEnableForEmailUsingHash("49919fd6890f32d00cad6be9dbe277c3f1f84476d2ca0ec4dd74dbf03114b8d7"));
        
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
    public void testRemoveEmail() {
        String primaryEmail = "angel1@montenegro.com";
        String primaryEmailHash = "2965a4115f2639b43e5feb78adddfff52e5324813b6c12b4e25ebdac052f72df";
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Not the owner
        emailDao.removeEmail("4444-4444-4444-4443", primaryEmail);
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Right owner 
        emailDao.removeEmail("4444-4444-4444-4444", primaryEmail);
        assertFalse(emailDao.emailExists(primaryEmailHash));
    }
    
    @Test
    public void testRemoveEmailCaseSensitive() {    	
    	String primaryEmail = "sPiKe@miLLigan.com";
    	String primaryEmailHash = "fa755fdf4ba30ea92bbbd382f4787526d162110cd83192a3bec180e6a09396b5";
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Not the owner
        emailDao.removeEmail("4444-4444-4444-4443", primaryEmail);
        assertTrue(emailDao.emailExists(primaryEmailHash));
        //Right owner and delete even if it is primary
        emailDao.removeEmail("4444-4444-4444-4441", primaryEmail);
        assertFalse(emailDao.emailExists(primaryEmailHash));
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
