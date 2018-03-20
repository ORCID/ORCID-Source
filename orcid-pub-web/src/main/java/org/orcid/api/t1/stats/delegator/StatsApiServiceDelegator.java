package org.orcid.api.t1.stats.delegator;

import javax.ws.rs.core.Response;

import org.orcid.core.utils.statistics.StatisticsEnum;

public interface StatsApiServiceDelegator {

    public void updateToLatestStatisticsTimeline();
    
    public Response getStatsSummary();

    public Response getAllStatsTimelines();

    public Response getStatsTimeline(StatisticsEnum type);

}
