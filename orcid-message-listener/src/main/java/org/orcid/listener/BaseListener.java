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
package org.orcid.listener;

import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.fusesource.hawtbuf.UTF8Buffer;

public abstract class BaseListener {

    protected Map<String, String> getMapFromMessage(Message message) {
        try {
            ActiveMQMapMessage mapMessage = (ActiveMQMapMessage) message;
            Map<String, Object> contentMap = mapMessage.getContentMap();
            Map<String, String> map = new HashMap<>();
            for (String key : contentMap.keySet()) {
                map.put(key, ((UTF8Buffer) contentMap.get(key)).toString());
            }
            return map;
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    
}
