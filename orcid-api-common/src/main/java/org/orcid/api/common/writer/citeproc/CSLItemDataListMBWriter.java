package org.orcid.api.common.writer.citeproc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
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

@Deprecated
@Provider
@Produces({ OrcidApiConstants.APPLICATION_CITEPROC }) //text/x-bibliography ?
public class CSLItemDataListMBWriter implements MessageBodyWriter<CSLItemDataList> {

    private final StringJsonBuilderFactory jsonFactory = new StringJsonBuilderFactory();

    @Override
    public long getSize(CSLItemDataList arg0, Class<?> arg1, Type arg2, Annotation[] arg3, MediaType arg4) {
        return -1;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] arg2, MediaType arg3) {
        return CSLItemDataList.class.isAssignableFrom(type);
    }

    @Override
    public void writeTo(CSLItemDataList data, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException, WebApplicationException {
        final PrintStream printStream = new PrintStream(entityStream);
        printStream.println('[');
        boolean first = true;
        for (CSLItemData item : data.getData()){
            if (item != null){
                if (!first)
                    printStream.print(',');
                else
                    first = false;
                printStream.println(item.toJson(jsonFactory.createJsonBuilder()).toString());                   
            }
        }
        printStream.println(']');
    }

}
