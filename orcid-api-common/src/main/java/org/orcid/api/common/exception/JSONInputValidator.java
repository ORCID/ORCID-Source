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

import org.orcid.core.exception.ApplicationException;
import org.orcid.core.exception.InvalidJSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class JSONInputValidator {

    private static final Map<Class<?>, String> SCHEMA_LOCATIONS;
    private static final Map<Class<?>, Validator> VALIDATORS;
    private static final Map<Class<?>, JAXBContext> CONTEXTS;
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONInputValidator.class);
    
    static {
        SCHEMA_LOCATIONS = new HashMap<>();

        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Work.class, "/record_3.0_dev1/work-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Funding.class, "/record_3.0_dev1/funding-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Education.class, "/record_3.0_dev1/education-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Employment.class, "/record_3.0_dev1/employment-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.PeerReview.class, "/record_3.0_dev1/peer-review-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.groupid.GroupIdRecord.class, "/group-id-3.0_dev1/group-id-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.notification.permission.NotificationPermission.class,
                "/notification_3.0_dev1/notification-permission-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.ResearcherUrl.class, "/record_3.0_dev1/researcher-url-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.OtherName.class, "/record_3.0_dev1/other-name-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.PersonExternalIdentifier.class, "/record_3.0_dev1/person-external-identifier-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Keyword.class, "/record_3.0_dev1/keyword-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Address.class, "/record_3.0_dev1/address-3.0_dev1.xsd");
        
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Distinction.class, "/record_3.0_dev1/distinction-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.InvitedPosition.class, "/record_3.0_dev1/invited-position-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Membership.class, "/record_3.0_dev1/membership-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Qualification.class, "/record_3.0_dev1/qualification-3.0_dev1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.v3.dev1.record.Service.class, "/record_3.0_dev1/service-3.0_dev1.xsd");
        
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

        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.Work.class, "/record_2.0_rc4/work-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.Funding.class, "/record_2.0_rc4/funding-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.Education.class, "/record_2.0_rc4/education-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.Employment.class, "/record_2.0_rc4/employment-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.PeerReview.class, "/record_2.0_rc4/peer-review-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.groupid_rc4.GroupIdRecord.class, "/group-id-2.0_rc4/group-id-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission.class, "/notification_2.0_rc4/notification-permission-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.ResearcherUrl.class, "/record_2.0_rc4/researcher-url-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.OtherName.class, "/record_2.0_rc4/other-name-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier.class, "/record_2.0_rc4/person-external-identifier-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.Keyword.class, "/record_2.0_rc4/keyword-2.0_rc4.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc4.Address.class, "/record_2.0_rc4/address-2.0_rc4.xsd");

        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.Work.class, "/record_2.0_rc3/work-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.Funding.class, "/record_2.0_rc3/funding-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.Education.class, "/record_2.0_rc3/education-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.Employment.class, "/record_2.0_rc3/employment-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.PeerReview.class, "/record_2.0_rc3/peer-review-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.groupid_rc3.GroupIdRecord.class, "/group-id-2.0_rc3/group-id-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission.class, "/notification_2.0_rc3/notification-permission-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.ResearcherUrl.class, "/record_2.0_rc3/researcher-url-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.OtherName.class, "/record_2.0_rc3/other-name-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier.class, "/record_2.0_rc3/person-external-identifier-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.Keyword.class, "/record_2.0_rc3/keyword-2.0_rc3.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc3.Address.class, "/record_2.0_rc3/address-2.0_rc3.xsd");

        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.Work.class, "/record_2.0_rc2/work-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.Funding.class, "/record_2.0_rc2/funding-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.Education.class, "/record_2.0_rc2/education-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.Employment.class, "/record_2.0_rc2/employment-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.PeerReview.class, "/record_2.0_rc2/peer-review-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.groupid_rc2.GroupIdRecord.class, "/group-id-2.0_rc2/group-id-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.notification.permission_rc2.NotificationPermission.class, "/notification_2.0_rc2/notification-permission-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.ResearcherUrl.class, "/record_2.0_rc2/researcher-url-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.OtherName.class, "/record_2.0_rc2/other-name-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier.class, "/record_2.0_rc2/person-external-identifier-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.Keyword.class, "/record_2.0_rc2/keyword-2.0_rc2.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc2.Address.class, "/record_2.0_rc2/address-2.0_rc2.xsd");

        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc1.Work.class, "/record_2.0_rc1/work-2.0_rc1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc1.Funding.class, "/record_2.0_rc1/funding-2.0_rc1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc1.Education.class, "/record_2.0_rc1/education-2.0_rc1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc1.Employment.class, "/record_2.0_rc1/employment-2.0_rc1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.record_rc1.PeerReview.class, "/record_2.0_rc1/peer-review-2.0_rc1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.groupid_rc1.GroupIdRecord.class, "/group-id-2.0_rc1/group-id-2.0_rc1.xsd");
        SCHEMA_LOCATIONS.put(org.orcid.jaxb.model.notification.permission_rc1.NotificationPermission.class, "/notification_2.0_rc1/notification-permission-2.0_rc1.xsd");
        
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
            VALIDATORS.get(clazz).validate(source);
        } catch (SAXException e) {
            Map<String, String> params = new HashMap<>();
            params.put("error", e.getCause().getCause().getMessage());
            throw new InvalidJSONException(params);
        } catch (Exception e) {
            throw new ApplicationException(e);
        } 
    }

    public boolean canValidate(Class<?> clazz){
        return SCHEMA_LOCATIONS.containsKey(clazz);
    }

}
