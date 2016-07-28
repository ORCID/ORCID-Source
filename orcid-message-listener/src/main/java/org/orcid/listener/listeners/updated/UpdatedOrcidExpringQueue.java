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
package org.orcid.listener.listeners.updated;

import org.orcid.listener.GenericExpiringQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UpdatedOrcidExpringQueue extends GenericExpiringQueue<UpdatedOrcidWorker>{

    /** Uses the UpdatedOrcidWorker to process items in the queue
     * 
     * @param secondsToWait how long the account should be incactive before processing
     * @param forceCleanup if true, register a thread that automatically scans for inactive entries and evicts them
     * @param removalListener the logic to be applied when items are evicted from the cache.
     */
    @Autowired
    public UpdatedOrcidExpringQueue(
            @Value("${org.orcid.listener.lastUpdateSecondsToWait}") int secondsToWait, 
            @Value("${org.orcid.listener.lastUpdateForceCleanup}") Boolean forceCleanup, 
            UpdatedOrcidWorker removalListener) {
        super(secondsToWait, forceCleanup, removalListener);
    }
    
}
