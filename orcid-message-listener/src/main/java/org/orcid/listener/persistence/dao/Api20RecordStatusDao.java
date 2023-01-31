package org.orcid.listener.persistence.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.listener.persistence.entities.Api20RecordStatusEntity;
import org.orcid.listener.persistence.util.ActivityType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Api20RecordStatusDao {
    @PersistenceContext
    protected EntityManager entityManager;

    public Api20RecordStatusEntity get(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT * FROM api_2_0_record_status WHERE orcid = :orcid", Api20RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return (Api20RecordStatusEntity) query.getSingleResult();
    }

    public boolean exists(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM api_2_0_record_status WHERE orcid=:orcid");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger) query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    @Transactional
    public void create(String orcid, Boolean summaryOk, List<ActivityType> failedElements) throws EntityExistsException {
        Api20RecordStatusEntity entity = new Api20RecordStatusEntity();
        entity.setId(orcid);
        Date now = new Date();
        updateStatus(entity, now, summaryOk, failedElements);
        entity.setDateCreated(now);
        entity.setLastModified(now);
        entityManager.persist(entity);
    }

    @Transactional
    public void update(String orcid, Boolean summaryOk, List<ActivityType> failedElements) throws IllegalArgumentException {
        Date now = new Date();
        Query query = entityManager.createNativeQuery("SELECT * FROM api_2_0_record_status WHERE orcid = :orcid", Api20RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        Api20RecordStatusEntity element = (Api20RecordStatusEntity) query.getSingleResult();
        updateStatus(element, now, summaryOk, failedElements);
        element.setLastModified(now);        
        entityManager.merge(element);
    }
    
    private void updateStatus(Api20RecordStatusEntity element, Date now, Boolean summaryOk, List<ActivityType> failedElements) {
        if (summaryOk) {
            element.setSummaryLastIndexed(now);
            element.setSummaryStatus(0);
        } else {
            element.setSummaryStatus(element.getSummaryStatus() == null ? 1 : element.getSummaryStatus() + 1);
        }

        if (failedElements.contains(ActivityType.EDUCATIONS)) {
            element.setEducationsStatus(element.getEducationsStatus() == null ? 1 : element.getEducationsStatus() + 1);
        } else {
            element.setEducationsLastIndexed(now);
            element.setEducationsStatus(0);
        }

        if (failedElements.contains(ActivityType.EMPLOYMENTS)) {
            element.setEmploymentsStatus(element.getEmploymentsStatus() == null ? 1 : element.getEmploymentsStatus() + 1);
        } else {
            element.setEmploymentsLastIndexed(now);
            element.setEmploymentsStatus(0);
        }

        if (failedElements.contains(ActivityType.FUNDINGS)) {
            element.setFundingsStatus(element.getFundingsStatus() == null ? 1 : element.getFundingsStatus() + 1);
        } else {
            element.setFundingsLastIndexed(now);
            element.setFundingsStatus(0);
        }

        if (failedElements.contains(ActivityType.PEER_REVIEWS)) {
            element.setPeerReviewsStatus(element.getPeerReviewsStatus() == null ? 1 : element.getPeerReviewsStatus() + 1);
        } else {
            element.setPeerReviewsLastIndexed(now);
            element.setPeerReviewsStatus(0);
        }

        if (failedElements.contains(ActivityType.WORKS)) {
            element.setWorksStatus(element.getWorksStatus() == null ? 1 : element.getWorksStatus() + 1);
        } else {
            element.setWorksLastIndexed(now);
            element.setWorksStatus(0);
        }
    }

    public List<Api20RecordStatusEntity> getFailedElements(int batchSize) {
        TypedQuery<Api20RecordStatusEntity> query = entityManager.createQuery(
                "FROM Api20RecordStatusEntity WHERE summaryStatus > 0 OR educationsStatus > 0 OR employmentsStatus > 0 OR fundingsStatus > 0 OR peerReviewsStatus > 0 OR worksStatus > 0 ORDER BY id",
                Api20RecordStatusEntity.class);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }    
    
    @Transactional
    public boolean setSummaryFail(String orcid) throws IllegalArgumentException {        
        Query query = entityManager.createNativeQuery("UPDATE api_2_0_record_status SET summary_status = summary_status + 1 WHERE orcid = :orcid", Api20RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    @Transactional
    public boolean setSummaryOk(String orcid) throws IllegalArgumentException {        
        Query query = entityManager.createNativeQuery("UPDATE api_2_0_record_status SET summary_status = 0, summary_last_indexed = now() WHERE orcid = :orcid", Api20RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    @Transactional
    public boolean setActivityFail(String orcid, ActivityType type) throws IllegalArgumentException {        
        Query query = entityManager.createNativeQuery("UPDATE api_2_0_record_status SET " + type.getStatusColumnName() + " = " + type.getStatusColumnName() + " + 1 WHERE orcid = :orcid", Api20RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    @Transactional
    public boolean setActivityOk(String orcid, ActivityType type) throws IllegalArgumentException {        
        Query query = entityManager.createNativeQuery("UPDATE api_2_0_record_status SET " + type.getStatusColumnName() + " = 0, " + type.getLastIndexedColumnName() + " = now() WHERE orcid = :orcid", Api20RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
}
