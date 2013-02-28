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

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.message.OrcidMessage;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONConfiguration.MappedBuilder;
import com.sun.jersey.api.json.JSONJAXBContext;

import static org.orcid.api.common.OrcidApiConstants.*;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 12/04/2012
 */
@Provider
@Consumes(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
@Produces(value = { VND_ORCID_JSON, ORCID_JSON, MediaType.APPLICATION_JSON })
public class OrcidJsonJaxbContexResolver implements ContextResolver<JAXBContext> {

    private JAXBContext context;
    private Class<?>[] types = { OrcidMessage.class };

    public OrcidJsonJaxbContexResolver() throws JAXBException {
        MappedBuilder builder = JSONConfiguration.mapped();
        Map<String, String> namespaces = new HashMap<String, String>();
        namespaces.put("http://www.orcid.org/ns/orcid", "orcid");
        builder.xml2JsonNs(namespaces);
        builder.rootUnwrapping(false);
        this.context = new JSONJAXBContext(builder.build(), types);
    }

    @Override
    public JAXBContext getContext(Class<?> type) {
        return context;
    }

}