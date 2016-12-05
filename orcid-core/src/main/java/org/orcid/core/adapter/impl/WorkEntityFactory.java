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

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;

import org.orcid.jaxb.model.notification.permission_rc4.Item;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.NotificationWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

/**
 * @author Will Simpson
 */
public class WorkEntityFactory implements ObjectFactory<NotificationWorkEntity> {

    private WorkDao workDao;

    public WorkEntityFactory(WorkDao workDao) {
        this.workDao = workDao;
    }

    @Override
    public NotificationWorkEntity create(Object source, MappingContext mappingContext) {
        mappingContext.getSourceObjects();
        NotificationWorkEntity nwe = new NotificationWorkEntity();
        String putCode = ((Item) source).getPutCode();
        if (putCode != null) {
            WorkEntity work = workDao.find(Long.valueOf(putCode));
            nwe.setWork(work);
        }
        return nwe;
    }

}
