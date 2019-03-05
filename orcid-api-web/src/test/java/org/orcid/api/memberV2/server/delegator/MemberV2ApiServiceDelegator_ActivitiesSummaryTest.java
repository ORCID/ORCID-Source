package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_v2.LastModifiedDate;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.TranslatedTitle;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.FundingGroup;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkGroup;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;
import org.orcid.jaxb.model.record.summary_v2.Works;
import org.orcid.jaxb.model.record_v2.Address;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.jaxb.model.record_v2.Keyword;
import org.orcid.jaxb.model.record_v2.OtherName;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_v2.ResearcherUrl;
import org.orcid.jaxb.model.record_v2.Work;
import org.orcid.jaxb.model.record_v2.WorkBulk;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.Utils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegator_ActivitiesSummaryTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV2ApiServiceDelegator")
    protected MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, WorkBulk, Address, Keyword> serviceDelegator;

    @BeforeClass
    public static void initDBUnitData() throws Exception {
        initDBUnitData(DATA_FILES);
    }

    @AfterClass
    public static void removeDBUnitData() throws Exception {
        Collections.reverse(DATA_FILES);
        removeDBUnitData(DATA_FILES);
    }

    @Test
    public void testViewActivitiesReadPublic() {
        SecurityContextTestUtils.setUpSecurityContextForClientOnly("APP-5555555555555555", ScopePathType.READ_PUBLIC);
        Response r = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        Utils.assertIsPublicOrSource(as, "APP-5555555555555555");
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewActivitiesWrongToken() {
        SecurityContextTestUtils.setUpSecurityContext("some-other-user", ScopePathType.READ_LIMITED);
        serviceDelegator.viewActivities(ORCID);
    }

    @Test
    public void testReadPublicScope_Activities() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        // Check you get only public activities
        Response r = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
        testActivities(as, ORCID);
    }

    @Test
    public void testViewActitivies() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewActivities(ORCID);
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());
        assertNotNull(as.getEducations());
        assertEquals(4, as.getEducations().getSummaries().size());

        for (EducationSummary element : as.getEducations().getSummaries()) {
            if (element.getPutCode().equals(Long.valueOf(20))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(21))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(22))) {
                found3 = true;
            } else if (element.getPutCode().equals(Long.valueOf(25))) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        found1 = found2 = found3 = found4 = false;

        assertNotNull(as.getEmployments());
        assertEquals(4, as.getEmployments().getSummaries().size());

        for (EmploymentSummary element : as.getEmployments().getSummaries()) {
            if (element.getPutCode().equals(Long.valueOf(17))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(18))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(19))) {
                found3 = true;
            } else if (element.getPutCode().equals(Long.valueOf(23))) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        found1 = found2 = found3 = found4 = false;

        assertNotNull(as.getFundings());
        assertEquals(4, as.getFundings().getFundingGroup().size());

        for (FundingGroup group : as.getFundings().getFundingGroup()) {
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary element = group.getFundingSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(10))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found3 = true;
            } else if (element.getPutCode().equals(Long.valueOf(13))) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        found1 = found2 = found3 = found4 = false;

        assertNotNull(as.getPeerReviews());
        assertEquals(4, as.getPeerReviews().getPeerReviewGroup().size());

        for (PeerReviewGroup group : as.getPeerReviews().getPeerReviewGroup()) {
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary element = group.getPeerReviewSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(9))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(10))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                found3 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        found1 = found2 = found3 = found4 = false;

        assertNotNull(as.getWorks());
        assertEquals(4, as.getWorks().getWorkGroup().size());

        for (WorkGroup group : as.getWorks().getWorkGroup()) {
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary element = group.getWorkSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(11))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(13))) {
                found3 = true;
            } else if (element.getPutCode().equals(Long.valueOf(14))) {
                found4 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
    }

    @Test
    public void testCleanEmptyFieldsOnActivities() {
        LastModifiedDate lmd = new LastModifiedDate(DateUtils.convertToXMLGregorianCalendar(System.currentTimeMillis()));
        Works works = new Works();
        works.setLastModifiedDate(lmd);
        WorkGroup group = new WorkGroup();
        group.setLastModifiedDate(lmd);
        for (int i = 0; i < 5; i++) {
            WorkSummary summary = new WorkSummary();
            summary.setLastModifiedDate(lmd);
            WorkTitle title = new WorkTitle();
            title.setTitle(new Title("Work " + i));
            title.setTranslatedTitle(new TranslatedTitle("", ""));
            summary.setTitle(title);
            group.getWorkSummary().add(summary);
        }
        works.getWorkGroup().add(group);
        ActivitiesSummary as = new ActivitiesSummary();
        as.setWorks(works);

        ActivityUtils.cleanEmptyFields(as);

        assertNotNull(as);
        assertNotNull(as.getWorks());
        Utils.verifyLastModified(as.getWorks().getLastModifiedDate());
        assertNotNull(as.getWorks().getWorkGroup());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertNotNull(as.getWorks().getWorkGroup().get(0).getWorkSummary());
        Utils.verifyLastModified(as.getWorks().getWorkGroup().get(0).getLastModifiedDate());
        assertEquals(5, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        for (WorkSummary summary : as.getWorks().getWorkGroup().get(0).getWorkSummary()) {
            Utils.verifyLastModified(summary.getLastModifiedDate());
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            assertTrue(summary.getTitle().getTitle().getContent().startsWith("Work "));
            assertNull(summary.getTitle().getTranslatedTitle());
        }
    }

    private void testActivities(ActivitiesSummary as, String orcid) {
        boolean found1 = false, found2 = false, found3 = false;
        // This is more an utility that will work only for 0000-0000-0000-0003
        assertEquals("0000-0000-0000-0003", orcid);

        assertNotNull(as);
        assertNotNull(as.getPath());
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());      
        Utils.verifyLastModified(as.getLastModifiedDate());
        assertNotNull(as.getEducations());
        assertEquals(3, as.getEducations().getSummaries().size());

        for (EducationSummary element : as.getEducations().getSummaries()) {
            if (element.getPutCode().equals(Long.valueOf(20))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(21))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(22))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getEmployments());
        assertEquals(3, as.getEmployments().getSummaries().size());

        for (EmploymentSummary element : as.getEmployments().getSummaries()) {
            if (element.getPutCode().equals(Long.valueOf(17))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(18))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(19))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getFundings());
        assertEquals(3, as.getFundings().getFundingGroup().size());

        for (FundingGroup group : as.getFundings().getFundingGroup()) {
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary element = group.getFundingSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(10))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getPeerReviews());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());

        for (PeerReviewGroup group : as.getPeerReviews().getPeerReviewGroup()) {
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary element = group.getPeerReviewSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(9))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(10))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getWorks());
        assertEquals(3, as.getWorks().getWorkGroup().size());

        for (WorkGroup group : as.getWorks().getWorkGroup()) {
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary element = group.getWorkSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(11))) {
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(13))) {
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;
    }

    @Test
    public void testViewActitivies_AffiliationsReadLimited_NoSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, "APP-5555555555555556", ScopePathType.AFFILIATIONS_READ_LIMITED);
        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertNotNull(as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());
        // Limited educations
        boolean found1 = false, found2 = false, found3 = false;
        assertNotNull(as.getEducations());
        assertEquals(3, as.getEducations().getSummaries().size());
        for (EducationSummary education : as.getEducations().getSummaries()) {
            Long putCode = education.getPutCode();
            if (putCode == 20L) {
                assertEquals(Visibility.PUBLIC, education.getVisibility());
                found1 = true;
            } else if (putCode == 21L) {
                assertEquals(Visibility.LIMITED, education.getVisibility());
                found2 = true;
            } else if (putCode == 25L) {
                assertEquals(Visibility.LIMITED, education.getVisibility());
                found3 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Limited employments
        found1 = found2 = found3 = false;
        assertNotNull(as.getEmployments());
        assertEquals(3, as.getEmployments().getSummaries().size());

        for (EmploymentSummary employment : as.getEmployments().getSummaries()) {
            Long putCode = employment.getPutCode();
            if (putCode == 17L) {
                assertEquals(Visibility.PUBLIC, employment.getVisibility());
                found1 = true;
            } else if (putCode == 18L) {
                assertEquals(Visibility.LIMITED, employment.getVisibility());
                found2 = true;
            } else if (putCode == 23L) {
                assertEquals(Visibility.LIMITED, employment.getVisibility());
                found3 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility());

        // Only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

        // Only public works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(Long.valueOf(11), as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getVisibility());
    }

    @Test
    public void testViewActitivies_FundingReadLimited_NoSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, "APP-5555555555555556", ScopePathType.FUNDING_READ_LIMITED);
        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertNotNull(as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());
        // Only public educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertEquals(Long.valueOf(20), as.getEducations().getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getEducations().getSummaries().get(0).getVisibility());

        // Only public employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertEquals(Long.valueOf(17), as.getEmployments().getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getEmployments().getSummaries().get(0).getVisibility());

        // Limited funding
        boolean found1 = false, found2 = false, found3 = false;
        assertNotNull(as.getFundings());
        assertEquals(3, as.getFundings().getFundingGroup().size());

        for (FundingGroup group : as.getFundings().getFundingGroup()) {
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary element = group.getFundingSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(10))) {
                assertEquals(Visibility.PUBLIC, element.getVisibility());
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(11))) {
                assertEquals(Visibility.LIMITED, element.getVisibility());
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(13))) {
                assertEquals(Visibility.LIMITED, element.getVisibility());
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

        // Only public works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(Long.valueOf(11), as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getVisibility());
    }

    @Test
    public void testViewActitivies_PeerReviewReadLimited_NoSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, "APP-5555555555555556", ScopePathType.PEER_REVIEW_READ_LIMITED);
        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertNotNull(as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());
        // Only public educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertEquals(Long.valueOf(20), as.getEducations().getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getEducations().getSummaries().get(0).getVisibility());

        // Only public employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertEquals(Long.valueOf(17), as.getEmployments().getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getEmployments().getSummaries().get(0).getVisibility());

        // Only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility());

        // Limited peer reviews
        boolean found1 = false, found2 = false, found3 = false;
        assertNotNull(as.getPeerReviews());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());

        for (PeerReviewGroup group : as.getPeerReviews().getPeerReviewGroup()) {
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary element = group.getPeerReviewSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(9))) {
                assertEquals(Visibility.PUBLIC, element.getVisibility());
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(10))) {
                assertEquals(Visibility.LIMITED, element.getVisibility());
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                assertEquals(Visibility.LIMITED, element.getVisibility());
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);

        // Only public works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(Long.valueOf(11), as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getVisibility());
    }

    @Test
    public void testViewActitivies_WorksReadLimited_NoSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, "APP-5555555555555556", ScopePathType.ORCID_WORKS_READ_LIMITED);
        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertNotNull(as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());
        // Only public educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertEquals(Long.valueOf(20), as.getEducations().getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getEducations().getSummaries().get(0).getVisibility());

        // Only public employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertEquals(Long.valueOf(17), as.getEmployments().getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getEmployments().getSummaries().get(0).getVisibility());

        // Only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility());

        // Only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

        // Limited works
        assertNotNull(as.getWorks());
        assertEquals(3, as.getWorks().getWorkGroup().size());

        boolean found1 = false, found2 = false, found3 = false;

        for (WorkGroup group : as.getWorks().getWorkGroup()) {
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary element = group.getWorkSummary().get(0);
            if (element.getPutCode().equals(Long.valueOf(11))) {
                assertEquals(Visibility.PUBLIC, element.getVisibility());
                found1 = true;
            } else if (element.getPutCode().equals(Long.valueOf(12))) {
                assertEquals(Visibility.LIMITED, element.getVisibility());
                found2 = true;
            } else if (element.getPutCode().equals(Long.valueOf(14))) {
                assertEquals(Visibility.LIMITED, element.getVisibility());
                found3 = true;
            } else {
                fail("Invalid put code " + element.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
    }

    @Test
    public void testViewActitivies_NoReadLimited_NoSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, "APP-5555555555555556", ScopePathType.READ_PUBLIC);
        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());        
        Utils.verifyLastModified(as.getLastModifiedDate());
        // Only public educations
        assertNotNull(as.getEducations());
        assertEquals("/0000-0000-0000-0003/educations", as.getEducations().getPath());       
        assertEquals(1, as.getEducations().getSummaries().size());
        assertEquals(Long.valueOf(20), as.getEducations().getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getEducations().getSummaries().get(0).getVisibility());

        // Only public employments
        assertNotNull(as.getEmployments());
        assertEquals("/0000-0000-0000-0003/employments", as.getEmployments().getPath());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertEquals(Long.valueOf(17), as.getEmployments().getSummaries().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getEmployments().getSummaries().get(0).getVisibility());

        // Only public funding
        assertNotNull(as.getFundings());
        assertEquals("/0000-0000-0000-0003/fundings", as.getFundings().getPath());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility());

        // Only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals("/0000-0000-0000-0003/peer-reviews", as.getPeerReviews().getPath());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

        // Only public works
        assertNotNull(as.getWorks());
        assertEquals("/0000-0000-0000-0003/works", as.getWorks().getPath());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(Long.valueOf(11), as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getVisibility());
    }
}
