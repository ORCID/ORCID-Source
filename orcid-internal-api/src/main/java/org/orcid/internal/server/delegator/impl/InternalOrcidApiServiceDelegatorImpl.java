package org.orcid.internal.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import javax.ws.rs.core.Response;

import org.orcid.internal.server.delegator.InternalOrcidApiServiceDelegator;

public class InternalOrcidApiServiceDelegatorImpl implements InternalOrcidApiServiceDelegator {

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }
    
}
