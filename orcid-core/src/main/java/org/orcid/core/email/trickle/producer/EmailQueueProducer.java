package org.orcid.core.email.trickle.producer;

import javax.annotation.Resource;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailQueueProducer {

    @Resource
    private JmsTemplate jmsTemplate;
    
    private static final String EMAIL_QUEUE = "email.queue";

    public void queueEmail(EmailTrickleItem item) {
        jmsTemplate.convertAndSend(EMAIL_QUEUE, item);
    }

}
