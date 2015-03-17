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
package org.orcid.api.common.jaxb;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.log4j.Logger;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.notification.addactivities.NotificationAddActivities;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.Work;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.xml.sax.SAXException;

/**
 * orcid-api - Nov 10, 2011 - OrcidValidationJaxbContextResolver
 * 
 * @author Declan Newman (declan)
 **/
@Provider
@Produces(value = { OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
@Consumes(value = { OrcidApiConstants.VND_ORCID_XML, OrcidApiConstants.ORCID_XML, MediaType.APPLICATION_XML, MediaType.WILDCARD })
public class OrcidValidationJaxbContextResolver implements ContextResolver<Unmarshaller> {

    private static final Logger logger = Logger.getLogger(OrcidValidationJaxbContextResolver.class);
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(NotificationAddActivities.class, "notification_2.0_rc1/notification-add-activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Work.class, "record_2.0_rc1/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Funding.class, "record_2.0_rc1/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Education.class, "record_2.0_rc1/education-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Employment.class, "record_2.0_rc1/employment-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(OrcidMessage.class, "orcid-message-");
    }
    private JAXBContext jaxbContext;
    private Map<String, Schema> schemaByPath = new ConcurrentHashMap<>();

    @Override
    public Unmarshaller getContext(Class<?> type) {
        try {
            Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
            String schemaFilenamePrefix = getSchemaFilenamePrefix(type);
            if (schemaFilenamePrefix != null) {
                Schema schema = getSchema(schemaFilenamePrefix);
                unmarshaller.setSchema(schema);
                unmarshaller.setEventHandler(new OrcidValidationHandler());
            }
            return unmarshaller;
        } catch (JAXBException e) {
            throw new WebApplicationException(getResponse(e));
        } catch (SAXException e) {
            throw new WebApplicationException(getResponse(e));
        }
    }

    private JAXBContext getJAXBContext() {
        if (jaxbContext == null) {
            try {
                jaxbContext = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS.size()]));
            } catch (JAXBException e) {
                throw new RuntimeException("Error creating JAXB context", e);
            }
        }
        return jaxbContext;
    }

    private Schema getSchema(String schemaFilenamePrefix) throws SAXException {
        String schemaPath = "/" + schemaFilenamePrefix + getApiVersion() + ".xsd";
        Schema schema = schemaByPath.get(schemaPath);
        if (schema != null) {
            return schema;
        }
        Source source = new StreamSource(OrcidValidationJaxbContextResolver.class.getResourceAsStream(schemaPath));
        schema = createSchemaFactory().newSchema(source);
        schemaByPath.put(schemaPath, schema);
        return schema;
    }

    private String getSchemaFilenamePrefix(Class<?> type) {
        return SCHEMA_FILENAME_PREFIX_BY_CLASS.get(type);
    }

    private SchemaFactory createSchemaFactory() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new OrcidResourceResolver(schemaFactory.getResourceResolver()));
        return schemaFactory;
    }

    private String getApiVersion() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        String apiVersion = (String) requestAttributes.getAttribute(ApiVersionFilter.API_VERSION_REQUEST_ATTRIBUTE_NAME, RequestAttributes.SCOPE_REQUEST);
        return apiVersion;
    }

    private Response getResponse(Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setErrorDesc(new ErrorDesc(e.getMessage()));
        return Response.serverError().entity(entity).build();
    }

    public class OrcidValidationHandler implements ValidationEventHandler {
        @Override
        public boolean handleEvent(ValidationEvent event) {
            if (event.getSeverity() == ValidationEvent.FATAL_ERROR || event.getSeverity() == ValidationEvent.ERROR) {
                logger.error(event.getMessage());
                throw new OrcidBadRequestException(event.getMessage());
            } else if (event.getSeverity() == ValidationEvent.WARNING) {
                logger.warn(event.getMessage());
            }
            return true;
        }
    }

}
