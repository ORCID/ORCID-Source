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
package org.orcid.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalListeners;

public class GenericExpiringQueue<T extends RemovalListener<String, LastModifiedMessage>> implements ApplicationListener<ContextClosedEvent> {

    Logger LOG = LoggerFactory.getLogger(GenericExpiringQueue.class);
    private final ExecutorService executor;
    private final ScheduledExecutorService cleanup;
    private final Cache<String, LastModifiedMessage> cacheQueue;

    /**
     * Create a LastUpdatedCacheQueue
     * 
     * Creates a guava cache that expires entries five minutes after last
     * access. On expiry, the listener is called which performs the update(s).
     * 
     * Note that the cache performs cleanup and does removal as part of get/put
     * operations (better performance in live env) I have made it do these
     * ansynchronously so they do not block access to the cache In testing, set
     * forceCleanup to true or call cache.cleanUp() manually. Forcecleanup
     * delays 120 seconds before starting to clean.
     * 
     * For more info on guava caches, see
     * https://github.com/google/guava/wiki/CachesExplained Considered using
     * DelayQueue, but this makes it far easier.
     * http://stackoverflow.com/questions/27948867/efficiently-update-an-element
     * -in-a-delayqueue
     * 
     * This class registers itself to listen for context events to ensure
     * threads close on exit
     * 
     * @param secondsToWait
     *            how long the account should be inactive before processing
     * @param forceCleanup
     *            if true, register a thread that automatically scans for
     *            inactive entries and evicts them.
     * @param removalListener
     *            the logic to be applied when items are evicted from the cache.
     */
    public GenericExpiringQueue(int secondsToWait, Boolean forceCleanup, T removalListener) {
        LOG.info("Creating cacheQueue with " + secondsToWait + " seconds wait and forceCleanup = " + forceCleanup + " using "
                + removalListener.getClass().getSimpleName());

        // create a thread that does the removal - we can fiddle with the
        // Executor if we need more threads
        executor = Executors.newCachedThreadPool();

        // create the expiring cache
        cacheQueue = CacheBuilder.newBuilder().expireAfterAccess(secondsToWait, TimeUnit.SECONDS)
                .removalListener(RemovalListeners.asynchronous(removalListener, executor)).build();

        // if we want to force cleanup regularly, we create a thread to do it
        // here.
        if (forceCleanup) {
            cleanup = Executors.newSingleThreadScheduledExecutor();
            cleanup.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    cacheQueue.cleanUp();
                }
            }, 120, 10, TimeUnit.SECONDS);
        } else
            cleanup = null;
    }

    /**
     * Get the cache
     * 
     * @return the underlying guava cache instance
     */
    public Cache<String, LastModifiedMessage> getCache() {
        return cacheQueue;
    }

    /**
     * Add the executor thread to the Spring context shutdown so it doesn't
     * prevent tomcat stopping.
     * 
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent arg0) {
        executor.shutdown();
        if (cleanup != null)
            cleanup.shutdown();
    }

}
