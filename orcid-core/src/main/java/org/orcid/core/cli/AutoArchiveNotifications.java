package org.orcid.core.cli;

import org.orcid.core.manager.NotificationManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * @author Will Simpson
 * 
 */
public class AutoArchiveNotifications {

    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        NotificationManager notificationManager = (NotificationManager) context.getBean("notificationManager");
        notificationManager.processOldNotificationsToAutoArchive();
        notificationManager.processOldNotificationsToAutoDelete();
        System.exit(0);
    }

}
