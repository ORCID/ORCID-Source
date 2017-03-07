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
package org.orcid.core.manager.read_only.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbInvalidRecordDataChangeAdapter;
import org.orcid.core.manager.read_only.InvalidRecordDataChangesManagerReadOnly;
import org.orcid.model.invalid_record_data_change.InvalidRecordDataChange;
import org.orcid.persistence.dao.InvalidRecordDataChangeDao;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangeEntity;
import org.springframework.cache.annotation.Cacheable;

public class InvalidRecordDataChangesManagerReadOnlyImpl implements InvalidRecordDataChangesManagerReadOnly {

    @Resource
    private InvalidRecordDataChangeDao dao;
    
    @Resource
    private JpaJaxbInvalidRecordDataChangeAdapter adapter;
    
    @Override
    @Cacheable(value = "invalid-record-data-change-page-desc", key = "#lastElement.toString().concat('-').concat(#pageSize.toString())")
    public List<InvalidRecordDataChange> getInvalidRecordDataChangesDescending(Long lastElement, Long pageSize) {
        List<InvalidRecordDataChangeEntity> entities = dao.getByDateCreated(lastElement, pageSize, true);
        return adapter.toInvalidRecordDataChanges(entities);
    }

    @Override
    @Cacheable(value = "invalid-record-data-change-page-asc", key = "#lastElement.toString().concat('-').concat(#pageSize.toString())")
    public List<InvalidRecordDataChange> getInvalidRecordDataChangesAscending(Long lastElement, Long pageSize) {
        List<InvalidRecordDataChangeEntity> entities = dao.getByDateCreated(lastElement, pageSize, false);
        return adapter.toInvalidRecordDataChanges(entities);
    }

}
