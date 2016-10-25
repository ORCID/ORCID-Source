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
package org.orcid.listener.jersey;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class OrcidJerseyClientHandler {
    /**
     * Create a default client with client configuration.
     *
     * @param cc the client configuration.
     * @return a default client.
     */
    public static Client create(ClientConfig cc, boolean isLocalHost) {
        Client client = null;
        if(isLocalHost) {
            client = Client.create(cc);
        } else {
            ClientConfig config = new DefaultClientConfig();
            config.getClasses().add(JacksonJaxbJsonProvider.class);
            client = Client.create(config);
        }
        return client;
    }    
}
