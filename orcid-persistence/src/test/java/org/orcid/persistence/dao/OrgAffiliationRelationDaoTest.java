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
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OrgAffiliationRelationDaoTest extends DBUnitTest {

    private static String USER_ORCID = "4444-4444-4444-4443";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0003";
    
    @Resource(name = "orgAffiliationRelationDao")
    private OrgAffiliationRelationDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/OrgsEntityData.xml", "/data/OrgAffiliationEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrgAffiliationEntityData.xml", "/data/OrgsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml"));
    }
    
    @Test
    public void removeAllTest() {
        long initialNumber = dao.countAll();
        long elementThatBelogsToUser = dao.getByUser(USER_ORCID).size();
        long otherUserElements = dao.getByUser(OTHER_USER_ORCID).size();
        assertEquals(3, elementThatBelogsToUser);
        assertTrue(elementThatBelogsToUser < initialNumber);
        assertEquals(35, otherUserElements);
        //Remove all elements that belongs to USER_ORCID
        dao.removeAllAffiliations(USER_ORCID);
        
        long finalNumberOfElements = dao.countAll();
        long finalNumberOfOtherUserElements = dao.getByUser(OTHER_USER_ORCID).size();
        long finalNumberOfElementsThatBelogsToUser = dao.getByUser(USER_ORCID).size();
        assertEquals(0, finalNumberOfElementsThatBelogsToUser);
        assertEquals(otherUserElements, finalNumberOfOtherUserElements);
        assertEquals((initialNumber - elementThatBelogsToUser), finalNumberOfElements);
    }
    
    @Test
    public void hasPublicAffiliationsTest() {
        assertTrue(dao.hasPublicAffiliations("0000-0000-0000-0003"));
        assertFalse(dao.hasPublicAffiliations("0000-0000-0000-0002"));
    }
    
    @Test
    public void mergeTest() {
        OrgAffiliationRelationEntity e = dao.find(1006L);
        e.setDisplayIndex(1100L);
        Date dateCreated = e.getDateCreated();
        Date lastModified = e.getLastModified();
        dao.merge(e);

        OrgAffiliationRelationEntity updated = dao.find(1006L);
        assertEquals(dateCreated, updated.getDateCreated());
        assertTrue(updated.getLastModified().after(lastModified));
    }
    
    @Test
    public void persistTest() {
        OrgEntity o = new OrgEntity();
        o.setId(2L);
        OrgAffiliationRelationEntity e = new OrgAffiliationRelationEntity();
        e.setProfile(new ProfileEntity("0000-0000-0000-0002")); 
        e.setVisibility("PRIVATE");
        e.setAffiliationType("EDUCATION"); 
        e.setOrg(o);
        
        dao.persist(e);
        assertNotNull(e.getId());
        assertNotNull(e.getDateCreated());
        assertNotNull(e.getLastModified());
        assertEquals(e.getDateCreated(), e.getLastModified());
        
        OrgAffiliationRelationEntity e2 = dao.find(e.getId());
        assertNotNull(e2.getDateCreated());
        assertNotNull(e2.getLastModified());
        assertEquals(e.getLastModified(), e2.getLastModified());
        assertEquals(e.getDateCreated(), e2.getDateCreated());
        assertEquals(e2.getDateCreated(), e2.getLastModified());
    }
}
