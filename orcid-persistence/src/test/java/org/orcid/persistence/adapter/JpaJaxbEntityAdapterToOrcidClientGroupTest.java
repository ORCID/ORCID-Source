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
package org.orcid.persistence.adapter;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.jaxb.model.clientgroup.OrcidClientGroup;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 
 * @author Will Simpson
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class JpaJaxbEntityAdapterToOrcidClientGroupTest extends DBUnitTest {

    public static final String CLIENT_GROUP = "/orcid-client-group-for-db-unit-data.xml";

    @Autowired
    private GenericDao<ProfileEntity, String> profileDao;

    @Autowired
    private JpaJaxbEntityAdapter adapter;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml",
                "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileWorksEntityData.xml", "/data/WorksEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void testToOrcidProfile() throws IOException {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-4446");
        OrcidClientGroup orcidClientGroup = adapter.toOrcidClientGroup(profileEntity);
        assertNotNull(orcidClientGroup);
        String expected = IOUtils.toString(getClass().getResourceAsStream(CLIENT_GROUP), "UTF-8");
        assertEquals(expected, orcidClientGroup.toString());
    }

}
