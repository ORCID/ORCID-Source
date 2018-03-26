package org.orcid.listener.jersey;

import java.util.Map;
import java.util.Set;

import org.orcid.util.DevJerseyClientConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class OrcidJerseyClientHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrcidJerseyClientHandler.class);

    public static Client create(boolean isDevelopmentMode, Map<String, Object> properties) {
        ClientConfig config = null;
        if (isDevelopmentMode) {
            // DANGER!!! Trust all certs
            LOGGER.info("TRUSTING ALL SSL CERTS IN DEV MODE!!!");
            config = new DevJerseyClientConfig();
        } else {
            config = new DefaultClientConfig();
        }
        Set<String> keyset = properties.keySet();
        for (String key : keyset) {
            config.getProperties().put(key, properties.get(key));
        }
        config.getClasses().add(JacksonJaxbJsonProvider.class);
        return Client.create(config);
    }

}
