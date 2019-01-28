package org.orcid.api.common;

import static org.orcid.core.api.OrcidApiConstants.CLIENT_PATH;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public interface OrcidApiService<T> {

    /**
     * @return Plain text message indicating health of service
     */
    @GET
    @Produces(value = { MediaType.TEXT_PLAIN })
    @Path(STATUS_PATH)
    public T viewStatusText();

    /**
     * Sends a redirect from the client URI to the group URI
     * 
     * @param clientId
     *            the client ID that corresponds to the client
     * @return a redirect to the ORCID record for the client's group
     */
    @GET
    @Path(CLIENT_PATH)
    T viewClient(String clientId);

}
