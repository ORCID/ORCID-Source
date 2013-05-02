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
import static junit.framework.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.PersistenceException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ResearcherUrlDaoTest extends DBUnitTest {

    @Resource
    private ResearcherUrlDao researcherUrlDao;
    
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
        assertNotNull(researcherUrlDao);        
    }
    
    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testfindResearcherUrls() {
       List<ResearcherUrlEntity> researcherUrls = researcherUrlDao.getResearcherUrls("4444-4444-4444-4443");
       assertNotNull(researcherUrls);
       assertEquals(2, researcherUrls.size());
    }
    
    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testfindResearcherUrl() {
       ResearcherUrlEntity researcherUrl = researcherUrlDao.getResearcherUrl(1);
       assertNotNull(researcherUrl);
       assertEquals("444_1", researcherUrl.getUrlName());
    }
    
    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddResearcherUrl(){
        assertEquals(2,researcherUrlDao.getResearcherUrls("4444-4444-4444-4443").size());
        boolean result = researcherUrlDao.addResearcherUrls("4444-4444-4444-4443", "www.4443.com", "test");
        assertTrue(result);
        assertEquals(3,researcherUrlDao.getResearcherUrls("4444-4444-4444-4443").size());
    }
    
    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testDeleteResearcherUrl(){
        List<ResearcherUrlEntity> researcherUrls = researcherUrlDao.getResearcherUrls("4444-4444-4444-4443");
        assertNotNull(researcherUrls);
        assertEquals(2,researcherUrls.size());
        researcherUrlDao.deleteResearcherUrl(researcherUrls.get(0).getId());
        researcherUrls = researcherUrlDao.getResearcherUrls("4444-4444-4444-4443");
        assertNotNull(researcherUrls);
        assertEquals(1,researcherUrls.size());
    }
    
    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testCannotAddDuplicatedResearcherUrl(){
        try {
            researcherUrlDao.addResearcherUrls("4444-4444-4444-4443", "http://www.researcherurl2.com?id=1", "test");
            fail();
        } catch (PersistenceException e){
            
        } 
    }
}