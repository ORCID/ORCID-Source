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

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.persistence.jpa.entities.AddressEntity;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class AddressDaoTest extends DBUnitTest {
    @Resource
    AddressDao addressDao;

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
        assertNotNull(addressDao);
    }

    @Test
    public void findTest() {
        AddressEntity address = addressDao.getAddress("4444-4444-4444-4442", 1L);
        assertNotNull(address);
        assertNotNull(address.getLastModified());
        assertNotNull(address.getDateCreated());
        assertEquals(Long.valueOf(1), address.getId());
        assertNotNull(address.getUser());
        assertEquals("4444-4444-4444-4442", address.getUser().getId());
        assertEquals(Iso3166Country.US, address.getIso2Country());
        assertTrue(address.getPrimary());

        address = addressDao.getAddress("4444-4444-4444-4447", 2L);
        assertNotNull(address);
        assertNotNull(address.getLastModified());
        assertNotNull(address.getDateCreated());
        assertEquals(Long.valueOf(2), address.getId());
        assertNotNull(address.getUser());
        assertEquals("4444-4444-4444-4447", address.getUser().getId());
        assertEquals(Iso3166Country.US, address.getIso2Country());
        assertTrue(address.getPrimary());

        address = addressDao.getAddress("4444-4444-4444-4447", 3L);
        assertNotNull(address);
        assertNotNull(address.getLastModified());
        assertNotNull(address.getDateCreated());
        assertEquals(Long.valueOf(3), address.getId());
        assertNotNull(address.getUser());
        assertEquals("4444-4444-4444-4447", address.getUser().getId());
        assertEquals(Iso3166Country.CR, address.getIso2Country());
        assertFalse(address.getPrimary());
    }
    
    @Test
    public void findByOrcidTest() {
        List<AddressEntity> addresses = addressDao.getAddresses("4444-4444-4444-4447", 0L);
        assertNotNull(addresses);
        assertEquals(4, addresses.size());
    }

    @Test
    public void pendingToMigrateTest() {
        List<Object[]> pendingToMigrate = addressDao.findAddressesToMigrate();
        assertNotNull(pendingToMigrate);
        assertEquals(1, pendingToMigrate.size());

        for (Object[] item : pendingToMigrate) {
            String orcid = (String) item[0];
            String iso2Country = (String) item[1];
            assertEquals("5555-5555-5555-5558", orcid);
            assertEquals("GB", iso2Country);
        }
    }
}
