package org.orcid.internal.server;

import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.orcid.internal.server.delegator.InternalOrcidApiServiceDelegator;
import org.springframework.beans.factory.annotation.Value;

public class InternalOrcidApiServiceBase {
    
    @Value("${org.orcid.core.internalApiBaseUri}")
    protected String internalApiBaseUri;
    
    @Resource
    InternalOrcidApiServiceDelegator serviceDelegator;
    
    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    @ApiOperation(value = "Check the server status", response=String.class)
    public Response viewStatusText() {
        return serviceDelegator.viewStatusText();
    }
}
