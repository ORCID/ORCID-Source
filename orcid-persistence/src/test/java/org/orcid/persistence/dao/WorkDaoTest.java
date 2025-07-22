package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WorkDaoTest extends DBUnitTest {

    private static String USER_ORCID = "4444-4444-4444-4443";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0003";

    @Resource(name = "workDao")
    private WorkDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/WorksEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/WorksEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }

    @Test
    public void getWorksByOrcid() {
        List<Object[]> works = dao.getWorksByOrcid(USER_ORCID, false);
        assertEquals(3, works.size());
        List<Integer> featuredWorks = new ArrayList<Integer>();
        for (Object[] result : works) {
            int featuredDisplayIndex = (int) result[17];
            if (featuredDisplayIndex > 0) {
                featuredWorks.add(featuredDisplayIndex);
            };
        }
        assertEquals(featuredWorks.size(), 2);
    }

    @Test
    public void getFeaturedWorksByOrcid() {
        List<Object[]> works = dao.getWorksByOrcid(USER_ORCID, true);
        assertEquals(2, works.size());
        for (Object[] result : works) {
            int featuredDisplayIndex = (int) result[17];
            assertTrue(featuredDisplayIndex > 0);
        }
    }

    @Test
    public void removeAllWorksTest() {
        long initialNumber = dao.countAll();
        long elementThatBelogsToUser = dao.getWorkLastModifiedList(USER_ORCID).size();
        long otherUserElements = dao.getWorkLastModifiedList(OTHER_USER_ORCID).size();
        assertTrue(elementThatBelogsToUser > 0);
        assertTrue(elementThatBelogsToUser < initialNumber);
        assertEquals(6, otherUserElements);
        //Remove all elements that belongs to USER_ORCID
        dao.removeWorks(USER_ORCID);

        long finalNumberOfElements = dao.countAll();
        long finalNumberOfOtherUserElements = dao.getWorkLastModifiedList(OTHER_USER_ORCID).size();
        long finalNumberOfElementsThatBelogsToUser = dao.getWorkLastModifiedList(USER_ORCID).size();
        assertEquals(0, finalNumberOfElementsThatBelogsToUser);
        assertEquals(otherUserElements, finalNumberOfOtherUserElements);
        assertEquals((initialNumber - elementThatBelogsToUser), finalNumberOfElements);
    }

    @Test
    public void testHasPublicWorks() {
        assertTrue(dao.hasPublicWorks("0000-0000-0000-0003"));
        assertFalse(dao.hasPublicWorks("0000-0000-0000-0002"));
    }

    @Test
    public void mergeTest() {
        WorkEntity e = dao.find(18L);
        e.setDescription("UPDATED_DESCRIPTION");
        Date dateCreated = e.getDateCreated();
        Date lastModified = e.getLastModified();
        dao.merge(e);

        WorkEntity updated = dao.find(18L);
        assertEquals(dateCreated, updated.getDateCreated());
        assertTrue(updated.getLastModified().after(lastModified));
    }

    @Test
    public void persistTest() {
        WorkEntity e = new WorkEntity();
        e.setOrcid("0000-0000-0000-0002");
        e.setVisibility("PRIVATE");

        dao.persist(e);
        assertNotNull(e.getId());
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertEquals(e.getDateCreated(), e.getLastModified());

        WorkEntity e2 = dao.find(e.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        assertEquals(e.getLastModified(), e2.getLastModified());
        assertEquals(e.getDateCreated(), e2.getDateCreated());
        assertEquals(e2.getDateCreated(), e2.getLastModified());
    }

    @Test
    public void getWorksByOrcidId_Deprecated() {
        List<WorkEntity> works = dao.getWorksByOrcidId("0000-0000-0000-0003");
        List<Long> existingIds = new ArrayList<Long>(Arrays.asList(11L, 12L, 13L, 14L, 15L, 16L));
        assertEquals(6, works.size());
        for(WorkEntity w : works) {
            assertTrue(existingIds.contains(w.getId()));
            existingIds.remove(w.getId());
        }
        assertTrue("Elements not found: " + existingIds, existingIds.isEmpty());
    }
}
