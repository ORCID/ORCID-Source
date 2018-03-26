package org.orcid.core.adapter.impl;

import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.NotificationWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.ObjectFactory;

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
