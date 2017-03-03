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
package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.orcid.persistence.dao.InvalidRecordDataChangesDao;
import org.orcid.persistence.jpa.entities.InvalidRecordDataChangesEntity;

public class InvalidRecordDataChangesDaoImpl implements InvalidRecordDataChangesDao {

    @Resource(name="entityManager")
    protected EntityManager entityManager;
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<InvalidRecordDataChangesEntity> getByDateCreated(Long lastId, Long pageSize, boolean descendantOrder) {
        String queryStr = "SELECT * FROM invalid_record_data_changes WHERE id {GTorLT} {LAST_SEQUENCE} ORDER BY id {ORDER} LIMIT :pageSize";
        
        String GTorLT = descendantOrder ? "<" : ">";
        String lastIdStr = descendantOrder ? "(select (max(id) + 1) from invalid_record_data_changes)" : "0";
        if(lastId != null) {
            lastIdStr = String.valueOf(lastId);
        }
        
        queryStr = queryStr.replace("{GTorLT}", GTorLT);
        queryStr = queryStr.replace("{LAST_SEQUENCE}", lastIdStr);
        queryStr = queryStr.replace("{ORDER}", descendantOrder ? "DESC" : "ASC");
        
        Query query = entityManager.createNativeQuery(queryStr, InvalidRecordDataChangesEntity.class);        
        query.setParameter("pageSize", pageSize);
        return (List<InvalidRecordDataChangesEntity>) query.getResultList();
    }

}
