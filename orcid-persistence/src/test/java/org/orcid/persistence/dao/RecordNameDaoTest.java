package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.jpa.entities.RecordNameEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, locations = { "classpath:orcid-persistence-context.xml" })
public class RecordNameDaoTest extends DBUnitTest {
    @Resource 
    RecordNameDao recordNameDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/RecordNameEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml", 
                "/data/SecurityQuestionEntityData.xml"));
    }
    
    @Test
    public void testfindByOrcid() {
        RecordNameEntity recordName = recordNameDao.getRecordName("4444-4444-4444-4442", System.currentTimeMillis());
        assertNotNull(recordName);
        assertEquals("Credit Name", recordName.getCreditName());
        assertEquals("Given Names", recordName.getGivenNames());
        assertEquals("Family Name", recordName.getFamilyName());
        assertEquals(Visibility.PUBLIC, recordName.getVisibility());        
    }
    
    @Test
    public void testfindByCreditName() {
        RecordNameEntity recordName = recordNameDao.findByCreditName("P. Sellers III");
        assertNotNull(recordName);
        assertEquals("P. Sellers III", recordName.getCreditName());
        assertEquals("Peter", recordName.getGivenNames());
        assertEquals("Sellers", recordName.getFamilyName());
        assertEquals(Visibility.LIMITED, recordName.getVisibility());        
    }
    
    @Test
    public void testUpdate() {
        RecordNameEntity recordName = recordNameDao.getRecordName("4444-4444-4444-4447", System.currentTimeMillis());
        assertNotNull(recordName);
        assertEquals("Credit Name", recordName.getCreditName());
        assertEquals("Given Names", recordName.getGivenNames());
        assertEquals("Family Name", recordName.getFamilyName());
        assertEquals(Visibility.LIMITED, recordName.getVisibility()); 
        
        recordName.setCreditName("Updated Credit Name");
        recordName.setGivenNames("Updated Given Names");
        recordName.setFamilyName("Updated Family Name");
        recordName.setVisibility(Visibility.PUBLIC);
        assertTrue(recordNameDao.updateRecordName(recordName));
        
        RecordNameEntity updatedRecordName = recordNameDao.getRecordName("4444-4444-4444-4447", System.currentTimeMillis());
        assertEquals("Updated Credit Name", updatedRecordName.getCreditName());
        assertEquals("Updated Given Names", updatedRecordName.getGivenNames());
        assertEquals("Updated Family Name", updatedRecordName.getFamilyName());
        assertEquals(Visibility.PUBLIC, updatedRecordName.getVisibility());         
    }
    
    @Test
    public void testExists() {
        assertTrue(recordNameDao.exists("4444-4444-4444-4441"));
        assertTrue(recordNameDao.exists("4444-4444-4444-4443"));
        assertTrue(recordNameDao.exists("4444-4444-4444-4445"));
        assertTrue(recordNameDao.exists("4444-4444-4444-4497"));
        assertTrue(recordNameDao.exists("0000-0000-0000-0001"));
        assertTrue(recordNameDao.exists("0000-0000-0000-0002"));
        assertTrue(recordNameDao.exists("0000-0000-0000-0003"));
        assertTrue(recordNameDao.exists("0000-0000-0000-0004"));
        
        assertFalse(recordNameDao.exists("0000-0000-0000-0000"));
        assertFalse(recordNameDao.exists("0000-0000-0000-000X"));
        assertFalse(recordNameDao.exists("0000-0000-0000-1000"));
        assertFalse(recordNameDao.exists("0000-0000-0000-1001"));
        assertFalse(recordNameDao.exists("0000-0000-0000-1002"));
    }
}
