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

import java.util.Date;
import java.util.Map;

import org.orcid.utils.listener.MessageConstants;

import com.google.common.collect.ImmutableMap;

/**
 * Represents an immutable last modified event
 * 
 * @author tom
 *
 */
public class LastModifiedMessage extends BaseMessage {

    /**
     * Create a map from the component parts
     * 
     * @param orcid
     * @param date
     */
    public LastModifiedMessage(String orcid, Date date) {
        super(ImmutableMap.of(MessageConstants.TYPE.value, MessageConstants.TYPE_LAST_UPDATED.value, 
                MessageConstants.ORCID.value, orcid, MessageConstants.DATE.value,
                (date == null) ? "" : String.valueOf(date.getTime())));
    }

    /**
     * Convert a map that was sent back into an object
     * 
     * @param m
     */
    public LastModifiedMessage(Map<String, String> m) {
        super(ImmutableMap.copyOf(m));
    }

    public Date getLastUpdated() {
        String time = map.get(MessageConstants.DATE.value);
        if (time == null)
            return null;
        return new Date(Long.valueOf(time));
    }
}
