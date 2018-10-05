package org.orcid.api.common.jaxb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
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
import org.orcid.core.api.OrcidApiConstants;
import org.orcid.core.exception.OrcidBadRequestException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.web.filters.ApiVersionFilter;
import org.orcid.jaxb.model.message.ErrorDesc;
import org.orcid.jaxb.model.message.OrcidMessage;
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
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(org.orcid.jaxb.model.groupid_rc1.GroupIdRecord.class, "group-id-2.0_rc1/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.put(org.orcid.jaxb.model.notification.permission_rc1.NotificationPermission.class, "notification_2.0_rc1/notification-permission-");
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
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.notification.permission_rc2.NotificationPermission.class, "notification_2.0_rc2/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Address.class, "record_2.0_rc2/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Addresses.class, "record_2.0_rc2/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Education.class, "record_2.0_rc2/education-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Email.class, "record_2.0_rc2/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.Employment.class, "record_2.0_rc2/employment-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier.class, "record_2.0_rc2/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.put(org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers.class, "record_2.0_rc2/person-external-identifier-");
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
    
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.groupid_rc3.GroupIdRecord.class, "group-id-2.0_rc3/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission.class, "notification_2.0_rc3/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Address.class, "record_2.0_rc3/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Addresses.class, "record_2.0_rc3/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Education.class, "record_2.0_rc3/education-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Email.class, "record_2.0_rc3/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Employment.class, "record_2.0_rc3/employment-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier.class, "record_2.0_rc3/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers.class, "record_2.0_rc3/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Funding.class, "record_2.0_rc3/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Keyword.class, "record_2.0_rc3/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Keywords.class, "record_2.0_rc3/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Name.class, "record_2.0_rc3/personal-details-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.OtherName.class, "record_2.0_rc3/other-name-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.OtherNames.class, "record_2.0_rc3/other-name-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.PeerReview.class, "record_2.0_rc3/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.ResearcherUrl.class, "record_2.0_rc3/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.ResearcherUrls.class, "record_2.0_rc3/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.Work.class, "record_2.0_rc3/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record_rc3.WorkBulk.class, "record_2.0_rc3/bulk-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record.summary_rc3.Educations.class, "record_2.0_rc3/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.put(org.orcid.jaxb.model.record.summary_rc3.Employments.class, "record_2.0_rc3/activities-");
    }
    
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.groupid_rc4.GroupIdRecord.class, "group-id-2.0_rc4/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission.class, "notification_2.0_rc4/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Address.class, "record_2.0_rc4/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Addresses.class, "record_2.0_rc4/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Education.class, "record_2.0_rc4/education-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Email.class, "record_2.0_rc4/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Employment.class, "record_2.0_rc4/employment-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier.class, "record_2.0_rc4/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers.class, "record_2.0_rc4/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Funding.class, "record_2.0_rc4/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Keyword.class, "record_2.0_rc4/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Keywords.class, "record_2.0_rc4/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Name.class, "record_2.0_rc4/personal-details-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.OtherName.class, "record_2.0_rc4/other-name-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.OtherNames.class, "record_2.0_rc4/other-name-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.PeerReview.class, "record_2.0_rc4/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.ResearcherUrl.class, "record_2.0_rc4/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.ResearcherUrls.class, "record_2.0_rc4/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.Work.class, "record_2.0_rc4/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record_rc4.WorkBulk.class, "record_2.0_rc4/bulk-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record.summary_rc4.Educations.class, "record_2.0_rc4/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.put(org.orcid.jaxb.model.record.summary_rc4.Employments.class, "record_2.0_rc4/activities-");
    }
    
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
    
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.groupid.GroupIdRecord.class, "group-id-3.0_rc1/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.notification.permission.NotificationPermission.class, "notification_3.0_rc1/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Address.class, "record_3.0_rc1/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Addresses.class, "record_3.0_rc1/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Education.class, "record_3.0_rc1/education-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Email.class, "record_3.0_rc1/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Employment.class, "record_3.0_rc1/employment-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier.class, "record_3.0_rc1/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers.class, "record_3.0_rc1/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Funding.class, "record_3.0_rc1/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Keyword.class, "record_3.0_rc1/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Keywords.class, "record_3.0_rc1/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Name.class, "record_3.0_rc1/personal-details-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.OtherName.class, "record_3.0_rc1/other-name-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.OtherNames.class, "record_3.0_rc1/other-name-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.PeerReview.class, "record_3.0_rc1/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl.class, "record_3.0_rc1/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls.class, "record_3.0_rc1/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Work.class, "record_3.0_rc1/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.WorkBulk.class, "record_3.0_rc1/bulk-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.summary.Educations.class, "record_3.0_rc1/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.summary.Employments.class, "record_3.0_rc1/activities-");
        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Distinction.class, "record_3.0_rc1/distinction-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.InvitedPosition.class, "record_3.0_rc1/invited-position-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Membership.class, "record_3.0_rc1/membership-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Qualification.class, "record_3.0_rc1/qualification-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.Service.class, "record_3.0_rc1/service-");
        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.summary.Distinctions.class, "record_3.0_rc1/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositions.class, "record_3.0_rc1/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.summary.Memberships.class, "record_3.0_rc1/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.summary.Qualifications.class, "record_3.0_rc1/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.summary.Services.class, "record_3.0_rc1/activities-");
        
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResources.class, "record_3.0_rc1/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.ResearchResource.class, "record_3.0_rc1/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceItem.class, "record_3.0_rc1/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceProposal.class, "record_3.0_rc1/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceHosts.class, "record_3.0_rc1/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.put(org.orcid.jaxb.model.v3.rc1.record.ResearchResourceTitle.class, "record_3.0_rc1/research-resource-");

    }
    
    private static final Map<Class<?>, String> SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2 = new HashMap<>();
    static {
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.groupid.GroupIdRecord.class, "group-id-3.0_rc2/group-id-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.notification.permission.NotificationPermission.class, "notification_3.0_rc2/notification-permission-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(OrcidMessage.class, "orcid-message-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Address.class, "record_3.0_rc2/address-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Addresses.class, "record_3.0_rc2/address-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Education.class, "record_3.0_rc2/education-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Email.class, "record_3.0_rc2/email-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Employment.class, "record_3.0_rc2/employment-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier.class, "record_3.0_rc2/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifiers.class, "record_3.0_rc2/person-external-identifier-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Funding.class, "record_3.0_rc2/funding-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Keyword.class, "record_3.0_rc2/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Keywords.class, "record_3.0_rc2/keyword-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Name.class, "record_3.0_rc2/personal-details-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.OtherName.class, "record_3.0_rc2/other-name-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.OtherNames.class, "record_3.0_rc2/other-name-");        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.PeerReview.class, "record_3.0_rc2/peer-review-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl.class, "record_3.0_rc2/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.ResearcherUrls.class, "record_3.0_rc2/researcher-url-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Work.class, "record_3.0_rc2/work-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.WorkBulk.class, "record_3.0_rc2/bulk-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.summary.Educations.class, "record_3.0_rc2/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.summary.Employments.class, "record_3.0_rc2/activities-");
        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Distinction.class, "record_3.0_rc2/distinction-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.InvitedPosition.class, "record_3.0_rc2/invited-position-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Membership.class, "record_3.0_rc2/membership-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Qualification.class, "record_3.0_rc2/qualification-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.Service.class, "record_3.0_rc2/service-");
        
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.summary.Distinctions.class, "record_3.0_rc2/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.summary.InvitedPositions.class, "record_3.0_rc2/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.summary.Memberships.class, "record_3.0_rc2/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.summary.Qualifications.class, "record_3.0_rc2/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.summary.Services.class, "record_3.0_rc2/activities-");
        
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources.class, "record_3.0_rc2/activities-");
        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.ResearchResource.class, "record_3.0_rc2/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.ResearchResourceItem.class, "record_3.0_rc2/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.ResearchResourceProposal.class, "record_3.0_rc2/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.ResearchResourceHosts.class, "record_3.0_rc2/research-resource-");
