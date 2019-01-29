package org.orcid.scheduler.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class PublicProfileValidationClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PublicProfileValidationClient.class);

    public static Client create(boolean isDevelopmentMode) {
        ClientConfig config = null;
        if (isDevelopmentMode) {
            // DANGER!!! Trust all certs
            LOGGER.info("TRUSTING ALL SSL CERTS IN DEV MODE!!!");
            config = new PublicProfileValidationDevClientConfig();
        } else {
            config = new DefaultClientConfig();
        }
        config.getClasses().add(JacksonJaxbJsonProvider.class);
        return Client.create(config);
    }
    
    

}
