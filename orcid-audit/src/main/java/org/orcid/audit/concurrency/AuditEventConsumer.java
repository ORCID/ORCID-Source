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
package org.orcid.audit.concurrency;

import org.orcid.audit.entities.AuditEvent;
import org.orcid.audit.manager.AuditEventManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 24/07/2012
 */
public class AuditEventConsumer implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuditEventConsumer.class);

    private final Queue<AuditEvent> queue;
    private final AuditEventManager auditEventManager;
    private final ExecutorService executorService;

    // Explicitly setting in the constructor to enable us to use final
    public AuditEventConsumer(Queue<AuditEvent> queue, AuditEventManager auditEventManager, ExecutorService executorService) {
        this.queue = queue;
        this.auditEventManager = auditEventManager;
        this.executorService = executorService;
    }

    @Override
    public void run() {
        LOGGER.info("Starting up the audit event service");
        while (true) {
            consume();
            try {
                synchronized (queue) {
                    queue.wait();
                }
            } catch (InterruptedException e) {
                LOGGER.error("Thread has been interrupted", e);
            }
        }
    }

    private void consume() {
        while (!queue.isEmpty()) {
            final AuditEvent event = queue.poll();
            if (event != null) {
                LOGGER.debug("Firing an event for {}", event);
                executorService.execute(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            auditEventManager.persist(event);
                        } catch (Exception e) {
                            LoggerFactory.getLogger(this.getClass()).error("Cannot store event {}", e, event);
                        }
                    }
                });

            }
        }
    }


    public void shutdownNow() {
        LOGGER.info("Shutting down the audit event executor");
        List<Runnable> runnables = executorService.shutdownNow();
        if (runnables != null && !runnables.isEmpty()) {
            LOGGER.warn("There are {} audit events that will not be written due to the executor service being shutdown", runnables.size());
        }
    }
}
