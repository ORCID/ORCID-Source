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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.orcid.test.DBUnitTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
@Transactional
public class WebhookDaoTest extends DBUnitTest {

    @Resource
    private WebhookDao webhookDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    private static final List<String> DATA_FILES = Arrays.asList("/data/SecurityQuestionEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml",
            "/data/ProfileWorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/WebhookEntityData.xml");

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES, null);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        List<String> reversedDataFiles = new ArrayList<String>(DATA_FILES);
        Collections.reverse(reversedDataFiles);
        removeDBUnitData(reversedDataFiles, null);
    }

    @Test
    @Rollback(true)
    public void testMergeFindAndRemove() {
        ProfileEntity profile = new ProfileEntity("4444-4444-4444-4448");
        profileDao.merge(profile);
        ProfileEntity clientProfile = new ProfileEntity("4444-4444-4444-4449");
        profileDao.merge(clientProfile);
        ClientDetailsEntity clientDetails = new ClientDetailsEntity();
        clientDetails.setProfileEntity(clientProfile);
        clientDetails.setId(clientProfile.getId());
        clientDetailsDao.merge(clientDetails);
        WebhookEntity webhook = new WebhookEntity();
        webhook.setProfile(profile);
        webhook.setUri("http://semantico.com/orcid/1234");
        webhook.setClientDetails(clientDetails);
        webhookDao.merge(webhook);

        WebhookEntityPk pk = new WebhookEntityPk();
        pk.setProfile(profile);
        pk.setUri("http://semantico.com/orcid/1234");
        WebhookEntity retrieved = webhookDao.find(pk);

        assertNotNull(retrieved);
        assertEquals("4444-4444-4444-4448", retrieved.getProfile().getId());
        assertEquals("http://semantico.com/orcid/1234", retrieved.getUri());
        assertEquals("4444-4444-4444-4449", retrieved.getClientDetails().getClientId());
        assertTrue(retrieved.isEnabled());
        assertEquals(0, retrieved.getFailedAttemptCount());
        assertNull(retrieved.getLastFailed());

        webhookDao.remove(pk);
        retrieved = webhookDao.find(pk);
        assertNull(retrieved);
    }

    @Test
    @Rollback(true)
    public void testFindWebhooksReadyToProcess() {
        Date now = new Date();
        List<WebhookEntity> results = webhookDao.findWebhooksReadyToProcess(now, 5, 10);
        assertNotNull(results);
        assertEquals(1, results.size());
        Set<String> orcids = new HashSet<>();
        for (WebhookEntity result : results) {
            orcids.add(result.getProfile().getId());
        }
        assertTrue(orcids.contains("4444-4444-4444-4443"));
    }

    @Test
    @Rollback(true)
    public void testFindWebhooksReadyToProcessWhenIsNotReadyForRetry() {
        Date now = new Date();
        WebhookEntityPk pk = new WebhookEntityPk();
        pk.setProfile(profileDao.find("4444-4444-4444-4443"));
        pk.setUri("http://nowhere.com/orcid/4444-4444-4444-4443");
        WebhookEntity webhook = webhookDao.find(pk);
        webhook.setLastFailed(new Date(now.getTime() - 120 * 1000));
        webhook.setFailedAttemptCount(1);
        webhookDao.merge(webhook);

        List<WebhookEntity> results = webhookDao.findWebhooksReadyToProcess(now, 5, 10);
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    @Rollback(true)
    public void testCountWebhooksReadyToProcess() {
        Date now = new Date();
        long count = webhookDao.countWebhooksReadyToProcess(now, 5);
        assertNotNull(count);
        assertEquals(1, count);
    }

}
