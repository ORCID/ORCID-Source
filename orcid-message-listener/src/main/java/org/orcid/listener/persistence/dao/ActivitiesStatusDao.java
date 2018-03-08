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

import org.orcid.listener.persistence.entities.ActivitiesStatusEntity;
import org.orcid.listener.persistence.util.ActivityType;
import org.springframework.stereotype.Component;

@Component
public class ActivitiesStatusDao {
    @PersistenceContext
    protected EntityManager entityManager;

    public ActivitiesStatusEntity get(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT * FROM activities_status WHERE orcid = :orcid", ActivitiesStatusEntity.class);
        query.setParameter("orcid", orcid);
        return (ActivitiesStatusEntity) query.getSingleResult();
    }

    public boolean exists(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM activities_status WHERE orcid=:orcid");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger) query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    public void create(String orcid, ActivityType type, Integer status) {
        ActivitiesStatusEntity entity = new ActivitiesStatusEntity();
        entity.setId(orcid);
        Date now = new Date();
        switch (type) {
        case EDUCATIONS:
            entity.setEducationsStatus(status);
            entity.setEducationsLastIndexed(now);
            break;
        case EMPLOYMENTS:
            entity.setEmploymentsStatus(status);
            entity.setEmploymentsLastIndexed(now);
            break;
        case FUNDINGS:
            entity.setFundingsStatus(status);
            entity.setFundingsLastIndexed(now);
            break;
        case PEER_REVIEWS:
            entity.setPeerReviewsStatus(status);
            entity.setPeerReviewsLastIndexed(now);
            break;
        case WORKS:
            entity.setWorksStatus(status);
            entity.setWorksLastIndexed(now);
            break;
        }
        entity.setDateCreated(now);
        entity.setLastModified(now);
        entityManager.persist(entity);
    }

    public boolean updateFailCount(String orcid, ActivityType type) {
        Query query = entityManager.createNativeQuery(
                "UPDATE activities_status SET " + type.getStatusColumnName() + " = (" + type.getStatusColumnName() + " + 1), last_modified=now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    public boolean success(String orcid, ActivityType type) {
        Query query = entityManager.createNativeQuery(
                "UPDATE activities_status SET " + type.getStatusColumnName() + " = 0, " + type.getLastIndexedColumnName() + " = now(), last_modified=now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    public boolean successAll(String orcid) {
        Query query = entityManager.createNativeQuery(
                "UPDATE activities_status SET educations_status=0, educations_last_indexed=now(), employments_status=0, employments_last_indexed=now(), fundings_status=0, fundings_last_indexed=now(), peer_reviews_status=0, peer_reviews_last_indexed=now(), works_status=0, works_last_indexed=now(), last_modified=now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    public boolean failAll(String orcid) {
        Query query = entityManager.createNativeQuery(
                "UPDATE activities_status SET educations_status=(educations_status + 1), employments_status=(employments_status + 1), fundings_status=(fundings_status + 1), peer_reviews_status=(peer_reviews_status + 1), works_status=(works_status + 1), last_modified=now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    public List<ActivitiesStatusEntity> getFailedElements(int batchSize) {
        TypedQuery<ActivitiesStatusEntity> query = entityManager.createQuery(
                "FROM ActivitiesStatusEntity WHERE educationsStatus > 0 OR employmentsStatus > 0 OR fundingsStatus > 0 OR peerReviewsStatus > 0 OR worksStatus > 0 ORDER BY id",
                ActivitiesStatusEntity.class);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }
}
