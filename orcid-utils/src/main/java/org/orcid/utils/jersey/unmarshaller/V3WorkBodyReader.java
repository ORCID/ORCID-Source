package org.orcid.utils.jersey.unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.orcid.jaxb.model.v3.release.record.Work;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

@Provider
@Consumes({ "application/xml", "application/json" })
public class V3WorkBodyReader implements MessageBodyReader<Work> {

    private final Unmarshaller unmarshaller;

    public V3WorkBodyReader() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Work.class);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException jaxbException) {
            throw new ProcessingException("Error deserializing a " + Work.class, jaxbException);
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Work.class;
    }

    @Override
    public Work readFrom(Class<Work> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        try {
            return (Work) unmarshaller.unmarshal(entityStream);
        } catch (JAXBException e) {
            throw new ProcessingException("Error deserializing a " + Work.class, e);
        }
    }

}
