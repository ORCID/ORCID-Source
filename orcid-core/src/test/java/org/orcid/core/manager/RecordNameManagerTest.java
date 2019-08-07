package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;
import org.orcid.jaxb.model.common_v2.CreditName;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.FamilyName;
import org.orcid.jaxb.model.record_v2.GivenNames;
import org.orcid.jaxb.model.record_v2.Name;

public class RecordNameManagerTest extends BaseTest {
    @Resource
    private RecordNameManager recordNameManager;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/RecordNameEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/RecordNameEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }
    
    @Test
    public void testCreateRecordName() {
        Name name = new Name();
        long time = System.currentTimeMillis();
        name.setCreditName(new CreditName("Credit Name " + time));
        name.setFamilyName(new FamilyName("Family Name " + time));
        name.setGivenNames(new GivenNames("Given Names " + time));
        name.setVisibility(Visibility.PRIVATE);
        String orcid = "0000-0000-0000-0005";
        recordNameManager.createRecordName(orcid, name);
        Name newName = recordNameManager.getRecordName(orcid);
        assertNotNull(newName);
        assertEquals("Credit Name " + time, newName.getCreditName().getContent());
        assertEquals("Family Name " + time, newName.getFamilyName().getContent());
        assertEquals("Given Names " + time, newName.getGivenNames().getContent());
        assertEquals(Visibility.PRIVATE, newName.getVisibility());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFailOnCreatingOnARecordThatAlreadyHaveRecordName() {
        Name name = new Name();
        long time = System.currentTimeMillis();
        name.setCreditName(new CreditName("Credit Name " + time));
        name.setFamilyName(new FamilyName("Family Name " + time));
        name.setGivenNames(new GivenNames("Given Names " + time));
        name.setVisibility(Visibility.PRIVATE);
        String orcid = "0000-0000-0000-0001";
        recordNameManager.createRecordName(orcid, name);
        fail();
    }
    
    @Test
    public void testExists() {
        assertTrue(recordNameManager.exists("0000-0000-0000-0001"));
        assertTrue(recordNameManager.exists("0000-0000-0000-0002"));
        assertTrue(recordNameManager.exists("0000-0000-0000-0003"));
        assertTrue(recordNameManager.exists("0000-0000-0000-0004"));
        
        assertFalse(recordNameManager.exists("0000-0000-0000-1000"));
        assertFalse(recordNameManager.exists("0000-0000-0000-1001"));
        assertFalse(recordNameManager.exists("0000-0000-0000-1002"));
        assertFalse(recordNameManager.exists("0000-0000-0000-1003"));
        assertFalse(recordNameManager.exists("0000-0000-0000-1004"));
    }
    
    @Test
    public void testFindByCreditName() {
        Name name = recordNameManager.findByCreditName("Adm. Credit");
        assertNotNull(name);
        assertEquals("4444-4444-4444-4440", name.getPath());
    }
    
    @Test
    public void testGetRecordName() {
        String orcid = "0000-0000-0000-0001";
        Name name = recordNameManager.getRecordName(orcid);
        assertNotNull(name);
        assertEquals("Leonardo", name.getGivenNames().getContent());
        assertEquals("da Vinci", name.getFamilyName().getContent());
        assertEquals("Leonardo", name.getCreditName().getContent());
        assertEquals(Visibility.PRIVATE, name.getVisibility());        
    }
    
    @Test
    public void testUpdateRecordName() {
        String orcid = "0000-0000-0000-0002";
        Name name = recordNameManager.getRecordName(orcid);
        assertNotNull(name);
        assertEquals("Given Names", name.getGivenNames().getContent());
        assertEquals("Family Name", name.getFamilyName().getContent());
        assertEquals("Credit Name", name.getCreditName().getContent());
        assertEquals(Visibility.LIMITED, name.getVisibility());
        LastModifiedDate lastModified = name.getLastModifiedDate();
        assertNotNull(lastModified);
        
        long now = System.currentTimeMillis();
        
        name.getCreditName().setContent("Updated Credit Name " + now);
        name.getFamilyName().setContent("Updated Family Name " + now);
        name.getGivenNames().setContent("Updated Given Names " + now);
        name.setVisibility(Visibility.PRIVATE);
        
        Boolean updated = recordNameManager.updateRecordName(orcid, name);
        assertTrue(updated);
        Name updatedName = recordNameManager.getRecordName(orcid);
        
        LastModifiedDate updatedLastModified = updatedName.getLastModifiedDate();
        assertNotNull(updatedLastModified);
        assertFalse(updatedLastModified.equals(lastModified));
        
        assertNotNull(updatedName);
        assertEquals("Updated Given Names " + now, updatedName.getGivenNames().getContent());
        assertEquals("Updated Family Name " + now, updatedName.getFamilyName().getContent());
        assertEquals("Updated Credit Name " + now, updatedName.getCreditName().getContent());
        assertEquals(Visibility.PRIVATE, updatedName.getVisibility());        
    }        
}
