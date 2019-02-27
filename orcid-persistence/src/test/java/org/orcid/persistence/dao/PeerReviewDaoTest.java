package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class PeerReviewDaoTest extends DBUnitTest {

    private static String USER_ORCID = "4444-4444-4444-4446";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0003";
    
    @Resource(name = "peerReviewDao")
    private PeerReviewDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/OrgsEntityData.xml", "/data/PeerReviewEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/PeerReviewEntityData.xml", "/data/OrgsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }

    @Test
    public void removeAllTest() {
        long initialNumber = dao.countAll();
        long elementThatBelogsToUser = dao.getByUser(USER_ORCID, 0L).size();
        long otherUserElements = dao.getByUser(OTHER_USER_ORCID, 0L).size();
        assertEquals(4, elementThatBelogsToUser);
        assertTrue(elementThatBelogsToUser < initialNumber);
        assertEquals(5, otherUserElements);
        //Remove all elements that belongs to USER_ORCID
        dao.removeAllPeerReviews(USER_ORCID);
        
        long finalNumberOfElements = dao.countAll();
        long finalNumberOfOtherUserElements = dao.getByUser(OTHER_USER_ORCID, 0L).size();
        long finalNumberOfElementsThatBelogsToUser = dao.getByUser(USER_ORCID, 0L).size();
        assertEquals(0, finalNumberOfElementsThatBelogsToUser);
        assertEquals(otherUserElements, finalNumberOfOtherUserElements);
        assertEquals((initialNumber - elementThatBelogsToUser), finalNumberOfElements);
    }
    
    @Test
    public void hasPublicPeerReviewsTest() {
        assertTrue(dao.hasPublicPeerReviews("0000-0000-0000-0003"));
        assertFalse(dao.hasPublicPeerReviews("0000-0000-0000-0002"));
    }
}
