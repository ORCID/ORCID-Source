package org.orcid.core.utils;

import org.springframework.beans.factory.InitializingBean;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.filter.LoggingFilter;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidJerseyConfigurer implements InitializingBean {

    private Client jerseyClient;
    private boolean loggingEnabled;

    public void setJerseyClient(Client jerseyClient) {
        this.jerseyClient = jerseyClient;
    }

    public void setLoggingEnabled(boolean loggingEnabled) {
        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (loggingEnabled) {
            jerseyClient.addFilter(new LoggingFilter());
        }
    }

}
