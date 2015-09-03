package org.orcid.internal.server.delegator.impl;

import static org.orcid.core.api.OrcidApiConstants.STATUS_OK_MESSAGE;

import javax.ws.rs.core.Response;

import org.orcid.internal.server.delegator.InternalApiServiceDelegator;

public class InternalApiServiceDelegatorImpl implements InternalApiServiceDelegator {

    @Override
    public Response viewStatusText() {
        return Response.ok(STATUS_OK_MESSAGE).build();
    }

}
