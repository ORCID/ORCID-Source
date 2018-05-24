package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.rc1.notification.Notification;
import org.orcid.persistence.jpa.entities.NotificationEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public interface JpaJaxbNotificationAdapter {

    NotificationEntity toNotificationEntity(Notification notification);

    Notification toNotification(NotificationEntity notificationEntity);

    List<Notification> toNotification(Collection<NotificationEntity> notificationEntities);

}
