package org.orcid.core.manager.impl;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.ehcache.Cache;
import org.orcid.core.manager.read_only.StatisticsManagerReadOnly;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author Shobhit Tyagi
 */
@EnableScheduling
public class StatisticsCacheManagerImpl implements StatisticsCacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(StatisticsCacheManagerImpl.class);

    @Resource
    StatisticsManagerReadOnly statisticsManagerReadOnly;

    @Resource(name = "statisticsCache")
    private Cache<String, Object> statisticsCache;

    private static final String CACHE_STATISTICS_KEY = "cache_statistics_key";
    private static final String CACHE_TIMELINE_KEY = "cache_timeline_key";

    private Long liveIds;

    @PostConstruct
    public void initCache() {
        setLatestStatisticsSummary();
        setLatestStatisticsTimeline();
    }

    @Override
    public StatisticsSummary retrieve() {
        try {
            if (!statisticsCache.containsKey(CACHE_STATISTICS_KEY)) {
                setLatestStatisticsSummary();
            }
            return (StatisticsSummary) statisticsCache.get(CACHE_STATISTICS_KEY);
        } catch (Exception e) {
            LOG.error("Error fetching statistics in 'retrieve'", e);
            return null;
        }
    }

    @Override
    public StatisticsTimeline getStatisticsTimelineModel(StatisticsEnum type) {
        try {
            if (!statisticsCache.containsKey(CACHE_TIMELINE_KEY)) {
                setLatestStatisticsTimeline();
            }
            @SuppressWarnings("unchecked")
            Map<StatisticsEnum, StatisticsTimeline> statisticsTimelineMap = (Map<StatisticsEnum, StatisticsTimeline>) statisticsCache.get(CACHE_TIMELINE_KEY);
            return statisticsTimelineMap.get(type);
        } catch (Exception e) {
            LOG.error("Error fetching statistics in 'getStatisticsTimelineModel'", e);
            return null;
        }
    }

    @Override
    public String retrieveLiveIds(Locale locale) {
        if (this.liveIds == null) {
            StatisticsSummary statisticsSummary = retrieve();
            if (statisticsSummary == null || statisticsSummary.getStatistics() == null
                    || statisticsSummary.getStatistics().get(StatisticsEnum.KEY_LIVE_IDS.value()) == null) {
                return "0";
            } else {
                this.liveIds = statisticsSummary.getStatistics().get(StatisticsEnum.KEY_LIVE_IDS.value());
            }
        }
        NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.format(this.liveIds);
    }

    @Scheduled(fixedDelayString = "${statistics.key.interval.delay:3600000}")
    private void setLatestStatisticsSummary() {
        LOG.info("Getting the latest statistics summary");
        StatisticsSummary summary = statisticsManagerReadOnly.getLatestStatisticsModel();
        if(summary != null) {
            if (!statisticsCache.containsKey(CACHE_STATISTICS_KEY)) {
                statisticsCache.put(CACHE_STATISTICS_KEY, summary);
            } else {
                statisticsCache.replace(CACHE_STATISTICS_KEY, summary);
            }
        }
        this.liveIds = (summary == null) ? null : summary.getStatistics().get(StatisticsEnum.KEY_LIVE_IDS.value());
        LOG.info("Caching liveIds value to:" + this.liveIds);
        LOG.info("Latest statistics summary is set");
    }

    @Scheduled(fixedDelayString = "${statistics.key.interval.delay:3600000}")
    private void setLatestStatisticsTimeline() {
        LOG.info("Getting the latest statistics timeline map");
        Map<StatisticsEnum, StatisticsTimeline> latestStatisticsTimelineMap = new HashMap<StatisticsEnum, StatisticsTimeline>();
        for (StatisticsEnum type : StatisticsEnum.values()) {
            StatisticsTimeline statisticsTimeline = statisticsManagerReadOnly.getStatisticsTimelineModel(type);
            latestStatisticsTimelineMap.put(type, statisticsTimeline);
        }
        if (!statisticsCache.containsKey(CACHE_TIMELINE_KEY)) {
            statisticsCache.put(CACHE_TIMELINE_KEY, latestStatisticsTimelineMap);
        } else {
            statisticsCache.replace(CACHE_TIMELINE_KEY, latestStatisticsTimelineMap);
        }
        LOG.info("Latest statistics timeline map is set");
    }
}
