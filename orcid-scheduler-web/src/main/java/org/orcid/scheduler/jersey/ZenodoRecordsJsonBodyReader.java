package org.orcid.scheduler.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.orcid.scheduler.loader.source.zenodo.api.ZenodoRecords;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

@Provider
@Consumes({ "application/json" })
public class ZenodoRecordsJsonBodyReader implements MessageBodyReader<ZenodoRecords> {

    static ObjectMapper mapperFromJSON;
    
    public ZenodoRecordsJsonBodyReader() {        
        mapperFromJSON = new ObjectMapper(); // thread safe!
        mapperFromJSON.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);         
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == ZenodoRecords.class;
    }

    @Override
    public ZenodoRecords readFrom(Class<ZenodoRecords> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        try {
            return mapperFromJSON.readValue(entityStream, type);
        } catch (IOException e) {
            throw new ProcessingException("Error deserializing a " + ZenodoRecords.class, e);
        }
    }

}
