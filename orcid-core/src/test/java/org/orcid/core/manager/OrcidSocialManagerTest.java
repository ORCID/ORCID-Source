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
package org.orcid.core.manager;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.orcid.core.BaseTest;

public class OrcidSocialManagerTest extends BaseTest {

    @Resource
    OrcidSocialManager orcidSocialManager;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/SourceClientDetailsEntityData.xml",
                "/data/ProfileEntityData.xml", "/data/ClientDetailsEntityData.xml"));
    }

    @Before
    public void before() {
        assertNotNull(orcidSocialManager);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml",
                "/data/SecurityQuestionEntityData.xml"));
    }

    @Test
    public void testGetTwitterAuthUrl() {
        try {
            String url = orcidSocialManager.getTwitterAuthorizationUrl("4444-4444-4444-4442");
            assertNotNull(url);
        } catch (Exception e) {
            fail();
        }
    }

}
