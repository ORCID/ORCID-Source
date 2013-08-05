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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.WorkContributorEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class WorkContributorDaoTest extends DBUnitTest {
    @Resource
    WorkContributorDao workContributorDao;
    @Resource
    WorkDao workDao;

    @Resource
    ProfileDao profileDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/WorksEntityData.xml", "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    @Rollback(true)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testAddWorkContributor() {
        WorkContributorEntity workContributor = new WorkContributorEntity();
        ProfileEntity profile = getProfile();
        WorkEntity work = getWork();

        if (profile == null || work == null)
            fail();

        workContributor.setProfile(profile);
        workContributor.setWork(work);

        assertNull(workContributor.getId());

        try {
            workContributor = workContributorDao.addWorkContributor(workContributor);
        } catch (Exception e) {
            fail();
        }

        assertNotNull(workContributor.getId());
    }

    private WorkEntity getWork() {
        List<WorkEntity> works = workDao.getAll();
        if (works == null || works.isEmpty())
            return null;
        return works.get(0);
    }

    private ProfileEntity getProfile() {
        List<ProfileEntity> profiles = profileDao.getAll();
        if (profiles == null || profiles.isEmpty())
            return null;
        return profiles.get(0);
    }

}
