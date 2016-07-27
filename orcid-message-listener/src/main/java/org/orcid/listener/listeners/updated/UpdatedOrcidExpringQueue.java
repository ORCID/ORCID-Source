package org.orcid.listener.listeners.updated;

import org.orcid.listener.GenericExpiringQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UpdatedOrcidExpringQueue extends GenericExpiringQueue<UpdatedOrcidWorker>{

    /** Uses the UpdatedOrcidWorker to process items in the queue
     * 
     * @param secondsToWait
     * @param forceCleanup
     * @param removalListener
     */
    @Autowired
    public UpdatedOrcidExpringQueue(
            @Value("${org.orcid.listener.lastUpdateSecondsToWait:300}") int secondsToWait, 
            @Value("${org.orcid.listener.lastUpdateForceCleanup:true}") Boolean forceCleanup, 
            UpdatedOrcidWorker removalListener) {
        super(secondsToWait, forceCleanup, removalListener);
    }
    
}
