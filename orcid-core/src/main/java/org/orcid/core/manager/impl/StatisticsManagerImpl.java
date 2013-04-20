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
package org.orcid.core.manager.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticsManager;
import org.orcid.persistence.dao.StatisticsDao;

public class StatisticsManagerImpl implements StatisticsManager {

    @Resource
    private StatisticsDao statisticsDao;

    public static final String KEY_LIVE_IDS = "liveIds";
    public static final String KEY_IDS_WITH_VERIFIED_EMAIL = "idsWithVerifiedEmail";
    public static final String KEY_IDS_WITH_WORKS = "idsWithWorks";
    public static final String KEY_NUMBER_OF_WORKS = "works";
    public static final String KEY_WORKS_WITH_DOIS = "worksWithDois";

    @Override
    public Map<String, Long> getStatistics() {
        Map<String, Long> statistics = new HashMap<String, Long>();
        statistics.put(KEY_LIVE_IDS, statisticsDao.getLiveIds());
        statistics.put(KEY_IDS_WITH_VERIFIED_EMAIL, statisticsDao.getAccountsWithVerifiedEmails());
        statistics.put(KEY_IDS_WITH_WORKS, statisticsDao.getAccountsWithWorks());
        statistics.put(KEY_NUMBER_OF_WORKS, statisticsDao.getNumberOfWorks());
        statistics.put(KEY_WORKS_WITH_DOIS, statisticsDao.getNumberOfWorksWithDOIs());
        return statistics;
    }

}
