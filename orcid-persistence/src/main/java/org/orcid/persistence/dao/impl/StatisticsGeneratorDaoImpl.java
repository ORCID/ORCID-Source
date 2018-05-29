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
    
    public long getAccountsWithEducation() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM org_affiliation_relation WHERE org_affiliation_relation_role = 'EDUCATION'");
        BigInteger numberOfAccountsWithEducation = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithEducation.longValue();
    }
    
    public long getAccountsWithEmployment() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(DISTINCT orcid) FROM org_affiliation_relation WHERE org_affiliation_relation_role = 'EMPLOYMENT'");
        BigInteger numberOfAccountsWithEmployment = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithEmployment.longValue();
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
    public long getNumberOfEducation() {
        Query query = entityManager.createNativeQuery("select count(*) from org_affiliation_relation where org_affiliation_relation_role = 'EDUCATION'");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }

    @Override
    public long getNumberOfFunding() {
        Query query = entityManager.createNativeQuery("select count(*) from profile_funding");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }
    
    @Override
    public long getNumberOfPeerReview() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM peer_review");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }
    
    @Override
    public long getNumberOfPersonId() {
        Query query = entityManager.createNativeQuery("SELECT COUNT(*) FROM external_identifier");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }

    @Override
    public long getNumberOfEmploymentUniqueOrg() {
        Query query = entityManager.createNativeQuery("select count(distinct(org_id)) from org_affiliation_relation where org_affiliation_relation_role = 'EMPLOYMENT'");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }

    @Override
    public long getNumberOfEducationUniqueOrg() {
        Query query = entityManager.createNativeQuery("select count(distinct(org_id)) from org_affiliation_relation where org_affiliation_relation_role = 'EDUCATION'");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }

    @Override
    public long getNumberOfFundingUniqueOrg() {
        Query query = entityManager.createNativeQuery("select count(distinct(org_id)) from profile_funding");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }
}
