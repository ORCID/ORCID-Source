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
package org.orcid.core.manager.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.ProfileEntityManager;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * 2011-2012 ORCID
 * 
 * @author: Declan Newman (declan) Date: 10/02/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml" })
public class ProfileEntityManagerImplTest extends DBUnitTest {

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Resource
    private ProfileEntityManager profileEntityManager;

    @Before
    public void init() {
        assertNotNull(profileEntityManager);
    }

    @Test
    @Transactional("transactionManager")
    @Rollback(true)
    public void testFindByOrcid() throws Exception {
        String harrysOrcid = "4444-4444-4444-4444";
        ProfileEntity profileEntity = profileEntityManager.findByOrcid(harrysOrcid);
        assertNotNull(profileEntity);
        assertEquals("Harry", profileEntity.getGivenNames());
        assertEquals("Secombe", profileEntity.getFamilyName());
        assertEquals(harrysOrcid, profileEntity.getId());
    }
    
    @Test
    public void testDeprecateProfile() throws Exception {
        ProfileEntity profileEntityToDeprecate = profileEntityManager.findByOrcid("4444-4444-4444-4441");
        ProfileEntity primaryProfileEntity = profileEntityManager.findByOrcid("4444-4444-4444-4442");        
        assertNull(profileEntityToDeprecate.getPrimaryRecord());        
        boolean result = profileEntityManager.deprecateProfile(profileEntityToDeprecate, primaryProfileEntity);
        assertTrue(result);
        profileEntityToDeprecate = profileEntityManager.findByOrcid("4444-4444-4444-4441");        
        assertNotNull(profileEntityToDeprecate.getPrimaryRecord());
        assertEquals("4444-4444-4444-4442", profileEntityToDeprecate.getPrimaryRecord().getId());
    }

}
