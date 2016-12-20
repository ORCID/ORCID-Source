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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.persistence.NoResultException;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.persistence.jpa.entities.OtherNameEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OtherNameDaoTest extends DBUnitTest {

    @Resource
    private OtherNameDao otherNameDao;

    @Resource
    private ProfileDao profileDao;

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
        assertNotNull(otherNameDao);
    }

    @Test
    public void testfindOtherNameByOrcid() {
        List<OtherNameEntity> otherNames = otherNameDao.getOtherNames("4444-4444-4444-4446", 0L);
        assertNotNull(otherNames);
        assertEquals(4, otherNames.size());
    }

    @Test
    public void testUpdateOtherName() {
        try {
            otherNameDao.updateOtherName(null);
            fail();
        } catch (UnsupportedOperationException e) {

        }
    }

    @Test
    public void testAddOtherName() {
        Date profileLastModifiedOrig = profileDao.retrieveLastModifiedDate("4444-4444-4444-4441");
        assertEquals(2, otherNameDao.getOtherNames("4444-4444-4444-4441", 0L).size());
        boolean result = otherNameDao.addOtherName("4444-4444-4444-4441", "OtherName");
        assertEquals(true, result);
        assertEquals(3, otherNameDao.getOtherNames("4444-4444-4444-4441", 0L).size());
        assertFalse("Profile last modified date should have been updated", profileLastModifiedOrig.after(profileDao.retrieveLastModifiedDate("4444-4444-4444-4441")));
        
        
        OtherNameEntity entity = new OtherNameEntity();
        entity.setDisplayName("The other name");
        entity.setProfile(new ProfileEntity("4444-4444-4444-4441"));
        entity.setSourceId("4444-4444-4444-4441");
        entity.setVisibility(Visibility.PUBLIC);
        otherNameDao.persist(entity);
        assertEquals(4, otherNameDao.getOtherNames("4444-4444-4444-4441", 0L).size());
    }

    @Test    
    public void testDeleteOtherName() {
        Date now = new Date();
        Date justBeforeStart = new Date(now.getTime() - 1000);
        List<OtherNameEntity> otherNames = otherNameDao.getOtherNames("4444-4444-4444-4443", 0L);
        assertNotNull(otherNames);
        assertEquals(2, otherNames.size());
        OtherNameEntity otherName = otherNames.get(0);
        assertTrue(otherNameDao.deleteOtherName(otherName));
        List<OtherNameEntity> updatedOtherNames = otherNameDao.getOtherNames("4444-4444-4444-4443", 0L);
        assertNotNull(updatedOtherNames);
        assertEquals(1, updatedOtherNames.size());
        assertTrue("Profile last modified date should have been updated", justBeforeStart.before(profileDao.retrieveLastModifiedDate("4444-4444-4444-4443")));
    }
    
    @Test
    public void testGetOtherName() {
        OtherNameEntity otherName = otherNameDao.getOtherName("4444-4444-4444-4443", 2L);
        assertNotNull(otherName);
        assertEquals("Flibberdy Flabinah", otherName.getDisplayName());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());
    }

    @Test(expected = NoResultException.class)
    public void testGetInvalidOtherName() {
        otherNameDao.getOtherName("4444-4444-4444-4443", 100L);
        fail();
    }
}