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
package org.orcid.core.version.impl;

import java.util.Date;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.core.version.V2VersionObjectFactory;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecords;
import org.orcid.jaxb.model.notification.permission_rc2.NotificationPermission;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.Educations;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.Employments;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.Fundings;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc2.Works;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.utils.DateUtils;

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
        
        // ACTIVITY SUMMARY
        mapperFactory.classMap(ActivitiesSummary.class, org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary.class).field("educations", "educations")
                .field("employments", "employments")
                .field("fundings.fundingGroup{identifiers}", "fundings.fundingGroup{identifiers}")
                .field("fundings.fundingGroup{fundingSummary}", "fundings.fundingGroup{fundingSummary}")
                .field("peerReviews.peerReviewGroup{identifiers}", "peerReviews.peerReviewGroup{identifiers}")
                .field("peerReviews.peerReviewGroup{peerReviewSummary}", "peerReviews.peerReviewGroup{peerReviewSummary}")
                .field("works.workGroup{identifiers}", "works.workGroup{identifiers}")
                .field("works.workGroup{workSummary}", "works.workGroup{workSummary}")
                .customize(new CustomMapper<ActivitiesSummary, org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary>() {
                    @Override
                    public void mapAtoB(ActivitiesSummary actSummaryRc2, org.orcid.jaxb.model.record.summary_rc3.ActivitiesSummary actSummaryRc3,
                            MappingContext context) {

                        SortedSet<Date> latestDates = new TreeSet<>();

                        latestDates.add(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(actSummaryRc3.getEducations()));
                        latestDates.add(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(actSummaryRc3.getEmployments()));
                        latestDates.add(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(actSummaryRc3.getFundings()));
                        latestDates.add(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(actSummaryRc3.getPeerReviews()));
                        latestDates.add(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(actSummaryRc3.getWorks()));

                        actSummaryRc3.setLastModifiedDate(new org.orcid.jaxb.model.common_rc3.LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(latestDates.last())));
                    }
                })
                .register();

        // EDUCATION SUMMARY
        mapperFactory.classMap(Educations.class, org.orcid.jaxb.model.record.summary_rc3.Educations.class).field("summaries", "summaries")
                .customize(new CustomMapper<Educations, org.orcid.jaxb.model.record.summary_rc3.Educations>() {
                    @Override
                    public void mapAtoB(Educations educationsRc2, org.orcid.jaxb.model.record.summary_rc3.Educations educationsRc3, MappingContext context) {
                        educationsRc3.setLastModifiedDate(
                                new org.orcid.jaxb.model.common_rc3.LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(educationsRc3))));
                    }
                }).register();

        // EMPLOYMENT SUMMARY
        mapperFactory.classMap(Employments.class, org.orcid.jaxb.model.record.summary_rc3.Employments.class).field("summaries", "summaries")
                .customize(new CustomMapper<Employments, org.orcid.jaxb.model.record.summary_rc3.Employments>() {
                    @Override
                    public void mapAtoB(Employments employmentsRc2, org.orcid.jaxb.model.record.summary_rc3.Employments employmentsRc3, MappingContext context) {
                        employmentsRc3.setLastModifiedDate(
                                new org.orcid.jaxb.model.common_rc3.LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(employmentsRc3))));
                    }
                }).register();

        // FUNDINGS
        mapperFactory.classMap(Fundings.class, org.orcid.jaxb.model.record.summary_rc3.Fundings.class)
                .field("fundingGroup{identifiers}", "fundingGroup{identifiers}")
                .field("fundingGroup{fundingSummary}", "fundingGroup{fundingSummary}")
                .customize(new CustomMapper<Fundings, org.orcid.jaxb.model.record.summary_rc3.Fundings>() {
                    @Override
                    public void mapAtoB(Fundings fundingsRc2, org.orcid.jaxb.model.record.summary_rc3.Fundings fundingsRc3, MappingContext context) {
                        fundingsRc3.setLastModifiedDate(
                                new org.orcid.jaxb.model.common_rc3.LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(fundingsRc3))));
                    }
                }).register();
        
        // PEER REVIEWS
        mapperFactory.classMap(PeerReviews.class, org.orcid.jaxb.model.record.summary_rc3.PeerReviews.class)
                .field("peerReviewGroup{identifiers}", "peerReviewGroup{identifiers}")
                .field("peerReviewGroup{peerReviewSummary}", "peerReviewGroup{peerReviewSummary}")
                .customize(new CustomMapper<PeerReviews, org.orcid.jaxb.model.record.summary_rc3.PeerReviews>() {
                    @Override
                    public void mapAtoB(PeerReviews peerReviewsRc2, org.orcid.jaxb.model.record.summary_rc3.PeerReviews peerReviewsRc3, MappingContext context) {
                        peerReviewsRc3.setLastModifiedDate(
                                new org.orcid.jaxb.model.common_rc3.LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(peerReviewsRc3))));
                    }
                }).register();

        // WORKS
        mapperFactory.classMap(Works.class, org.orcid.jaxb.model.record.summary_rc3.Works.class)
                .field("workGroup{identifiers}", "workGroup{identifiers}")
                .field("workGroup{workSummary}", "workGroup{workSummary}")
                .customize(new CustomMapper<Works, org.orcid.jaxb.model.record.summary_rc3.Works>() {
                    @Override
                    public void mapAtoB(Works worksRc2, org.orcid.jaxb.model.record.summary_rc3.Works worksRc3, MappingContext context) {
                        worksRc3.setLastModifiedDate(
                                new org.orcid.jaxb.model.common_rc3.LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(worksRc3))));
                    }
                })
                .register();
        
        //GROUP ID
        mapperFactory.classMap(GroupIdRecords.class, org.orcid.jaxb.model.groupid_rc3.GroupIdRecords.class)
        .field("groupIdRecord", "groupIdRecord").customize(new CustomMapper<GroupIdRecords, org.orcid.jaxb.model.groupid_rc3.GroupIdRecords>() {
            @Override
            public void mapAtoB(GroupIdRecords groupsRc2, org.orcid.jaxb.model.groupid_rc3.GroupIdRecords groupsRc3, MappingContext context) {
            	groupsRc3.setLastModifiedDate(
                        new org.orcid.jaxb.model.common_rc3.LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(Api2_0_rc3_LastModifiedDatesHelper.calculateLatest(groupsRc3))));
            }
        }).register();
        
        
        //ExternalIDs
        mapperFactory.classMap(ExternalIDs.class, org.orcid.jaxb.model.record_rc3.ExternalIDs.class)
        .register();
        
        //ExternalID
        mapperFactory.classMap(ExternalID.class, org.orcid.jaxb.model.record_rc3.ExternalID.class)        
        .register();
        
        //Other names
        mapperFactory.classMap(OtherNames.class, org.orcid.jaxb.model.record_rc3.OtherNames.class).register();;
        mapperFactory.classMap(OtherName.class, org.orcid.jaxb.model.record_rc3.OtherName.class).register();
                
        //Keywords
        mapperFactory.classMap(Keywords.class, org.orcid.jaxb.model.record_rc3.Keywords.class).register();;
        mapperFactory.classMap(Keyword.class, org.orcid.jaxb.model.record_rc3.Keyword.class).register();
        
        //Address
        mapperFactory.classMap(Addresses.class, org.orcid.jaxb.model.record_rc3.Addresses.class).register();;
        mapperFactory.classMap(Address.class, org.orcid.jaxb.model.record_rc3.Address.class).register();
        
        //ResearcherUrl
        mapperFactory.classMap(ResearcherUrls.class, org.orcid.jaxb.model.record_rc3.ResearcherUrls.class).register();;
        mapperFactory.classMap(ResearcherUrl.class, org.orcid.jaxb.model.record_rc3.ResearcherUrl.class).register();
        
        //Person External ID
        mapperFactory.classMap(PersonExternalIdentifiers.class, org.orcid.jaxb.model.record_rc3.PersonExternalIdentifiers.class).register();;
        mapperFactory.classMap(PersonExternalIdentifier.class, org.orcid.jaxb.model.record_rc3.PersonExternalIdentifier.class).register();
        
        // WORK 
        mapperFactory.classMap(Work.class, org.orcid.jaxb.model.record_rc3.Work.class).byDefault().register();
        mapperFactory.classMap(WorkSummary.class, org.orcid.jaxb.model.record.summary_rc3.WorkSummary.class).byDefault().register();
        
        //FUNDING
        mapperFactory.classMap(Funding.class, org.orcid.jaxb.model.record_rc3.Funding.class).byDefault().register();
        mapperFactory.classMap(FundingSummary.class, org.orcid.jaxb.model.record.summary_rc3.FundingSummary.class).byDefault().register();
        
        //EDUCATION
        mapperFactory.classMap(Education.class, org.orcid.jaxb.model.record_rc3.Education.class).byDefault().register();
        mapperFactory.classMap(EducationSummary.class, org.orcid.jaxb.model.record.summary_rc3.EducationSummary.class).byDefault().register();
        
        //EMPLOYMENT
        mapperFactory.classMap(Employment.class, org.orcid.jaxb.model.record_rc3.Employment.class).byDefault().register();
        mapperFactory.classMap(EmploymentSummary.class, org.orcid.jaxb.model.record.summary_rc3.EmploymentSummary.class).byDefault().register();
        
        //PEER REVIEW
        mapperFactory.classMap(PeerReview.class, org.orcid.jaxb.model.record_rc3.PeerReview.class).byDefault().register();        
        mapperFactory.classMap(PeerReviewSummary.class, org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary.class).byDefault().register();                
        
        //NOTIFICATIONS
        mapperFactory.classMap(NotificationPermission.class, org.orcid.jaxb.model.notification.permission_rc3.NotificationPermission.class).byDefault().register();;
        
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
