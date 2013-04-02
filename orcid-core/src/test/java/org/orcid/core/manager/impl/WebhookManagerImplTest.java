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

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.annotation.Resource;

import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.orcid.core.BaseTest;
import org.orcid.core.manager.WebhookManager;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.ClientDetailsEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
public class WebhookManagerImplTest extends BaseTest {

    @Resource
    private WebhookManager webhookManager;

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private WebhookDao mockWebhookDao;

    private ClientDetailsEntity clientDetails;

    private ProfileEntity testProfile;

    @Before
    public void init() throws Exception {
        assertNotNull(webhookManager);

        WebhookManagerImpl webhookManagerImpl = getTargetObject(webhookManager, WebhookManagerImpl.class);
        webhookManagerImpl.setHttpClient(mockHttpClient);
        when(mockHttpClient.execute(Matchers.<HttpUriRequest> any())).thenReturn(new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_NOT_FOUND, "Not found"));
        when(mockHttpClient.execute(Matchers.<HttpPost> argThat(new ArgumentMatcher<HttpPost>() {
            public boolean matches(Object argument) {
                if (argument == null || !(argument instanceof HttpPost)) {
                    return false;
                }
                HttpPost httpPost = (HttpPost) argument;
                return httpPost.getURI().getHost().equals("qa-1.orcid.org");
            }
        }))).thenReturn(new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        webhookManagerImpl.setWebhookDao(mockWebhookDao);

        ProfileEntity profile = new ProfileEntity();
        profile.setId("0000-0000-0000-0001");
        clientDetails = new ClientDetailsEntity();
        clientDetails.setProfileEntity(profile);
        clientDetails.setId("123456789");

        assertNotNull(clientDetails.getProfileEntity());
        assertNotNull(clientDetails.getId());

        testProfile = new ProfileEntity("4444-4444-4444-4444");
    }

    @Test
    public void testValidUriOnWebhook() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetails(clientDetails);
        webhook.setUri("http://qa-1.orcid.org");
        webhook.setProfile(testProfile);
        webhookManager.processWebhook(webhook);
        assertEquals(webhook.getFailedAttemptCount(), 0);
        verify(mockWebhookDao, times(1)).merge(webhook);
    }

    @Test
    public void testUnexsistingUriOnWebhook() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetails(clientDetails);
        webhook.setUri("http://unexisting.orcid.com");
        webhook.setProfile(testProfile);
        webhookManager.processWebhook(webhook);
        assertEquals(webhook.getFailedAttemptCount(), 1);
        for (int i = 0; i < 3; i++) {
            webhookManager.processWebhook(webhook);
        }
        assertEquals(webhook.getFailedAttemptCount(), 4);
        verify(mockWebhookDao, times(4)).merge(webhook);
    }

    @Test
    public void testInvalidUriOnWebhook() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetails(clientDetails);
        webhook.setUri("http://123.qa-1.orcid.org");
        webhook.setProfile(testProfile);
        webhookManager.processWebhook(webhook);
        assertEquals(webhook.getFailedAttemptCount(), 1);
        for (int i = 0; i < 3; i++) {
            webhookManager.processWebhook(webhook);
        }
        assertEquals(webhook.getFailedAttemptCount(), 4);
        verify(mockWebhookDao, times(4)).merge(webhook);
    }

    @Test
    public void testFailAttemptCounterReset() {
        WebhookEntity webhook = new WebhookEntity();
        webhook.setClientDetails(clientDetails);
        webhook.setUri("http://123.qa-1.orcid.org");
        webhook.setProfile(testProfile);
        webhookManager.processWebhook(webhook);
        assertEquals(webhook.getFailedAttemptCount(), 1);

        webhook.setUri("http://unexisting.orcid.com");
        webhookManager.processWebhook(webhook);
        assertEquals(webhook.getFailedAttemptCount(), 2);

        webhook.setUri("http://qa-1.orcid.org");
        webhookManager.processWebhook(webhook);
        assertEquals(webhook.getFailedAttemptCount(), 0);
        verify(mockWebhookDao, times(3)).merge(webhook);
    }

}
