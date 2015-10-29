package org.orcid.record_2_0_rc2;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

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
import org.orcid.jaxb.model.record_2_rc1.GroupsContainer;
import org.orcid.jaxb.model.record_2_rc1.summary.ActivitiesSummary;
import org.orcid.utils.DateUtils;

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

                        SortedSet<Date> latestDates = new TreeSet<>();

                        // ==========Education============
                        latestDates.add(calculateLatest(actSummaryRc1.getEducations(), actSummaryRc2.getEducations()));

                        // ==========Employment============
                        latestDates.add(calculateLatest(actSummaryRc1.getEmployments(), actSummaryRc2.getEmployments()));

                        // ==========Fundings============
                        latestDates.add(calculateLatest(actSummaryRc1.getFundings(), actSummaryRc2.getFundings()));

                        // ==========Peer reviews============
                        latestDates.add(calculateLatest(actSummaryRc1.getPeerReviews(), actSummaryRc2.getPeerReviews()));

                        // ==========Works============
                        latestDates.add(calculateLatest(actSummaryRc1.getWorks(), actSummaryRc2.getWorks()));

                        actSummaryRc2.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendarNoTimeZoneNoMillis(latestDates.last())));
                    }

                    private Date calculateLatest(ActivitiesContainer actContainerRc1, org.orcid.jaxb.model.record_2_rc2.ActivitiesContainer actContainerRc2) {
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
                        return latestActSummary.toGregorianCalendar().getTime();
                    }

                    private Date calculateLatest(GroupsContainer groupsContainerRc1, org.orcid.jaxb.model.record_2_rc2.GroupsContainer groupsContainerRc2) {
                        Date latestGrp = null;
                        if (groupsContainerRc1.retrieveGroups() != null && !groupsContainerRc1.retrieveGroups().isEmpty()) {
                            List<? extends Group> groupsRc1 = new ArrayList<>(groupsContainerRc1.retrieveGroups());
                            List<org.orcid.jaxb.model.record_2_rc2.Group> groupsRc2 = new ArrayList<>(groupsContainerRc2.retrieveGroups());
                            if (groupsRc1.get(0).getActivities() != null && !groupsRc1.get(0).getActivities().isEmpty()) {
                                for (int index = 0; index < groupsRc1.size(); index++) {
                                    Group grpRc1 = groupsRc1.get(index);
                                    latestGrp = calculateLatests(grpRc1, groupsRc2.get(index));
                                }
                                groupsContainerRc2.setLastModifiedDate(new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(latestGrp)));
                            }
                        }
                        return latestGrp;
                    }

                    private Date calculateLatests(Group groupRc1, org.orcid.jaxb.model.record_2_rc2.Group groupRc2) {
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
                        return latestActSummary.toGregorianCalendar().getTime();
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

        // Compare rc2Activities2(Converted with the mapper) and
        // rc2Activities2 (Given XML)
        assertEquals(rc2Activities1.toString(), rc2Activities1.toString());
    }
}
