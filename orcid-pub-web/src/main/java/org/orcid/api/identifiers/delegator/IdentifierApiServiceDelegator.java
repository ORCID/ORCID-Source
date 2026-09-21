package org.orcid.api.identifiers.delegator;

import jakarta.ws.rs.core.Response;

public interface IdentifierApiServiceDelegator {

    public Response getIdentifierTypes(String locale);
    
}
