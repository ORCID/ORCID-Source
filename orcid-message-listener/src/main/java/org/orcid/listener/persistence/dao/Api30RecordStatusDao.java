package org.orcid.listener.persistence.dao;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.listener.persistence.entities.Api30RecordStatusEntity;
import org.orcid.listener.persistence.util.ActivityType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class Api30RecordStatusDao {
    @PersistenceContext
    protected EntityManager entityManager;

    public Api30RecordStatusEntity get(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT * FROM api_3_0_record_status WHERE orcid = :orcid", Api30RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        return (Api30RecordStatusEntity) query.getSingleResult();
    }

    public boolean exists(String orcid) {
        Query query = entityManager.createNativeQuery("SELECT count(*) FROM api_3_0_record_status WHERE orcid=:orcid");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger) query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    public void create(String orcid, Boolean summaryOk, List<ActivityType> failedElements) throws EntityExistsException {
        Api30RecordStatusEntity entity = new Api30RecordStatusEntity();
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
        Query query = entityManager.createNativeQuery("SELECT * FROM api_3_0_record_status WHERE orcid = :orcid", Api30RecordStatusEntity.class);
        query.setParameter("orcid", orcid);
        Api30RecordStatusEntity element = (Api30RecordStatusEntity) query.getSingleResult();
        updateStatus(element, now, summaryOk, failedElements);
        element.setLastModified(now);        
        entityManager.merge(element);
    }
    
    private void updateStatus(Api30RecordStatusEntity element, Date now, Boolean summaryOk, List<ActivityType> failedElements) {
        if (summaryOk) {
            element.setSummaryLastIndexed(now);
            element.setSummaryStatus(0);
        } else {
            element.setSummaryStatus(element.getSummaryStatus() == null ? 1 : element.getSummaryStatus() + 1);
        }

        if (failedElements.contains(ActivityType.DISTINCTIONS)) {
            element.setDistinctionsStatus(element.getDistinctionsStatus() == null ? 1 : element.getDistinctionsStatus() + 1);
        } else {
            element.setDistinctionsLastIndexed(now);
            element.setDistinctionsStatus(0);
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

        if (failedElements.contains(ActivityType.INVITED_POSITIONS)) {
            element.setInvitedPositionsStatus(element.getInvitedPositionsStatus() == null ? 1 : element.getInvitedPositionsStatus() + 1);
        } else {
            element.setInvitedPositionsLastIndexed(now);
            element.setInvitedPositionsStatus(0);
        }

        if (failedElements.contains(ActivityType.MEMBERSHIP)) {
            element.setMembershipStatus(element.getMembershipStatus() == null ? 1 : element.getMembershipStatus() + 1);
        } else {
            element.setMembershipLastIndexed(now);
            element.setMembershipStatus(0);
        }

        if (failedElements.contains(ActivityType.PEER_REVIEWS)) {
            element.setPeerReviewsStatus(element.getPeerReviewsStatus() == null ? 1 : element.getPeerReviewsStatus() + 1);
        } else {
            element.setPeerReviewsLastIndexed(now);
            element.setPeerReviewsStatus(0);
        }

        if (failedElements.contains(ActivityType.QUALIFICATIONS)) {
            element.setQualificationsStatus(element.getQualificationsStatus() == null ? 1 : element.getQualificationsStatus() + 1);
        } else {
            element.setQualificationsLastIndexed(now);
            element.setQualificationsStatus(0);
        }

        if (failedElements.contains(ActivityType.RESEARCH_RESOURCES)) {
            element.setResearchResourcesStatus(element.getResearchResourcesStatus() == null ? 1 : element.getResearchResourcesStatus() + 1);
        } else {
            element.setResearchResourcesLastIndexed(now);
            element.setResearchResourcesStatus(0);
        }

        if (failedElements.contains(ActivityType.SERVICES)) {
            element.setServicesStatus(element.getServicesStatus() == null ? 1 : element.getServicesStatus() + 1);
        } else {
            element.setServicesLastIndexed(now);
            element.setServicesStatus(0);
        }

        if (failedElements.contains(ActivityType.WORKS)) {
            element.setWorksStatus(element.getWorksStatus() == null ? 1 : element.getWorksStatus() + 1);
        } else {
            element.setWorksLastIndexed(now);
            element.setWorksStatus(0);
        }
    }

    public List<Api30RecordStatusEntity> getFailedElements(int batchSize) {
        TypedQuery<Api30RecordStatusEntity> query = entityManager.createQuery(
                "FROM Api30RecordStatusEntity WHERE summaryStatus > 0 OR distinctionsStatus > 0 OR educationsStatus > 0 OR employmentsStatus > 0 OR fundingsStatus > 0 OR invitedPositionsStatus > 0 OR membershipStatus > 0 OR peerReviewsStatus > 0 OR qualificationsStatus > 0 OR researchResourcesStatus > 0 OR servicesStatus > 0 OR worksStatus > 0 ORDER BY id",
                Api30RecordStatusEntity.class);
        query.setMaxResults(batchSize);
        return query.getResultList();
    }
}
