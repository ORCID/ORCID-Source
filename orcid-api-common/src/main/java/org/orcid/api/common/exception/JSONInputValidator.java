package org.orcid.api.common.exception;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.util.JAXBSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.InvalidJSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class JSONInputValidator {

    private static final Map<Class<?>, String> SCHEMA_LOCATIONS;
    private static final Map<Class<?>, Validator> VALIDATORS;
    private static final Map<Class<?>, JAXBContext> CONTEXTS;
    
    private static final Map<Class<?>, String> SCHEMA_LOCATIONS_2_1_API;
    private static final Map<Class<?>, Validator> VALIDATORS_2_1_API;
    private static final Map<Class<?>, JAXBContext> CONTEXTS_2_1_API;
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONInputValidator.class);
    
    static {
        SCHEMA_LOCATIONS = new HashMap<>();
       
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Work.class, "/record_3.0/work-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Funding.class, "/record_3.0/funding-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Education.class, "/record_3.0/education-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Employment.class, "/record_3.0/employment-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.PeerReview.class, "/record_3.0/peer-review-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.groupid.GroupIdRecord.class, "/group-id-3.0/group-id-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.notification.permission.NotificationPermission.class,
                "/notification_3.0/notification-permission-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.ResearcherUrl.class, "/record_3.0/researcher-url-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.OtherName.class, "/record_3.0/other-name-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.PersonExternalIdentifier.class, "/record_3.0/person-external-identifier-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Keyword.class, "/record_3.0/keyword-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Address.class, "/record_3.0/address-3.0.xsd");
        
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Distinction.class, "/record_3.0/distinction-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.InvitedPosition.class, "/record_3.0/invited-position-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Membership.class, "/record_3.0/membership-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Qualification.class, "/record_3.0/qualification-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.Service.class, "/record_3.0/service-3.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.release.record.ResearchResource.class, "/record_3.0/research-resource-3.0.xsd");
        
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.Work.class, "/record_2.0/work-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.Funding.class, "/record_2.0/funding-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.Education.class, "/record_2.0/education-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.Employment.class, "/record_2.0/employment-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.PeerReview.class, "/record_2.0/peer-review-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.groupid_v2.GroupIdRecord.class, "/group-id-2.0/group-id-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.notification.permission_v2.NotificationPermission.class, "/notification_2.0/notification-permission-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.ResearcherUrl.class, "/record_2.0/researcher-url-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.OtherName.class, "/record_2.0/other-name-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier.class, "/record_2.0/person-external-identifier-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.Keyword.class, "/record_2.0/keyword-2.0.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_v2.Address.class, "/record_2.0/address-2.0.xsd");
      
        VALIDATORS = new HashMap<Class<?>, Validator>();
        CONTEXTS = new HashMap<Class<?>, JAXBContext>();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        for (Class<?> c : SCHEMA_LOCATIONS.keySet()){
            try {
                URL u = JSONInputValidator.class.getResource(SCHEMA_LOCATIONS.get(c));
                Schema schema = sf.newSchema(u);
                Validator validator = schema.newValidator();            
                VALIDATORS.put(c, validator);
                CONTEXTS.put(c, JAXBContext.newInstance(c));
            } catch (JAXBException e) {
                throw new ApplicationException(e);
            } catch (SAXException e) {
                throw new ApplicationException(e);
            } 
        }                

        SCHEMA_LOCATIONS_2_1_API = new HashMap<>();
        
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.Work.class, "/record_2.1/work-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.Funding.class, "/record_2.1/funding-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.Education.class, "/record_2.1/education-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.Employment.class, "/record_2.1/employment-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.PeerReview.class, "/record_2.1/peer-review-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.groupid_v2.GroupIdRecord.class, "/group-id-2.1/group-id-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.notification.permission_v2.NotificationPermission.class, "/notification_2.1/notification-permission-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.ResearcherUrl.class, "/record_2.1/researcher-url-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.OtherName.class, "/record_2.1/other-name-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.PersonExternalIdentifier.class, "/record_2.1/person-external-identifier-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.Keyword.class, "/record_2.1/keyword-2.1.xsd");
        SCHEMA_LOCATIONS_2_1_API.put(org.orcid.jaxb.model.record_v2.Address.class, "/record_2.1/address-2.1.xsd");
        
        VALIDATORS_2_1_API = new HashMap<Class<?>, Validator>();
        CONTEXTS_2_1_API = new HashMap<Class<?>, JAXBContext>();
        
        for(Class<?> c : SCHEMA_LOCATIONS_2_1_API.keySet()) {
            try {
                URL u = JSONInputValidator.class.getResource(SCHEMA_LOCATIONS_2_1_API.get(c));
                Schema schema = sf.newSchema(u);
                Validator validator = schema.newValidator();            
                VALIDATORS_2_1_API.put(c, validator);
                CONTEXTS_2_1_API.put(c, JAXBContext.newInstance(c));
            } catch (JAXBException e) {
                throw new ApplicationException(e);
            } catch (SAXException e) {
                throw new ApplicationException(e);
            }
        }
    }
    
    public void validateJSONInput(Object obj) {
        Class<?> clazz = obj.getClass();
        JAXBSource source = null;

        if (!canValidate(clazz)){
            LOGGER.error("Cannot validate "+clazz.getName());
            return;
        }
        
        try {
            source = new JAXBSource(CONTEXTS.get(clazz), obj);
            Validator validator = VALIDATORS.get(clazz);
            if(validator != null) {
                validator.validate(source);
            } else {
                throw new InvalidJSONException("Unable to find validator for class " + clazz.getName());
            }
        } catch (SAXException e) {
            Map<String, String> params = new HashMap<>();
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if(rootCause != null) {
                throw new InvalidJSONException(rootCause.getMessage(), e);
            } else {
                // For SAXException, the message is usually 2 levels deep
                throw new InvalidJSONException(e.getCause().getCause().getMessage(), e);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to find validator for class " + clazz.getName());
            Map<String, String> params = new HashMap<>();
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if(rootCause != null) {
                throw new InvalidJSONException(rootCause.getMessage(), e);
            } else {
                throw new InvalidJSONException(e.getMessage(), e);
            }
        }
    }
    
    public void validate2_1APIJSONInput(Object obj) {
        Class<?> clazz = obj.getClass();
        JAXBSource source = null;

        if (!SCHEMA_LOCATIONS_2_1_API.containsKey(clazz)){
            LOGGER.error("Cannot validate "+clazz.getName());
            return;
        }
        
        try {
            source = new JAXBSource(CONTEXTS_2_1_API.get(clazz), obj);
            VALIDATORS_2_1_API.get(clazz).validate(source);
        } catch (SAXException e) {
            Map<String, String> params = new HashMap<>();
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if(rootCause != null) {
                throw new InvalidJSONException(rootCause.getMessage(), e);
            } else {
                // For SAXException, the message is usually 2 levels deep
                throw new InvalidJSONException(e.getCause().getCause().getMessage(), e);
            }
        } catch (Exception e) {
            throw new ApplicationException(e);
        } 
    }

    public boolean canValidate(Class<?> clazz){
        return SCHEMA_LOCATIONS.containsKey(clazz);
    }

}
