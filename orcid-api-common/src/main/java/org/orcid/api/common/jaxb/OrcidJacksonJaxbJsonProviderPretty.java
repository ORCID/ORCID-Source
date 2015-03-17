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
package org.orcid.api.common.jaxb;

import static org.orcid.core.api.OrcidApiConstants.ORCID_JSON;
import static org.orcid.core.api.OrcidApiConstants.VND_ORCID_JSON;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.fasterxml.jackson.jaxrs.cfg.Annotations;




/**
 * @author Will Simpson
 */
@Provider
@Consumes({ VND_ORCID_JSON, ORCID_JSON, "text/orcid+json" })
@Produces({ VND_ORCID_JSON, ORCID_JSON, "text/orcid+json" })
public class OrcidJacksonJaxbJsonProviderPretty extends JacksonJaxbJsonProvider {

    public OrcidJacksonJaxbJsonProviderPretty() {
        super();
        configureAll();
    }

    public OrcidJacksonJaxbJsonProviderPretty(Annotations... annotationsToUse) {
        super(annotationsToUse);
        configureAll();
    }

    public OrcidJacksonJaxbJsonProviderPretty(ObjectMapper mapper, Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
        configureAll();
    }
    
    private void configureAll() {
        configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        configure(SerializationFeature.INDENT_OUTPUT, true);
    }

}
