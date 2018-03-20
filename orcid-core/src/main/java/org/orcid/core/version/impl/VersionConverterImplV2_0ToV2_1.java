package org.orcid.core.version.impl;

import javax.annotation.Resource;

import org.orcid.core.manager.impl.OrcidUrlManager;
import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionObjectFactory;
import org.orcid.jaxb.model.common_v2.ContributorOrcid;
import org.orcid.jaxb.model.common_v2.OrcidIdBase;
import org.orcid.jaxb.model.common_v2.OrcidIdentifier;
import org.orcid.jaxb.model.common_v2.SourceClientId;
import org.orcid.jaxb.model.common_v2.SourceOrcid;
import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecords;
import org.orcid.jaxb.model.notification.permission_v2.NotificationPermission;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.Educations;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.Employments;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.Fundings;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Addresses;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Email;
import org.orcid.jaxb.model.record_v2.Emails;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.Keywords;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.OtherNames;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.Person;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_v2.Record;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.ResearcherUrls;
import org.orcid.jaxb.model.record_v2.Work;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

public class VersionConverterImplV2_0ToV2_1 implements V2VersionConverter {

    private static final String LOWER_VERSION = "2.0";
    private static final String UPPER_VERSION = "2.1";

    private static MapperFacade mapper;

    @Resource
    private OrcidUrlManager orcidUrlManager;

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

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public VersionConverterImplV2_0ToV2_1() {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        OrcidIdBaseMapper orcidIdBaseMapper = new OrcidIdBaseMapper();        

        // GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, GroupIdRecords.class).byDefault().register();
        mapperFactory.classMap(GroupIdRecord.class, GroupIdRecord.class).byDefault().register();

        // ExternalIDs
        mapperFactory.classMap(ExternalIDs.class, ExternalIDs.class).byDefault().register();
        mapperFactory.classMap(ExternalID.class, ExternalID.class).byDefault().register();

        // Contributor
        mapperFactory.classMap(ContributorOrcid.class, ContributorOrcid.class).customize(orcidIdBaseMapper).register();
        
        // Source Orcid
        mapperFactory.classMap(SourceOrcid.class, SourceOrcid.class).customize(orcidIdBaseMapper).register();
        
        // Source client ID
        mapperFactory.classMap(SourceClientId.class, SourceClientId.class).customize(orcidIdBaseMapper).register();
        
        // Orcid identifier
        mapperFactory.classMap(OrcidIdentifier.class, OrcidIdentifier.class).customize(orcidIdBaseMapper).register();
        
        // Other names
        mapperFactory.classMap(OtherNames.class, OtherNames.class).byDefault().register();
        mapperFactory.classMap(OtherName.class, OtherName.class).byDefault().register();

        // Keywords
        mapperFactory.classMap(Keywords.class, Keywords.class).byDefault().register();
        mapperFactory.classMap(Keyword.class, Keyword.class).byDefault().register();

        // Address
        mapperFactory.classMap(Addresses.class, Addresses.class).byDefault().register();
        mapperFactory.classMap(Address.class, Address.class).byDefault().register();

        // ResearcherUrl
        mapperFactory.classMap(ResearcherUrls.class, ResearcherUrls.class).byDefault().register();
        mapperFactory.classMap(ResearcherUrl.class, ResearcherUrl.class).byDefault().register();

        // Person External ID
        mapperFactory.classMap(PersonExternalIdentifiers.class, PersonExternalIdentifiers.class).byDefault().register();
        mapperFactory.classMap(PersonExternalIdentifier.class, PersonExternalIdentifier.class).byDefault().register();

        // Emails
        mapperFactory.classMap(Emails.class, Emails.class).byDefault().register();
        mapperFactory.classMap(Email.class, Email.class).byDefault().register();

        // WORK
        mapperFactory.classMap(WorkGroup.class, WorkGroup.class).byDefault().register();
        mapperFactory.classMap(Works.class, Works.class).byDefault().register();
        mapperFactory.classMap(Work.class, Work.class).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, WorkSummary.class).byDefault().register();

