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
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.persistence.jpa.entities.BiographyEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(inheritInitializers = false, inheritLocations = false, locations = { "classpath:orcid-persistence-context.xml" })
public class BiographyDaoTest extends DBUnitTest {
    @Resource 
    BiographyDao biographyDao;
    
    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/BiographyEntityData.xml"));
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/BiographyEntityData.xml", "/data/ProfileEntityData.xml", "/data/SourceClientDetailsEntityData.xml", 
                "/data/SecurityQuestionEntityData.xml"));
    }
    
    @Test
    public void testfindByOrcid() {
        BiographyEntity bio = biographyDao.getBiography("4444-4444-4444-4447");
        assertNotNull(bio);
        assertEquals("Biography for 4444-4444-4444-4447", bio.getBiography());
        assertEquals(Visibility.LIMITED, bio.getVisibility());        
    }
    
    @Test
    public void testUpdate() {
        BiographyEntity bio = biographyDao.getBiography("4444-4444-4444-4442");
        assertNotNull(bio);
        assertEquals("Biography for 4444-4444-4444-4442", bio.getBiography());
        assertEquals(Visibility.PUBLIC, bio.getVisibility()); 
        
        bio.setBiography("Updated biography");
        bio.setVisibility(Visibility.LIMITED);
        assertTrue(biographyDao.updateBiography("4444-4444-4444-4442", bio.getBiography(), bio.getVisibility()));
        
        BiographyEntity updatedBio = biographyDao.getBiography("4444-4444-4444-4442");
        assertNotNull(updatedBio);
        assertEquals("Updated biography", bio.getBiography());
        assertEquals(Visibility.LIMITED, bio.getVisibility());
    }
}
