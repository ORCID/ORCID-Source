package org.orcid.listener;

import org.orcid.util.GenericExpiringQueue;
import org.orcid.utils.listener.LastModifiedMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.google.common.cache.RemovalListener;

@Component
public class UpdatedOrcidExpringQueue<T extends RemovalListener<String, LastModifiedMessage>> extends GenericExpiringQueue<RemovalListener<String, LastModifiedMessage>> {

    /**
     * Uses the UpdatedOrcidWorker to process items in the queue
     * 
     * @param secondsToWait
     *            how long the account should be incactive before processing
     * @param forceCleanup
     *            if true, register a thread that automatically scans for
     *            inactive entries and evicts them
     * @param removalListener
     *            the logic to be applied when items are evicted from the cache.
     */
    @Autowired
    public UpdatedOrcidExpringQueue(@Value("${org.orcid.listener.lastUpdateSecondsToWait}") int secondsToWait,
            @Value("${org.orcid.listener.lastUpdateForceCleanup}") Boolean forceCleanup, T removalListener) {
        super(secondsToWait, forceCleanup, removalListener);
    }

}
