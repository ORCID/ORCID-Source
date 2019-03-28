package org.orcid.scheduler.email.trickle.listener;

import javax.annotation.Resource;

import org.orcid.core.email.trickle.producer.EmailTrickleItem;
import org.orcid.scheduler.email.trickle.manager.TrickleManager;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailQueueListener {
    
    private static final String EMAIL_QUEUE = "email.queue";
    
    @Resource
    private TrickleManager trickleManager;

    @JmsListener(destination = EMAIL_QUEUE)
    public void receiveMessage(@Payload EmailTrickleItem item) {
        trickleManager.attemptSend(item);
    }

}
