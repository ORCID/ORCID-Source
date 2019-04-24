package org.orcid.integration.blackbox.api.webhook.tests;

import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;

import org.codehaus.jettison.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.integration.api.helper.OauthHelper;
import org.orcid.integration.blackbox.api.v12.T2OAuthAPIService;
import org.orcid.integration.blackbox.api.v2.BlackBoxBase;
import org.orcid.jaxb.model.message.ScopePathType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.sun.jersey.api.client.ClientResponse;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:test-context.xml" })
public class WebhooksTest extends BlackBoxBase {

    @Resource
    protected OauthHelper oauthHelper;

    @Resource(name = "t2OAuthClient")
    private T2OAuthAPIService<ClientResponse> apiClient;

    @Test
    public void registerWebhookTest() throws JSONException {
        String webhookTokenClient1 = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.WEBHOOK);

        // Register
        ClientResponse response = apiClient.registerWebhook(this.getUser1OrcidId(), "http%3A%2F%2Forcid.org%2F1", webhookTokenClient1);
        assertEquals(201, response.getStatus());

        // Unregister
        response = apiClient.unregisterWebhook(this.getUser1OrcidId(), "http%3A%2F%2Forcid.org%2F1", webhookTokenClient1);
        assertEquals(204, response.getStatus());
    }

    @Test
    public void otherClientCantUnregisterWebhookTest() throws JSONException {
        String webhookTokenClient1 = oauthHelper.getClientCredentialsAccessToken(this.getClient1ClientId(), this.getClient1ClientSecret(), ScopePathType.WEBHOOK);
        String webhookTokenClient2 = oauthHelper.getClientCredentialsAccessToken(this.getClient2ClientId(), this.getClient2ClientSecret(), ScopePathType.WEBHOOK);

        // Register
        ClientResponse response = apiClient.registerWebhook(this.getUser1OrcidId(), "http%3A%2F%2Forcid.org%2FunregisterTest", webhookTokenClient1);
        assertEquals(201, response.getStatus());

        // Try to unregister
        response = apiClient.unregisterWebhook(this.getUser1OrcidId(), "http%3A%2F%2Forcid.org%2FunregisterTest", webhookTokenClient2);
        assertEquals(403, response.getStatus());

        // Unregister it with original client
        response = apiClient.unregisterWebhook(this.getUser1OrcidId(), "http%3A%2F%2Forcid.org%2FunregisterTest", webhookTokenClient1);
        assertEquals(204, response.getStatus());
    }
}
