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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.OrcidOauth2AuthoriziationCodeDetail;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 24/04/2012
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OrcidOauth2AuthoriziationCodeDetailDaoTest extends DBUnitTest {

    @Resource(name = "orcidOauth2AuthoriziationCodeDetailDao")
    private OrcidOauth2AuthoriziationCodeDetailDao orcidOauth2AuthoriziationCodeDetailDao;

    @Resource(name = "profileDao")
    private ProfileDao profileDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/ClientDetailsEntityData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/SubjectEntityData.xml",
                "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    @Transactional
    @Rollback
    public void testOrcidOauth2AuthoriziationCodeDetailDaoPersist() {
        OrcidOauth2AuthoriziationCodeDetail detail = getOrcidOauth2AuthoriziationCodeDetail();
        orcidOauth2AuthoriziationCodeDetailDao.persist(detail);
        OrcidOauth2AuthoriziationCodeDetail anotherDetail = orcidOauth2AuthoriziationCodeDetailDao.find(detail.getId());
        assertNotNull(anotherDetail);
        checkValues(anotherDetail);
        anotherDetail = orcidOauth2AuthoriziationCodeDetailDao.removeAndReturn(anotherDetail.getId());
        assertNotNull(anotherDetail);
        anotherDetail = orcidOauth2AuthoriziationCodeDetailDao.find(anotherDetail.getId());
        assertNull(anotherDetail);
    }

    private void checkValues(OrcidOauth2AuthoriziationCodeDetail anotherDetail) {
        assertEquals("a-code", anotherDetail.getId());
        assertEquals("a-password", anotherDetail.getCredentials());
        assertEquals("a-session-id", anotherDetail.getSessionId());
        assertEquals("192.168.0.1", anotherDetail.getRemoteAddress());
        assertEquals(true, anotherDetail.getApproved());
        assertEquals(true, anotherDetail.getAuthenticated());
        assertEquals(2, anotherDetail.getAuthorities().size());
        assertEquals(2, anotherDetail.getResourceIds().size());
        assertEquals(2, anotherDetail.getScopes().size());
        assertEquals("4444-4444-4444-4441", anotherDetail.getProfileEntity().getId());

    }

    public OrcidOauth2AuthoriziationCodeDetail getOrcidOauth2AuthoriziationCodeDetail() {
        ProfileEntity profileEntity = profileDao.find("4444-4444-4444-4441");
        OrcidOauth2AuthoriziationCodeDetail detail = new OrcidOauth2AuthoriziationCodeDetail();
        detail.setId("a-code");
        detail.setApproved(true);
        detail.setAuthenticated(true);
        detail.setAuthorities(new HashSet<String>(Arrays.asList("ROLE_USER", "ROLE_CLIENT")));
        detail.setCredentials("a-password");
        detail.setProfileEntity(profileEntity);
        detail.setRemoteAddress("192.168.0.1");
        detail.setResourceIds(new HashSet<String>(Arrays.asList("orcid", "porkid")));
        detail.setScopes(new HashSet<String>(Arrays.asList("/orcid-profile/create", "/orcid-profile/update")));
        detail.setSessionId("a-session-id");
        return detail;
    }
}
