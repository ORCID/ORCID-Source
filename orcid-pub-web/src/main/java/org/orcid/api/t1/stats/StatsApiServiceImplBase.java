package org.orcid.api.t1.stats;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.orcid.api.common.writer.stats.StatsTimelineList;
import org.orcid.api.t1.stats.delegator.StatsApiServiceDelegator;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.message.ScopeConstants;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import io.swagger.annotations.AuthorizationScope;

abstract public class StatsApiServiceImplBase {

    private StatsApiServiceDelegator serviceDelegator;

    public void setServiceDelegator(StatsApiServiceDelegator serviceDelegator) {
        this.serviceDelegator = serviceDelegator;
    }

    /**
     * @return Latest stats for the ORCID service
     */
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path("")
    @ApiOperation(value = "Fetch latest statistics summary", authorizations = { @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_PUBLIC, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Statistic found", response = StatisticsSummary.class),
            @ApiResponse(code = 404, message = "Statistic not found") })
    public Response viewStatsSummary() {
        return serviceDelegator.getStatsSummary();
    }

    /**
     * @return A time series for a given statistic
     */
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, OrcidApiConstants.TEXT_CSV })
    @Path(OrcidApiConstants.STATS_ALL)
    @ApiOperation(value = "Fetch a time series for all statistics", authorizations = { @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_PUBLIC, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Statistic found", response = StatsTimelineList.class),
            @ApiResponse(code = 404, message = "Statistic not found") })
    public Response viewAllStatsTimelines() {
        return serviceDelegator.getAllStatsTimelines();
    }

    /**
     * @return A time series for a given statistic
     */
    @GET
    @Produces(value = { MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
    @Path(OrcidApiConstants.STATS)
    @ApiOperation(value = "Fetch a time series for a given statistic", notes = "Valid statistic types can be inferred from the /statistics resource.  e.g. 'works'", authorizations = { @Authorization(value = "orcid_two_legs", scopes = { @AuthorizationScope(scope = ScopeConstants.READ_PUBLIC, description = "you need this") }) })
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Statistic found", response = StatisticsTimeline.class),
            @ApiResponse(code = 404, message = "Statistic not found") })
    public Response viewStatsTimeline(@ApiParam(allowableValues=StatisticsEnum.allowableSwaggerValues) @PathParam("type") StatisticsEnum statisticName) {
        return serviceDelegator.getStatsTimeline(statisticName);
    }
    
    //StatisticsEnum

}
