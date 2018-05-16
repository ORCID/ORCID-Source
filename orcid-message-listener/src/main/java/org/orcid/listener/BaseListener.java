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
import java.util.function.Consumer;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQMapMessage;
import org.fusesource.hawtbuf.UTF8Buffer;
import org.orcid.utils.listener.LastModifiedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseListener <T extends Consumer<LastModifiedMessage>> implements MessageListener {
    
    Logger LOG = LoggerFactory.getLogger(BaseListener.class);
    
    private T processor;
    
    /** Note the processor param is used by spring to create the correct beans.
     * 
     * @param processor
     * @param queueName
     */
    public BaseListener(T processor){
        this.processor = processor;
    }
    
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
    
    @Override
    public void onMessage(Message message) {
        Map<String, String> map = getMapFromMessage(message);
        LastModifiedMessage lastModifiedMessage = new LastModifiedMessage(map);
        String orcid = lastModifiedMessage.getOrcid();
        try {
            LOG.info("Recieved " + message.getJMSDestination() + " message for orcid " + orcid + " " + lastModifiedMessage.getLastUpdated());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        getProcessor().accept(lastModifiedMessage);
    }
    
    public T getProcessor() {
        return processor;
    }
    
}