package org.orcid.core.version.impl;

import javax.annotation.Resource;

import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionObjectFactory;
import org.orcid.jaxb.model.error_rc3.OrcidError;
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc3.GroupIdRecords;
import org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission;
import org.orcid.jaxb.model.record.summary_rc3.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc3.Educations;
import org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc3.Employments;
import org.orcid.jaxb.model.record.summary_rc3.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc3.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc3.Fundings;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc3.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc3.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc3.Works;
import org.orcid.jaxb.model.record_rc3.Address;
import org.orcid.jaxb.model.record_rc3.Addresses;
import org.orcid.jaxb.model.record_rc3.Education;
import org.orcid.jaxb.model.record_rc3.Email;
import org.orcid.jaxb.model.record_rc3.Emails;
import org.orcid.jaxb.model.record_rc3.Employment;
import org.orcid.jaxb.model.record_rc3.ExternalID;
import org.orcid.jaxb.model.record_rc3.ExternalIDs;
import org.orcid.jaxb.model.record_rc3.Funding;
import org.orcid.jaxb.model.record_rc3.Keyword;
import org.orcid.jaxb.model.record_rc3.Keywords;
import org.orcid.jaxb.model.record_rc3.OtherName;
import org.orcid.jaxb.model.record_rc3.OtherNames;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.jaxb.model.record_rc3.Person;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc3.Record;
import org.orcid.jaxb.model.record_rc3.ResearcherUrl;
import org.orcid.jaxb.model.record_rc3.ResearcherUrls;
import org.orcid.jaxb.model.record_rc3.Work;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class VersionConverterImplV2_0_rc3ToV2_0_rc4 implements V2VersionConverter {

    private static final String LOWER_VERSION = "2.0_rc3";
    private static final String UPPER_VERSION = "2.0_rc4";

    private static final String RC3_PEER_REVIEW_TYPE = "PEER-REVIEW";
    private static final String RC4_PEER_REVIEW_TYPE = "peer-review";

    private final static MapperFacade mapper;

    static {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();
        
        //GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, org.orcid.jaxb.model.groupid_rc4.GroupIdRecords.class).byDefault().register();
        mapperFactory.classMap(GroupIdRecord.class, org.orcid.jaxb.model.groupid_rc4.GroupIdRecord.class).byDefault().register();
        
        //ExternalIDs
        mapperFactory.classMap(ExternalIDs.class, org.orcid.jaxb.model.record_rc4.ExternalIDs.class).byDefault().register();
        mapperFactory.classMap(ExternalID.class, org.orcid.jaxb.model.record_rc4.ExternalID.class).byDefault()
                .customize(new CustomMapper<ExternalID, org.orcid.jaxb.model.record_rc4.ExternalID>() {
                    public void mapAtoB(ExternalID rc3, org.orcid.jaxb.model.record_rc4.ExternalID rc4, MappingContext context) {
                        String extIdType = rc3.getType();
                        if (RC3_PEER_REVIEW_TYPE.equals(extIdType)) {
                            rc4.setType(RC4_PEER_REVIEW_TYPE);
                        }
                    }

                    public void mapBtoA(org.orcid.jaxb.model.record_rc4.ExternalID rc4, ExternalID rc3, MappingContext context) {
                        String extIdType = rc4.getType();
                        if (RC4_PEER_REVIEW_TYPE.equals(extIdType)) {
                            rc3.setType(RC3_PEER_REVIEW_TYPE);
                        }
                    }
                }).register();
        
        //Other names
        mapperFactory.classMap(OtherNames.class, org.orcid.jaxb.model.record_rc4.OtherNames.class).byDefault().register();
        mapperFactory.classMap(OtherName.class, org.orcid.jaxb.model.record_rc4.OtherName.class).byDefault().register();
                
        //Keywords
        mapperFactory.classMap(Keywords.class, org.orcid.jaxb.model.record_rc4.Keywords.class).byDefault().register();
        mapperFactory.classMap(Keyword.class, org.orcid.jaxb.model.record_rc4.Keyword.class).byDefault().register();
        
        //Address
        mapperFactory.classMap(Addresses.class, org.orcid.jaxb.model.record_rc4.Addresses.class).byDefault().register();
        mapperFactory.classMap(Address.class, org.orcid.jaxb.model.record_rc4.Address.class).byDefault().register();
        
        //ResearcherUrl
        mapperFactory.classMap(ResearcherUrls.class, org.orcid.jaxb.model.record_rc4.ResearcherUrls.class).byDefault().register();
        mapperFactory.classMap(ResearcherUrl.class, org.orcid.jaxb.model.record_rc4.ResearcherUrl.class).byDefault().register();
        
        //Person External ID
        mapperFactory.classMap(PersonExternalIdentifiers.class, org.orcid.jaxb.model.record_rc4.PersonExternalIdentifiers.class).byDefault().register();
        mapperFactory.classMap(PersonExternalIdentifier.class, org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier.class).byDefault().register();
        
        //Emails
        mapperFactory.classMap(Emails.class, org.orcid.jaxb.model.record_rc4.Emails.class).byDefault().register();
        mapperFactory.classMap(Email.class, org.orcid.jaxb.model.record_rc4.Email.class).byDefault().register();
        
        // WORK         
        mapperFactory.classMap(WorkGroup.class, org.orcid.jaxb.model.record.summary_rc4.WorkGroup.class).byDefault().register();
        mapperFactory.classMap(Works.class, org.orcid.jaxb.model.record_rc4.Works.class).byDefault().register();
        mapperFactory.classMap(Work.class, org.orcid.jaxb.model.record_rc4.Work.class).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, org.orcid.jaxb.model.record.summary_rc4.WorkSummary.class).byDefault().register();
        
        //FUNDING        
        mapperFactory.classMap(FundingGroup.class, org.orcid.jaxb.model.record.summary_rc4.FundingGroup.class).byDefault().register();
        mapperFactory.classMap(Fundings.class, org.orcid.jaxb.model.record.summary_rc4.Fundings.class).byDefault().register();
        mapperFactory.classMap(Funding.class, org.orcid.jaxb.model.record_rc4.Funding.class).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, org.orcid.jaxb.model.record.summary_rc4.FundingSummary.class).byDefault().register();
        
        //EDUCATION
        mapperFactory.classMap(org.orcid.jaxb.model.record.summary_rc3.Educations.class, org.orcid.jaxb.model.record.summary_rc4.Educations.class).byDefault().register();
        mapperFactory.classMap(Education.class, org.orcid.jaxb.model.record_rc4.Education.class).byDefault().register();
        mapperFactory.classMap(Educations.class, org.orcid.jaxb.model.record_rc4.Educations.class).byDefault().register();
        mapperFactory.classMap(EducationSummary.class, org.orcid.jaxb.model.record.summary_rc4.EducationSummary.class).byDefault().register();
        
        //EMPLOYMENT
        mapperFactory.classMap(org.orcid.jaxb.model.record.summary_rc3.Employments.class, org.orcid.jaxb.model.record.summary_rc4.Employments.class).byDefault().register();
        mapperFactory.classMap(Employment.class, org.orcid.jaxb.model.record_rc4.Employment.class).byDefault().register();
        mapperFactory.classMap(Employments.class, org.orcid.jaxb.model.record_rc4.Employments.class).byDefault().register();
        mapperFactory.classMap(EmploymentSummary.class, org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary.class).byDefault().register();
        
        //PEER REVIEW
        mapperFactory.classMap(PeerReviews.class, org.orcid.jaxb.model.record.summary_rc4.PeerReviews.class).byDefault().register();
        mapperFactory.classMap(PeerReview.class, org.orcid.jaxb.model.record_rc4.PeerReview.class).byDefault().register();        
        mapperFactory.classMap(PeerReviewSummary.class, org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary.class).byDefault().register();                
        
        //NOTIFICATIONS
        mapperFactory.classMap(NotificationPermission.class, org.orcid.jaxb.model.notification.permission_rc4.NotificationPermission.class).byDefault().register();
        
        //Person
        mapperFactory.classMap(Person.class, org.orcid.jaxb.model.record_rc4.Person.class).byDefault().register();
        
        //Record
        mapperFactory.classMap(Record.class, org.orcid.jaxb.model.record_rc4.Record.class).byDefault().register();
        
        // error
        mapperFactory.classMap(OrcidError.class, org.orcid.jaxb.model.error_rc4.OrcidError.class).byDefault().register();
        
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
