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

import org.orcid.core.api.OrcidApiConstants;

import de.undercouch.citeproc.csl.CSLItemData;
import de.undercouch.citeproc.helper.json.StringJsonBuilderFactory;

@Provider
@Produces({ OrcidApiConstants.APPLICATION_CITEPROC })
public class CSLItemDataMBWriter implements MessageBodyWriter<CSLItemData>{

    private final StringJsonBuilderFactory jsonFactory = new StringJsonBuilderFactory();

    @Override
    public long getSize(CSLItemData arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return CSLItemData.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(CSLItemData item, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4, MultivaluedMap<String, Object> arg5, OutputStream os)
            throws IOException, WebApplicationException {        
        os.write( item.toJson(jsonFactory.createJsonBuilder()).toString().getBytes());
    }

}
