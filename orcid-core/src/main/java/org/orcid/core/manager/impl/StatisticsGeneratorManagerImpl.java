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
package org.orcid.core.manager.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticsGeneratorManager;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.persistence.dao.StatisticsGeneratorDao;

public class StatisticsGeneratorManagerImpl implements StatisticsGeneratorManager {

    @Resource
    private StatisticsGeneratorDao statisticsGeneratorDao;

    @Override
    public Map<String, Long> generateStatistics() {        
        Map<String, Long> statistics = new HashMap<String, Long>();        
        statistics.put(StatisticsEnum.KEY_LIVE_IDS.value(), statisticsGeneratorDao.getLiveIds());
        statistics.put(StatisticsEnum.KEY_IDS_WITH_VERIFIED_EMAIL.value(), statisticsGeneratorDao.getAccountsWithVerifiedEmails());
        statistics.put(StatisticsEnum.KEY_IDS_WITH_WORKS.value(), statisticsGeneratorDao.getAccountsWithWorks());
        statistics.put(StatisticsEnum.KEY_NUMBER_OF_WORKS.value(), statisticsGeneratorDao.getNumberOfWorks());
        statistics.put(StatisticsEnum.KEY_UNIQUE_DOIS.value(), statisticsGeneratorDao.getNumberOfUniqueDOIs());
        statistics.put(StatisticsEnum.KEY_NUMBER_OF_EMPLOYMENT.value(), statisticsGeneratorDao.getNumberOfEmployment());
        statistics.put(StatisticsEnum.KEY_EMPLOYMENT_UNIQUE_ORG.value(), statisticsGeneratorDao.getNumberOfEmploymentUniqueOrg());
        statistics.put(StatisticsEnum.KEY_NUMBER_OF_EDUCATION.value(), statisticsGeneratorDao.getNumberOfEducation());
        statistics.put(StatisticsEnum.KEY_EDUCATION_UNIQUE_ORG.value(), statisticsGeneratorDao.getNumberOfEducationUniqueOrg());
        statistics.put(StatisticsEnum.KEY_NUMBER_OF_FUNDING.value(), statisticsGeneratorDao.getNumberOfFunding());
        statistics.put(StatisticsEnum.KEY_FUNDING_UNIQUE_ORG.value(), statisticsGeneratorDao.getNumberOfFundingUniqueOrg());
        
        return statistics;        
    }

}
