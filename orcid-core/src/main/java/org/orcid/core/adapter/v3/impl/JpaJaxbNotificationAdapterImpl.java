package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.adapter.v3.JpaJaxbNotificationAdapter;
import org.orcid.jaxb.model.v3.dev1.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.v3.dev1.notification.custom.NotificationCustom;
import org.orcid.jaxb.model.v3.dev1.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.v3.dev1.notification.Notification;
import org.orcid.model.v3.dev1.notification.institutional_sign_in.NotificationInstitutionalConnection;
import org.orcid.persistence.jpa.entities.NotificationAddItemsEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.NotificationEntity;
import org.orcid.persistence.jpa.entities.NotificationInstitutionalConnectionEntity;

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
