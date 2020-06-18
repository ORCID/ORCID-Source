package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.BiographyEntity;
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
public class BiographyDaoTest extends DBUnitTest {
    @Resource 
    BiographyDao biographyDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/BiographyEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/BiographyEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }
    
    @Test
    public void testfindByOrcid() {
        BiographyEntity bio = biographyDao.getBiography("4444-4444-4444-4447", System.currentTimeMillis());
        assertNotNull(bio);
        assertEquals("Biography for 4444-4444-4444-4447", bio.getBiography());
        assertEquals("LIMITED", bio.getVisibility());        
    }
    
    @Test
    public void testUpdate() {
        BiographyEntity bio = biographyDao.getBiography("4444-4444-4444-4442", System.currentTimeMillis());
        assertNotNull(bio);
        assertEquals("Biography for 4444-4444-4444-4442", bio.getBiography());
        assertEquals("PUBLIC", bio.getVisibility()); 
        
        bio.setBiography("Updated biography");
        bio.setVisibility("LIMITED");
        assertTrue(biographyDao.updateBiography("4444-4444-4444-4442", bio.getBiography(), bio.getVisibility()));
        
        BiographyEntity updatedBio = biographyDao.getBiography("4444-4444-4444-4442", System.currentTimeMillis());
        assertNotNull(updatedBio);
        assertEquals("Updated biography", bio.getBiography());
        assertEquals("LIMITED", bio.getVisibility());
    }
    
    @Test
    public void mergeTest() {
        BiographyEntity e = biographyDao.find(14L);
        e.setVisibility("LIMITED");
        Date dateCreated = e.getDateCreated();
        Date lastModified = e.getLastModified();
        biographyDao.merge(e);

        BiographyEntity updated = biographyDao.find(14L);
        assertEquals(dateCreated, updated.getDateCreated());
        assertTrue(updated.getLastModified().after(lastModified));
    }
    
    @Test
    public void persistTest() {
        BiographyEntity e = new BiographyEntity();
        e.setOrcid("0000-0000-0000-0002"); 
        e.setVisibility("PUBLIC");
        e.setBiography("My Bio");
        biographyDao.persist(e);
        assertNotNull(e.getId());
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertEquals(e.getDateCreated(), e.getLastModified());
        
        BiographyEntity e2 = biographyDao.find(e.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        assertEquals(e2.getDateCreated(), e2.getLastModified());
        assertEquals(e.getDateCreated(), e2.getDateCreated());
    }
}
