package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class WorkDaoTest extends DBUnitTest {

    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "4444-4444-4444-4443";
    
    @Resource(name = "workDao")
    private WorkDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/WorksEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/WorksEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }
    
    @Test
    public void removeAllWorksTest() {
        long initialNumber = dao.countAll();
        long elementThatBelogsToUser = dao.getWorkLastModifiedList(USER_ORCID).size();
        long otherUserElements = dao.getWorkLastModifiedList(OTHER_USER_ORCID).size();
        assertTrue(elementThatBelogsToUser > 0);
        assertTrue(elementThatBelogsToUser < initialNumber);
        assertEquals(3, otherUserElements);
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
    public void getWorksByOrcidIdTest() {
        List<WorkEntity> works = dao.getWorksByOrcidId("0000-0000-0000-0003");
        List<Long> existingIds = new ArrayList<Long>(Arrays.asList(11L, 12L, 13L, 14L, 15L, 16L));
        assertEquals(6, works.size());
        for(WorkEntity w : works) {
            assertTrue(existingIds.contains(w.getId()));
            existingIds.remove(w.getId());
        }
        assertTrue("Elements not found: " + existingIds, existingIds.isEmpty());
    }
    
    @Test
    public void testHasPublicWorks() {
        fail();
    }
}
