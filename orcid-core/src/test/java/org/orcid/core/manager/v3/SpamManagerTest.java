package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.v3.release.record.SourceType;
import org.orcid.jaxb.model.v3.release.record.Spam;

public class SpamManagerTest extends BaseTest {
      
    private static String USER_ORCID = "4444-4444-4444-4497";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4499";       
    
    @Resource
    private SpamManager spamManager;       

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/SpamEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/SpamEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }
    
    @Test
    public void testCreateSpam() {                
        spamManager.createOrUpdateSpam(USER_ORCID);
        Spam spam = spamManager.getSpam(USER_ORCID);
        assertNotNull(spam);
        assertEquals(spam.getSpamCounter(), Integer.valueOf(1));
        assertEquals(spam.getSourceType(), SourceType.USER);       
    }
        
    @Test
    public void testExists() {       
        assertTrue(spamManager.exists("0000-0000-0000-0003"));
        assertTrue(spamManager.exists("0000-0000-0000-0004"));
        
        assertFalse(spamManager.exists("0000-0000-0000-0005"));
        assertFalse(spamManager.exists("0000-0000-0000-0006"));
        
    }
    
    @Test
    public void testFindSpam() {                        
        Spam spam = spamManager.getSpam("0000-0000-0000-0003");
        assertNotNull(spam);
        assertEquals(spam.getSpamCounter(), Integer.valueOf(1));
        assertEquals(spam.getSourceType(), SourceType.USER);       
    }
    
    
    @Test
    public void testUpdateSpamCount() {                
        spamManager.createOrUpdateSpam(OTHER_USER_ORCID);
        Spam spam = spamManager.getSpam(OTHER_USER_ORCID);
        assertNotNull(spam);
        assertEquals(spam.getSpamCounter(), Integer.valueOf(2));
        assertEquals(spam.getSourceType(), SourceType.USER);       
    }
}
