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
package org.orcid.listener.persistence.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.listener.persistence.entities.RecordStatusEntity;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.springframework.stereotype.Component;

@Component
public class RecordStatusDao {
    @PersistenceContext
    protected EntityManager entityManager;

    public RecordStatusEntity get(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT * FROM record_status WHERE orcid = :orcid", RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return (RecordStatusEntity) query.getSingleResult();
    }

    public boolean exists(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM record_status WHERE orcid=:orcid");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger) query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    public void create(String orcid, AvailableBroker broker, Integer status) {
        RecordStatusEntity entity = new RecordStatusEntity();
        entity.setId(orcid);
        switch (broker) {
        case DUMP_STATUS_1_2_API:
            entity.setDumpStatus12Api(status);
            break;
        case DUMP_STATUS_2_0_API:
            entity.setDumpStatus20Api(status);
            break;
        case SOLR:
            entity.setSolrStatus20Api(status);
            break;
        }
        Date now = new Date();
        entity.setDateCreated(now);
        entity.setLastModified(now);
        entityManager.persist(entity);
    }

    public boolean updateStatus(String orcid, AvailableBroker broker, Integer status) {
        Query query = entityManager.createNativeQuery("UPDATE record_status SET " + broker + " = :status, last_modified = now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.setParameter("status", status);
        return query.executeUpdate() > 0;
    }

    public boolean updateStatus(String orcid, AvailableBroker broker) {
        Query query = entityManager.createNativeQuery("UPDATE record_status SET " + broker + " = (" + broker + " + 1), last_modified = now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    public List<RecordStatusEntity> getFailedElements(int batchSize) {
        TypedQuery<RecordStatusEntity> query = entityManager.createQuery("FROM RecordStatusEntity WHERE dumpStatus12Api > 0 OR dumpStatus20Api > 0 OR solrStatus20Api > 0 ORDER BY id", RecordStatusEntity.class);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }
}
