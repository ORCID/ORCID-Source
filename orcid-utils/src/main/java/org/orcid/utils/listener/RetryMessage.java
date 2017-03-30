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
}
