package org.orcid.record_2_0_rc2;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;

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
import org.orcid.jaxb.model.record_2_rc1.ActivitiesContainer;
import org.orcid.jaxb.model.record_2_rc1.Activity;
import org.orcid.jaxb.model.record_2_rc1.Group;
import org.orcid.jaxb.model.record_2_rc1.GroupableActivity;
import org.orcid.jaxb.model.record_2_rc1.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record_2_rc1.summary.FundingGroup;
import org.orcid.jaxb.model.record_2_rc1.summary.PeerReviewGroup;
import org.orcid.jaxb.model.record_2_rc1.summary.WorkGroup;

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
                        latestActSummary = calculateLatests(actSummaryRc1.getEducations(), actSummaryRc2.getEducations());

                        // ==========Employment============
                        latestActSummary = calculateLatests(actSummaryRc1.getEmployments(), actSummaryRc2.getEmployments());

                        // ==========Fundings============
                        if (actSummaryRc1.getFundings().getFundingGroup() != null && !actSummaryRc1.getFundings().getFundingGroup().isEmpty()) {
                            if (actSummaryRc1.getFundings().getFundingGroup().get(0).getFundingSummary() != null
                                    && !actSummaryRc1.getFundings().getFundingGroup().get(0).getFundingSummary().isEmpty()) {
                                XMLGregorianCalendar latestGrp = null;
                                for (int index = 0; index < actSummaryRc1.getFundings().getFundingGroup().size(); index++) {
                                    FundingGroup fundingGrpRc1 = actSummaryRc1.getFundings().getFundingGroup().get(index);

                                    latestGrp = calculateLatests(fundingGrpRc1, actSummaryRc2.getFundings().getFundingGroup().get(index));
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
                                XMLGregorianCalendar latestGrp = null;
                                for (int index = 0; index < actSummaryRc1.getPeerReviews().getPeerReviewGroup().size(); index++) {
                                    PeerReviewGroup peerReviewGrpRc1 = actSummaryRc1.getPeerReviews().getPeerReviewGroup().get(index);

                                    latestGrp = calculateLatests(peerReviewGrpRc1, actSummaryRc2.getPeerReviews().getPeerReviewGroup().get(index));
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
                                XMLGregorianCalendar latestGrp = null;
                                for (int index = 0; index < actSummaryRc1.getWorks().getWorkGroup().size(); index++) {
                                    WorkGroup workGrpRc1 = actSummaryRc1.getWorks().getWorkGroup().get(index);

                                    latestGrp = calculateLatests(workGrpRc1, actSummaryRc2.getWorks().getWorkGroup().get(index));
                                }
                                if (latestActSummary != null && latestActSummary.compare(latestGrp) == -1) {
                                    latestActSummary = latestGrp;
                                }

                                actSummaryRc2.getWorks().setLastModifiedDate(new LastModifiedDate(latestGrp));
                            }
                        }

                        actSummaryRc2.setLastModifiedDate(new LastModifiedDate(latestActSummary));
                    }

                    private XMLGregorianCalendar calculateLatests(ActivitiesContainer actContainerRc1,
                            org.orcid.jaxb.model.record_2_rc2.ActivitiesContainer actContainerRc2) {
                        XMLGregorianCalendar latestActSummary = null;
                        Collection<? extends Activity> activities = actContainerRc1.retrieveActivities();
                        if (activities != null && !activities.isEmpty()) {
                            Iterator<? extends Activity> activitiesIterator = activities.iterator();
                            XMLGregorianCalendar latest = activitiesIterator.next().getLastModifiedDate().getValue();
                            while (activitiesIterator.hasNext()) {
                                Activity activity = activitiesIterator.next();
                                if (latest.compare(activity.getLastModifiedDate().getValue()) == -1) {
                                    latest = activity.getLastModifiedDate().getValue();
                                }
                            }
                            latestActSummary = latest;
                            actContainerRc2.setLastModifiedDate(new LastModifiedDate(latest));
                        }
                        return latestActSummary;
                    }

                    private XMLGregorianCalendar calculateLatests(Group groupRc1, org.orcid.jaxb.model.record_2_rc2.Group groupRc2) {
                        XMLGregorianCalendar latestActSummary = null;
                        Collection<? extends GroupableActivity> activities = groupRc1.getActivities();
                        if (activities != null && !activities.isEmpty()) {
                            Iterator<? extends GroupableActivity> activitiesIterator = activities.iterator();
                            XMLGregorianCalendar latest = activitiesIterator.next().getLastModifiedDate().getValue();
                            while (activitiesIterator.hasNext()) {
                                GroupableActivity activity = activitiesIterator.next();
                                if (latest.compare(activity.getLastModifiedDate().getValue()) == -1) {
                                    latest = activity.getLastModifiedDate().getValue();
                                }
                            }
                            latestActSummary = latest;
                            groupRc2.setLastModifiedDate(new LastModifiedDate(latest));
                        }
                        return latestActSummary;
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
