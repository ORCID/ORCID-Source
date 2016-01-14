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
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecord;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.orcid.jaxb.model.notification.permission.NotificationPermission;
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
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(GroupIdRecord.class, "group-id-2.0_rc1/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(NotificationPermission.class, "notification_2.0_rc1/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(org.orcid.jaxb.model.record_rc1.Education.class, "record_2.0_rc1/education-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(org.orcid.jaxb.model.record_rc1.Employment.class, "record_2.0_rc1/employment-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(org.orcid.jaxb.model.record_rc1.Funding.class, "record_2.0_rc1/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(org.orcid.jaxb.model.record_rc1.PeerReview.class, "record_2.0_rc1/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(org.orcid.jaxb.model.record_rc1.Work.class, "record_2.0_rc1/work-");

    }
    
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.groupid_rc2.GroupIdRecord.class, "group-id-2.0_rc2/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(NotificationPermission.class, "notification_2.0_rc1/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Address.class, "record_2.0_rc2/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Addresses.class, "record_2.0_rc2/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Education.class, "record_2.0_rc2/education-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Email.class, "record_2.0_rc2/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Employment.class, "record_2.0_rc2/employment-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.ExternalIdentifier.class, "record_2.0_rc2/external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.ExternalIdentifiers.class, "record_2.0_rc2/external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Funding.class, "record_2.0_rc2/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Keyword.class, "record_2.0_rc2/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Keywords.class, "record_2.0_rc2/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Name.class, "record_2.0_rc2/personal-details-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.OtherName.class, "record_2.0_rc2/other-name-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.OtherNames.class, "record_2.0_rc2/other-name-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.PeerReview.class, "record_2.0_rc2/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.ResearcherUrl.class, "record_2.0_rc2/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.ResearcherUrls.class, "record_2.0_rc2/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Work.class, "record_2.0_rc2/work-");

    }
    private JAXBContext jaxbContext_2_0_rc1;
    private JAXBContext jaxbContext_2_0_rc2;
    private Map<String, Schema> schemaByPath = new ConcurrentHashMap<>();
    
    @Resource
    LocaleManager localeManager;

    @Override
    public Unmarshaller getContext(Class<?> type) {
        try {
        	String schemaFilenamePrefix = getSchemaFilenamePrefix(type);
            Unmarshaller unmarshaller = getJAXBContext().createUnmarshaller();
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
        try {
        	if(getApiVersion().equals("2.0_rc2")) {
        		if(jaxbContext_2_0_rc2 == null) {
        			jaxbContext_2_0_rc2 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.size()]));
        		}
        		return jaxbContext_2_0_rc2;
        	} else {
        		if(jaxbContext_2_0_rc1 == null) {
        			jaxbContext_2_0_rc1 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.size()]));
        		}
        		
        		return jaxbContext_2_0_rc1;
        	}
            
        } catch (JAXBException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.jaxb_context.exception"), e);
        }
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
    	if(getApiVersion().equals("2.0_rc2")) {
    		return SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.get(type);
    	}
    	
    	return SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.get(type);
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
