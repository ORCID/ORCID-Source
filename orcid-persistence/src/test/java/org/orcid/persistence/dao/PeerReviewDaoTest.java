package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-orcid-persistence-context.xml" })
public class PeerReviewDaoTest extends DBUnitTest {

    private static String USER_ORCID = "4444-4444-4444-4446";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0003";
    
    @Resource(name = "peerReviewDao")
    private PeerReviewDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
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
        assertTrue(dao.hasPublicPeerReviews("0000-0000-0000-0002"));
        assertFalse(dao.hasPublicPeerReviews("0000-0000-0000-0004"));
    }
    
    @Test
    public void mergeTest() {
        PeerReviewEntity e = dao.find(14L);
        e.setSubjectName("UPDATED_SUBJECT_NAME");
        Date dateCreated = e.getDateCreated();
        Date lastModified = e.getLastModified();
        dao.merge(e);

        PeerReviewEntity updated = dao.find(14L);
        assertEquals(dateCreated, updated.getDateCreated());
        assertTrue(updated.getLastModified().after(lastModified));
    }
    
    @Test
    public void persistTest() {
        OrgEntity o = new OrgEntity();
        o.setId(2L);
        PeerReviewEntity e = new PeerReviewEntity();
        e.setOrcid("0000-0000-0000-0002"); 
        e.setVisibility("PRIVATE");
        e.setRole("ROLE");
        e.setExternalIdentifiersJson("{&quot;workExternalIdentifier&quot;:[{&quot;workExternalIdentifierType&quot;:&quot;AGR&quot;,&quot;workExternalIdentifierId&quot;:{&quot;content&quot;:&quot;work:external-identifier-id#5&quot;}}]}");
        e.setType("TYPE");
        e.setOrg(o);
        
        dao.persist(e);
        assertNotNull(e.getId());
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertEquals(e.getDateCreated(), e.getLastModified());
        
        PeerReviewEntity e2 = dao.find(e.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        assertEquals(e.getLastModified(), e2.getLastModified());
        assertEquals(e.getDateCreated(), e2.getDateCreated());
        assertEquals(e2.getDateCreated(), e2.getLastModified());
    }
}
