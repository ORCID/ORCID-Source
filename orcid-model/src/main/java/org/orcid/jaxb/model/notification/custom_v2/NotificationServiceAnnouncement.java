package org.orcid.jaxb.model.notification.custom_v2;

import org.orcid.jaxb.model.notification_v2.NotificationType;
public class NotificationServiceAnnouncement extends NotificationCustom {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1413520169274297039L;

    {
        notificationType = NotificationType.SERVICE_ANNOUNCEMENT;
    }

}
