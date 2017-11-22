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
