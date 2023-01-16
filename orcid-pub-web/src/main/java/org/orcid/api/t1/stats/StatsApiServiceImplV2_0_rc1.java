package org.orcid.api.t1.stats;

import jakarta.ws.rs.Path;

import org.orcid.core.api.OrcidApiConstants;

import io.swagger.annotations.Api;

@Api("Statistics API v2.0_rc1")
@Path("/v2.0_rc1" + OrcidApiConstants.STATS_PATH)
public class StatsApiServiceImplV2_0_rc1 extends StatsApiServiceImplBase {

}
