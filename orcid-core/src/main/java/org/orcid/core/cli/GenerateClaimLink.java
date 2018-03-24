package org.orcid.core.cli;

import org.orcid.core.manager.NotificationManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class GenerateClaimLink {
    @SuppressWarnings("resource")
    public static void main(String[] args) {
        try {
            ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
            NotificationManager notificationManager = (NotificationManager) context.getBean("notificationManager");
            String email = args[0].trim();
            String baseUri = args[1].trim(); //Must allways be https://orcid.org ? lets leave it as a param just in case
            System.out.println(notificationManager.createClaimVerificationUrl(email, baseUri));
        } catch (Throwable t) {
            System.out.println(t);
        } finally {
            System.exit(0);
        }
    }
}
