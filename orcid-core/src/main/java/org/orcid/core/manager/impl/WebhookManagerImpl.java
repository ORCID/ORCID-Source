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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.orcid.core.manager.WebhookManager;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class WebhookManagerImpl implements WebhookManager {

    private int maxJobsPerClient;
    private int numberOfWebhookThreads;
    private int retryDelayMinutes;

    @Resource
    private DefaultHttpClient httpClient;

    @Resource
    private WebhookDao webhookDao;

    @Resource
    private TransactionTemplate transactionTemplate;

    private Map<String, Integer> clientWebhooks = new HashMap<String, Integer>();

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookManagerImpl.class);
    private static final int WEBHOOKS_BATCH_SIZE = 1000;

    public void setMaxJobsPerClient(int maxJobs) {
        this.maxJobsPerClient = maxJobs;
    }

    public void setNumberOfWebhookThreads(int numberOfWebhookThreads) {
        this.numberOfWebhookThreads = numberOfWebhookThreads;
    }

    public void setRetryDelayMinutes(int retryDelayMinutes) {
        this.retryDelayMinutes = retryDelayMinutes;
    }

    @Override
    public void processWebHooks() {
        // Log start time
        LOGGER.info("About to process webhooks");
        Date startTime = new Date();
        // Create thread pool of size determined by runtime property
        ExecutorService executorService = createThreadPoolForWebhooks();
        List<WebhookEntity> webhooks = new ArrayList<>(0);
        Map<WebhookEntityPk, WebhookEntity> mapOfpreviousBatch = null;
        do {
            mapOfpreviousBatch = WebhookEntity.mapById(webhooks);
            // Get chunk of webhooks to process for records that changed before
            // start time
            webhooks = webhookDao.findWebhooksReadyToProcess(startTime, retryDelayMinutes, WEBHOOKS_BATCH_SIZE);
            // Log the chunk size
            LOGGER.info("Found batch of {} webhooks to process", webhooks.size());
            // For each callback in chunk
            for (final WebhookEntity webhook : webhooks) {
                // Need to ignore anything in previous chunk
                if (mapOfpreviousBatch.containsKey(webhook.getId())) {
                    LOGGER.debug("Skipping webhook as was in previous batch: {}", webhook.getId());
                    continue;
                }
                // Submit job to thread pool
                executorService.execute(new Runnable() {
                    public void run() {
                        processWebhookInTransaction(webhook);
                    }
                });
            }
        } while (!webhooks.isEmpty());
        // Shutdown thread pool
        executorService.shutdown();
        try {
            executorService.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("Received an interupt exception whilst waiting for the webhook processing complete", e);
        }
        LOGGER.info("Finished processing webhooks");
    }

    private ExecutorService createThreadPoolForWebhooks() {
        return new ThreadPoolExecutor(numberOfWebhookThreads, numberOfWebhookThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(WEBHOOKS_BATCH_SIZE),
                Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
    }

    private void processWebhookInTransaction(final WebhookEntity webhook) {
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                processWebhook(webhook);
            }
        });
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