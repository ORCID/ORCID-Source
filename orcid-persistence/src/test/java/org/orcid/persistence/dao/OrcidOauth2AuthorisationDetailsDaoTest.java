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
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.OrcidOauth2TokenDetail;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 19/04/2012
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class OrcidOauth2AuthorisationDetailsDaoTest extends DBUnitTest {

    @Resource(name = "orcidOauth2TokenDetailDao")
    private OrcidOauth2TokenDetailDao orcidOauth2TokenDetailDao;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/SubjectEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/ClientDetailsEntityData.xml", "/data/OrcidOauth2AuthorisationDetailsData.xml"), null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        removeDBUnitData(Arrays.asList("/data/OrcidOauth2AuthorisationDetailsData.xml", "/data/ClientDetailsEntityData.xml", "/data/ProfileEntityData.xml",
                "/data/SubjectEntityData.xml", "/data/SecurityQuestionEntityData.xml"), null);
    }

    @Test
    @Transactional
    @Rollback
    public void testFindById() throws Exception {
        List<OrcidOauth2TokenDetail> all = orcidOauth2TokenDetailDao.getAll();
        assertEquals(3, all.size());

        for (OrcidOauth2TokenDetail detail : all) {
            OrcidOauth2TokenDetail another = orcidOauth2TokenDetailDao.find(detail.getId());
            assertNotNull(another);
            assertEquals(detail.getId(), another.getId());
            assertTrue(detail.getTokenExpiration().after(new Date()));
            String refreshTokenValue = detail.getRefreshTokenValue();
            assertNotNull(refreshTokenValue);
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByRefreshTokenValue() throws Exception {
        List<OrcidOauth2TokenDetail> all = orcidOauth2TokenDetailDao.getAll();
        assertEquals(3, all.size());

        for (OrcidOauth2TokenDetail detail : all) {
            String refreshToken = detail.getRefreshTokenValue();
            OrcidOauth2TokenDetail another = orcidOauth2TokenDetailDao.findByRefreshTokenValue(refreshToken);
            assertNotNull(another);
            assertEquals(refreshToken, another.getRefreshTokenValue());
            assertTrue(detail.getTokenExpiration().after(new Date()));
            assertTrue(another.getRefreshTokenExpiration().after(new Date()));
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByAuthenticationKey() throws Exception {
        List<OrcidOauth2TokenDetail> all = orcidOauth2TokenDetailDao.getAll();
        assertEquals(3, all.size());

        for (OrcidOauth2TokenDetail detail : all) {
            OrcidOauth2TokenDetail another = orcidOauth2TokenDetailDao.findByTokenValue(detail.getTokenValue());
            assertNotNull(another);
            assertEquals(detail.getId(), another.getId());
            assertTrue(detail.getTokenExpiration().after(new Date()));
            assertTrue(detail.getRefreshTokenExpiration().after(new Date()));
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByTokenValue() throws Exception {
        List<OrcidOauth2TokenDetail> all = orcidOauth2TokenDetailDao.getAll();
        assertEquals(3, all.size());

        for (OrcidOauth2TokenDetail detail : all) {
            OrcidOauth2TokenDetail another = orcidOauth2TokenDetailDao.findByAuthenticationKey(detail.getAuthenticationKey());
            assertNotNull(another);
            assertEquals(detail.getId(), another.getId());
            assertTrue(detail.getTokenExpiration().after(new Date()));
            assertTrue(detail.getRefreshTokenExpiration().after(new Date()));
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByUsername() throws Exception {
        List<OrcidOauth2TokenDetail> all = orcidOauth2TokenDetailDao.getAll();
        assertEquals(3, all.size());

        for (OrcidOauth2TokenDetail detail : all) {
            List<OrcidOauth2TokenDetail> allForClient = orcidOauth2TokenDetailDao.findByUserName(detail.getProfile().getId());
            assertEquals(1, allForClient.size());
        }
    }

    @Test
    @Transactional
    @Rollback
    public void testFindByClientId() throws Exception {
        List<OrcidOauth2TokenDetail> all = orcidOauth2TokenDetailDao.getAll();
        assertEquals(3, all.size());

        for (OrcidOauth2TokenDetail detail : all) {
            ClientDetailsEntity clientDetailsEntity = detail.getClientDetailsEntity();
            List<OrcidOauth2TokenDetail> allForClient = orcidOauth2TokenDetailDao.findByClientId(clientDetailsEntity.getProfileEntity().getId());
            assertEquals(1, allForClient.size());
        }
    }

}
