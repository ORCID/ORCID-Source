package org.orcid.core.cli;

import org.orcid.core.manager.EmailMessageSender;
import org.orcid.core.togglz.OrcidTogglzConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.togglz.core.context.ContextClassLoaderFeatureManagerProvider;
import org.togglz.core.manager.FeatureManager;
import org.togglz.core.manager.FeatureManagerBuilder;

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
        bootstrapTogglz(context.getBean(OrcidTogglzConfiguration.class));
        emailMessageSender.sendEmailMessages();
        System.exit(0);
    }

    private static void bootstrapTogglz(OrcidTogglzConfiguration togglzConfig) {
        FeatureManager featureManager = new FeatureManagerBuilder().togglzConfig(togglzConfig).build();
        ContextClassLoaderFeatureManagerProvider.bind(featureManager);
    }

}
