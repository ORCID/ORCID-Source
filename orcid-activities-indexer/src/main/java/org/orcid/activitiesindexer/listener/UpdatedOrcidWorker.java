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
package org.orcid.activitiesindexer.listener;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.orcid.activitiesindexer.s3.S3MessageProcessor;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

@Component
public class UpdatedOrcidWorker implements RemovalListener<String, LastModifiedMessage> {

    Logger LOG = LoggerFactory.getLogger(UpdatedOrcidWorker.class);

    @Resource
    private S3MessageProcessor s3Processor;

    private final ExecutorService executor;

    public UpdatedOrcidWorker(@Value("${org.orcid.message-listener.updated_orcid.threads:5}") Integer maxThreads) {
        executor = Executors.newFixedThreadPool(maxThreads);
    }
    
    /**
     * Fires when the queue evicts after an inactivity period.
     * Populates the Amazon S3 buckets and updates solr index
     */
    public void onRemoval(RemovalNotification<String, LastModifiedMessage> removal) {
        if (removal.wasEvicted()) {
            executor.submit(() -> {
                LastModifiedMessage m = removal.getValue();
                LOG.info("Removing " + removal.getKey() + " from UpdatedOrcidCacheQueue '" + m.getLastUpdated() + "' Removal cause " + removal.getCause() );            
                s3Processor.accept(m);
            });            
        }
    }
}
