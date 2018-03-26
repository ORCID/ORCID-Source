package org.orcid.core.version.impl;

import javax.annotation.Resource;

import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionObjectFactory;
import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecords;
import org.orcid.jaxb.model.notification.permission_rc2.NotificationPermission;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.Educations;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.Employments;
import org.orcid.jaxb.model.record.summary_rc2.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.Fundings;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc2.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc2.Works;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.Record;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.jaxb.model.record_rc2.Work;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class VersionConverterImplV2_0_rc2ToV2_0_rc3 implements V2VersionConverter {

    private static final String LOWER_VERSION = "2.0_rc2";
    private static final String UPPER_VERSION = "2.0_rc3";

    private final static MapperFacade mapper;

    static {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        
        //GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, org.orcid.jaxb.model.groupid_rc3.GroupIdRecords.class).byDefault().register();
        mapperFactory.classMap(GroupIdRecord.class, org.orcid.jaxb.model.groupid_rc3.GroupIdRecord.class).byDefault().register();
        
        //ExternalIDs
        mapperFactory.classMap(ExternalIDs.class, org.orcid.jaxb.model.record_rc3.ExternalIDs.class).byDefault().register();
        mapperFactory.classMap(ExternalID.class, org.orcid.jaxb.model.record_rc3.ExternalID.class).byDefault().register();
        
        //Other names
        mapperFactory.classMap(OtherNames.class, org.orcid.jaxb.model.record_rc3.OtherNames.class).byDefault().register();
        mapperFactory.classMap(OtherName.class, org.orcid.jaxb.model.record_rc3.OtherName.class).byDefault().register();
                
        //Keywords
        mapperFactory.classMap(Keywords.class, org.orcid.jaxb.model.record_rc3.Keywords.class).byDefault().register();
        mapperFactory.classMap(Keyword.class, org.orcid.jaxb.model.record_rc3.Keyword.class).byDefault().register();
        
        //Address
        mapperFactory.classMap(Addresses.class, org.orcid.jaxb.model.record_rc3.Addresses.class).byDefault().register();
        mapperFactory.classMap(Address.class, org.orcid.jaxb.model.record_rc3.Address.class).byDefault().register();
        
        //ResearcherUrl
        mapperFactory.classMap(ResearcherUrls.class, org.orcid.jaxb.model.record_rc3.ResearcherUrls.class).byDefault().register();
        mapperFactory.classMap(ResearcherUrl.class, org.orcid.jaxb.model.record_rc3.ResearcherUrl.class).byDefault().register();
        
        //Person External ID
        mapperFactory.classMap(PersonExternalIdentifiers.class, org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers.class).byDefault().register();
        mapperFactory.classMap(PersonExternalIdentifier.class, org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier.class).byDefault().register();
        
        //Emails
        mapperFactory.classMap(Emails.class, org.orcid.jaxb.model.record_rc3.Emails.class).byDefault().register();
        mapperFactory.classMap(Email.class, org.orcid.jaxb.model.record_rc3.Email.class).byDefault().register();
        
        // WORK         
        mapperFactory.classMap(WorkGroup.class, org.orcid.jaxb.model.record.summary_rc3.WorkGroup.class).byDefault().register();
        mapperFactory.classMap(Works.class, org.orcid.jaxb.model.record_rc3.Works.class).byDefault().register();
        mapperFactory.classMap(Work.class, org.orcid.jaxb.model.record_rc3.Work.class).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, org.orcid.jaxb.model.record.summary_rc3.WorkSummary.class).byDefault().register();
        
        //FUNDING        
        mapperFactory.classMap(FundingGroup.class, org.orcid.jaxb.model.record.summary_rc3.FundingGroup.class).byDefault().register();
        mapperFactory.classMap(Fundings.class, org.orcid.jaxb.model.record.summary_rc3.Fundings.class).byDefault().register();
        mapperFactory.classMap(Funding.class, org.orcid.jaxb.model.record_rc3.Funding.class).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, org.orcid.jaxb.model.record.summary_rc3.FundingSummary.class).byDefault().register();
        
        //EDUCATION
        mapperFactory.classMap(org.orcid.jaxb.model.record.summary_rc2.Educations.class, org.orcid.jaxb.model.record.summary_rc3.Educations.class).byDefault().register();
        mapperFactory.classMap(Education.class, org.orcid.jaxb.model.record_rc3.Education.class).byDefault().register();
        mapperFactory.classMap(Educations.class, org.orcid.jaxb.model.record_rc3.Educations.class).byDefault().register();
        mapperFactory.classMap(EducationSummary.class, org.orcid.jaxb.model.record.summary_rc3.EducationSummary.class).byDefault().register();
        
        //EMPLOYMENT
        mapperFactory.classMap(org.orcid.jaxb.model.record.summary_rc2.Employments.class, org.orcid.jaxb.model.record.summary_rc3.Employments.class).byDefault().register();
        mapperFactory.classMap(Employment.class, org.orcid.jaxb.model.record_rc3.Employment.class).byDefault().register();
        mapperFactory.classMap(Employments.class, org.orcid.jaxb.model.record_rc3.Employments.class).byDefault().register();
        mapperFactory.classMap(EmploymentSummary.class, org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary.class).byDefault().register();
        
        //PEER REVIEW
        mapperFactory.classMap(PeerReviews.class, org.orcid.jaxb.model.record.summary_rc3.PeerReviews.class).byDefault().register();
        mapperFactory.classMap(PeerReview.class, org.orcid.jaxb.model.record_rc3.PeerReview.class).byDefault().register();        
        mapperFactory.classMap(PeerReviewSummary.class, org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary.class).byDefault().register();                
        
        //NOTIFICATIONS
        mapperFactory.classMap(NotificationPermission.class, org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission.class).byDefault().register();
        
        //Person
        mapperFactory.classMap(Person.class, org.orcid.jaxb.model.record_rc3.Person.class).byDefault().register();
        
        //Record
        mapperFactory.classMap(Record.class, org.orcid.jaxb.model.record_rc3.Record.class).byDefault()
        .customize(new CustomMapper<Record, org.orcid.jaxb.model.record_rc3.Record>(){
            @Override
            public void mapBtoA(org.orcid.jaxb.model.record_rc3.Record recordRc3, Record record,
                    MappingContext context) {
                if(recordRc3 != null && recordRc3.getHistory() != null && recordRc3.getHistory().getLastModifiedDate() != null) {
                    org.orcid.jaxb.model.common_rc3.LastModifiedDate rc3LastModified = recordRc3.getHistory().getLastModifiedDate();
                    record.setLastModifiedDate(new LastModifiedDate(rc3LastModified.getValue()));                               
                }                
            }
            
        }).register();
        
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
