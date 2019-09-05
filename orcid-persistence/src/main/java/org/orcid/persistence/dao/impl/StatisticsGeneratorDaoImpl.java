package org.orcid.persistence.dao.impl;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.orcid.persistence.dao.StatisticsGeneratorDao;

public class StatisticsGeneratorDaoImpl implements StatisticsGeneratorDao {

    protected EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public long getLiveIds() {
        Query query = entityManager.createNativeQuery("select count(*) from profile where profile_deactivation_date is null and record_locked = false");
        BigInteger numberOfLiveIds = (BigInteger) query.getSingleResult();
        return numberOfLiveIds.longValue();
    }
    
    public long getAccountsWithEmployment() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM org_affiliation_relation WHERE org_affiliation_relation_role = 'EMPLOYMENT'");
        BigInteger numberOfAccountsWithEmployment = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithEmployment.longValue();
    }
    
    public long getAccountsWithEducationQualification() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM org_affiliation_relation WHERE (org_affiliation_relation_role = 'EDUCATION' or org_affiliation_relation_role = 'QUALIFICATION')");
        BigInteger numberOfAccountsWithEducation = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithEducation.longValue();
    }
    
    public long getAccountsWithInvitedPositionDistinction() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM org_affiliation_relation WHERE (org_affiliation_relation_role = 'INVITED_POSITION' or org_affiliation_relation_role = 'DISTINCTION')");
        BigInteger numberOfAccountsWithInvitedPositionDistinction = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithInvitedPositionDistinction.longValue();
    }
    
    public long getAccountsWithMembershipService() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM org_affiliation_relation WHERE (org_affiliation_relation_role = 'MEMBERSHIP' or org_affiliation_relation_role = 'SERVICE')");
        BigInteger numberOfAccountsWithMembershipService = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithMembershipService.longValue();
    }
    
    public long getAccountsWithExternalId() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM (" +
                "SELECT DISTINCT orcid FROM external_identifier " +
                "UNION (SELECT DISTINCT orcid FROM work WHERE (external_ids_json IS NOT NULL AND external_ids_json->>'workExternalIdentifier' != '[]')) " +
                "UNION (SELECT DISTINCT orcid FROM profile_funding WHERE (external_identifiers_json IS NOT NULL AND external_identifiers_json->>'fundingExternalIdentifier' != '[]')) " +
                "UNION (SELECT DISTINCT orcid FROM org_affiliation_relation oar, org o WHERE (oar.org_id=o.id AND o.org_disambiguated_id IS NOT NULL)) " +
                "UNION (SELECT DISTINCT orcid FROM peer_review WHERE (external_identifiers_json IS NOT NULL AND external_identifiers_json->>'workExternalIdentifier' != '[]')) " +
                ") AS DISTINCT_orcids");
        BigInteger numberOfAccountsWithExternalIds = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithExternalIds.longValue();
    }
    
    public long getAccountsWithFunding() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM profile_funding");
        BigInteger numberOfAccountsWithFunding = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithFunding.longValue();
    }
    
    public long getAccountsWithPeerReview() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM peer_review");
        BigInteger numberOfAccountsWithPeerReview = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithPeerReview.longValue();
    }
    
    public long getAccountsWithResearchResource() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM research_resource");
        BigInteger numberOfAccountsWithResearchResource = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithResearchResource.longValue();
    }
    
    public long getAccountsWithPersonId() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM external_identifier");
        BigInteger numberOfAccountsWithPersonId = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithPersonId.longValue();
    }

    public long getAccountsWithVerifiedEmails() {
        Query query = entityManager
                .createNativeQuery("select count(distinct profile.orcid) from email join profile on profile.profile_deactivation_date is null and email.is_verified=true and email.orcid=profile.orcid and profile.record_locked = false");
        BigInteger numberOfLiveIdsWithVerifiedEmail = (BigInteger) query.getSingleResult();
        return numberOfLiveIdsWithVerifiedEmail.longValue();
    }

    public long getAccountsWithWorks() {
        Query query = entityManager.createNativeQuery("select count (distinct orcid) from work");
        BigInteger numberOfAccountsWithWorks = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithWorks.longValue();
    }

    public long getNumberOfWorks() {
        Query query = entityManager.createNativeQuery("select count(*) from work");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }

    public long getNumberOfUniqueDOIs() {
        Query query = entityManager
                .createNativeQuery("SELECT COUNT(DISTINCT j->'workExternalIdentifierId'->>'content') FROM (SELECT json_array_elements(json_extract_path(external_ids_json, 'workExternalIdentifier')) AS j FROM work) AS a WHERE j->>'workExternalIdentifierType' = 'DOI'");
        BigInteger numberOfWorksWithDOIs = (BigInteger) query.getSingleResult();
        return numberOfWorksWithDOIs.longValue();
    }

    @Override
    public long getNumberOfEmployment() {
        Query query = entityManager.createNativeQuery("select count(*) from org_affiliation_relation where org_affiliation_relation_role = 'EMPLOYMENT'");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }

    @Override
    public long getNumberOfEducationQualification() {
        Query query = entityManager.createNativeQuery("select count(*) from org_affiliation_relation where (org_affiliation_relation_role = 'EDUCATION' or org_affiliation_relation_role = 'QUALIFICATION')");
        BigInteger numberOfEducationQualification = (BigInteger) query.getSingleResult();
        return numberOfEducationQualification.longValue();
    }
    
    @Override
    public long getNumberOfInvitedPositionDistinction() {
        Query query = entityManager.createNativeQuery("select count(*) from org_affiliation_relation where (org_affiliation_relation_role = 'INVITED_POSITION' or org_affiliation_relation_role = 'DISTINCTION')");
        BigInteger numberOfInvitedPositionDistinction = (BigInteger) query.getSingleResult();
        return numberOfInvitedPositionDistinction.longValue();
    }
    
    @Override
    public long getNumberOfMembershipService() {
        Query query = entityManager.createNativeQuery("select count(*) from org_affiliation_relation where (org_affiliation_relation_role = 'MEMBERSHIP' or org_affiliation_relation_role = 'SERVICE')");
        BigInteger numberOfMembershipService = (BigInteger) query.getSingleResult();
        return numberOfMembershipService.longValue();
    }

    @Override
    public long getNumberOfFunding() {
        Query query = entityManager.createNativeQuery("select count(*) from profile_funding");
        BigInteger numberOfFunding = (BigInteger) query.getSingleResult();
        return numberOfFunding.longValue();
    }
    
    @Override
    public long getNumberOfPeerReview() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM peer_review");
        BigInteger numberOfPeerReview = (BigInteger) query.getSingleResult();
        return numberOfPeerReview.longValue();
    }
    
    @Override
    public long getNumberOfResearchResource() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM research_resource");
        BigInteger numberOfResearchResource = (BigInteger) query.getSingleResult();
        return numberOfResearchResource.longValue();
    }
    
    @Override
    public long getNumberOfPersonId() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM external_identifier");
        BigInteger numberOfPersonId = (BigInteger) query.getSingleResult();
        return numberOfPersonId.longValue();
    }

    @Override
    public long getNumberOfEmploymentUniqueOrg() {
        Query query = entityManager.createNativeQuery("select count(distinct(org_id)) from org_affiliation_relation where org_affiliation_relation_role = 'EMPLOYMENT'");
        BigInteger numberOfEmploymentUniqueOrg = (BigInteger) query.getSingleResult();
        return numberOfEmploymentUniqueOrg.longValue();
    }

    @Override
    public long getNumberOfEducationQualificationUniqueOrg() {
        Query query = entityManager.createNativeQuery("select count(distinct(org_id)) from org_affiliation_relation where (org_affiliation_relation_role = 'EDUCATION' or org_affiliation_relation_role = 'QUALIFICATION')");
        BigInteger numberOfEducationQualificationUniqueOrg = (BigInteger) query.getSingleResult();
        return numberOfEducationQualificationUniqueOrg.longValue();
    }
    
    @Override
    public long getNumberOfInvitedPositionDistinctionUniqueOrg() {
        Query query = entityManager.createNativeQuery("select count(distinct(org_id)) from org_affiliation_relation where (org_affiliation_relation_role = 'INVITED_POSITION' or org_affiliation_relation_role = 'DISTINCTION')");
        BigInteger numberOfInvitedPositionDistinctionUniqueOrg = (BigInteger) query.getSingleResult();
        return numberOfInvitedPositionDistinctionUniqueOrg.longValue();
    }
    
    @Override
    public long getNumberOfMembershipServiceUniqueOrg() {
        Query query = entityManager.createNativeQuery("select count(distinct(org_id)) from org_affiliation_relation where (org_affiliation_relation_role = 'MEMBERSHIP' or org_affiliation_relation_role = 'SERVICE')");
        BigInteger numberOfEducationQualificationUniqueOrg = (BigInteger) query.getSingleResult();
        return numberOfEducationQualificationUniqueOrg.longValue();
    }

    @Override
    public long getNumberOfFundingUniqueOrg() {
        Query query = entityManager.createNativeQuery("select count(distinct(org_id)) from profile_funding");
        BigInteger numberOfFundingUniqueOrg = (BigInteger) query.getSingleResult();
        return numberOfFundingUniqueOrg.longValue();
    }
}
