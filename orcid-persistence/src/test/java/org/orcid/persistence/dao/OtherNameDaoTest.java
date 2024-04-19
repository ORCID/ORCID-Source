package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
public class OtherNameDaoTest extends DBUnitTest {

    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0001";
    
    @Resource(name = "otherNameDao")
    private OtherNameDao dao;

    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private ProfileLastModifiedDao profileLastModifiedDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml"));
    }

    @Test
    public void testfindOtherNameByOrcid() {
        List<OtherNameEntity> otherNames = dao.getOtherNames("4444-4444-4444-4446", 0L);
        assertNotNull(otherNames);
        assertEquals(4, otherNames.size());
    }

    @Test
    public void testUpdateOtherName() {
        try {
            dao.updateOtherName(null);
            fail();
        } catch (UnsupportedOperationException e) {

        }
    }    
    
    @Test
    public void testGetOtherName() {
        OtherNameEntity otherName = dao.getOtherName("4444-4444-4444-4443", 2L);
        assertNotNull(otherName);
        assertEquals("Flibberdy Flabinah", otherName.getDisplayName());
        assertEquals("PUBLIC", otherName.getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testGetInvalidOtherName() {
        dao.getOtherName("4444-4444-4444-4443", 100L);
        fail();
    }
    
    @Test
    public void removeAllTest() {
        long initialNumber = dao.countAll();
        long elementThatBelogsToUser = dao.getOtherNames(USER_ORCID, 0L).size();
        long otherUserElements = dao.getOtherNames(OTHER_USER_ORCID, 0L).size();
        assertEquals(5, elementThatBelogsToUser);
        assertTrue(elementThatBelogsToUser < initialNumber);
        assertEquals(3, otherUserElements);
        //Remove all elements that belongs to USER_ORCID
        dao.removeAllOtherNames(USER_ORCID);
        
        long finalNumberOfElements = dao.countAll();
        long finalNumberOfOtherUserElements = dao.getOtherNames(OTHER_USER_ORCID, 0L).size();
        long finalNumberOfElementsThatBelogsToUser = dao.getOtherNames(USER_ORCID, 0L).size();
        assertEquals(0, finalNumberOfElementsThatBelogsToUser);
        assertEquals(otherUserElements, finalNumberOfOtherUserElements);
        assertEquals((initialNumber - elementThatBelogsToUser), finalNumberOfElements);
    }
    
    @Test
    public void mergeTest() {
        OtherNameEntity e = dao.find(20L);
        e.setDisplayIndex(1000L);
        Date dateCreated = e.getDateCreated();
        Date lastModified = e.getLastModified();
        dao.merge(e);

        OtherNameEntity updated = dao.find(20L);
        assertEquals(dateCreated, updated.getDateCreated());
        assertTrue(updated.getLastModified().after(lastModified));
    }
    
    @Test
    public void persistTest() {
        OtherNameEntity e = new OtherNameEntity();
        e.setOrcid("0000-0000-0000-0002"); 
        e.setVisibility("PUBLIC");
        e.setDisplayName("OTHER_NAME");
        
        dao.persist(e);
        assertNotNull(e.getId());
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertEquals(e.getDateCreated(), e.getLastModified());
        
        OtherNameEntity e2 = dao.find(e.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        assertEquals(e.getLastModified(), e2.getLastModified());
        assertEquals(e.getDateCreated(), e2.getDateCreated());
        assertEquals(e2.getDateCreated(), e2.getLastModified());
    }
}