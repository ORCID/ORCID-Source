/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.integration.api.internal;

import static org.orcid.core.api.OrcidApiConstants.INTERNAL_API_PERSON_READ;
import static org.orcid.core.api.OrcidApiConstants.STATUS_PATH;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.orcid.api.common.OrcidClientHelper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;

public class InternalOAuthOrcidApiClientImpl implements InternalOAuthAPIService<ClientResponse> {

    private OrcidClientHelper orcidClientHelper;

    public InternalOAuthOrcidApiClientImpl(URI baseUri, Client c) throws URISyntaxException {
        orcidClientHelper = new OrcidClientHelper(baseUri, c);
    }
    
    @Override
    @GET
    @Produces("text/plain")
    @Path("/status")
    public ClientResponse viewStatusText(String orcid, String accessToken) {
        URI statusPath = UriBuilder.fromPath(STATUS_PATH).build();
        return orcidClientHelper.getClientResponseWithToken(statusPath, MediaType.TEXT_HTML, accessToken);
    }

    @Override
    @GET
    @Produces({ "application/vnd.orcid+json; qs=4", "application/orcid+json; qs=2", "application/xml", "application/json" })
    @Path("/{orcid}/authenticate")
    public ClientResponse viewPersonLastModified(String orcid, String accessToken) {
        URI statusPath = UriBuilder.fromPath(INTERNAL_API_PERSON_READ).build(orcid);
        return orcidClientHelper.getClientResponseWithToken(statusPath, MediaType.TEXT_HTML, accessToken);
    }    
}