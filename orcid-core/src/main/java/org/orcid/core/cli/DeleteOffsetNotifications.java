package org.orcid.core.cli;

import org.orcid.core.manager.v3.NotificationManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DeleteOffsetNotifications {
    @SuppressWarnings("resource")
    public static void main(String args[]) {
        ApplicationContext context = new ClassPathXmlApplicationContext("orcid-core-context.xml");
        NotificationManager notificationManager = (NotificationManager) context.getBean("notificationManagerV3");
        notificationManager.deleteOffsetNotifications();
        System.exit(0);
    }
}
