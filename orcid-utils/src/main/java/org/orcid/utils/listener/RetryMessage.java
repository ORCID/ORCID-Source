package org.orcid.utils.listener;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * Represents an immutable last modified event
 * 
 * @author tom
 *
 */
public class RetryMessage extends BaseMessage {
    
    public static final String BROKER_NAME = "bn";
    
    public Map<?, ?> retryTypes;
    
    /**
     * Create a map from the component parts
     * 
     * @param orcid
     * @param date
     */
    public RetryMessage(String orcid, String brokerName) {
        super(ImmutableMap.of(MessageConstants.TYPE.value, MessageConstants.TYPE_RETRY.value, 
                MessageConstants.ORCID.value, orcid, BROKER_NAME, brokerName));
    }

    /**
     * Convert a map that was sent back into an object
     * 
     * @param m
     */
    public RetryMessage(Map<String, String> m) {
        super(ImmutableMap.copyOf(m));
    }

    public Map<?, ?> getRetryTypes() {
        return retryTypes;
    }

    public void setRetryTypes(Map<?, ?> retryTypes) {
        this.retryTypes = retryTypes;
    }   
}
