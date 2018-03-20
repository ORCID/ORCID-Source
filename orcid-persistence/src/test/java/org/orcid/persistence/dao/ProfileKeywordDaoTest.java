package org.orcid.persistence.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ProfileKeywordDaoTest extends DBUnitTest {

    private static String USER_ORCID = "0000-0000-0000-0003";
    private static String OTHER_USER_ORCID = "0000-0000-0000-0001";
    
    @Resource(name = "profileKeywordDao")
    private ProfileKeywordDao dao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Test    
    public void testfindProfileKeywords() {
        List<ProfileKeywordEntity> keywords = dao.getProfileKeywords("4444-4444-4444-4441", 0L);
        assertNotNull(keywords);
        assertEquals(2, keywords.size());
    }

    @Test    
    public void testAddProfileKeyword() {
        assertEquals(4, dao.getProfileKeywords("4444-4444-4444-4443", 0L).size());
        boolean result = dao.addProfileKeyword("4444-4444-4444-4443", "new_keyword", "4444-4444-4444-4443", null, org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);
        assertTrue(result);    
        assertEquals(5, dao.getProfileKeywords("4444-4444-4444-4443", 0L).size());
        
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        entity.setKeywordName("this is my keyword");
        entity.setProfile(new ProfileEntity("4444-4444-4444-4443"));
        entity.setSourceId("4444-4444-4444-4443");
        entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PUBLIC);        
        
        dao.persist(entity);        
        assertEquals(6, dao.getProfileKeywords("4444-4444-4444-4443", 0L).size());
    }
        
    @Test
    public void testDeleteProfileKeyword() {
        assertEquals(1, dao.getProfileKeywords("4444-4444-4444-4442", 0L).size());
        dao.deleteProfileKeyword("4444-4444-4444-4442", "My keyword");
        assertEquals(0, dao.getProfileKeywords("4444-4444-4444-4442", 0L).size());
    }
    
    @Test
    public void removeAllTest() {
        long initialNumber = dao.countAll();
        long elementThatBelogsToUser = dao.getProfileKeywords(USER_ORCID, 0L).size();
        long otherUserElements = dao.getProfileKeywords(OTHER_USER_ORCID, 0L).size();
        assertEquals(5, elementThatBelogsToUser);
        assertTrue(elementThatBelogsToUser < initialNumber);
        assertEquals(3, otherUserElements);
        //Remove all elements that belongs to USER_ORCID
        dao.removeAllKeywords(USER_ORCID);
        
        long finalNumberOfElements = dao.countAll();
        long finalNumberOfOtherUserElements = dao.getProfileKeywords(OTHER_USER_ORCID, 0L).size();
        long finalNumberOfElementsThatBelogsToUser = dao.getProfileKeywords(USER_ORCID, 0L).size();
        assertEquals(0, finalNumberOfElementsThatBelogsToUser);
        assertEquals(otherUserElements, finalNumberOfOtherUserElements);
        assertEquals((initialNumber - elementThatBelogsToUser), finalNumberOfElements);
    }
}
