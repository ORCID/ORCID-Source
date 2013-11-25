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
package org.orcid.api.common.jaxb;

import org.codehaus.jackson.jaxrs.Annotations;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig.Feature;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.Provider;

import static org.orcid.api.common.OrcidApiConstants.*;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 12/04/2012
 */
@Provider
@Consumes( { VND_ORCID_JSON, ORCID_JSON, "text/orcid+json" })
@Produces( { VND_ORCID_JSON, ORCID_JSON, "text/orcid+json" })
public class OrcidJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider {

    public OrcidJacksonJaxbJsonProvider() {
        super();
        configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,true);
    }

    public OrcidJacksonJaxbJsonProvider(Annotations... annotationsToUse) {
        super(annotationsToUse);
        configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,true);
    }

    public OrcidJacksonJaxbJsonProvider(ObjectMapper mapper, Annotations[] annotationsToUse) {
        super(mapper, annotationsToUse);
        configure(Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY,true);
    }
    
}
