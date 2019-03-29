package org.orcid.scheduler.email.trickle.listener;

import org.orcid.core.email.trickle.producer.EmailTrickleItem;
import org.orcid.scheduler.email.trickle.manager.TrickleManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;

public class EmailQueueListener {
    
    private static final String EMAIL_QUEUE = "email.queue";
    
    private TrickleManager trickleManager;

    @JmsListener(destination = EMAIL_QUEUE)
    public void receiveMessage(@Payload EmailTrickleItem item) {
        trickleManager.attemptSend(item);
    }

    public void setTrickleManager(TrickleManager trickleManager) {
        this.trickleManager = trickleManager;
    }
    
}
