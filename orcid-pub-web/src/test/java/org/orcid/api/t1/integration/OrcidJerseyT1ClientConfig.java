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
package org.orcid.api.t1.integration;

import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import java.util.Map;
import java.util.Set;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 25/04/2012
 */
public class OrcidJerseyT1ClientConfig extends DefaultClientConfig implements ClientConfig {

    public OrcidJerseyT1ClientConfig(Set<Class<?>> providers) {
        super(providers);
    }

    public void setProperties(Map<String, Object> properties) {
        Set<String> keyset = properties.keySet();
        for (String key : keyset) {
            getProperties().put(key, properties.get(key));
        }
    }
}
