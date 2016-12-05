/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.orcid.core.adapter.JpaJaxbNotificationAdapter;
import org.orcid.jaxb.model.notification.amended_rc4.NotificationAmended;
import org.orcid.jaxb.model.notification.custom_rc4.NotificationCustom;
import org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission;
import org.orcid.jaxb.model.notification_rc4.Notification;
import org.orcid.model.notification.institutional_sign_in_rc4.NotificationInstitutionalConnection;
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
