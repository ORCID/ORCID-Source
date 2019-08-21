package org.orcid.listener.persistence.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.listener.persistence.entities.ActivitiesStatusEntity;
import org.orcid.listener.persistence.entities.Api30RecordStatusEntity;
import org.orcid.listener.persistence.entities.RecordStatusEntity;
import org.orcid.listener.persistence.util.ActivityType;
import org.orcid.listener.persistence.util.AvailableBroker;
import org.springframework.stereotype.Component;

@Component
public class Api30RecordStatusDao {
    @PersistenceContext
    protected EntityManager entityManager;

    public Api30RecordStatusEntity get(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT * FROM record_status WHERE orcid = :orcid", Api30RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return (Api30RecordStatusEntity) query.getSingleResult();
    }

    public boolean exists(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM record_status WHERE orcid=:orcid");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger) query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    public void create(String orcid, Boolean summary) {
        Api30RecordStatusEntity entity = new Api30RecordStatusEntity();
        entity.setId(orcid);
        Date now = new Date();
        if (summary) {
            entity.setSummaryStatus(0);
            entity.setSummaryLastIndexed(now);
        } else {
            entity.setSummaryStatus(1);
        }
        entity.setDistinctionsStatus(0);
        entity.setEducationsStatus(0);
        entity.setEmploymentsStatus(0);
        entity.setFundingsStatus(0);
        entity.setInvitedPositionsStatus(0);
        entity.setMembershipStatus(0);
        entity.setPeerReviewsStatus(0);
        entity.setQualificationsStatus(0);
        entity.setResearchResourcesStatus(0);
        entity.setServicesStatus(0);
        entity.setWorksStatus(0);
        entity.setDateCreated(now);
        entity.setLastModified(now);
        entityManager.persist(entity);
    }

    public boolean updateFailCount(String orcid, ActivityType type) {
        Query query = entityManager.createNativeQuery("UPDATE api_3_0_record_status SET " + type.getStatusColumnName() + " = (" + type.getStatusColumnName()
                + " + 1), last_modified=now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    public boolean success(String orcid, ActivityType type) {
        Query query = entityManager.createNativeQuery("UPDATE api_3_0_record_status SET " + type.getStatusColumnName() + " = 0, " + type.getLastIndexedColumnName()
                + " = now(), last_modified=now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    public boolean updateFailCountOnSummary(String orcid) {
        Query query = entityManager.createNativeQuery("UPDATE api_3_0_record_status SET summary_status = (summary_status + 1), last_modified=now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    public boolean success(String orcid) {
        Query query = entityManager
                .createNativeQuery("UPDATE api_3_0_record_status SET summary_status = 0, summary_last_indexed=now(), last_modified=now() WHERE orcid = :orcid");
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }

    public List<Api30RecordStatusEntity> getFailedElements(int batchSize) {
        TypedQuery<Api30RecordStatusEntity> query = entityManager.createQuery(
                "FROM Api30RecordStatusEntity WHERE summaryStatus > 0 OR distinctionsStatus > 0 OR educationsStatus > 0 OR employmentsStatus > 0 OR fundingsStatus > 0 OR invitedPositionsStatus > 0 OR membershipStatus > 0 OR peerReviewsStatus > 0 OR qualificationsStatus > 0 OR researchResourcesStatus > 0 OR servicesStatus > 0 OR worksStatus > 0 ORDER BY id",
                Api30RecordStatusEntity.class);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }
}
