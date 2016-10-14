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
package org.orcid.listener.clients;

import java.net.URI;
import java.net.URISyntaxException;

import javax.annotation.Resource;
import javax.ws.rs.core.MediaType;

import org.orcid.jaxb.model.record_rc3.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/** This is a temporary implementation. 
 * Angel - replace with yours when merging your branch.
 * 
 * @author tom
 *
 */
@Component
public class Orcid20APIClient {

    Logger LOG = LoggerFactory.getLogger(Orcid20APIClient.class);
    
    //@Resource
    protected Client jerseyClient;
    
    protected final URI baseUri;

    @Autowired
    public Orcid20APIClient(@Value("${org.orcid.message-lisener.api20BaseURI}") String baseUri) throws URISyntaxException {
        LOG.info("Creating Orcid20APIClient with baseUri = " + baseUri);
        this.baseUri = new URI(baseUri);
        ClientConfig config = new DefaultClientConfig();
        config.getClasses().add(JacksonJaxbJsonProvider.class);
        jerseyClient = Client.create(config);
    }

    /**
     * Fetches the profile from the ORCID public API v1.2
     * 
     * @param orcid
     * @return
     */
    public Record fetchPublicProfile(String orcid) throws LockedRecordException{
        WebResource webResource = jerseyClient.resource(baseUri);
        ClientResponse response = webResource.path(orcid + "/record").accept(MediaType.APPLICATION_XML).get(ClientResponse.class);
        if (response.getStatus() != 200) {
            if (response.getStatus() == 409)
                throw new LockedRecordException();
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }
        return response.getEntity(Record.class);
    }
}