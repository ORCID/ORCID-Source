package org.orcid.jaxb.model.v3.release.notification.custom;

import org.orcid.jaxb.model.v3.release.notification.NotificationType;

public class NotificationAdministrative extends NotificationCustom {

    /**
     * 
     */
    private static final long serialVersionUID = -1041297870527253503L;

    {
        notificationType = NotificationType.ADMINISTRATIVE;
    }

}
