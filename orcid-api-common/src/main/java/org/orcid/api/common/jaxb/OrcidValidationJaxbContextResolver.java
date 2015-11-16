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

import javax.annotation.Resource;
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
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.jaxb.model.groupid.GroupIdRecord;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.notification.permission.NotificationPermission;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Email;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
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
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(NotificationPermission.class, "notification_2.0_rc1/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Work.class, "record_2.0_rc1/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Funding.class, "record_2.0_rc1/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Education.class, "record_2.0_rc1/education-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Employment.class, "record_2.0_rc1/employment-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(PeerReview.class, "record_2.0_rc1/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(GroupIdRecord.class, "group-id-2.0_rc1/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(ResearcherUrl.class, "record_2.0_rc2/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(Email.class, "record_2.0_rc2/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS.put(OtherName.class, "record_2.0_rc2/other-names-");
    }
    private JAXBContext jaxbContext;
    private Map<String, Schema> schemaByPath = new ConcurrentHashMap<>();
    
    @Resource
    LocaleManager localeManager;

    @Override
    public Unmarshaller getContext(Class<?> type) {
        try {
            Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
            String schemaFilenamePrefix = getSchemaFilenamePrefix(type);
            // Old OrcidMessage APIs - do not validate here as we will
            // break "broke" integrations 
            if (!type.equals(OrcidMessage.class)) {
                if (schemaFilenamePrefix != null) {
                    Schema schema = getSchema(schemaFilenamePrefix);
                    unmarshaller.setSchema(schema);
                    unmarshaller.setEventHandler(new OrcidValidationHandler());
                }
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
                throw new RuntimeException(localeManager.resolveMessage("apiError.jaxb_context.exception"), e);
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
