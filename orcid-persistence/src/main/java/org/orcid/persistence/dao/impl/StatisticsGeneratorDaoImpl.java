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
