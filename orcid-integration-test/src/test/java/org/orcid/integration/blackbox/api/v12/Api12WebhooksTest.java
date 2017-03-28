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
package org.orcid.integration.blackbox.api.v12;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.net.URLEncoder;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.APIRequestType;
import org.orcid.integration.blackbox.api.v2.release.BlackBoxBaseV2Release;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class Api12WebhooksTest extends BlackBoxBaseV2Release {
    
    @Resource(name = "t2OAuthClient_1_2")
    protected T2OAuthAPIService<ClientResponse> t2OAuthClient_1_2;
    
    @Test
    public void testRegisterAndUnRegisterWebhook() throws Exception {
        String userOrcid = getUser1OrcidId();
        String accessToken = getClientCredentialsAccessToken(ScopePathType.WEBHOOK, this.getClient1ClientId(), this.getClient1ClientSecret(), APIRequestType.MEMBER);
        String webhookUri = URLEncoder.encode("http://nowhere.com", "UTF-8");
        ClientResponse putResponse = t2OAuthClient_1_2.registerWebhook(userOrcid, webhookUri, accessToken);
        assertNotNull(putResponse);
        assertEquals(201, putResponse.getStatus());
        ClientResponse deleteResponse = t2OAuthClient_1_2.unregisterWebhook(userOrcid, webhookUri, accessToken);
        assertNotNull(deleteResponse);
        assertEquals(204, deleteResponse.getStatus());
    }
}
