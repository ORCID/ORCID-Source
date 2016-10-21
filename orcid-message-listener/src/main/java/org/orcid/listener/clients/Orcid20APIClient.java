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
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.orcid.jaxb.model.record_rc3.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

@Component
public class Orcid20APIClient {

    Logger LOG = LoggerFactory.getLogger(Orcid20APIClient.class);
    
    @Resource
    protected Client jerseyClient;
    
    protected final URI baseUri;
    
    protected final String host;

    @Autowired
    public Orcid20APIClient(@Value("${org.orcid.message-listener.api20BaseURI}") String baseUri, @Value("${org.orcid.message-listener.host_header_override}") String hostHeaderOverride) throws URISyntaxException {
        LOG.info("Creating Orcid20APIClient with baseUri = " + baseUri + " and host = " + hostHeaderOverride);
        this.baseUri = new URI(baseUri);
        this.host = hostHeaderOverride;
    }

    /**
     * Fetches the profile from the ORCID public API v1.2
     * 
     * @param orcid
     * @return
     */
    public Record fetchPublicProfile(String orcid) throws LockedRecordException{
        WebResource webResource = jerseyClient.resource(baseUri);                
        ClientResponse response = webResource.path(orcid + "/record").accept(MediaType.APPLICATION_XML).header(HttpHeaders.HOST, host).get(ClientResponse.class);
        if (response.getStatus() != 200) {
            if (response.getStatus() == 409) {
                throw new LockedRecordException();
            }
            
            LOG.error("Unable to fetch public record " + orcid + " on API 2.0 HTTP error code: " + response.getStatus());
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
        }
        return response.getEntity(Record.class);
    }        
}
