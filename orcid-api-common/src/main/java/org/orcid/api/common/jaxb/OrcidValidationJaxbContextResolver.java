package org.orcid.api.common.jaxb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.MarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.orcid.api.common.filter.ApiVersionFilter;
import org.orcid.api.common.util.ApiUtils;
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.jaxb.model.common.adapters.IllegalEnumValueException;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.sun.istack.SAXException2;
import com.sun.xml.bind.api.AccessorException;

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
     
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_V2 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.groupid_v2.GroupIdRecord.class, "group-id-2.0/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.notification.permission_v2.NotificationPermission.class, "notification_2.0/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Address.class, "record_2.0/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Addresses.class, "record_2.0/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Education.class, "record_2.0/education-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Email.class, "record_2.0/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Employment.class, "record_2.0/employment-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier.class, "record_2.0/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers.class, "record_2.0/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Funding.class, "record_2.0/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Keyword.class, "record_2.0/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Keywords.class, "record_2.0/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Name.class, "record_2.0/personal-details-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.OtherName.class, "record_2.0/other-name-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.OtherNames.class, "record_2.0/other-name-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.PeerReview.class, "record_2.0/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.ResearcherUrl.class, "record_2.0/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.ResearcherUrls.class, "record_2.0/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.Work.class, "record_2.0/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record_v2.WorkBulk.class, "record_2.0/bulk-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record.summary_v2.Educations.class, "record_2.0/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.put(org.orcid.jaxb.model.record.summary_v2.Employments.class, "record_2.0/activities-");
    }
    
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.groupid_v2.GroupIdRecord.class, "group-id-2.1/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.notification.permission_v2.NotificationPermission.class, "notification_2.1/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Address.class, "record_2.1/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Addresses.class, "record_2.1/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Education.class, "record_2.1/education-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Email.class, "record_2.1/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Employment.class, "record_2.1/employment-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier.class, "record_2.1/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers.class, "record_2.1/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Funding.class, "record_2.1/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Keyword.class, "record_2.1/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Keywords.class, "record_2.1/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Name.class, "record_2.1/personal-details-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.OtherName.class, "record_2.1/other-name-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.OtherNames.class, "record_2.1/other-name-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.PeerReview.class, "record_2.1/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.ResearcherUrl.class, "record_2.1/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.ResearcherUrls.class, "record_2.1/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.Work.class, "record_2.1/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record_v2.WorkBulk.class, "record_2.1/bulk-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record.summary_v2.Educations.class, "record_2.1/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.put(org.orcid.jaxb.model.record.summary_v2.Employments.class, "record_2.1/activities-");
    }
    
      
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord.class, "group-id-3.0/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission.class, "notification_3.0/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Address.class, "record_3.0/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Addresses.class, "record_3.0/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Education.class, "record_3.0/education-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Email.class, "record_3.0/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Employment.class, "record_3.0/employment-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier.class, "record_3.0/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifiers.class, "record_3.0/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Funding.class, "record_3.0/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Keyword.class, "record_3.0/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Keywords.class, "record_3.0/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Name.class, "record_3.0/personal-details-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.OtherName.class, "record_3.0/other-name-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.OtherNames.class, "record_3.0/other-name-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.PeerReview.class, "record_3.0/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.ResearcherUrl.class, "record_3.0/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.ResearcherUrls.class, "record_3.0/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Work.class, "record_3.0/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.WorkBulk.class, "record_3.0/bulk-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.summary.Educations.class, "record_3.0/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.summary.Employments.class, "record_3.0/activities-");
        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Distinction.class, "record_3.0/distinction-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.InvitedPosition.class, "record_3.0/invited-position-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Membership.class, "record_3.0/membership-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Qualification.class, "record_3.0/qualification-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.Service.class, "record_3.0/service-");
        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.summary.Distinctions.class, "record_3.0/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.summary.InvitedPositions.class, "record_3.0/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.summary.Memberships.class, "record_3.0/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.summary.Qualifications.class, "record_3.0/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.summary.Services.class, "record_3.0/activities-");
        
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.summary.ResearchResources.class, "record_3.0/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.ResearchResource.class, "record_3.0/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.ResearchResourceItem.class, "record_3.0/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.ResearchResourceProposal.class, "record_3.0/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.ResearchResourceHosts.class, "record_3.0/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.put(org.orcid.jaxb.model.v3.release.record.ResearchResourceTitle.class, "record_3.0/research-resource-");

    }
    
    private JAXBContext jaxbContext_2_0_rc1;
    private JAXBContext jaxbContext_2_0_rc2;
    private JAXBContext jaxbContext_2_0_rc3;
    private JAXBContext jaxbContext_2_0_rc4;
    private JAXBContext jaxbContext_2_0;
    private JAXBContext jaxbContext_2_1;
    private JAXBContext jaxbContext_3_0_rc1;
    private JAXBContext jaxbContext_3_0_rc2;
    private JAXBContext jaxbContext_3_0;
    private Map<String, Schema> schemaByPath = new ConcurrentHashMap<>();
    
    @Resource
    LocaleManager localeManager;

    @Override
    public Unmarshaller getContext(Class<?> type) {
        try {
            String apiVersion = ApiUtils.getApiVersion();
            String schemaFilenamePrefix = getSchemaFilenamePrefix(type, apiVersion);
            Unmarshaller unmarshaller = getJAXBContext(apiVersion).createUnmarshaller();
            // Old OrcidMessage APIs - do not validate here as we will
            // break "broke" integrations
            // Lets not validate WorkBulk here, we will delegate that to 
            // the controller
            if (OrcidMessage.class.equals(type) 
                    || org.orcid.jaxb.model.record_v2.WorkBulk.class.equals(type)
                    || org.orcid.jaxb.model.v3.release.record.WorkBulk.class.equals(type)) {
                return unmarshaller;
            }
            if (schemaFilenamePrefix != null) {
                Schema schema = getSchema(schemaFilenamePrefix, apiVersion);
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

    public void validate(Object toValidate) {
        String apiVersion = ApiUtils.getApiVersion();
        validate(toValidate, apiVersion);
    }
    
    private void validate(Object toValidate, String apiVersion) {
        String schemaFilenamePrefix = getSchemaFilenamePrefix(toValidate.getClass(), apiVersion);
        try {
            Schema schema = getSchema(schemaFilenamePrefix, apiVersion);
            JAXBContext context = JAXBContext.newInstance(toValidate.getClass());
            Source source = new JAXBSource(context, toValidate);        
            Validator validator = schema.newValidator();
            validator.validate(source);
        } catch (SAXException | JAXBException | IOException e) {
            // Check if it is an IllegalEnumValueException
            if(SAXParseException.class.isAssignableFrom(e.getClass())) {
                Throwable t = e.getCause();
                if(t != null && MarshalException.class.isAssignableFrom(t.getClass())) {
                    MarshalException me = (MarshalException) t;
                    Throwable linkedException = me.getLinkedException();
                    if(linkedException != null && SAXException2.class.isAssignableFrom(linkedException.getClass())) {
                        SAXException2 sa2 = (SAXException2) linkedException;
                        Exception sa2e = sa2.getException();
                        if(sa2e != null && AccessorException.class.isAssignableFrom(sa2e.getClass())) {
                            Throwable cause = sa2e.getCause();
                            if (cause != null && IllegalEnumValueException.class.isAssignableFrom(cause.getClass())) {
                                // Validation exceptions should return
                                // BAD_REQUEST status
                                // Lets throw the IllegalEnumValueException so
                                // the end user gets a detailed error message
                                // and not the default one from spring
                                throw new WebApplicationException(cause, Status.BAD_REQUEST.getStatusCode());
                            }
                        }
                    }
                }
            } 
            
            //Validation exceptions should return BAD_REQUEST status
            throw new WebApplicationException(e, Status.BAD_REQUEST.getStatusCode());                       
        }       
    }
    
    /**
     * Validation for v2.0 objects, where API version is not taken from request attributes.
     * @param toValidate
     */
    public void validateV2(Object toValidate) {
        validate(toValidate, "2.0");
    }
    
    private JAXBContext getJAXBContext(String apiVersion) {
        try {
            if(apiVersion != null) {
            	if(apiVersion.equals("2.0")) {
                    if(jaxbContext_2_0 == null) {
                        jaxbContext_2_0 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.size()]));
                    }
                    return jaxbContext_2_0;
                } else if(apiVersion.equals("2.1")) {
                    if(jaxbContext_2_1 == null) {
                        jaxbContext_2_1 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.size()]));
                    }
                    return jaxbContext_2_1;
                }  else if(apiVersion.equals("3.0")) {
                    if(jaxbContext_3_0 == null) {
                        jaxbContext_3_0 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.size()]));
                    }
                    return jaxbContext_3_0;
                }
            }
            //Return v3 as the last resource
            if(jaxbContext_3_0 == null) {
                jaxbContext_3_0 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.size()]));
            }
            return jaxbContext_3_0;           
        } catch (JAXBException e) {
            throw new RuntimeException(localeManager.resolveMessage("apiError.jaxb_context.exception"), e);
        }
    }

    private Schema getSchema(String schemaFilenamePrefix, String apiVersion) throws SAXException {
        apiVersion = (apiVersion == null ? "" : apiVersion);
        String schemaPath = "/" + schemaFilenamePrefix + apiVersion + ".xsd";
        Schema schema = schemaByPath.get(schemaPath);
        if (schema != null) {
            return schema;
        }
        Source source = new StreamSource(OrcidValidationJaxbContextResolver.class.getResourceAsStream(schemaPath));
        schema = createSchemaFactory().newSchema(source);
        schemaByPath.put(schemaPath, schema);
        return schema;
    }

    private String getSchemaFilenamePrefix(Class<?> type, String apiVersion) {        
        if(apiVersion != null) {
            if(apiVersion.equals("2.0")) {
                return SCHEMA_FILENAME_PREFIX_BY_CLASS_V2.get(type);
            }   
            if(apiVersion.equals("2.1")) {
                return SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.get(type);
            }
            if(apiVersion.equals("3.0")) {
                return SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.get(type);
            }
        }               
        return SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0.get(type);
    }

    private SchemaFactory createSchemaFactory() {
        SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        schemaFactory.setResourceResolver(new OrcidResourceResolver(schemaFactory.getResourceResolver()));
        return schemaFactory;
    }

    private Response getResponse(Throwable e) {
        OrcidMessage entity = new OrcidMessage();
        entity.setErrorDesc(new ErrorDesc(e.getMessage()));
        return Response.serverError().entity(entity).build();
    }

    public class OrcidValidationHandler implements ValidationEventHandler {
        @Override
        public boolean handleEvent(ValidationEvent event) {
            if(event.getLinkedException() != null && AccessorException.class.isAssignableFrom(event.getLinkedException().getClass()) && event.getLinkedException().getCause() != null && IllegalEnumValueException.class.isAssignableFrom(event.getLinkedException().getCause().getClass()))  {
                throw (IllegalEnumValueException) event.getLinkedException().getCause();
            } else if (event.getSeverity() == ValidationEvent.FATAL_ERROR || event.getSeverity() == ValidationEvent.ERROR) {                
                throw new OrcidBadRequestException(event.getMessage());
            } else if (event.getSeverity() == ValidationEvent.WARNING) {
                logger.warn(event.getMessage());
            }
            return true;
        }
    }
}
