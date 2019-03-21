package org.orcid.email.trickle.queue.producer;

import javax.annotation.Resource;
import javax.jms.JMSException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailQueueProducer {

    @Resource
    private JmsTemplate jmsTemplate;

    private static final String EMAIL_QUEUE = "email.queue";

    private void queueEmail(int counter) throws JMSException {
        jmsTemplate.convertAndSend(EMAIL_QUEUE, "message " + counter);
    }

    public static void main(String[] args) throws JMSException {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-email-trickle-context.xml");
        EmailQueueProducer test = (EmailQueueProducer) context.getBean(EmailQueueProducer.class);

        int counter = 0;
        while (true) {
            test.queueEmail(counter++);
        }
    }

}
