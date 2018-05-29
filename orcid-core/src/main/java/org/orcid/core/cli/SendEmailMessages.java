package org.orcid.core.cli;

import org.orcid.core.manager.EmailMessageSender;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SendEmailMessages {

    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        EmailMessageSender emailMessageSender = (EmailMessageSender) context.getBean("emailMessageSender");
        emailMessageSender.sendEmailMessages();
        System.exit(0);
    }

}
