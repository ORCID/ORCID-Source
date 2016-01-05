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

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.orcid.core.version.V2Convertible;
import org.orcid.core.version.V2VersionConverter;
import org.orcid.jaxb.model.common.LastModifiedDate;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.Educations;
import org.orcid.jaxb.model.record.summary_rc1.Employments;
import org.orcid.jaxb.model.record.summary_rc1.Fundings;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviews;
import org.orcid.jaxb.model.record.summary_rc1.Works;
import org.orcid.utils.DateUtils;

public class VersionConverterImplV2_0_rc1ToV2_0rc2 implements V2VersionConverter {

    private static final String LOWER_VERSION = "2.0_rc1";
    private static final String UPPER_VERSION = "2.0_rc2";

    private final static MapperFacade mapper;

    static {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        // ACTIVITY SUMMARY
        mapperFactory.classMap(ActivitiesSummary.class, org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary.class).field("educations", "educations")
                .field("employments", "employments").field("fundings.fundingGroup{identifiers}", "fundings.fundingGroup{identifiers}")
                .field("fundings.fundingGroup{fundingSummary}", "fundings.fundingGroup{fundingSummary}")
                .field("peerReviews.peerReviewGroup{identifiers}", "peerReviews.peerReviewGroup{identifiers}")
                .field("peerReviews.peerReviewGroup{peerReviewSummary}", "peerReviews.peerReviewGroup{peerReviewSummary}")
                .field("works.workGroup{identifiers}", "works.workGroup{identifiers}").field("works.workGroup{workSummary}", "works.workGroup{workSummary}")
                .customize(new CustomMapper<ActivitiesSummary, org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary>() {
                    @Override
                    public void mapAtoB(ActivitiesSummary actSummaryRc1, org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary actSummaryRc2,
                            MappingContext context) {

                        SortedSet<Date> latestDates = new TreeSet<>();

                        latestDates.add(VersionConverterHelper.calculateLatest(actSummaryRc1.getEducations(), actSummaryRc2.getEducations()));
                        latestDates.add(VersionConverterHelper.calculateLatest(actSummaryRc1.getEmployments(), actSummaryRc2.getEmployments()));
                        latestDates.add(VersionConverterHelper.calculateLatest(actSummaryRc1.getFundings(), actSummaryRc2.getFundings()));
                        latestDates.add(VersionConverterHelper.calculateLatest(actSummaryRc1.getPeerReviews(), actSummaryRc2.getPeerReviews()));
                        latestDates.add(VersionConverterHelper.calculateLatest(actSummaryRc1.getWorks(), actSummaryRc2.getWorks()));

                        actSummaryRc2.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(latestDates.last())));
                    }
                }).register();

        // EDUCATION SUMMARY
        mapperFactory.classMap(Educations.class, org.orcid.jaxb.model.record.summary_rc2.Educations.class).field("summaries", "summaries")
                .customize(new CustomMapper<Educations, org.orcid.jaxb.model.record.summary_rc2.Educations>() {
                    @Override
                    public void mapAtoB(Educations educationsRc1, org.orcid.jaxb.model.record.summary_rc2.Educations educationsRc2, MappingContext context) {
                        educationsRc2.setLastModifiedDate(new LastModifiedDate(
                                DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(VersionConverterHelper.calculateLatest(educationsRc1, educationsRc2))));
                    }
                }).register();

        // EMPLOYMENT SUMMARY
        mapperFactory.classMap(Employments.class, org.orcid.jaxb.model.record.summary_rc2.Employments.class).field("summaries", "summaries")
                .customize(new CustomMapper<Employments, org.orcid.jaxb.model.record.summary_rc2.Employments>() {
                    @Override
                    public void mapAtoB(Employments employmentsRc1, org.orcid.jaxb.model.record.summary_rc2.Employments employmentsRc2, MappingContext context) {
                        employmentsRc2.setLastModifiedDate(new LastModifiedDate(
                                DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(VersionConverterHelper.calculateLatest(employmentsRc1, employmentsRc2))));
                    }
                }).register();

        // FUNDINGS
        mapperFactory.classMap(Fundings.class, org.orcid.jaxb.model.record.summary_rc2.Fundings.class).field("fundingGroup{identifiers}", "fundingGroup{identifiers}")
                .field("fundingGroup{fundingSummary}", "fundingGroup{fundingSummary}")
                .customize(new CustomMapper<Fundings, org.orcid.jaxb.model.record.summary_rc2.Fundings>() {
                    @Override
                    public void mapAtoB(Fundings fundingsRc1, org.orcid.jaxb.model.record.summary_rc2.Fundings fundingsRc2, MappingContext context) {
                        fundingsRc2.setLastModifiedDate(new LastModifiedDate(
                                DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(VersionConverterHelper.calculateLatest(fundingsRc1, fundingsRc2))));
                    }
                }).register();

        // PEER REVIEWS
        mapperFactory.classMap(PeerReviews.class, org.orcid.jaxb.model.record.summary_rc2.PeerReviews.class)
                .field("peerReviewGroup{identifiers}", "peerReviewGroup{identifiers}").field("peerReviewGroup{peerReviewSummary}", "peerReviewGroup{peerReviewSummary}")
                .customize(new CustomMapper<PeerReviews, org.orcid.jaxb.model.record.summary_rc2.PeerReviews>() {
                    @Override
                    public void mapAtoB(PeerReviews peerReviewsRc1, org.orcid.jaxb.model.record.summary_rc2.PeerReviews peerReviewsRc2, MappingContext context) {
                        peerReviewsRc2.setLastModifiedDate(new LastModifiedDate(
                                DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(VersionConverterHelper.calculateLatest(peerReviewsRc1, peerReviewsRc2))));
                    }
                }).register();

        // WORKS
        mapperFactory.classMap(Works.class, org.orcid.jaxb.model.record.summary_rc2.Works.class).field("workGroup{identifiers}", "workGroup{identifiers}")
                .field("workGroup{workSummary}", "workGroup{workSummary}").customize(new CustomMapper<Works, org.orcid.jaxb.model.record.summary_rc2.Works>() {
                    @Override
                    public void mapAtoB(Works worksRc1, org.orcid.jaxb.model.record.summary_rc2.Works worksRc2, MappingContext context) {
                        worksRc2.setLastModifiedDate(new LastModifiedDate(
                                DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(VersionConverterHelper.calculateLatest(worksRc1, worksRc2))));
                    }
                }).register();
        mapper = mapperFactory.getMapperFacade();
    }

    @Override
    public String getLowerVersion() {
        return LOWER_VERSION;
    }

    @Override
    public String getUpperVersion() {
        return UPPER_VERSION;
    }

    @Override
    public V2Convertible downgrade(Object targetObject, V2Convertible objectToDowngrade) {
        mapper.map(objectToDowngrade.getObjectToConvert(), targetObject);
        return new V2Convertible(objectToDowngrade.getObjectToConvert(), LOWER_VERSION);
    }

    @Override
    public V2Convertible upgrade(Object targetObject, V2Convertible objectToUpgrade) {
        mapper.map(objectToUpgrade.getObjectToConvert(), targetObject);
        return new V2Convertible(objectToUpgrade.getObjectToConvert(), UPPER_VERSION);
    }

}
