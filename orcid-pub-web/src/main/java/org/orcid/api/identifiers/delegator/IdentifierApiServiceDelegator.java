package org.orcid.api.identifiers.delegator;

import javax.ws.rs.core.Response;

public interface IdentifierApiServiceDelegator {

    public Response getIdentifierTypes(String locale);
    
}
