package org.orcid.core.email.trickle;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Resource;
import javax.jms.JMSException;

import org.apache.commons.io.IOUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
public class EmailQueueProducer {

    @Resource
    private JmsTemplate jmsTemplate;
    
    private String emailString;

    private static final String EMAIL_QUEUE = "email.queue";

    private void queueEmail(int counter) throws JMSException, JmsException, IOException {
        jmsTemplate.convertAndSend(EMAIL_QUEUE, counter + " " + getEmailString());
    }

    public static void main(String[] args) throws JMSException, IOException {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-email-trickle-context.xml");
        EmailQueueProducer test = (EmailQueueProducer) context.getBean(EmailQueueProducer.class);

        int counter = 0;
        for (int i = 0; i < 100000; i++) {
            test.queueEmail(counter++);
        }
        
    }
    
    
    private String getEmailString() throws IOException {
        if (emailString != null) {
            return emailString;
        }
        
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        InputStream email = getClass().getResourceAsStream("/email.txt");
        IOUtils.copy(email, byteArrayOutputStream);
        email.close();
        byteArrayOutputStream.close();
        emailString = byteArrayOutputStream.toString();
        return emailString;
    }
    
}
