package org.orcid.core.adapter.mapstruct.impl;

import org.apache.commons.lang3.StringUtils;
import org.mapstruct.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.persistence.dao.WorkDao;
import org.orcid.persistence.jpa.entities.NotificationWorkEntity;
import org.orcid.persistence.jpa.entities.WorkEntity;


@Component
public class NotificationWorkEntityFactory {

    private final WorkDao workDao;

    @Autowired
    public NotificationWorkEntityFactory(WorkDao workDao) {
        this.workDao = workDao;
    }

    @ObjectFactory
    public NotificationWorkEntity create(Item source) {
        NotificationWorkEntity entity = new NotificationWorkEntity();
        
        if (source != null && StringUtils.isNotBlank(source.getPutCode())) {
            try {
                Long putCode = Long.valueOf(source.getPutCode().trim());
                WorkEntity work = workDao.find(putCode);
                entity.setWork(work);
            } catch (NumberFormatException e) {
                // Safeguard against non-numeric putCode payloads
                entity.setWork(null);
            }
        }
        
        return entity;
    }
}