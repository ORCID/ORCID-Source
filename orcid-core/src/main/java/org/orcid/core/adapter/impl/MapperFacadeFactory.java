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

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.orcid.jaxb.model.notification.addactivities.Activity;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.jaxb.model.notification.custom.NotificationCustom;
import org.orcid.persistence.jpa.entities.NotificationActivityEntity;
import org.orcid.persistence.jpa.entities.NotificationAddActivitiesEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.springframework.beans.factory.FactoryBean;

/**
 * 
 * @author Will Simpson
 * 
 */
public class MapperFacadeFactory implements FactoryBean<MapperFacade> {

    @Override
    public MapperFacade getObject() throws Exception {
        MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        mapperFactory.classMap(NotificationCustomEntity.class, NotificationCustom.class).field("dateCreated", "createdDate").field("id", "putCode.path").byDefault()
                .register();
        mapperFactory.classMap(NotificationAddActivitiesEntity.class, NotificationAddActivities.class).field("dateCreated", "createdDate").field("id", "putCode.path")
                .field("authorizationUrl", "authorizationUrl.uri").field("notificationActivities", "activities.activities").byDefault().register();
        mapperFactory.classMap(NotificationActivityEntity.class, Activity.class).field("externalIdType", "externalId.externalIdType")
                .field("externalIdValue", "externalId.externalIdValue").byDefault().register();
        return mapperFactory.getMapperFacade();
    }

    @Override
    public Class<?> getObjectType() {
        return MapperFacade.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
