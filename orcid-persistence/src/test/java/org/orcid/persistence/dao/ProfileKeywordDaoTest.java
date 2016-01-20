/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.dao;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ProfileKeywordDaoTest extends DBUnitTest {

    @Resource
    private ProfileKeywordDao profileKeywordDao;

    @Resource
    private OtherNameDao otherNameDao;
    
    @Resource
    private ProfileDao profileDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Before
    public void beforeRunning() {
        assertNotNull(profileKeywordDao);
    }

    @Test    
    public void testfindProfileKeywords() {
        List<ProfileKeywordEntity> keywords = profileKeywordDao.getProfileKeywors("4444-4444-4444-4441");
        assertNotNull(keywords);
        assertEquals(2, keywords.size());
    }

    @Test    
    public void testAddProfileKeyword() {
        assertEquals(4, profileKeywordDao.getProfileKeywors("4444-4444-4444-4443").size());
        boolean result = profileKeywordDao.addProfileKeyword("4444-4444-4444-4443", "new_keyword", "4444-4444-4444-4443", null);
        assertTrue(result);    
        assertEquals(5, profileKeywordDao.getProfileKeywors("4444-4444-4444-4443").size());
        
        ProfileKeywordEntity entity = new ProfileKeywordEntity();
        entity.setKeywordName("this is my keyword");
        entity.setProfile(new ProfileEntity("4444-4444-4444-4443"));
        entity.setSource(new SourceEntity(new ProfileEntity("4444-4444-4444-4443")));
        entity.setVisibility(Visibility.PUBLIC);        
        
        profileKeywordDao.persist(entity);        
        assertEquals(6, profileKeywordDao.getProfileKeywors("4444-4444-4444-4443").size());
    }
        
    @Test
    public void testDeleteProfileKeyword() {
        assertEquals(1, profileKeywordDao.getProfileKeywors("4444-4444-4444-4442").size());
        profileKeywordDao.deleteProfileKeyword("4444-4444-4444-4442", "My keyword");
        assertEquals(0, profileKeywordDao.getProfileKeywors("4444-4444-4444-4442").size());
    }
}
