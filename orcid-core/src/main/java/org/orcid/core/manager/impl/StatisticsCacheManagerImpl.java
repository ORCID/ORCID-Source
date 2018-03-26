package org.orcid.core.manager.impl;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.read_only.StatisticsManagerReadOnly;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

/**
 * @author Shobhit Tyagi
 */
public class StatisticsCacheManagerImpl implements StatisticsCacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsCacheManagerImpl.class);

    @Resource
    StatisticsManagerReadOnly statisticsManagerReadOnly;

    Object statisticsSummaryLocker = new Object();
    Object statisticsTimelineLocker = new Object();

    @Resource(name = "statisticsCache")
    private Cache statisticsCache;

    private static final String CACHE_STATISTICS_KEY = "cache_statistics_key";
    private static final String CACHE_TIMELINE_KEY = "cache_timeline_key";

    @Override
    public StatisticsSummary retrieve() {
        try {
            synchronized (statisticsSummaryLocker) {
                if (statisticsCache.get(CACHE_STATISTICS_KEY) == null) {
                    setLatestStatisticsSummary();
                }

                return toStatisticsSummary(statisticsCache.get(CACHE_STATISTICS_KEY));
            }
        } catch(Exception e) {
            LOG.error("Error fetching statistics in 'retrieve'", e);
            return null;
        }
    }

    static public StatisticsSummary toStatisticsSummary(Element element) {
        return (StatisticsSummary) (element != null ? element.getObjectValue() : null);
    }

    @Override
    public StatisticsTimeline getStatisticsTimelineModel(StatisticsEnum type) {
        try {
            synchronized (statisticsTimelineLocker) {
                if (statisticsCache.get(CACHE_TIMELINE_KEY) == null) {
                    setLatestStatisticsTimeline();
                }
                Map<StatisticsEnum, StatisticsTimeline> statisticsTimelineMap = toStatisticsTimelineMap(statisticsCache.get(CACHE_TIMELINE_KEY));                                       
                return statisticsTimelineMap.get(type);
            }
            
        } catch(Exception e) {
            LOG.error("Error fetching statistics in 'getStatisticsTimelineModel'", e);
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    static public Map<StatisticsEnum, StatisticsTimeline> toStatisticsTimelineMap(Element element) {
        return (Map<StatisticsEnum, StatisticsTimeline>) (element != null ? element.getObjectValue() : null);
    }

    @Override
    public String retrieveLiveIds(Locale locale) {
        StatisticsSummary statisticsSummary = retrieve();
        if (statisticsSummary == null 
                || statisticsSummary.getStatistics() == null 
                || statisticsSummary.getStatistics().get(StatisticsEnum.KEY_LIVE_IDS.value()) == null) {
            return "0";
        }
        Long amount = statisticsSummary.getStatistics().get(StatisticsEnum.KEY_LIVE_IDS.value());
        NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.format(amount);
    }

    @Override    
    public void setLatestStatisticsSummary() {
        LOG.info("Getting the latest statistics summary");

        StatisticsSummary summary = statisticsManagerReadOnly.getLatestStatisticsModel();

        if (statisticsCache.get(CACHE_STATISTICS_KEY) == null) {
            statisticsCache.put(new Element(CACHE_STATISTICS_KEY, summary));
        } else {
            statisticsCache.replace(new Element(CACHE_STATISTICS_KEY, summary));
        }
    }

    @Override    
    public synchronized void setLatestStatisticsTimeline() {
        LOG.info("Getting the latest statistics timeline map");

        Map<StatisticsEnum, StatisticsTimeline> latestStatisticsTimelineMap = new HashMap<StatisticsEnum, StatisticsTimeline>();
        for (StatisticsEnum type : StatisticsEnum.values()) {
            StatisticsTimeline statisticsTimeline = statisticsManagerReadOnly.getStatisticsTimelineModel(type);
            latestStatisticsTimelineMap.put(type, statisticsTimeline);
        }
        if (statisticsCache.get(CACHE_TIMELINE_KEY) == null) {
            statisticsCache.put(new Element(CACHE_TIMELINE_KEY, latestStatisticsTimelineMap));
        } else {
            statisticsCache.replace(new Element(CACHE_TIMELINE_KEY, latestStatisticsTimelineMap));
        }
    }
}
