package org.orcid.email.trickle.queue.listener;

import java.util.Date;

import javax.annotation.Resource;

import org.orcid.email.trickle.manager.TrickleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailQueueListener {
    
    private static final Logger LOG = LoggerFactory.getLogger(EmailQueueListener.class);
    
    private static final String EMAIL_QUEUE = "email.queue";
    
    @Resource
    private TrickleManager trickleManager;

    @JmsListener(destination = EMAIL_QUEUE)
    public void receiveMessage(@Payload String message) {
        LOG.info("received message {} at {}", message, new Date());
        trickleManager.attemptSend();
    }

}
