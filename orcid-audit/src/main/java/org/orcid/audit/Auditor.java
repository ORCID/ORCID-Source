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
package org.orcid.audit;

import org.orcid.audit.entities.AuditEvent;
import org.orcid.audit.entities.AuditEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.util.Queue;

/**
 * 2011-2012 - ORCID
 *
 * @author Declan Newman (declan)
 *         Date: 24/07/2012
 */
public class Auditor {

    private static final Logger LOGGER = LoggerFactory.getLogger(Auditor.class);

    @Resource(name = "auditEventQueue")
    private Queue<AuditEvent> auditEventQueue;

    public void audit(AuditEvent event) {
        try {
            if (!auditEventQueue.offer(event)) {
                LOGGER.warn("Unable to add {} to the queue as it has exceeded its maximum size.", event);
            } else {
                synchronized (auditEventQueue) {
                    auditEventQueue.notify();
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Unable to add the event {} to the audit event queue", event, e);
        }
    }

    public void audit(String recordModifiedOrcid, String recordModifierOrcid, String recordModifierType, String recordModifierIp, String recordModifierIso2Country,
                      AuditEventType eventType, String eventMethod, String eventDescription) {
        audit(new AuditEvent(recordModifiedOrcid, recordModifierOrcid, recordModifierType, recordModifierIp, recordModifierIso2Country, eventType, eventMethod, eventDescription));
    }
}
