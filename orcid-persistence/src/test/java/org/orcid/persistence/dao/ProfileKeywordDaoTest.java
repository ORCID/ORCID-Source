/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import org.orcid.persistence.jpa.entities.ProfileKeywordEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ProfileKeywordDaoTest extends DBUnitTest {

    @Resource
    private ProfileKeywordDao profileKeywordDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Before
    public void beforeRunning() {
        assertNotNull(profileKeywordDao);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testfindProfileKeywords() {
        List<ProfileKeywordEntity> keywords = profileKeywordDao.getProfileKeywors("4444-4444-4444-4443");
        assertNotNull(keywords);
        assertEquals(2, keywords.size());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddProfileKeyword() {
        assertEquals(2, profileKeywordDao.getProfileKeywors("4444-4444-4444-4443").size());
        boolean result = profileKeywordDao.addProfileKeyword("4444-4444-4444-4443", "new_keyword");
        assertTrue(result);
        assertEquals(3, profileKeywordDao.getProfileKeywors("4444-4444-4444-4443").size());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testDeleteProfileKeyword() {
        assertEquals(2, profileKeywordDao.getProfileKeywors("4444-4444-4444-4443").size());
        profileKeywordDao.deleteProfileKeyword("4444-4444-4444-4443", "tea making");
        assertEquals(1, profileKeywordDao.getProfileKeywors("4444-4444-4444-4443").size());
    }
}
