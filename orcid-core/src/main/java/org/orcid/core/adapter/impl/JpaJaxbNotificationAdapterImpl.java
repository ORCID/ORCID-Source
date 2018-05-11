package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.adapter.JpaJaxbNotificationAdapter;
import org.orcid.jaxb.model.notification.amended_v2.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_v2.NotificationAdministrative;
import org.orcid.jaxb.model.notification.custom_v2.NotificationCustom;
import org.orcid.jaxb.model.notification.custom_v2.NotificationServiceAnnouncement;
import org.orcid.jaxb.model.notification.custom_v2.NotificationTip;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.notification_v2.Notification;
import org.orcid.model.notification.institutional_sign_in_v2.NotificationInstitutionalConnection;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAdministrativeEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;
import org.orcid.persistence.jpa.entities.NotificationServiceAnnouncementEntity;
import org.orcid.persistence.jpa.entities.NotificationTipEntity;

import ma.glasnost.orika.MapperFacade;

/**
 * 
 * @author Will Simpson
 * 
 */
public class JpaJaxbNotificationAdapterImpl implements JpaJaxbNotificationAdapter {

    private MapperFacade mapperFacade;

    private static final Map<Class<? extends Notification>, Class<? extends NotificationEntity>> JAXB2JPA_CLASS_MAP = new HashMap<>();
    static {
        JAXB2JPA_CLASS_MAP.put(NotificationPermission.class, NotificationAddItemsEntity.class);
        JAXB2JPA_CLASS_MAP.put(NotificationCustom.class, NotificationCustomEntity.class);
        JAXB2JPA_CLASS_MAP.put(NotificationAmended.class, NotificationAmendedEntity.class);
        JAXB2JPA_CLASS_MAP.put(NotificationInstitutionalConnection.class, NotificationInstitutionalConnectionEntity.class);
        JAXB2JPA_CLASS_MAP.put(NotificationAdministrative.class, NotificationAdministrativeEntity.class);
        JAXB2JPA_CLASS_MAP.put(NotificationServiceAnnouncement.class, NotificationServiceAnnouncementEntity.class);
        JAXB2JPA_CLASS_MAP.put(NotificationTip.class, NotificationTipEntity.class);        
    }

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public NotificationEntity toNotificationEntity(Notification notification) {
        if (notification == null) {
            return null;
        }
        return mapperFacade.map(notification, JAXB2JPA_CLASS_MAP.get(notification.getClass()));
    }

    @Override
    public Notification toNotification(NotificationEntity notificationEntity) {
        if (notificationEntity == null) {
            return null;
        }
        return mapperFacade.map(notificationEntity, Notification.class);
    }

    @Override
    public List<Notification> toNotification(Collection<NotificationEntity> notificationEntities) {
        if (notificationEntities == null) {
            return null;
        }
        return mapperFacade.mapAsList(notificationEntities, Notification.class);
    }

}
