package org.orcid.core.manager.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
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

    private int maxJobsPerClient;
    private int numberOfWebhookThreads;
    private int retryDelayMinutes;
    private int maxPerRun;

    @Resource
    private WebhookDao webhookDao;
    
    @Resource
    private WebhookDao webhookDaoReadOnly;    

    @Resource
    private TransactionTemplate transactionTemplate;

    private Map<String, Integer> clientWebhooks = new HashMap<String, Integer>();

    private Set<String> blacklistedClients = new HashSet<String>();

    private Object mainWebhooksLock = new Object();

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookManagerImpl.class);
    
    @Value("${org.orcid.scheduler.webhooks.batchSize:5000}")
    private int webhooksBatchSize;

    @Resource
    private HttpRequestUtils httpRequestUtils;
    
    public void setMaxJobsPerClient(int maxJobs) {
        this.maxJobsPerClient = maxJobs;
    }

    public void setNumberOfWebhookThreads(int numberOfWebhookThreads) {
        this.numberOfWebhookThreads = numberOfWebhookThreads;
    }

    public void setRetryDelayMinutes(int retryDelayMinutes) {
        this.retryDelayMinutes = retryDelayMinutes;
    }

    public void setMaxPerRun(int maxPerRun) {
        this.maxPerRun = maxPerRun;
    }

    public void setWebhookDao(WebhookDao webhookDao) {
        this.webhookDao = webhookDao;
    }

    @Override
    public void processWebhooks() {
        // Only want one of these running at a time, otherwise we will
        // potentially have two threads retrieving the same stuff from the DB
        // for processing.
        LOGGER.info("Waiting for main webhooks lock");
        synchronized (mainWebhooksLock) {
            LOGGER.info("Obtained main webhooks lock");
            blacklistedClients.clear();
            processWebhooksInternal();
        }
        LOGGER.info("Released main webhooks lock");
    }

    private void processWebhooksInternal() {
        // Log start time
        LOGGER.info("About to process webhooks");
        Date startTime = new Date();
        long count = webhookDaoReadOnly.countWebhooksReadyToProcess(startTime, retryDelayMinutes);
        LOGGER.info("Total number of webhooks ready to process={}", count);
        // Create thread pool of size determined by runtime property
        ExecutorService executorService = createThreadPoolForWebhooks();
        List<WebhookEntity> webhooks = new ArrayList<>(0);
        Map<WebhookEntityPk, WebhookEntity> mapOfpreviousBatch = null;
        int executedCount = 0;
        OUTER: do {
            mapOfpreviousBatch = WebhookEntity.mapById(webhooks);
            // Get chunk of webhooks to process for records that changed before
            // start time
            webhooks = webhookDaoReadOnly.findWebhooksReadyToProcess(startTime, retryDelayMinutes, webhooksBatchSize);
            // Log the chunk size
            LOGGER.info("Found batch of {} webhooks to process", webhooks.size());
            int executedCountAtStartOfChunk = executedCount;

            // Process the fetched chunk, deferring any webhook whose client is currently maxed
            List<WebhookEntity> toProcessNow = new ArrayList<>(webhooks);
            while (!toProcessNow.isEmpty()) {
                List<WebhookEntity> deferred = new ArrayList<>();
                int scheduledThisRound = 0;
                for (final WebhookEntity webhook : toProcessNow) {
                    if (executedCount == maxPerRun) {
                        LOGGER.info("Reached maxiumum of {} webhooks for this run", executedCount);
                        break OUTER;
                    }
                    // Need to ignore anything in previous chunk
                    if (mapOfpreviousBatch.containsKey(webhook.getId())) {
                        LOGGER.debug("Skipping webhook as was in previous batch: {}", webhook.getId());
                        continue;
                    }
                    // If this client's concurrent limit is reached, defer this webhook to try later in the same batch
                    String clientId = webhook.getClientDetailsId();
                    if (webhookMaxed(clientId)) {
                        LOGGER.debug("Deferring webhook {} for client {} as max concurrent limit reached; will retry in current batch", webhook.getId(), clientId);
                        deferred.add(webhook);
                        continue;
                    }

                    // Submit job to thread pool
                    executorService.execute(new Runnable() {
                        public void run() {
                            processWebhookInTransaction(webhook);
                        }
                    });
                    scheduledThisRound++;
                    executedCount++;
                }

                if (deferred.isEmpty()) {
                    break; // nothing left to retry within this batch
                }

                if (scheduledThisRound == 0) {
                    // All remaining webhooks are maxed for their clients. Wait briefly for in-flight tasks to finish, then try again.
                    try {
                        LOGGER.debug("All remaining webhooks deferred due to client limits; waiting briefly before retrying current batch");
                        Thread.sleep(250L);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        LOGGER.warn("Interrupted while waiting to retry deferred webhooks in current batch", ie);
                        break;
                    }
                }

                // Retry only the deferred set in this batch before fetching again from DB
                toProcessNow = deferred;
            }

            if (executedCount == executedCountAtStartOfChunk) {
                LOGGER.info("No more webhooks added to pool in this iteration (all were in previous chunk or deferred with no capacity)");
                break;
            }
        } while (!webhooks.isEmpty());
        executorService.shutdown();
        try {
            LOGGER.info("Waiting for webhooks thread pool to finish");
            executorService.awaitTermination(120, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            LOGGER.warn("Received an interupt exception whilst waiting for the webhook processing complete", e);
        }
        LOGGER.info("Finished processing webhooks. Number of webhooks processed={}", executedCount);
    }

    private ExecutorService createThreadPoolForWebhooks() {
        // The queue size is half the batch size, to make sure the thread pool
        // has a chance to do some stuff before we go back to the DB for more,
        return new ThreadPoolExecutor(numberOfWebhookThreads, numberOfWebhookThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(
                webhooksBatchSize / 2), Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
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
        String clientId = webhook.getClientDetailsId();
        String orcid = webhook.getProfile();
        String uri = webhook.getUri();

        if (isBlacklisted(clientId)) {
            LOGGER.warn("Client: {} is blacklisted in this run; cannot process webhook: {}", new Object[] { clientId, webhook.getUri() });
            return;
        }

        if (webhookMaxed(clientId)) {
            LOGGER.warn("Thread limit exceeded by Client: {} With ORCID: {}; cannot process webhook: {}", new Object[] { clientId, orcid, webhook.getUri() });
            return;
        }

        increaseWebhook(clientId);

        // Execute the request and get the client response
        try {
            int statusCode = doPost(uri);
            if (statusCode >= 200 && statusCode < 300) {
                LOGGER.debug("Webhook {} for Client: {} With ORCID: {} has been processed", new Object[] { webhook.getUri(), clientId, orcid });
                webhookDao.markAsSent(orcid, uri);
            } else if (statusCode == 429) {
                LOGGER.warn("Webhook {} for Client: {} With ORCID: {} failed with 429. Blacklisting client for this run.", new Object[] { webhook.getUri(), clientId,
                        orcid });
                blacklistClient(clientId);
                webhookDao.markAsFailed(orcid, uri);
            } else {
                LOGGER.warn("Webhook {} for Client: {} With ORCID: {} failed with status code {}. Incrementing failure count for later retry.", new Object[] {
                        webhook.getUri(), clientId, orcid, statusCode });
                webhookDao.markAsFailed(orcid, uri);
            }
        } catch (Exception e) {
            LOGGER.warn("Exception processing webhook '{}' for '{}':'{}'. Error: {}", new Object[] { webhook.getUri(), clientId, orcid, e.getMessage() });
            webhookDao.markAsFailed(orcid, uri);
        } finally {
            decreaseWebhook(clientId);
        }
    }

    private synchronized void blacklistClient(String clientId) {
        blacklistedClients.add(clientId);
    }

    private synchronized boolean isBlacklisted(String clientId) {
        return blacklistedClients.contains(clientId);
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
            LOGGER.error(String.format("Error processing webhook %s", url), e);
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