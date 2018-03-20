package org.orcid.api.common.jaxb;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Resource;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.locale.LocaleManager;
import org.orcid.jaxb.model.notification_v2.Notification;

/**
 * 
 * @author Will Simpson
 *
 */
@Provider
@Produces(value = { OrcidApiConstants.VND_ORCID_JSON, OrcidApiConstants.ORCID_JSON, MediaType.APPLICATION_JSON })
public class NotificationJsonMessageBodyWriter implements MessageBodyWriter<Notification> {

	@Resource
	LocaleManager localeManager;
	
    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Notification.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(Notification notification, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        OrcidJacksonJaxbJsonProvider jaxbJsonProvider = new OrcidJacksonJaxbJsonProvider();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            jaxbJsonProvider.writeTo(notification, type, genericType, annotations, mediaType, null, bos);
        } catch (IOException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.fetch_notification_size.exception"), e);
        }
        return bos.size();
    }

    @Override
    public void writeTo(Notification notification, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        OrcidJacksonJaxbJsonProvider jaxbJsonProvider = new OrcidJacksonJaxbJsonProvider();
        jaxbJsonProvider.writeTo(notification, type, genericType, annotations, mediaType, httpHeaders, entityStream);
    }

}
