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

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-persistence-context.xml" })
public class WebhookDaoTest {

    @Resource
    private WebhookDao webhookDao;

    @Resource
    private ProfileDao profileDao;

    @Resource
    private ClientDetailsDao clientDetailsDao;

    @Test
    @Rollback(true)
    public void testMergeFindAndRemove() {
        ProfileEntity profile = new ProfileEntity("4444-4444-4444-4441");
        profileDao.merge(profile);
        ProfileEntity clientProfile = new ProfileEntity("4444-4444-4444-4442");
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
        assertEquals("4444-4444-4444-4441", retrieved.getProfile().getId());
        assertEquals("http://semantico.com/orcid/1234", retrieved.getUri());
        assertEquals("4444-4444-4444-4442", retrieved.getClientDetails().getClientId());
        assertTrue(retrieved.isEnabled());
        assertEquals(0, retrieved.getFailedAttemptCount());
        assertNull(retrieved.getLastFailed());

        webhookDao.remove(pk);
        retrieved = webhookDao.find(pk);
        assertNull(retrieved);
    }

}
