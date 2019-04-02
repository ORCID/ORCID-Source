package org.orcid.core.email.trickle.producer;

import org.springframework.jms.core.JmsTemplate;

public class EmailQueueProducer {

    private JmsTemplate jmsTemplate;
    
    private static final String EMAIL_QUEUE = "email.queue";

    public void queueEmail(EmailTrickleItem item) {
        jmsTemplate.convertAndSend(EMAIL_QUEUE, item);
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

}
