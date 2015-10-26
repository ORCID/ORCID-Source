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
package org.orcid.internal.server;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.manager.OrcidProfileManager;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.internal.server.delegator.InternalApiServiceDelegator;
import org.orcid.internal.util.LastModifiedResponse;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.test.DBUnitTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-internal-api-context.xml", "classpath:orcid-internal-api-security-context.xml" })
public class InternalApiServiceDelegatorTest extends DBUnitTest {

    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml");

    private static final String USER_ORCID = "5555-5555-5555-5558";
    
    @Resource
    private InternalApiServiceDelegator internalApiServiceDelegator;
    
    @Resource
    OrcidProfileManager orcidProfileManager;
    
    @Before
    public void before() throws Exception {
        initDBUnitData(DATA_FILES);
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.INTERNAL_PERSON_LAST_MODIFIED);
    }

    @Test
    public void viewStatusText() {
        Response response = internalApiServiceDelegator.viewStatusText();
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertEquals("OK I am here", String.valueOf(response.getEntity()));
    }
    
    @Test
    public void viewLastModified() {
        Response response = internalApiServiceDelegator.viewPersonLastModified(USER_ORCID);
        assertNotNull(response);
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity().getClass().isAssignableFrom(LastModifiedResponse.class));
        LastModifiedResponse obj = (LastModifiedResponse) response.getEntity();
        assertNotNull(obj);
        assertEquals(USER_ORCID, obj.getOrcid());
        assertEquals(orcidProfileManager.retrieveLastModifiedDate(USER_ORCID).toString(), obj.getLastModified());
    }
}
