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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ResearcherUrlEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class ResearcherUrlDaoTest extends DBUnitTest {

    @Resource
    private ResearcherUrlDao researcherUrlDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"));
    }

    @Before
    public void beforeRunning() {
        assertNotNull(researcherUrlDao);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testfindResearcherUrls() {
        List<ResearcherUrlEntity> researcherUrls = researcherUrlDao.getResearcherUrls("4444-4444-4444-4443", 0L);
        assertNotNull(researcherUrls);
        assertEquals(6, researcherUrls.size());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testfindResearcherUrl() {
        ResearcherUrlEntity researcherUrl = researcherUrlDao.getResearcherUrl("4444-4444-4444-4441", 1L);
        assertNotNull(researcherUrl);
        assertEquals("444_1", researcherUrl.getUrlName());
        
        try {
            researcherUrl = researcherUrlDao.getResearcherUrl("4444-4444-4444-5555", 1L);
            fail();
        } catch(NoResultException e) {
            
        }
        
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddResearcherUrl() {
        assertEquals(6, researcherUrlDao.getResearcherUrls("4444-4444-4444-4443", 0L).size());
        ResearcherUrlEntity newRUrl = new ResearcherUrlEntity();
        newRUrl.setDateCreated(new Date());
        newRUrl.setLastModified(new Date());
        newRUrl.setClientSourceId("APP-5555555555555555");
        newRUrl.setUrl("www.4443.com");
        newRUrl.setUrlName("test");
        newRUrl.setUser(new ProfileEntity("4444-4444-4444-4443"));
        newRUrl.setVisibility(Visibility.PUBLIC);
        newRUrl = researcherUrlDao.merge(newRUrl);
        assertNotNull(newRUrl);
        assertEquals(7, researcherUrlDao.getResearcherUrls("4444-4444-4444-4443", 0L).size());
        for(ResearcherUrlEntity rUrl : researcherUrlDao.getResearcherUrls("4444-4444-4444-4443", 0L)) {
            if("www.4443.com".equals(rUrl.getUrl())) {
                assertEquals("APP-5555555555555555", rUrl.getElementSourceId());
            }
        }
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testDeleteResearcherUrl() {
        List<ResearcherUrlEntity> researcherUrls = researcherUrlDao.getResearcherUrls("4444-4444-4444-4443", 0L);
        assertNotNull(researcherUrls);
        assertEquals(6, researcherUrls.size());
        researcherUrlDao.deleteResearcherUrl("4444-4444-4444-4443", researcherUrls.get(0).getId());
        researcherUrls = researcherUrlDao.getResearcherUrls("4444-4444-4444-4443", 0L);
        assertNotNull(researcherUrls);
        assertEquals(5, researcherUrls.size());
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testCannotAddDuplicatedResearcherUrl() {
        try {
            ResearcherUrlEntity newRUrl = new ResearcherUrlEntity();
            newRUrl.setDateCreated(new Date());
            newRUrl.setLastModified(new Date());
            newRUrl.setClientSourceId("4444-4444-4444-4443");
            newRUrl.setUrl("http://www.researcherurl2.com?id=1");
            newRUrl.setUrlName("test");
            newRUrl.setUser(new ProfileEntity("4444-4444-4444-4443"));
            newRUrl.setVisibility(Visibility.PUBLIC);
            newRUrl = researcherUrlDao.merge(newRUrl);
            assertNotNull(newRUrl);
            fail();
        } catch (PersistenceException e) {

        }
    }
}