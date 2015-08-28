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
package org.orcid.api.common.writer.citeproc;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.orcid.jaxb.model.record.Work;

import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.helper.json.StringJsonBuilderFactory;

@Provider
@Produces({ "application/vnd.citationstyles.csl+json" })
public class CiteprocWorkMBWriter implements MessageBodyWriter<Work>{

    private final WorkToCiteprocTranslator translator = new WorkToCiteprocTranslator();
    private final StringJsonBuilderFactory jsonFactory = new StringJsonBuilderFactory();
    
    @Override
    public long getSize(Work arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Work.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(Work work, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
    OutputStream entityStream) throws IOException, WebApplicationException {
        CSLItemData data = translator.toCiteproc(work);        
        entityStream.write( data.toJson(jsonFactory.createJsonBuilder()).toString().getBytes());
    }

}
