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

import java.text.NumberFormat;
import java.util.Locale;

import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.orcid.core.manager.StatisticsManager;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;

/**
 * @author Shobhit Tyagi
 */
public class StatisticsCacheManagerImpl implements StatisticsCacheManager {

    @Resource
    StatisticsManager statisticsManager;

    @Resource(name = "statisticsCache")
    private Cache statisticsCache;

    LockerObjectsManager lockers = new LockerObjectsManager();

    @Override
    public StatisticsSummary retrieve() {
        StatisticsSummary statisticsSummary = toStatisticsSummary(statisticsCache.get("statisticsValues"));
        StatisticKeyEntity statisticKeyEntity = toStatisticsKey(statisticsCache.get("statisticsLatestKey"));
        StatisticKeyEntity statisticKeyEntityLatest = statisticsManager.getLatestKey();
        if (needsFresh(statisticsSummary, statisticKeyEntity, statisticKeyEntityLatest)) {
            try {
                synchronized (lockers.obtainLock("Statistics")) {
                    statisticsSummary = toStatisticsSummary(statisticsCache.get("statisticsValues"));
                    statisticKeyEntity = toStatisticsKey(statisticsCache.get("statisticsLatestKey"));
                    statisticKeyEntityLatest = statisticsManager.getLatestKey();
                    if (needsFresh(statisticsSummary, statisticKeyEntity, statisticKeyEntityLatest)) {
                        StatisticsSummary temp = statisticsManager.getLatestStatisticsModel();
                        if (temp != null && temp.getStatistics() != null && !temp.getStatistics().isEmpty()) {
                            statisticsSummary = temp;
                            statisticsCache.put(new Element("statisticsValues", statisticsSummary));
                            statisticsCache.put(new Element("statisticsLatestKey", statisticKeyEntityLatest));
                        }
                    }
                }
            } finally {
                lockers.releaseLock("Statistics");
            }
        }
        return statisticsSummary;
    }

    @Override
    public String retrieveLiveIds(Locale locale) {
        StatisticsSummary statisticsSummary = retrieve();
        long amount = statisticsSummary.getStatistics().get(StatisticsEnum.KEY_LIVE_IDS.value());
        NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.format(amount);
    }

    @Override
    public void remove() {
        statisticsCache.remove("statisticsValues");
        statisticsCache.remove("statisticsLatestKey");
    }

    @Override
    public StatisticsTimeline getStatisticsTimelineModel(StatisticsEnum type) {
        String cacheKey = new StringBuffer("statisticsTimeline").append(type.value()).toString();
        StatisticsTimeline statisticsTimeline = toStatisticsTimeline(statisticsCache.get(cacheKey));
        StatisticKeyEntity statisticKeyEntityLatest = statisticsManager.getLatestKey();
        if (needsFreshTimeline(statisticsTimeline, statisticKeyEntityLatest)) {
            try {
                synchronized (lockers.obtainLock("Statistics")) {
                    statisticsTimeline = toStatisticsTimeline(statisticsCache.get(cacheKey));
                    statisticKeyEntityLatest = statisticsManager.getLatestKey();
                    if (needsFreshTimeline(statisticsTimeline, statisticKeyEntityLatest)) {
                        StatisticsTimeline temp = statisticsManager.getStatisticsTimelineModel(type);
                        if (temp != null) {
                            statisticsTimeline = temp;
                            statisticsCache.put(new Element(cacheKey, statisticsTimeline));
                        }
                    }
                }
            } finally {
                lockers.releaseLock("Statistics");
            }
        }
        return statisticsTimeline;
    }

    private boolean needsFreshTimeline(StatisticsTimeline statisticsTimeline, StatisticKeyEntity statisticKeyEntityLatest) {
        if (statisticsTimeline == null)
            return true;
        if (statisticsTimeline.getTimeline() == null || statisticsTimeline.getTimeline().isEmpty())
            return true;
        if (!statisticsTimeline.getTimeline().containsKey(statisticKeyEntityLatest.getGenerationDate()))
            return true;
        return false;
    }

    private StatisticsTimeline toStatisticsTimeline(Element element) {
        return (StatisticsTimeline) (element != null ? element.getObjectValue() : null);
    }

    private StatisticsSummary toStatisticsSummary(Element element) {
        return (StatisticsSummary) (element != null ? element.getObjectValue() : null);
    }

    private StatisticKeyEntity toStatisticsKey(Element element) {
        return (StatisticKeyEntity) (element != null ? element.getObjectValue() : null);
    }

    private boolean needsFresh(StatisticsSummary statisticsSummary, StatisticKeyEntity statisticKeyEntity, StatisticKeyEntity statisticKeyEntityLatest) {
        if (statisticsSummary == null || statisticsSummary.getStatistics() == null)
            return true;
        if (!statisticKeyEntity.equals(statisticKeyEntityLatest))
            return true;
        return false;
    }
}
