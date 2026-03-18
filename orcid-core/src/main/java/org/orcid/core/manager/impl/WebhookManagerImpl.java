package org.orcid.core.manager.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.orcid.core.manager.WebhookManager;
import org.orcid.core.utils.http.HttpRequestUtils;
import org.orcid.persistence.dao.WebhookDao;
import org.orcid.persistence.jpa.entities.WebhookEntity;
import org.orcid.persistence.jpa.entities.keys.WebhookEntityPk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

public class WebhookManagerImpl implements WebhookManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookManagerImpl.class);

    @Value("${org.orcid.scheduler.webhooks.batchSize:5000}")
    private int webhooksBatchSize;

    @Value("${org.orcid.core.webhookRetryDelayMinutes:10}")
    private int retryDelayMinutes;

    @Value("${org.orcid.core.manager.impl.WebhookManagerImpl.maxIterations:3}")
    private int maxIterations;

    @Resource
    private WebhookDao webhookDao;
    
    @Resource
    private WebhookDao webhookDaoReadOnly;    

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private HttpRequestUtils httpRequestUtils;

    private final Set<String> blacklistedClients = new HashSet<String>();

    private int executedCount = 0;

    private int failedCount = 0;

    public void setWebhookDao(WebhookDao webhookDao) {
        this.webhookDao = webhookDao;
    }

    @Override
    public void processWebhooks() {
        // Log start time
        LOGGER.info("About to process webhooks, settings: batchSize=" + webhooksBatchSize + ", retryDelayMinutes={}, maxIterations={}", retryDelayMinutes, maxIterations);
        Date startTime = new Date();
        // Create thread pool of size determined by runtime property
        ExecutorService executorService = createThreadPoolForWebhooks();
        int iterations = 0;

        // Init counters
        executedCount = 0;
        failedCount = 0;

        // Clear the blacklisted clients before the iteration starts
        blacklistedClients.clear();

        // Get initial chunk of webhooks to process for records that changed before
        List<WebhookEntity> webhooks = webhookDaoReadOnly.findWebhooksReadyToProcess(startTime, retryDelayMinutes, webhooksBatchSize, blacklistedClients);

        OUTER: do {
            iterations++;
            // Log the chunk size
            LOGGER.info("Found batch of {} webhooks to process on iteration {}", webhooks.size(), iterations);
            CountDownLatch latch = new CountDownLatch(webhooks.size());

            while (!webhooks.isEmpty()) {
                int scheduledThisRound = 0;
                Iterator<WebhookEntity> iterator = webhooks.iterator();
                while (iterator.hasNext()) {
                    final WebhookEntity webhook = iterator.next();

                    // Submit job to thread pool
                    executorService.execute(new Runnable() {
                        public void run() {
                            processWebhook(webhook, latch);
                        }
                    });
                    iterator.remove();
                }

                if (webhooks.isEmpty()) {
                    break; // nothing left to retry within this batch
                }
            }

            try {
                latch.await();
            } catch (InterruptedException e) {
                LOGGER.warn("Received an interrupt exception whilst waiting for the webhook processing complete", e);
                Thread.currentThread().interrupt();
            }

            if (iterations >= maxIterations) {
                LOGGER.info("Reached maximum of " + maxIterations + " iterations for this run");
                break;
            }
            webhooks = webhookDaoReadOnly.findWebhooksReadyToProcess(startTime, retryDelayMinutes, webhooksBatchSize, blacklistedClients);
        } while (!webhooks.isEmpty());

        try {
            LOGGER.info("Waiting for webhooks thread pool to finish");
            // Shutdown the thread pool
            executorService.shutdown();
            // Wait for the thread pool to finish
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("Received an interupt exception whilst waiting for the webhook processing complete", e);
        }

        LOGGER.info("Finished processing webhooks. Number of webhooks processed={}, number of webhooks failed={}", executedCount, failedCount);
    }

    private ExecutorService createThreadPoolForWebhooks() {
        LOGGER.info("Creating thread pool with {} threads", Runtime.getRuntime().availableProcessors());
        return Executors.newWorkStealingPool(Runtime.getRuntime().availableProcessors());
    }

    private void processWebhook(WebhookEntity webhook, CountDownLatch latch) {
        String clientId = webhook.getClientDetailsId();
        String orcid = webhook.getProfile();
        String uri = webhook.getUri();

        if (isBlacklisted(clientId)) {
            LOGGER.warn("Client: {} is blacklisted in this run; cannot process webhook: {}", new Object[] { clientId, webhook.getUri() });
            latch.countDown();
            return;
        }

        // Log attempt to process webhook
        LOGGER.debug("Processing webhook {} for Client: {} With ORCID: {}", new Object[] { webhook.getUri(), clientId, orcid });
        // Execute the request and get the client response
        try {
            int statusCode = doPost(uri);
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    updateWebhookOnDatabase(statusCode, uri, orcid, clientId);
                }
            });
        } catch (Exception e) {
            LOGGER.warn("Exception processing webhook '{}' for '{}':'{}'. Error: {}", new Object[] { webhook.getUri(), clientId, orcid, e.getMessage() });

            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    webhookDao.markAsFailed(orcid, uri);
                    incrementFailedCount();
                }
            });
        } finally {
            //The latch will always decrement, preventing a frozen thread.
            latch.countDown();
        }
    }

    private void updateWebhookOnDatabase(Integer statusCode, String uri, String orcid, String clientId) {
        if (statusCode >= 200 && statusCode < 300) {
            LOGGER.debug("Webhook {} for Client: {} With ORCID: {} has been processed", new Object[] { uri, clientId, orcid });
            webhookDao.markAsSent(orcid, uri);
            incrementExecutedCount();
        } else if (statusCode == 429) {
            LOGGER.warn("Webhook {} for Client: {} With ORCID: {} failed with 429. Blacklisting client for this run.", new Object[] { uri, clientId,
                    orcid });
            blacklistClient(clientId);
            webhookDao.markAsFailed(orcid, uri);
            incrementFailedCount();
        } else {
            LOGGER.warn("Webhook {} for Client: {} With ORCID: {} failed with status code {}. Incrementing failure count for later retry.", new Object[] {
                    uri, clientId, orcid, statusCode });
            webhookDao.markAsFailed(orcid, uri);
            incrementFailedCount();
        }
    }

    private synchronized void blacklistClient(String clientId) {
        blacklistedClients.add(clientId);
    }

    private synchronized boolean isBlacklisted(String clientId) {
        return blacklistedClients.contains(clientId);
    }

    private synchronized void incrementExecutedCount() {
        this.executedCount++;
    }

    private synchronized void incrementFailedCount() {
        this.failedCount++;
    }

    /**
     * Executes a post request to a specific URL.
     * 
     * @param url
     *            the URL where the post request will be sent
     * @return httpResponse the response from the URL after executing the
     *         request
     * @throws URISyntaxException 
     * @throws InterruptedException 
     * */
    private int doPost(String url) {
        if (!url.toLowerCase().startsWith("http")) {
            url = "http://" + url;
        }
        try {            
            HttpResponse<String> response = httpRequestUtils.doPost(url);
            return response.statusCode();
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOGGER.error(String.format("Error processing webhook %s", url), (e.getCause() == null) ? e.toString() : e.getCause().getMessage());
        } 
        return 0;
    }

    @Override
    public void update(WebhookEntity webhook) {
        webhookDao.merge(webhook);
        webhookDao.flush();
    }

    @Override
    public void delete(WebhookEntityPk webhookPk) {
        webhookDao.remove(webhookPk);
        webhookDao.flush();
    }

    @Override
    public WebhookEntity find(WebhookEntityPk webhookPk) {
        return webhookDao.find(webhookPk);
    }
}