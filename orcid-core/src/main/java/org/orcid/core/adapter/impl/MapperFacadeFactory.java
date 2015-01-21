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
import ma.glasnost.orika.metadata.ClassMapBuilder;

import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.notification.addactivities.Activity;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.jaxb.model.notification.amended.NotificationAmended;
import org.orcid.jaxb.model.notification.custom.NotificationCustom;
import org.orcid.persistence.jpa.entities.NotificationActivityEntity;
import org.orcid.persistence.jpa.entities.NotificationAddActivitiesEntity;
import org.orcid.persistence.jpa.entities.NotificationAmendedEntity;
import org.orcid.persistence.jpa.entities.NotificationCustomEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
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
        mapCommonFields(mapperFactory.classMap(NotificationCustomEntity.class, NotificationCustom.class)).register();
        mapCommonFields(mapperFactory.classMap(NotificationAddActivitiesEntity.class, NotificationAddActivities.class)).field("authorizationUrl", "authorizationUrl.uri")
                .field("notificationActivities", "activities.activities").register();
        mapCommonFields(mapperFactory.classMap(NotificationAmendedEntity.class, NotificationAmended.class)).register();
        mapperFactory.classMap(NotificationActivityEntity.class, Activity.class).field("externalIdType", "externalId.externalIdType")
                .field("externalIdValue", "externalId.externalIdValue").byDefault().register();
        mapperFactory.classMap(SourceEntity.class, Source.class).field("sourceClient.id", "clientId.path").byDefault().register();
        return mapperFactory.getMapperFacade();
    }

    private ClassMapBuilder<?, ?> mapCommonFields(ClassMapBuilder<?, ?> builder) {
        return builder.field("dateCreated", "createdDate").field("id", "putCode").byDefault();
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
