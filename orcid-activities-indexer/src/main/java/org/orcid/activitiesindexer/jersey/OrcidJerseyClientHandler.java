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
package org.orcid.activitiesindexer.jersey;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.orcid.activitiesindexer.util.DevJerseyClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.glassfish.jersey.jackson.JacksonFeature;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;

public class OrcidJerseyClientHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidJerseyClientHandler.class);

    public static Client create(boolean isDevelopmentMode, Map<String, Object> properties) {
        ClientBuilder builder = ClientBuilder.newBuilder();
        if (isDevelopmentMode) {
            // DANGER!!! Trust all certs
            LOGGER.info("TRUSTING ALL SSL CERTS IN DEV MODE!!!");
            DevJerseyClientConfig.apply(builder);
        }

        Client client = builder.register(JacksonFeature.class).build();

        Set<String> keyset = new HashMap<>(properties).keySet();
        for (String key : keyset) {
            client.property(key, properties.get(key));
        }

        return client;
    }

}
