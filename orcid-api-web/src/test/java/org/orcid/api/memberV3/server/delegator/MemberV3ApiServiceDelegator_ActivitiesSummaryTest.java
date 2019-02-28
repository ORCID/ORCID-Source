package org.orcid.api.memberV3.server.delegator;

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
import org.orcid.api.common.util.v3.ActivityUtils;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.common.Title;
import org.orcid.jaxb.model.v3.rc2.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.rc2.common.Visibility;
import org.orcid.jaxb.model.v3.rc2.record.Address;
import org.orcid.jaxb.model.v3.rc2.record.Distinction;
import org.orcid.jaxb.model.v3.rc2.record.Education;
import org.orcid.jaxb.model.v3.rc2.record.Employment;
import org.orcid.jaxb.model.v3.rc2.record.Funding;
import org.orcid.jaxb.model.v3.rc2.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc2.record.Keyword;
import org.orcid.jaxb.model.v3.rc2.record.Membership;
import org.orcid.jaxb.model.v3.rc2.record.OtherName;
import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc2.record.Qualification;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc2.record.Service;
import org.orcid.jaxb.model.v3.rc2.record.Work;
import org.orcid.jaxb.model.v3.rc2.record.WorkBulk;
import org.orcid.jaxb.model.v3.rc2.record.WorkTitle;
import org.orcid.jaxb.model.v3.rc2.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.Works;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.v3.Utils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV3ApiServiceDelegator_ActivitiesSummaryTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    // Now on, for any new test, PLAESE USER THIS ORCID ID
    protected final String ORCID = "0000-0000-0000-0003";

    @Resource(name = "memberV3ApiServiceDelegator")
    protected MemberV3ApiServiceDelegator<Distinction, Education, Employment, PersonExternalIdentifier, InvitedPosition, Funding, GroupIdRecord, Membership, OtherName, PeerReview, Qualification, ResearcherUrl, Service, Work, WorkBulk, Address, Keyword, ResearchResource> serviceDelegator;

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
    public void testViewActitivies() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewActivities(ORCID);
        boolean found1 = false, found2 = false, found3 = false;
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());

        assertNotNull(as.getDistinctions());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        for (AffiliationGroup<DistinctionSummary> group : as.getDistinctions().retrieveGroups()) {
            DistinctionSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(30))) {
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(27), group.getActivities().get(1).getPutCode());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(28))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(29))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getEducations());
        assertEquals(3, as.getEducations().retrieveGroups().size());

        for (AffiliationGroup<EducationSummary> group : as.getEducations().retrieveGroups()) {
            EducationSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(25))) {
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(20), group.getActivities().get(1).getPutCode());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(21))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(22))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getEmployments());
        assertEquals(3, as.getEmployments().retrieveGroups().size());

        for (AffiliationGroup<EmploymentSummary> group : as.getEmployments().retrieveGroups()) {
            EmploymentSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(23))) {
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(17), group.getActivities().get(1).getPutCode());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(18))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(19))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getInvitedPositions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        for (AffiliationGroup<InvitedPositionSummary> group : as.getInvitedPositions().retrieveGroups()) {
            InvitedPositionSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(35))) {
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(32), group.getActivities().get(1).getPutCode());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(33))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(34))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getMemberships());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        for (AffiliationGroup<MembershipSummary> group : as.getMemberships().retrieveGroups()) {
            MembershipSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(40))) {
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(37), group.getActivities().get(1).getPutCode());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(38))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(39))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getQualifications());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        for (AffiliationGroup<QualificationSummary> group : as.getQualifications().retrieveGroups()) {
            QualificationSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(45))) {
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(42), group.getActivities().get(1).getPutCode());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(43))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(44))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getServices());
        assertEquals(3, as.getServices().retrieveGroups().size());
        for (AffiliationGroup<ServiceSummary> group : as.getServices().retrieveGroups()) {
            ServiceSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(50))) {
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(47), group.getActivities().get(1).getPutCode());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(48))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(49))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        boolean found4 = false;

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
            for (PeerReviewDuplicateGroup duplicateGroup : group.getPeerReviewGroup()) {
                assertEquals(1, duplicateGroup.getPeerReviewSummary().size());
                PeerReviewSummary element = duplicateGroup.getPeerReviewSummary().get(0);
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
            summary.setTitle(title);
            title.setTranslatedTitle(new TranslatedTitle(""));
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

    @Test
    public void testReadPublicScope_Activities() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID, ScopePathType.READ_PUBLIC);
        // Check you get only public activities
        Response r = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
        String orcid = ORCID;

        boolean found1 = false, found2 = false, found3 = false;
        // This is more an utility that will work only for 0000-0000-0000-0003
        assertEquals("0000-0000-0000-0003", orcid);

        assertNotNull(as);
        assertNotNull(as.getPath());
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());

        assertNotNull(as.getDistinctions());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());

        for (AffiliationGroup<DistinctionSummary> group : as.getDistinctions().retrieveGroups()) {
            DistinctionSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(27))) {
                assertEquals(1, group.getActivities().size());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(28))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(29))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getEducations());
        assertEquals(3, as.getEducations().retrieveGroups().size());

        for (AffiliationGroup<EducationSummary> group : as.getEducations().retrieveGroups()) {
            EducationSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(20))) {
                assertEquals(1, group.getActivities().size());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(21))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(22))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getEmployments());
        assertEquals(3, as.getEmployments().retrieveGroups().size());

        for (AffiliationGroup<EmploymentSummary> group : as.getEmployments().retrieveGroups()) {
            EmploymentSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(17))) {
                assertEquals(1, group.getActivities().size());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(18))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(19))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getInvitedPositions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());

        for (AffiliationGroup<InvitedPositionSummary> group : as.getInvitedPositions().retrieveGroups()) {
            InvitedPositionSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(32))) {
                assertEquals(1, group.getActivities().size());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(33))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(34))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getMemberships());
        assertEquals(3, as.getMemberships().retrieveGroups().size());

        for (AffiliationGroup<MembershipSummary> group : as.getMemberships().retrieveGroups()) {
            MembershipSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(37))) {
                assertEquals(1, group.getActivities().size());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(38))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(39))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getQualifications());
        assertEquals(3, as.getQualifications().retrieveGroups().size());

        for (AffiliationGroup<QualificationSummary> group : as.getQualifications().retrieveGroups()) {
            QualificationSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(42))) {
                assertEquals(1, group.getActivities().size());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(43))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(44))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
            }
        }

        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        found1 = found2 = found3 = false;

        assertNotNull(as.getServices());
        assertEquals(3, as.getServices().retrieveGroups().size());

        for (AffiliationGroup<ServiceSummary> group : as.getServices().retrieveGroups()) {
            ServiceSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode().equals(Long.valueOf(47))) {
                assertEquals(1, group.getActivities().size());
                found1 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(48))) {
                assertEquals(1, group.getActivities().size());
                found2 = true;
            } else if (element0.getPutCode().equals(Long.valueOf(49))) {
                assertEquals(1, group.getActivities().size());
                found3 = true;
            } else {
                fail("Invalid put code " + element0.getPutCode());
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
            for (PeerReviewDuplicateGroup duplicateGroup : group.getPeerReviewGroup()) {
                assertEquals(1, duplicateGroup.getPeerReviewSummary().size());
                PeerReviewSummary element = duplicateGroup.getPeerReviewSummary().get(0);
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

        // Limited distinctions
        boolean found1 = false, found2 = false;

        assertNotNull(as.getDistinctions());
        assertEquals(2, as.getDistinctions().retrieveGroups().size());

        for (AffiliationGroup<DistinctionSummary> group : as.getDistinctions().retrieveGroups()) {
            DistinctionSummary element0 = group.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if (putCode == 30L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(27), group.getActivities().get(1).getPutCode());
                assertEquals(Visibility.PUBLIC, group.getActivities().get(1).getVisibility());
                found1 = true;
            } else if (putCode == 28L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                found2 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Limited educations
        assertNotNull(as.getEducations());
        assertEquals(2, as.getEducations().retrieveGroups().size());
        found1 = found2 = false;

        for (AffiliationGroup<EducationSummary> group : as.getEducations().retrieveGroups()) {
            EducationSummary element0 = group.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if (putCode == 25L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(20), group.getActivities().get(1).getPutCode());
                assertEquals(Visibility.PUBLIC, group.getActivities().get(1).getVisibility());
                found1 = true;
            } else if (putCode == 21L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                found2 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Limited employments
        found1 = found2 = false;
        assertNotNull(as.getEmployments());
        assertEquals(2, as.getEmployments().retrieveGroups().size());

        for (AffiliationGroup<EmploymentSummary> group : as.getEmployments().retrieveGroups()) {
            EmploymentSummary element0 = group.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if (putCode == 23L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(17), group.getActivities().get(1).getPutCode());
                assertEquals(Visibility.PUBLIC, group.getActivities().get(1).getVisibility());
                found1 = true;
            } else if (putCode == 18L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                found2 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Limited invited positions
        found1 = found2 = false;
        assertNotNull(as.getInvitedPositions());
        assertEquals(2, as.getInvitedPositions().retrieveGroups().size());

        for (AffiliationGroup<InvitedPositionSummary> group : as.getInvitedPositions().retrieveGroups()) {
            InvitedPositionSummary element0 = group.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if (putCode == 35L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(32), group.getActivities().get(1).getPutCode());
                assertEquals(Visibility.PUBLIC, group.getActivities().get(1).getVisibility());
                found1 = true;
            } else if (putCode == 33L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                found2 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Limited membership
        found1 = found2 = false;
        assertNotNull(as.getMemberships());
        assertEquals(2, as.getMemberships().retrieveGroups().size());

        for (AffiliationGroup<MembershipSummary> group : as.getMemberships().retrieveGroups()) {
            MembershipSummary element0 = group.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if (putCode == 40L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(37), group.getActivities().get(1).getPutCode());
                assertEquals(Visibility.PUBLIC, group.getActivities().get(1).getVisibility());
                found1 = true;
            } else if (putCode == 38L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                found2 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Limited qualifications
        found1 = found2 = false;
        assertNotNull(as.getQualifications());
        assertEquals(2, as.getQualifications().retrieveGroups().size());

        for (AffiliationGroup<QualificationSummary> group : as.getQualifications().retrieveGroups()) {
            QualificationSummary element0 = group.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if (putCode == 45L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(42), group.getActivities().get(1).getPutCode());
                assertEquals(Visibility.PUBLIC, group.getActivities().get(1).getVisibility());
                found1 = true;
            } else if (putCode == 43L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                found2 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Limited services
        found1 = found2 = false;
        assertNotNull(as.getServices());
        assertEquals(2, as.getServices().retrieveGroups().size());

        for (AffiliationGroup<ServiceSummary> group : as.getServices().retrieveGroups()) {
            ServiceSummary element0 = group.getActivities().get(0);
            Long putCode = element0.getPutCode();
            if (putCode == 50L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(47), group.getActivities().get(1).getPutCode());
                assertEquals(Visibility.PUBLIC, group.getActivities().get(1).getVisibility());
                found1 = true;
            } else if (putCode == 48L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                found2 = true;
            } else {
                fail("Invalid put code " + putCode);
            }
        }

        assertTrue(found1);
        assertTrue(found2);

        // Only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility());

        // Only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

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
        assertEquals(1, as.getEducations().retrieveGroups().size());

        EducationSummary educationSummary = as.getEducations().retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(20), educationSummary.getPutCode());
        assertEquals(Visibility.PUBLIC, educationSummary.getVisibility());

        // Only public employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().retrieveGroups().size());

        EmploymentSummary employmentSummary = as.getEmployments().retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(17), employmentSummary.getPutCode());
        assertEquals(Visibility.PUBLIC, employmentSummary.getVisibility());

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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

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
        assertEquals(1, as.getEducations().retrieveGroups().size());

        EducationSummary educationSummary = as.getEducations().retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(20), educationSummary.getPutCode());
        assertEquals(Visibility.PUBLIC, educationSummary.getVisibility());

        // Only public employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().retrieveGroups().size());

        EmploymentSummary employmentSummary = as.getEmployments().retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(17), employmentSummary.getPutCode());
        assertEquals(Visibility.PUBLIC, employmentSummary.getVisibility());

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
            for (PeerReviewDuplicateGroup duplicateGroup : group.getPeerReviewGroup()) {
                assertEquals(1, duplicateGroup.getPeerReviewSummary().size());
                PeerReviewSummary element = duplicateGroup.getPeerReviewSummary().get(0);
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
        assertEquals(1, as.getEducations().retrieveGroups().size());

        EducationSummary educationSummary = as.getEducations().retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(20), educationSummary.getPutCode());
        assertEquals(Visibility.PUBLIC, educationSummary.getVisibility());

        // Only public employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().retrieveGroups().size());

        EmploymentSummary employmentSummary = as.getEmployments().retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(17), employmentSummary.getPutCode());
        assertEquals(Visibility.PUBLIC, employmentSummary.getVisibility());

        // Only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertEquals(Long.valueOf(10), as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getVisibility());

        // Only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

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
        assertEquals(1, as.getEducations().retrieveGroups().size());

        EducationSummary educationSummary = as.getEducations().retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(20), educationSummary.getPutCode());
        assertEquals(Visibility.PUBLIC, educationSummary.getVisibility());

        // Only public employments
        assertNotNull(as.getEmployments());
        assertEquals("/0000-0000-0000-0003/employments", as.getEmployments().getPath());
        assertEquals(1, as.getEmployments().retrieveGroups().size());

        EmploymentSummary employmentSummary = as.getEmployments().retrieveGroups().iterator().next().getActivities().get(0);
        assertEquals(Long.valueOf(17), employmentSummary.getPutCode());
        assertEquals(Visibility.PUBLIC, employmentSummary.getVisibility());

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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertEquals(Long.valueOf(9), as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getVisibility());

        // Only public works
        assertNotNull(as.getWorks());
        assertEquals("/0000-0000-0000-0003/works", as.getWorks().getPath());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        assertEquals(Long.valueOf(11), as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode());
        assertEquals(Visibility.PUBLIC, as.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getVisibility());
    }
}
