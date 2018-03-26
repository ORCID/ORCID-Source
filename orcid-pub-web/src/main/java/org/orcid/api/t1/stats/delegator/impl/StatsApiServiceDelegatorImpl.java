package org.orcid.api.t1.stats.delegator.impl;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.orcid.api.common.writer.stats.StatsTimelineList;
import org.orcid.api.t1.stats.delegator.StatsApiServiceDelegator;
import org.orcid.core.manager.impl.StatisticsCacheManager;
import org.orcid.core.security.visibility.aop.AccessControl;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;
import org.springframework.scheduling.annotation.Scheduled;

public class StatsApiServiceDelegatorImpl implements StatsApiServiceDelegator {

    @Resource
    StatisticsCacheManager statisticsCacheManager;

    @Scheduled(fixedDelayString = "${statistics.summary.interval.delay:600000}")
    public void updateToLatestStatisticsTimeline() {  
        statisticsCacheManager.setLatestStatisticsTimeline();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getStatsSummary() {
        StatisticsSummary summary = statisticsCacheManager.retrieve();
        if (summary == null)
            return Response.status(Status.NOT_FOUND).build();

        return Response.ok(summary).build();
    }
    
    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getAllStatsTimelines() {
        StatisticsSummary summary = statisticsCacheManager.retrieve();
        if (summary == null)
            return Response.status(Status.NOT_FOUND).build();
        StatsTimelineList statsTimelines = new StatsTimelineList();
        for (String key : summary.getStatistics().keySet()){
            StatisticsTimeline timeline = statisticsCacheManager.getStatisticsTimelineModel(StatisticsEnum.fromString(key));
            if (timeline !=null)
                statsTimelines.getTimelines().add(timeline);
        }
        return Response.ok(statsTimelines).build();
    }

    @Override
    @AccessControl(requiredScope = ScopePathType.READ_PUBLIC, enableAnonymousAccess = true)
    public Response getStatsTimeline(StatisticsEnum type) {
        StatisticsTimeline timeline = statisticsCacheManager.getStatisticsTimelineModel(type);
        if (timeline == null)
            return Response.status(Status.NOT_FOUND).build();

        return Response.ok(timeline).build();
    }
    
}
