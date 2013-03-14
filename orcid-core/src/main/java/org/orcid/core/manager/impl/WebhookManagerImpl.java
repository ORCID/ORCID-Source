package org.orcid.core.manager.impl;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.orcid.core.manager.WebhookManager;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookManagerImpl implements WebhookManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookManagerImpl.class);
    private int maxJobsPerClient;

    @Resource
    private DefaultHttpClient httpClient;

    private Map<String, Integer> clientWebhooks = new HashMap<String, Integer>();

    public void setMaxJobsPerClient(int maxJobs) {
        this.maxJobsPerClient = maxJobs;
    }

    @Override
    public void processWebHooks() {
        // TODO Auto-generated method stub

    }

    @Override
    public void processWebhook(WebhookEntity webhook) {
        String clientId = webhook.getClientDetails().getClientId();
        String orcid = webhook.getClientDetails().getProfileEntity().getId();
        String uri = webhook.getUri();

        if (webhookMaxed(clientId)) {
            LOGGER.warn("Thread limit exceeded by Client: {} With ORCID: {}; cannot process webhook: {}", new Object[] { clientId, orcid, webhook.getUri() });
            return;
        }

        increaseWebhook(clientId);

        // Log attempt to process webhook
        LOGGER.info("Processing webhook {} for Client: {} With ORCID: {}", new Object[] { webhook.getUri(), clientId, orcid });
        // Execute the request and get the client response
        try {
            int statusCode = doPost(uri);
            if (statusCode == 200) {
                LOGGER.info("Webhook {} for Client: {} With ORCID: {} has been processed", new Object[] { webhook.getUri(), clientId, orcid });
                webhook.setLastModified(new Date());
                webhook.setFailedAttemptCount(0);
            } else {
                LOGGER.warn("Webhook {} for Client: {} With ORCID: {} could not be processed", new Object[] { webhook.getUri(), clientId, orcid });
                webhook.setLastFailed(new Date());
                webhook.setFailedAttemptCount(webhook.getFailedAttemptCount() + 1);
            }
        } finally {
            decreaseWebhook(clientId);
        }
    }

    /**
     * Increases webhooks count by 1 for the specific client;
     * 
     * @param clientId
     * */
    private synchronized void increaseWebhook(String clientId) {
        clientWebhooks.put(clientId, webhookCount(clientId) + 1);
    }

    /**
     * Decreases webhooks count by 1 for the specific client;
     * 
     * @param clientId
     * */
    private synchronized void decreaseWebhook(String clientId) {
        clientWebhooks.put(clientId, webhookCount(clientId) - 1);
    }

    /**
     * Return the number of webhooks associated with a specific user
     * 
     * @param clientId
     * @return the number of webhooks associated with the client
     * */
    private synchronized int webhookCount(String clientId) {
        if (!clientWebhooks.containsKey(clientId))
            clientWebhooks.put(clientId, 0);
        return clientWebhooks.get(clientId);
    }

    /**
     * Indicates if the max number of hooks has been reached by a client
     * 
     * @param clientId
     * @return true if there are more than this.maxJobsPerClient threads running
     *         for a client
     * */
    private synchronized boolean webhookMaxed(String clientId) {
        return webhookCount(clientId) > this.maxJobsPerClient ? true : false;
    }

    /**
     * Executes a post request to a specific URL.
     * 
     * @param url
     *            the URL where the post request will be sent
     * @return httpResponse the response from the URL after executing the
     *         request
     * */
    private int doPost(String url) {
        HttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            response = httpClient.execute(httpPost);
            return response.getStatusLine().getStatusCode();
        } catch (IllegalStateException | IOException e) {
            LOGGER.error(String.format("Error processing webhook %s", url), e);
        } finally {
            if (response != null && response.getEntity() != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException e) {
                    LOGGER.error(String.format("Unable to release connection for webhook %s", url), e);
                }
            }
        }
        return 0;
    }
}