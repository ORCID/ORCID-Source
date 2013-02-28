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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.HearAboutEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class HearAboutDaoTest {

    @Resource(name = "hearAboutDao")
    private GenericDao<HearAboutEntity, Integer> hearAboutDao;

    @Test
    @Rollback(true)
    public void testMergeFindAndRemove() {
        HearAboutEntity hearAbout = new HearAboutEntity();
        hearAbout.setName("Grapevine");
        hearAboutDao.merge(hearAbout);
        assertNotNull(hearAbout.getId());
        HearAboutEntity retrieved = hearAboutDao.find(hearAbout.getId());
        assertNotNull(retrieved);
        assertEquals("Grapevine", retrieved.getName());
        hearAboutDao.remove(hearAbout.getId());
        retrieved = hearAboutDao.find(hearAbout.getId());
        assertNull(retrieved);
    }

    @Test
    @Rollback(true)
    public void testConstructor() {
        HearAboutEntity hearAbout = new HearAboutEntity(1, "Miracles");
        hearAboutDao.merge(hearAbout);
        assertNotNull(hearAbout.getId());
        HearAboutEntity retrieved = hearAboutDao.find(hearAbout.getId());
        assertNotNull(retrieved);
        assertEquals("Miracles", retrieved.getName());
        hearAboutDao.remove(hearAbout.getId());
        retrieved = hearAboutDao.find(hearAbout.getId());
        assertNull(retrieved);
    }

}
