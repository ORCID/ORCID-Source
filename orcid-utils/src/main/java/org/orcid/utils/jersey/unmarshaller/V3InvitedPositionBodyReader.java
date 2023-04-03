package org.orcid.utils.jersey.unmarshaller;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.orcid.jaxb.model.v3.release.record.InvitedPosition;

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

@Provider
@Consumes({ "application/xml", "application/json" })
public class V3InvitedPositionBodyReader implements MessageBodyReader<InvitedPosition> {

    private final Unmarshaller unmarshaller;

    public V3InvitedPositionBodyReader() {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(InvitedPosition.class);
            unmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException jaxbException) {
            throw new ProcessingException("Error deserializing a " + InvitedPosition.class, jaxbException);
        }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == InvitedPosition.class;
    }

    @Override
    public InvitedPosition readFrom(Class<InvitedPosition> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
            InputStream entityStream) throws IOException, WebApplicationException {
        try {
            return (InvitedPosition) unmarshaller.unmarshal(entityStream);
        } catch (JAXBException e) {
            throw new ProcessingException("Error deserializing a " + InvitedPosition.class, e);
        }
    }

}
