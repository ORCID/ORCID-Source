/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbNotificationAdapter;
import org.orcid.jaxb.model.notification.Notification;
import org.orcid.persistence.jpa.entities.NotificationEntity;

/**
 * 
 * @author Will Simpson
 * 
 */
public class JpaJaxbNotificationAdapterImpl implements JpaJaxbNotificationAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
    }

    @Override
    public NotificationEntity toNotificationEntity(Notification notification) {
        if (notification == null) {
            return null;
        }
        return mapperFacade.map(notification, NotificationEntity.class);
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