        // FUNDING
        mapperFactory.classMap(FundingGroup.class, FundingGroup.class).byDefault().register();
        mapperFactory.classMap(Fundings.class, Fundings.class).byDefault().register();
        mapperFactory.classMap(Funding.class, Funding.class).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, FundingSummary.class).byDefault().register();

        // EDUCATION
        mapperFactory.classMap(org.orcid.jaxb.model.record_v2.Educations.class, org.orcid.jaxb.model.record_v2.Educations.class).byDefault().register();
        mapperFactory.classMap(Educations.class, Educations.class).byDefault().register();
        mapperFactory.classMap(Education.class, Education.class).byDefault().register();
        mapperFactory.classMap(EducationSummary.class, EducationSummary.class).byDefault().register();

        // EMPLOYMENT
        mapperFactory.classMap(org.orcid.jaxb.model.record_v2.Employments.class, org.orcid.jaxb.model.record_v2.Employments.class).byDefault().register();
        mapperFactory.classMap(Employments.class, Employments.class).byDefault().register();
        mapperFactory.classMap(Employment.class, Employment.class).byDefault().register();
        mapperFactory.classMap(EmploymentSummary.class, EmploymentSummary.class).byDefault().register();

        // PEER REVIEW
        mapperFactory.classMap(PeerReviews.class, PeerReviews.class).byDefault().register();
        mapperFactory.classMap(PeerReview.class, PeerReview.class).byDefault().register();
        mapperFactory.classMap(PeerReviewSummary.class, PeerReviewSummary.class).byDefault().register();

        // NOTIFICATIONS
        mapperFactory.classMap(NotificationPermission.class, org.orcid.jaxb.model.notification.permission_v2.NotificationPermission.class).byDefault().register();

        // Person
        mapperFactory.classMap(Person.class, Person.class).byDefault().register();

        // Record
        mapperFactory.classMap(Record.class, Record.class).byDefault().register();

        // OrcidError
        mapperFactory.classMap(OrcidError.class, OrcidError.class).byDefault().register();
        
        mapper = mapperFactory.getMapperFacade();
    }    
    
    private class OrcidIdBaseMapper<Y, A> extends CustomMapper<OrcidIdBase, OrcidIdBase> {       
        @Override
        public void mapAtoB(OrcidIdBase a, OrcidIdBase b, MappingContext context) {
            b.setHost(a.getHost());
            b.setPath(a.getPath());
            if(context.getProperty("downgrade") != null) {
                boolean isDowngrade = (boolean) context.getProperty("downgrade");
                if(isDowngrade) {
                    // From 2.1 to 2.0 set the base http uri
                    b.setUri(orcidUrlManager.getBaseUriHttp() + (a.getClass().isAssignableFrom(SourceClientId.class) ? "/client/" : "/") + b.getPath());                    
                } else {
                    // From 2.0 to 2.1 set the base uri which is https
                    b.setUri(orcidUrlManager.getBaseUrl() + (a.getClass().isAssignableFrom(SourceClientId.class) ? "/client/" : "/") + a.getPath());
                }
            }                        
        }
    }    
    
    @Override
    public V2Convertible downgrade(V2Convertible objectToDowngrade) {
        Object objectToConvert = objectToDowngrade.getObjectToConvert();
        Object targetObject = v2VersionObjectFactory.createEquivalentInstance(objectToConvert, LOWER_VERSION);
        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty("downgrade", true);
        mapper.map(objectToConvert, targetObject, context);
        return new V2Convertible(targetObject, LOWER_VERSION);
    }

    @Override
    public V2Convertible upgrade(V2Convertible objectToUpgrade) {
        Object objectToConvert = objectToUpgrade.getObjectToConvert();
        Object targetObject = v2VersionObjectFactory.createEquivalentInstance(objectToConvert, UPPER_VERSION);
        MappingContext context = new MappingContext.Factory().getContext();
        context.setProperty("downgrade", false);        
        mapper.map(objectToConvert, targetObject, context);
        return new V2Convertible(targetObject, UPPER_VERSION);
    }        
}