//        SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.put(org.orcid.jaxb.model.v3.rc2.record.ResearchResourceTitle.class, "record_3.0_rc2/research-resource-");

    }
    
    private JAXBContext jaxbContext_2_0_rc1;
    private JAXBContext jaxbContext_2_0_rc2;
    private JAXBContext jaxbContext_2_0_rc3;
    private JAXBContext jaxbContext_2_0_rc4;
    private JAXBContext jaxbContext_2_0;
    private JAXBContext jaxbContext_2_1;
    private JAXBContext jaxbContext_3_0_rc1;
    private JAXBContext jaxbContext_3_0_rc2;
    private Map<String, Schema> schemaByPath = new ConcurrentHashMap<>();
    
    @Resource
    LocaleManager localeManager;

    @Override
    public Unmarshaller getContext(Class<?> type) {
        try {
            String apiVersion = getApiVersion();
            String schemaFilenamePrefix = getSchemaFilenamePrefix(type, apiVersion);
            Unmarshaller unmarshaller = getJAXBContext(apiVersion).createUnmarshaller();
            // Old OrcidMessage APIs - do not validate here as we will
            // break "broke" integrations
            // Lets not validate WorkBulk here, we will delegate that to 
            // the controller
            if (OrcidMessage.class.equals(type) 
                    || org.orcid.jaxb.model.record_rc3.WorkBulk.class.equals(type) 
                    || org.orcid.jaxb.model.record_rc4.WorkBulk.class.equals(type)
                    || org.orcid.jaxb.model.record_v2.WorkBulk.class.equals(type)
                    || org.orcid.jaxb.model.v3.rc1.record.WorkBulk.class.equals(type)
                    || org.orcid.jaxb.model.v3.rc2.record.WorkBulk.class.equals(type)) {
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
        String apiVersion = getApiVersion();
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
                } else if(apiVersion.equals("2.0_rc4")) {
                    if(jaxbContext_2_0_rc4 == null) {
                        jaxbContext_2_0_rc4 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.size()]));
                    }
                    return jaxbContext_2_0_rc4;
                } else if(apiVersion.equals("2.0_rc3")) {
                    if(jaxbContext_2_0_rc3 == null) {
                        jaxbContext_2_0_rc3 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.size()]));
                    }
                    return jaxbContext_2_0_rc3;
                } else if(apiVersion.equals("2.0_rc2")) {
                    if(jaxbContext_2_0_rc2 == null) {
                        jaxbContext_2_0_rc2 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.size()]));
                    }
                    return jaxbContext_2_0_rc2;
                } else if(apiVersion.equals("2.1")) {
                    if(jaxbContext_2_1 == null) {
                        jaxbContext_2_1 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.size()]));
                    }
                    return jaxbContext_2_1;
                } else if(apiVersion.equals("3.0_rc1")) {
                    if(jaxbContext_3_0_rc1 == null) {
                        jaxbContext_3_0_rc1 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.size()]));
                    }
                    return jaxbContext_3_0_rc1;
                } else if(apiVersion.equals("3.0_rc2")) {
                    if(jaxbContext_3_0_rc2 == null) {
                        jaxbContext_3_0_rc2 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC2.size()]));
                    }
                    return jaxbContext_3_0_rc2;
                }
            }
            //Return rc1 as the last resource
            if(jaxbContext_2_0_rc1 == null) {
                jaxbContext_2_0_rc1 = JAXBContext.newInstance(SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.keySet().toArray(new Class[SCHEMA_FILENAME_PREFIX_BY_CLASS_RC1.size()]));
            }
            return jaxbContext_2_0_rc1;            
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
            if(apiVersion.equals("2.0_rc4")) {
                return SCHEMA_FILENAME_PREFIX_BY_CLASS_RC4.get(type);
            }
            if(apiVersion.equals("2.0_rc3")) {
                return SCHEMA_FILENAME_PREFIX_BY_CLASS_RC3.get(type);
            }
            if(apiVersion.equals("2.0_rc2")) {
                return SCHEMA_FILENAME_PREFIX_BY_CLASS_RC2.get(type);
            }   
            if(apiVersion.equals("2.1")) {
                return SCHEMA_FILENAME_PREFIX_BY_CLASS_V2_1.get(type);
            }
            if(apiVersion.equals("3.0_rc1")) {
                return SCHEMA_FILENAME_PREFIX_BY_CLASS_V3_0_RC1.get(type);
            }
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
