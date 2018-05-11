package org.orcid.jaxb.model.v3.rc1.notification.custom;

import org.orcid.jaxb.model.v3.rc1.notification.NotificationType;

public class NotificationServiceAnnouncement extends NotificationCustom {

    /**
     * 
     */
    private static final long serialVersionUID = 1413520169274297039L;

    {
        notificationType = NotificationType.SERVICE_ANNOUNCEMENT;
    }

}
