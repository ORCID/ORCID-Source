package org.orcid.core.version.impl;

import javax.annotation.Resource;

import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionObjectFactory;
import org.orcid.jaxb.model.error_rc4.OrcidError;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecords;
import org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.Educations;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc4.Employments;
import org.orcid.jaxb.model.record.summary_rc4.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.Fundings;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc4.Works;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Addresses;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Email;
import org.orcid.jaxb.model.record_rc4.Emails;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.jaxb.model.record_rc4.ExternalID;
import org.orcid.jaxb.model.record_rc4.ExternalIDs;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.Keywords;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.OtherNames;
import org.orcid.jaxb.model.record_rc4.PeerReview;
import org.orcid.jaxb.model.record_rc4.Person;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc4.Record;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.ResearcherUrls;
import org.orcid.jaxb.model.record_rc4.Work;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class VersionConverterImplV2_0_rc4ToV2_0 implements V2VersionConverter {

    private static final String LOWER_VERSION = "2.0_rc4";
    private static final String UPPER_VERSION = "2.0";

    private final static MapperFacade mapper;

    static {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        
        //GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, org.orcid.jaxb.model.groupid_v2.GroupIdRecords.class).byDefault().register();
        mapperFactory.classMap(GroupIdRecord.class, org.orcid.jaxb.model.groupid_v2.GroupIdRecord.class).byDefault().register();
        
        //ExternalIDs
        mapperFactory.classMap(ExternalIDs.class, org.orcid.jaxb.model.record_v2.ExternalIDs.class).byDefault().register();
        mapperFactory.classMap(ExternalID.class, org.orcid.jaxb.model.record_v2.ExternalID.class).byDefault().register();
        
        //Other names
        mapperFactory.classMap(OtherNames.class, org.orcid.jaxb.model.record_v2.OtherNames.class).byDefault().register();
        mapperFactory.classMap(OtherName.class, org.orcid.jaxb.model.record_v2.OtherName.class).byDefault().register();
                
        //Keywords
        mapperFactory.classMap(Keywords.class, org.orcid.jaxb.model.record_v2.Keywords.class).byDefault().register();
        mapperFactory.classMap(Keyword.class, org.orcid.jaxb.model.record_v2.Keyword.class).byDefault().register();
        
        //Address
        mapperFactory.classMap(Addresses.class, org.orcid.jaxb.model.record_v2.Addresses.class).byDefault().register();
        mapperFactory.classMap(Address.class, org.orcid.jaxb.model.record_v2.Address.class).byDefault().register();
        
        //ResearcherUrl
        mapperFactory.classMap(ResearcherUrls.class, org.orcid.jaxb.model.record_v2.ResearcherUrls.class).byDefault().register();
        mapperFactory.classMap(ResearcherUrl.class, org.orcid.jaxb.model.record_v2.ResearcherUrl.class).byDefault().register();
        
        //Person External ID
        mapperFactory.classMap(PersonExternalIdentifiers.class, org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers.class).byDefault().register();
        mapperFactory.classMap(PersonExternalIdentifier.class, org.orcid.jaxb.model.record_v2.PersonExternalIdentifier.class).byDefault().register();
        
        //Emails
        mapperFactory.classMap(Emails.class, org.orcid.jaxb.model.record_v2.Emails.class).byDefault().register();
        mapperFactory.classMap(Email.class, org.orcid.jaxb.model.record_v2.Email.class).byDefault().register();
        
        // WORK         
        mapperFactory.classMap(WorkGroup.class, org.orcid.jaxb.model.record.summary_v2.WorkGroup.class).byDefault().register();
        mapperFactory.classMap(Works.class, org.orcid.jaxb.model.record_v2.Works.class).byDefault().register();
        mapperFactory.classMap(Work.class, org.orcid.jaxb.model.record_v2.Work.class).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, org.orcid.jaxb.model.record.summary_v2.WorkSummary.class).byDefault().register();
        
        //FUNDING        
        mapperFactory.classMap(FundingGroup.class, org.orcid.jaxb.model.record.summary_v2.FundingGroup.class).byDefault().register();
        mapperFactory.classMap(Fundings.class, org.orcid.jaxb.model.record.summary_v2.Fundings.class).byDefault().register();
        mapperFactory.classMap(Funding.class, org.orcid.jaxb.model.record_v2.Funding.class).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, org.orcid.jaxb.model.record.summary_v2.FundingSummary.class).byDefault().register();
        
        //EDUCATION
        mapperFactory.classMap(org.orcid.jaxb.model.record.summary_rc4.Educations.class, org.orcid.jaxb.model.record.summary_v2.Educations.class).byDefault().register();
        mapperFactory.classMap(Education.class, org.orcid.jaxb.model.record_v2.Education.class).byDefault().register();
        mapperFactory.classMap(Educations.class, org.orcid.jaxb.model.record_v2.Educations.class).byDefault().register();
        mapperFactory.classMap(EducationSummary.class, org.orcid.jaxb.model.record.summary_v2.EducationSummary.class).byDefault().register();
        
        //EMPLOYMENT
        mapperFactory.classMap(org.orcid.jaxb.model.record.summary_rc4.Employments.class, org.orcid.jaxb.model.record.summary_v2.Employments.class).byDefault().register();
        mapperFactory.classMap(Employment.class, org.orcid.jaxb.model.record_v2.Employment.class).byDefault().register();
        mapperFactory.classMap(Employments.class, org.orcid.jaxb.model.record_v2.Employments.class).byDefault().register();
        mapperFactory.classMap(EmploymentSummary.class, org.orcid.jaxb.model.record.summary_v2.EmploymentSummary.class).byDefault().register();
        
        //PEER REVIEW
        mapperFactory.classMap(PeerReviews.class, org.orcid.jaxb.model.record.summary_v2.PeerReviews.class).byDefault().register();
        mapperFactory.classMap(PeerReview.class, org.orcid.jaxb.model.record_v2.PeerReview.class).byDefault().register();        
        mapperFactory.classMap(PeerReviewSummary.class, org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary.class).byDefault().register();                
        
        //NOTIFICATIONS
        mapperFactory.classMap(NotificationPermission.class, org.orcid.jaxb.model.notification.permission_v2.NotificationPermission.class).byDefault().register();
        
        //Person
        mapperFactory.classMap(Person.class, org.orcid.jaxb.model.record_v2.Person.class).byDefault().register();
        
        //Record
        mapperFactory.classMap(Record.class, org.orcid.jaxb.model.record_v2.Record.class).byDefault().register();
        
        // error
        mapperFactory.classMap(OrcidError.class, org.orcid.jaxb.model.error_v2.OrcidError.class).byDefault().register();
        
        mapper = mapperFactory.getMapperFacade();
    }

    @Resource
    private V2VersionObjectFactory v2VersionObjectFactory;

    @Override
    public String getLowerVersion() {
        return LOWER_VERSION;
    }

    @Override
    public String getUpperVersion() {
        return UPPER_VERSION;
    }

    @Override
    public V2Convertible downgrade(V2Convertible objectToDowngrade) {
        Object objectToConvert = objectToDowngrade.getObjectToConvert();
        Object targetObject = v2VersionObjectFactory.createEquivalentInstance(objectToConvert, LOWER_VERSION);
        mapper.map(objectToConvert, targetObject);
        return new V2Convertible(targetObject, LOWER_VERSION);
    }

    @Override
    public V2Convertible upgrade(V2Convertible objectToUpgrade) {
        Object objectToConvert = objectToUpgrade.getObjectToConvert();
        Object targetObject = v2VersionObjectFactory.createEquivalentInstance(objectToConvert, UPPER_VERSION);
        mapper.map(objectToUpgrade.getObjectToConvert(), targetObject);
        return new V2Convertible(targetObject, UPPER_VERSION);
    }       
}
