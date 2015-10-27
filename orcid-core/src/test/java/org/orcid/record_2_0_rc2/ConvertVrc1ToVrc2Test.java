package org.orcid.record_2_0_rc2;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.impl.DefaultMapperFactory;

import org.junit.Test;
import org.orcid.jaxb.model.common.LastModifiedDate;
import org.orcid.jaxb.model.record_2_rc1.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record_2_rc1.summary.EducationSummary;
import org.orcid.jaxb.model.record_2_rc1.summary.EmploymentSummary;
import org.orcid.jaxb.model.record_2_rc1.summary.FundingGroup;
import org.orcid.jaxb.model.record_2_rc1.summary.FundingSummary;
import org.orcid.jaxb.model.record_2_rc1.summary.PeerReviewGroup;
import org.orcid.jaxb.model.record_2_rc1.summary.PeerReviewSummary;
import org.orcid.jaxb.model.record_2_rc1.summary.WorkGroup;
import org.orcid.jaxb.model.record_2_rc1.summary.WorkSummary;

public class ConvertVrc1ToVrc2Test {

    private final static MapperFacade mapper;

    static {
        final MapperFactory mapperFactory = new DefaultMapperFactory.Builder().build();

        mapperFactory.classMap(ActivitiesSummary.class, org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary.class).field("educations", "educations")
                .field("employments", "employments").field("fundings.fundingGroup{identifiers}", "fundings.fundingGroup{identifiers}")
                .field("fundings.fundingGroup{fundingSummary}", "fundings.fundingGroup{fundingSummary}")
                .field("peerReviews.peerReviewGroup{identifiers}", "peerReviews.peerReviewGroup{identifiers}")
                .field("peerReviews.peerReviewGroup{peerReviewSummary}", "peerReviews.peerReviewGroup{peerReviewSummary}")
                .field("works.workGroup{identifiers}", "works.workGroup{identifiers}").field("works.workGroup{workSummary}", "works.workGroup{workSummary}")
                .customize(new CustomMapper<ActivitiesSummary, org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary>() {
                    @Override
                    public void mapAtoB(ActivitiesSummary actSummaryRc1, org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary actSummaryRc2, MappingContext context) {

                        XMLGregorianCalendar latestActSummary = null;

                        // ==========Education============
                        if (actSummaryRc1.getEducations().getSummaries() != null && !actSummaryRc1.getEducations().getSummaries().isEmpty()) {
                            XMLGregorianCalendar latest = actSummaryRc1.getEducations().getSummaries().get(0).getLastModifiedDate().getValue();
                            for (EducationSummary educationSummaryRc1 : actSummaryRc1.getEducations().getSummaries()) {
                                if (latest.compare(educationSummaryRc1.getLastModifiedDate().getValue()) == -1) {
                                    latest = educationSummaryRc1.getLastModifiedDate().getValue();
                                }
                            }
                            latestActSummary = latest;
                            actSummaryRc2.getEducations().setLastModifiedDate(new LastModifiedDate(latest));
                        }

                        // ==========Employment============
                        if (actSummaryRc1.getEmployments().getSummaries() != null && !actSummaryRc1.getEmployments().getSummaries().isEmpty()) {
                            XMLGregorianCalendar latest = actSummaryRc1.getEmployments().getSummaries().get(0).getLastModifiedDate().getValue();
                            for (EmploymentSummary employmentSummaryRc1 : actSummaryRc1.getEmployments().getSummaries()) {
                                if (latest.compare(employmentSummaryRc1.getLastModifiedDate().getValue()) == -1) {
                                    latest = employmentSummaryRc1.getLastModifiedDate().getValue();
                                }
                            }
                            if (latestActSummary != null && latestActSummary.compare(latest) == -1) {
                                latestActSummary = latest;
                            }
                            actSummaryRc2.getEmployments().setLastModifiedDate(new LastModifiedDate(latest));
                        }

                        // ==========Fundings============
                        if (actSummaryRc1.getFundings().getFundingGroup() != null && !actSummaryRc1.getFundings().getFundingGroup().isEmpty()) {
                            if (actSummaryRc1.getFundings().getFundingGroup().get(0).getFundingSummary() != null
                                    && !actSummaryRc1.getFundings().getFundingGroup().get(0).getFundingSummary().isEmpty()) {
                                XMLGregorianCalendar latestGrp = actSummaryRc1.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getLastModifiedDate()
                                        .getValue();
                                for (int index = 0; index < actSummaryRc1.getFundings().getFundingGroup().size(); index++) {
                                    FundingGroup fundingGrpRc1 = actSummaryRc1.getFundings().getFundingGroup().get(index);
                                    XMLGregorianCalendar latest = fundingGrpRc1.getFundingSummary().get(0).getLastModifiedDate().getValue();
                                    for (FundingSummary fundingSummaryRc1 : fundingGrpRc1.getFundingSummary()) {
                                        if (latest.compare(fundingSummaryRc1.getLastModifiedDate().getValue()) == -1) {
                                            latest = fundingSummaryRc1.getLastModifiedDate().getValue();
                                        }

                                        if (fundingSummaryRc1.getLastModifiedDate().getValue().compare(latestGrp) == -1) {
                                            latestGrp = fundingSummaryRc1.getLastModifiedDate().getValue();
                                        }
                                    }
                                    actSummaryRc2.getFundings().getFundingGroup().get(index).setLastModifiedDate(new LastModifiedDate(latest));
                                }
                                if (latestActSummary != null && latestActSummary.compare(latestGrp) == -1) {
                                    latestActSummary = latestGrp;
                                }

                                actSummaryRc2.getFundings().setLastModifiedDate(new LastModifiedDate(latestGrp));
                            }
                        }

                        // ==========Peer reviews============
                        if (actSummaryRc1.getPeerReviews().getPeerReviewGroup() != null && !actSummaryRc1.getPeerReviews().getPeerReviewGroup().isEmpty()) {
                            if (actSummaryRc1.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary() != null
                                    && !actSummaryRc1.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().isEmpty()) {
                                XMLGregorianCalendar latestGrp = actSummaryRc1.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0)
                                        .getLastModifiedDate().getValue();
                                for (int index = 0; index < actSummaryRc1.getPeerReviews().getPeerReviewGroup().size(); index++) {
                                    PeerReviewGroup peerReviewGrpRc1 = actSummaryRc1.getPeerReviews().getPeerReviewGroup().get(index);
                                    XMLGregorianCalendar latest = peerReviewGrpRc1.getPeerReviewSummary().get(0).getLastModifiedDate().getValue();
                                    for (PeerReviewSummary peerReviewSummaryRc1 : peerReviewGrpRc1.getPeerReviewSummary()) {
                                        if (latest.compare(peerReviewSummaryRc1.getLastModifiedDate().getValue()) == -1) {
                                            latest = peerReviewSummaryRc1.getLastModifiedDate().getValue();
                                        }

                                        if (peerReviewSummaryRc1.getLastModifiedDate().getValue().compare(latestGrp) == -1) {
                                            latestGrp = peerReviewSummaryRc1.getLastModifiedDate().getValue();
                                        }
                                    }
                                    actSummaryRc2.getPeerReviews().getPeerReviewGroup().get(index).setLastModifiedDate(new LastModifiedDate(latest));
                                }
                                if (latestActSummary != null && latestActSummary.compare(latestGrp) == -1) {
                                    latestActSummary = latestGrp;
                                }

                                actSummaryRc2.getPeerReviews().setLastModifiedDate(new LastModifiedDate(latestGrp));
                            }
                        }

                        // ==========Works============
                        if (actSummaryRc1.getWorks().getWorkGroup() != null && !actSummaryRc1.getWorks().getWorkGroup().isEmpty()) {
                            if (actSummaryRc1.getWorks().getWorkGroup().get(0).getWorkSummary() != null
                                    && !actSummaryRc1.getWorks().getWorkGroup().get(0).getWorkSummary().isEmpty()) {
                                XMLGregorianCalendar latestGrp = actSummaryRc1.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getLastModifiedDate().getValue();
                                for (int index = 0; index < actSummaryRc1.getWorks().getWorkGroup().size(); index++) {
                                    WorkGroup workGrpRc1 = actSummaryRc1.getWorks().getWorkGroup().get(index);
                                    XMLGregorianCalendar latest = workGrpRc1.getWorkSummary().get(0).getLastModifiedDate().getValue();
                                    for (WorkSummary WorkSummaryRc1 : workGrpRc1.getWorkSummary()) {
                                        if (latest.compare(WorkSummaryRc1.getLastModifiedDate().getValue()) == -1) {
                                            latest = WorkSummaryRc1.getLastModifiedDate().getValue();
                                        }

                                        if (WorkSummaryRc1.getLastModifiedDate().getValue().compare(latestGrp) == -1) {
                                            latestGrp = WorkSummaryRc1.getLastModifiedDate().getValue();
                                        }
                                    }
                                    actSummaryRc2.getWorks().getWorkGroup().get(index).setLastModifiedDate(new LastModifiedDate(latest));
                                }
                                if (latestActSummary != null && latestActSummary.compare(latestGrp) == -1) {
                                    latestActSummary = latestGrp;
                                }

                                actSummaryRc2.getWorks().setLastModifiedDate(new LastModifiedDate(latestGrp));
                            }
                        }

                        actSummaryRc2.setLastModifiedDate(new LastModifiedDate(latestActSummary));
                    }
                }).register();
        mapper = mapperFactory.getMapperFacade();
    }

    @Test
    public void upgradeToVrc2Test() throws JAXBException {

        JAXBContext jaxbContext1 = JAXBContext.newInstance(ActivitiesSummary.class);
        JAXBContext jaxbContext2 = JAXBContext.newInstance(org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext1.createUnmarshaller();

        InputStream rc1Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc1.xml");
        InputStream rc2Stream = ConvertVrc1ToVrc2Test.class.getClassLoader().getResourceAsStream("test-activities-2.0_rc2.xml");

        ActivitiesSummary rc1Activities = (ActivitiesSummary) jaxbUnmarshaller.unmarshal(rc1Stream);

        jaxbUnmarshaller = jaxbContext2.createUnmarshaller();
        org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary rc2Activities1 = (org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary) jaxbUnmarshaller
                .unmarshal(rc2Stream);

        org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary rc2Activities2 = new org.orcid.jaxb.model.record_2_rc2.summary.ActivitiesSummary();

        mapper.map(rc1Activities, rc2Activities2);

        // Compare rc2Activities1(Converted with the mapper) and
        // rc2Activities2(Given XML)
        assertTrue(rc2Activities2.equals(rc2Activities1));
    }
}
