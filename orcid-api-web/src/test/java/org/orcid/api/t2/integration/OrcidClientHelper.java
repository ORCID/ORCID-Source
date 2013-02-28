/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.api.t2.integration;

import java.net.URI;
import java.net.URISyntaxException;

import javax.ws.rs.core.UriBuilder;

import org.orcid.jaxb.model.message.OrcidMessage;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class OrcidClientHelper {

    protected Client jerseyClient;

    protected URI baseUri;

    public OrcidClientHelper(URI baseUri, Client client) throws URISyntaxException {
        this.jerseyClient = client;
        if (baseUri.toString().endsWith("/")) {
            String s = baseUri.toString();
            this.baseUri = new URI(s.substring(0, s.length() - 1));
        } else {
            this.baseUri = baseUri;
        }
    }

    public ClientResponse getClientResponse(URI uri, String accept) {
        return createRootResource(uri).accept(accept).get(ClientResponse.class);
    }

    public ClientResponse postClientResponse(URI uri, String accept, OrcidMessage orcidMessage) {
        return createRootResource(uri).accept(accept).type(accept).post(ClientResponse.class, orcidMessage);
    }

    public ClientResponse putClientResponse(URI uri, String accept, OrcidMessage orcidMessage) {
        ClientResponse response = createRootResource(uri).accept(accept).type(accept).put(ClientResponse.class, orcidMessage);
        return response;
    }

    public ClientResponse deleteClientResponse(URI uri, String accept) {
        return createRootResource(uri).accept(accept).type(accept).delete(ClientResponse.class);
    }

    public URI deriveUriFromRestPath(String restPath) {
        URI uri = UriBuilder.fromPath(restPath).build();
        return uri;
    }

    public URI deriveUriFromRestPath(String restPath, String orcid) {
        URI uri = UriBuilder.fromPath(restPath).build(orcid);
        return uri;
    }

    public WebResource createRootResource(URI uri) {
        return (jerseyClient.resource(resolveUri(uri)));
    }

    public WebResource createRootResource(String uri) {
        return createRootResource(deriveUriFromRestPath(uri));
    }

    private URI resolveUri(URI uri) {
        try {
            return new URI(baseUri.toString().concat(uri.toString()));
        } catch (URISyntaxException e) {
            throw new RuntimeException("Calculated URI is invalid. Please check the settings.", e);
        }
    }
}
