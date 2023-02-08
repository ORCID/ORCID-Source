package org.orcid.core.cli;

import java.util.Date;

import org.orcid.core.utils.listener.LastModifiedMessage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jms.core.JmsTemplate;

public class SendJMSMessage {
    
    
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        JmsTemplate jmsTemplate = (JmsTemplate) context.getBean("jmsTemplate");
        
        LastModifiedMessage mess = new LastModifiedMessage("0000-0002-8598-973X", new Date());
        
        jmsTemplate.convertAndSend("updateV3Record", mess.getMap());
        
        System.out.println("Done");
    }
}
