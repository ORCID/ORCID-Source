/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
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
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.orcid.persistence.dao.StatisticsDao;

public class StatisticsDaoImpl implements StatisticsDao {

    @PersistenceContext(unitName = "orcid")
    protected EntityManager entityManager;

    public long getLiveIds() {
        Query query = entityManager.createNativeQuery("select count(*) from profile where profile_deactivation_date is null");
        BigInteger numberOfLiveIds = (BigInteger) query.getSingleResult();
        return numberOfLiveIds.longValue();
    }

    public long getAccountsWithVerifiedEmails() {
        Query query = entityManager
                .createNativeQuery("select count(distinct profile.orcid) from email join profile on profile.profile_deactivation_date is null and email.is_verified=true and email.orcid=profile.orcid");
        BigInteger numberOfLiveIdsWithVerifiedEmail = (BigInteger) query.getSingleResult();
        return numberOfLiveIdsWithVerifiedEmail.longValue();
    }

    public long getAccountsWithWorks() {
        Query query = entityManager.createNativeQuery("select count (distinct orcid) from profile_work");
        BigInteger numberOfAccountsWithWorks = (BigInteger) query.getSingleResult();
        return numberOfAccountsWithWorks.longValue();
    }

    public long getNumberOfWorks() {
        Query query = entityManager.createNativeQuery("select count(*) from work");
        BigInteger numberOfWorks = (BigInteger) query.getSingleResult();
        return numberOfWorks.longValue();
    }

    public long getNumberOfWorksWithDOIs() {
        Query query = entityManager.createNativeQuery("select count(distinct identifier) from work_external_identifier where identifier_type='DOI'");
        BigInteger numberOfWorksWithDOIs = (BigInteger) query.getSingleResult();
        return numberOfWorksWithDOIs.longValue();
    }
}
