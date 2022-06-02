package org.orcid.listener.jersey.reader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.orcid.jaxb.model.v3.release.record.Record;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

@Provider
@Consumes({"application/xml", "application/json"})
public class V3RecordBodyReader implements MessageBodyReader<Record> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Record.class;
    }

    @Override
    public Record readFrom(Class<Record> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Record.class);
            Record record = (Record) jaxbContext.createUnmarshaller()
                .unmarshal(entityStream);
            return record;
        } catch (JAXBException jaxbException) {
            throw new ProcessingException("Error deserializing a " + Record.class,
                jaxbException);
        }
    }

}
