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
package org.orcid.api.memberV2.server.delegator;

import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
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
import org.orcid.jaxb.model.common_rc4.LastModifiedDate;
import org.orcid.jaxb.model.common_rc4.Title;
import org.orcid.jaxb.model.common_rc4.TranslatedTitle;
import org.orcid.jaxb.model.common_rc4.Visibility;
import org.orcid.jaxb.model.groupid_rc4.GroupIdRecord;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc4.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc4.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc4.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc4.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc4.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc4.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc4.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc4.Works;
import org.orcid.jaxb.model.record_rc4.Address;
import org.orcid.jaxb.model.record_rc4.Education;
import org.orcid.jaxb.model.record_rc4.Employment;
import org.orcid.jaxb.model.record_rc4.Funding;
import org.orcid.jaxb.model.record_rc4.Keyword;
import org.orcid.jaxb.model.record_rc4.OtherName;
import org.orcid.jaxb.model.record_rc4.PeerReview;
import org.orcid.jaxb.model.record_rc4.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc4.ResearcherUrl;
import org.orcid.jaxb.model.record_rc4.Work;
import org.orcid.jaxb.model.record_rc4.WorkBulk;
import org.orcid.jaxb.model.record_rc4.WorkTitle;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.orcid.test.helper.Utils;
import org.orcid.utils.DateUtils;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegator_ActivitiesSummaryTest extends DBUnitTest {
    protected static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
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

        // Check that lists returns only PUBLIC elements
        /**
         * ACTIVITIES
         */
        try {
            // Check you get only public activities
            Response r = serviceDelegator.viewActivities(ORCID);
            ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
            testActivities(as, ORCID);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testViewActitivies() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4446");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getPath());
        Utils.verifyLastModified(summary.getLastModifiedDate());
        // Check works
        assertNotNull(summary.getWorks());
        Utils.verifyLastModified(summary.getWorks().getLastModifiedDate());
        assertEquals(3, summary.getWorks().getWorkGroup().size());
        boolean foundPrivateWork = false;
        for (WorkGroup group : summary.getWorks().getWorkGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getWorkSummary());
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary work = group.getWorkSummary().get(0);
            Utils.verifyLastModified(work.getLastModifiedDate());
            assertThat(work.getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(6)), is(Long.valueOf(7))));
            assertThat(work.getPath(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
            assertThat(work.getTitle().getTitle().getContent(), anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
            if (work.getPutCode().equals(Long.valueOf(7))) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
                foundPrivateWork = true;
            }
        }
        assertTrue(foundPrivateWork);

        // Check fundings
        assertNotNull(summary.getFundings());
        Utils.verifyLastModified(summary.getFundings().getLastModifiedDate());
        assertEquals(3, summary.getFundings().getFundingGroup().size());
        boolean foundPrivateFunding = false;
        for (FundingGroup group : summary.getFundings().getFundingGroup()) {
            assertNotNull(group.getFundingSummary());
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary funding = group.getFundingSummary().get(0);
            Utils.verifyLastModified(funding.getLastModifiedDate());
            assertThat(funding.getPutCode(), anyOf(is(Long.valueOf(4)), is(Long.valueOf(5)), is(Long.valueOf(8))));
            assertThat(funding.getPath(), anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5"), is("/4444-4444-4444-4446/funding/8")));
            assertThat(funding.getTitle().getTitle().getContent(), anyOf(is("Private Funding"), is("Public Funding"), is("Limited Funding")));
            if (funding.getPutCode().equals(4L)) {
                assertEquals(Visibility.PRIVATE, funding.getVisibility());
                foundPrivateFunding = true;
            }
        }

        assertTrue(foundPrivateFunding);

        // Check Educations
        assertNotNull(summary.getEducations());
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(3, summary.getEducations().getSummaries().size());

        boolean foundPrivateEducation = false;
        for (EducationSummary education : summary.getEducations().getSummaries()) {
            Utils.verifyLastModified(education.getLastModifiedDate());
            assertThat(education.getPutCode(), anyOf(is(Long.valueOf(6)), is(Long.valueOf(7)), is(Long.valueOf(9))));
            assertThat(education.getPath(),
                    anyOf(is("/4444-4444-4444-4446/education/6"), is("/4444-4444-4444-4446/education/7"), is("/4444-4444-4444-4446/education/9")));
            assertThat(education.getDepartmentName(), anyOf(is("Education Dept # 1"), is("Education Dept # 2"), is("Education Dept # 3")));

            if (education.getPutCode().equals(6L)) {
                assertEquals(Visibility.PRIVATE, education.getVisibility());
                foundPrivateEducation = true;
            }
        }

        assertTrue(foundPrivateEducation);

        // Check Employments
        assertNotNull(summary.getEmployments());
        Utils.verifyLastModified(summary.getEmployments().getLastModifiedDate());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(3, summary.getEmployments().getSummaries().size());

        boolean foundPrivateEmployment = false;

        for (EmploymentSummary employment : summary.getEmployments().getSummaries()) {
            Utils.verifyLastModified(employment.getLastModifiedDate());
            assertThat(employment.getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(8)), is(Long.valueOf(11))));
            assertThat(employment.getPath(),
                    anyOf(is("/4444-4444-4444-4446/employment/5"), is("/4444-4444-4444-4446/employment/8"), is("/4444-4444-4444-4446/employment/11")));
            assertThat(employment.getDepartmentName(), anyOf(is("Employment Dept # 1"), is("Employment Dept # 2"), is("Employment Dept # 4")));
            if (employment.getPutCode().equals(5L)) {
                assertEquals(Visibility.PRIVATE, employment.getVisibility());
                foundPrivateEmployment = true;
            }
        }

        assertTrue(foundPrivateEmployment);

        // Check Peer reviews
        assertNotNull(summary.getPeerReviews());
        Utils.verifyLastModified(summary.getPeerReviews().getLastModifiedDate());
        assertEquals(3, summary.getPeerReviews().getPeerReviewGroup().size());

        boolean foundPrivatePeerReview = false;
        for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            assertNotNull(group.getPeerReviewSummary());
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary peerReview = group.getPeerReviewSummary().get(0);
            Utils.verifyLastModified(peerReview.getLastModifiedDate());
            assertThat(peerReview.getPutCode(), anyOf(is(Long.valueOf(1)), is(Long.valueOf(3)), is(Long.valueOf(4))));
            assertThat(peerReview.getGroupId(), anyOf(is("issn:0000001"), is("issn:0000002"), is("issn:0000003")));
            if (peerReview.getPutCode().equals(4L)) {
                assertEquals(Visibility.PRIVATE, peerReview.getVisibility());
                foundPrivatePeerReview = true;
            }
        }

        assertTrue(foundPrivatePeerReview);
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
}
