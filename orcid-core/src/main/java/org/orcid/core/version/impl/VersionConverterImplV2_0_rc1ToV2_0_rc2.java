package org.orcid.core.version.impl;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;

import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionObjectFactory;
import org.orcid.jaxb.model.common_rc2.LastModifiedDate;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.groupid_rc1.GroupIdRecords;
import org.orcid.jaxb.model.notification.permission_rc1.NotificationPermission;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc1.Educations;
import org.orcid.jaxb.model.record.summary_rc1.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc1.Employments;
import org.orcid.jaxb.model.record.summary_rc1.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc1.Fundings;
import org.orcid.jaxb.model.record.summary_rc1.Identifier;
import org.orcid.jaxb.model.record.summary_rc1.Identifiers;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc1.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc1.Works;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.FundingExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.FundingExternalIdentifiers;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifier;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierId;
import org.orcid.jaxb.model.record_rc1.WorkExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.utils.DateUtils;

public class VersionConverterImplV2_0_rc1ToV2_0_rc2 implements V2VersionConverter {

    private static final String LOWER_VERSION = "2.0_rc1";
    private static final String UPPER_VERSION = "2.0_rc2";

    private final static MapperFacade mapper;

    static {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

       // mapperFactory.getConverterFactory().registerConverter(new ActivityIdentifierToExternalIDConverter());
        mapperFactory.getConverterFactory().registerConverter(new FundingExternalIdentifiersToExternalIDConverter());
        mapperFactory.getConverterFactory().registerConverter(new WorkExternalIdentifiersToExternalIDConverter());
        mapperFactory.getConverterFactory().registerConverter(new WorkExternalIdentifierToExternalIDConverter());
        
        // ACTIVITY SUMMARY
        mapperFactory.classMap(ActivitiesSummary.class, org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary.class).field("educations", "educations")
                .field("employments", "employments")
                .field("fundings.fundingGroup{identifiers}", "fundings.fundingGroup{identifiers}")
                .field("fundings.fundingGroup{fundingSummary}", "fundings.fundingGroup{fundingSummary}")
                .field("peerReviews.peerReviewGroup{identifiers}", "peerReviews.peerReviewGroup{identifiers}")
                .field("peerReviews.peerReviewGroup{peerReviewSummary}", "peerReviews.peerReviewGroup{peerReviewSummary}")
                .field("works.workGroup{identifiers}", "works.workGroup{identifiers}")
                .field("works.workGroup{workSummary}", "works.workGroup{workSummary}")
                .customize(new CustomMapper<ActivitiesSummary, org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary>() {
                    @Override
                    public void mapAtoB(ActivitiesSummary actSummaryRc1, org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary actSummaryRc2,
                            MappingContext context) {

                        SortedSet<Date> latestDates = new TreeSet<>();

                        latestDates.add(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(actSummaryRc2.getEducations()));
                        latestDates.add(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(actSummaryRc2.getEmployments()));
                        latestDates.add(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(actSummaryRc2.getFundings()));
                        latestDates.add(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(actSummaryRc2.getPeerReviews()));
                        latestDates.add(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(actSummaryRc2.getWorks()));

                        actSummaryRc2.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(latestDates.last())));
                    }
                })
                .register();

        // EDUCATION SUMMARY
        mapperFactory.classMap(Educations.class, org.orcid.jaxb.model.record.summary_rc2.Educations.class).field("summaries", "summaries")
                .customize(new CustomMapper<Educations, org.orcid.jaxb.model.record.summary_rc2.Educations>() {
                    @Override
                    public void mapAtoB(Educations educationsRc1, org.orcid.jaxb.model.record.summary_rc2.Educations educationsRc2, MappingContext context) {
                        educationsRc2.setLastModifiedDate(
                                new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(educationsRc2))));
                    }
                }).register();

        // EMPLOYMENT SUMMARY
        mapperFactory.classMap(Employments.class, org.orcid.jaxb.model.record.summary_rc2.Employments.class).field("summaries", "summaries")
                .customize(new CustomMapper<Employments, org.orcid.jaxb.model.record.summary_rc2.Employments>() {
                    @Override
                    public void mapAtoB(Employments employmentsRc1, org.orcid.jaxb.model.record.summary_rc2.Employments employmentsRc2, MappingContext context) {
                        employmentsRc2.setLastModifiedDate(
                                new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(employmentsRc2))));
                    }
                }).register();

        // FUNDINGS
        mapperFactory.classMap(Fundings.class, org.orcid.jaxb.model.record.summary_rc2.Fundings.class)
                .field("fundingGroup{identifiers}", "fundingGroup{identifiers}")
                .field("fundingGroup{fundingSummary}", "fundingGroup{fundingSummary}")
                .customize(new CustomMapper<Fundings, org.orcid.jaxb.model.record.summary_rc2.Fundings>() {
                    @Override
                    public void mapAtoB(Fundings fundingsRc1, org.orcid.jaxb.model.record.summary_rc2.Fundings fundingsRc2, MappingContext context) {
                        fundingsRc2.setLastModifiedDate(
                                new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(fundingsRc2))));
                    }
                }).register();
        
        // PEER REVIEWS
        mapperFactory.classMap(PeerReviews.class, org.orcid.jaxb.model.record.summary_rc2.PeerReviews.class)
                .field("peerReviewGroup{identifiers}", "peerReviewGroup{identifiers}")
                .field("peerReviewGroup{peerReviewSummary}", "peerReviewGroup{peerReviewSummary}")
                .customize(new CustomMapper<PeerReviews, org.orcid.jaxb.model.record.summary_rc2.PeerReviews>() {
                    @Override
                    public void mapAtoB(PeerReviews peerReviewsRc1, org.orcid.jaxb.model.record.summary_rc2.PeerReviews peerReviewsRc2, MappingContext context) {
                        peerReviewsRc2.setLastModifiedDate(
                                new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(peerReviewsRc2))));
                    }
                }).register();

        // WORKS
        mapperFactory.classMap(Works.class, org.orcid.jaxb.model.record.summary_rc2.Works.class)
                .field("workGroup{identifiers}", "workGroup{identifiers}")
                .field("workGroup{workSummary}", "workGroup{workSummary}")
                .customize(new CustomMapper<Works, org.orcid.jaxb.model.record.summary_rc2.Works>() {
                    @Override
                    public void mapAtoB(Works worksRc1, org.orcid.jaxb.model.record.summary_rc2.Works worksRc2, MappingContext context) {
                        worksRc2.setLastModifiedDate(
                                new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(worksRc2))));
                    }
                })
                .register();
        
        //GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, org.orcid.jaxb.model.groupid_rc2.GroupIdRecords.class)
        .field("groupIdRecord", "groupIdRecord").customize(new CustomMapper<GroupIdRecords, org.orcid.jaxb.model.groupid_rc2.GroupIdRecords>() {
            @Override
            public void mapAtoB(GroupIdRecords groupsRc1, org.orcid.jaxb.model.groupid_rc2.GroupIdRecords groupsRc2, MappingContext context) {
            	groupsRc2.setLastModifiedDate(
                        new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc2_LastModifiedDatesHelper.calculateLatest(groupsRc2))));
            }
        }).register();
        
        
        //Identifiers to ExternalIDs
        mapperFactory.classMap(Identifiers.class, ExternalIDs.class)
        .field("identifier{}", "externalIdentifier{}")
        .register();
        
        mapperFactory.classMap(Identifier.class, ExternalID.class)
        .field("externalIdentifierId", "value")
        .field("externalIdentifierType", "type")
        .register();
        
        // WORK 
        mapperFactory.classMap(Work.class, org.orcid.jaxb.model.record_rc2.Work.class).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, org.orcid.jaxb.model.record.summary_rc2.WorkSummary.class).byDefault().register();
        
        //FUNDING
        mapperFactory.classMap(Funding.class, org.orcid.jaxb.model.record_rc2.Funding.class).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, org.orcid.jaxb.model.record.summary_rc2.FundingSummary.class).byDefault().register();
        
        //EDUCATION
        mapperFactory.classMap(Education.class, org.orcid.jaxb.model.record_rc2.Education.class).byDefault().register();
        mapperFactory.classMap(EducationSummary.class, org.orcid.jaxb.model.record.summary_rc2.EducationSummary.class).byDefault().register();
        
        //EMPLOYMENT
        mapperFactory.classMap(Employment.class, org.orcid.jaxb.model.record_rc2.Employment.class).byDefault().register();
        mapperFactory.classMap(EmploymentSummary.class, org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary.class).byDefault().register();
        
        //PEER REVIEW
        mapperFactory.classMap(PeerReview.class, org.orcid.jaxb.model.record_rc2.PeerReview.class).byDefault().register();        
        mapperFactory.classMap(PeerReviewSummary.class, org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary.class).byDefault().register();                
        
        //NOTIFICATIONS
        mapperFactory.classMap(NotificationPermission.class, org.orcid.jaxb.model.notification.permission_rc2.NotificationPermission.class).byDefault().register();;
        
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
    
    public static class FundingExternalIdentifiersToExternalIDConverter extends BidirectionalConverter<FundingExternalIdentifiers,ExternalIDs> {

        @Override
        public ExternalIDs convertTo(FundingExternalIdentifiers source, Type<ExternalIDs> destinationType) {
            ExternalIDs ids = new ExternalIDs();
            for (FundingExternalIdentifier identifier : source.getExternalIdentifier()){
                ExternalID id = new ExternalID();
                id.setType(identifier.getType().value());
                id.setValue(identifier.getValue());
                if (identifier.getUrl() != null){
                    id.setUrl(new Url(identifier.getUrl().getValue()));                    
                }
                if (identifier.getRelationship() != null){
                    id.setRelationship(Relationship.fromValue(identifier.getRelationship().value()));
                }
                ids.getExternalIdentifier().add(id);
            }
            return ids;
        }

        @Override
        public FundingExternalIdentifiers convertFrom(ExternalIDs source, Type<FundingExternalIdentifiers> destinationType) {
            FundingExternalIdentifiers identifiers = new FundingExternalIdentifiers();
            for (ExternalID id : source.getExternalIdentifier()){
                FundingExternalIdentifier identifier = new FundingExternalIdentifier();
                identifier.setType(org.orcid.jaxb.model.record_rc1.FundingExternalIdentifierType.fromValue(id.getType()));
                identifier.setValue(id.getValue());
                if (id.getUrl()!=null){
                    identifier.setUrl(new org.orcid.jaxb.model.common_rc1.Url(id.getUrl().getValue()));
                }
                if (id.getRelationship() !=null){
                    identifier.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.fromValue(id.getRelationship().value()));
                }
                identifiers.getExternalIdentifier().add(identifier);
            }
            return identifiers;
        }

     }
    
    public static class WorkExternalIdentifiersToExternalIDConverter extends BidirectionalConverter<WorkExternalIdentifiers,ExternalIDs> {

        @Override
        public ExternalIDs convertTo(WorkExternalIdentifiers source, Type<ExternalIDs> destinationType) {
            ExternalIDs ids = new ExternalIDs();
            for (WorkExternalIdentifier identifier : source.getExternalIdentifier()){
                ExternalID id = new ExternalID();
                id.setType(identifier.getWorkExternalIdentifierType().value());
                id.setValue(identifier.getWorkExternalIdentifierId().getContent());
                if (identifier.getUrl() != null){
                    id.setUrl(new Url(identifier.getUrl().getValue()));                    
                }
                if (identifier.getRelationship() != null){
                    id.setRelationship(Relationship.fromValue(identifier.getRelationship().value()));
                }
                ids.getExternalIdentifier().add(id);
            }
            return ids;
        }

        @Override
        public WorkExternalIdentifiers convertFrom(ExternalIDs source, Type<WorkExternalIdentifiers> destinationType) {
            WorkExternalIdentifiers identifiers = new WorkExternalIdentifiers();
            for (ExternalID id : source.getExternalIdentifier()){
                WorkExternalIdentifier identifier = new WorkExternalIdentifier();
                identifier.setWorkExternalIdentifierType(org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType.fromValue(id.getType()));
                identifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(id.getValue()));
                if (id.getUrl()!=null){
                    identifier.setUrl(new org.orcid.jaxb.model.common_rc1.Url(id.getUrl().getValue()));
                }
                if (id.getRelationship() !=null){
                    identifier.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.fromValue(id.getRelationship().value()));
                }
                identifiers.getExternalIdentifier().add(identifier);
            }
            return identifiers;
        }

     }
    
    public static class WorkExternalIdentifierToExternalIDConverter extends BidirectionalConverter<WorkExternalIdentifier,ExternalID> {

        @Override
        public ExternalID convertTo(WorkExternalIdentifier identifier, Type<ExternalID> destinationType) {
            ExternalID id = new ExternalID();
            id.setType(identifier.getWorkExternalIdentifierType().value());
            id.setValue(identifier.getWorkExternalIdentifierId().getContent());
            if (identifier.getUrl() != null){
                id.setUrl(new Url(identifier.getUrl().getValue()));                    
            }
            if (identifier.getRelationship() != null){
                id.setRelationship(Relationship.fromValue(identifier.getRelationship().value()));
            }
            return id;
        }

        @Override
        public WorkExternalIdentifier convertFrom(ExternalID id, Type<WorkExternalIdentifier> destinationType) {
            WorkExternalIdentifier identifier = new WorkExternalIdentifier();
            identifier.setWorkExternalIdentifierType(org.orcid.jaxb.model.record_rc1.WorkExternalIdentifierType.fromValue(id.getType()));
            identifier.setWorkExternalIdentifierId(new WorkExternalIdentifierId(id.getValue()));
            if (id.getUrl()!=null){
                identifier.setUrl(new org.orcid.jaxb.model.common_rc1.Url(id.getUrl().getValue()));
            }
            if (id.getRelationship() !=null){
                identifier.setRelationship(org.orcid.jaxb.model.record_rc1.Relationship.fromValue(id.getRelationship().value()));
            }
            return identifier;
        }

     }
}
