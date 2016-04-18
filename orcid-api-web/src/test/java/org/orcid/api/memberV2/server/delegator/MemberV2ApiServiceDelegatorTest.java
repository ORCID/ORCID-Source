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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.core.AnyOf.anyOf;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.NoResultException;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.api.common.util.ActivityUtils;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_rc2.Country;
import org.orcid.jaxb.model.common_rc2.Iso3166Country;
import org.orcid.jaxb.model.common_rc2.Organization;
import org.orcid.jaxb.model.common_rc2.OrganizationAddress;
import org.orcid.jaxb.model.common_rc2.Subtitle;
import org.orcid.jaxb.model.common_rc2.Title;
import org.orcid.jaxb.model.common_rc2.TranslatedTitle;
import org.orcid.jaxb.model.common_rc2.Url;
import org.orcid.jaxb.model.common_rc2.Visibility;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecord;
import org.orcid.jaxb.model.groupid_rc2.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc2.EducationSummary;
import org.orcid.jaxb.model.record.summary_rc2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_rc2.FundingGroup;
import org.orcid.jaxb.model.record.summary_rc2.FundingSummary;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc2.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc2.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc2.Works;
import org.orcid.jaxb.model.record_rc2.Address;
import org.orcid.jaxb.model.record_rc2.Addresses;
import org.orcid.jaxb.model.record_rc2.Citation;
import org.orcid.jaxb.model.record_rc2.Education;
import org.orcid.jaxb.model.record_rc2.Email;
import org.orcid.jaxb.model.record_rc2.Emails;
import org.orcid.jaxb.model.record_rc2.Employment;
import org.orcid.jaxb.model.record_rc2.ExternalID;
import org.orcid.jaxb.model.record_rc2.ExternalIDType;
import org.orcid.jaxb.model.record_rc2.ExternalIDs;
import org.orcid.jaxb.model.record_rc2.Funding;
import org.orcid.jaxb.model.record_rc2.FundingTitle;
import org.orcid.jaxb.model.record_rc2.FundingType;
import org.orcid.jaxb.model.record_rc2.Keyword;
import org.orcid.jaxb.model.record_rc2.Keywords;
import org.orcid.jaxb.model.record_rc2.OtherName;
import org.orcid.jaxb.model.record_rc2.OtherNames;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.jaxb.model.record_rc2.PeerReviewType;
import org.orcid.jaxb.model.record_rc2.Person;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifier;
import org.orcid.jaxb.model.record_rc2.PersonExternalIdentifiers;
import org.orcid.jaxb.model.record_rc2.Relationship;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.jaxb.model.record_rc2.Role;
import org.orcid.jaxb.model.record_rc2.Work;
import org.orcid.jaxb.model.record_rc2.WorkTitle;
import org.orcid.jaxb.model.record_rc2.WorkType;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml",
            "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml",
            "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml", "/data/RecordNameEntityData.xml", "/data/BiographyEntityData.xml");

    @Resource(name = "memberV2ApiServiceDelegator")
    private MemberV2ApiServiceDelegator<Education, Employment, PersonExternalIdentifier, Funding, GroupIdRecord, OtherName, PeerReview, ResearcherUrl, Work, Address, Keyword> serviceDelegator;

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
    @SuppressWarnings("unchecked")
    public void testViewActitivies() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4446");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        
        // Check works
        assertNotNull(summary.getWorks());
        assertEquals(3, summary.getWorks().getWorkGroup().size());
        boolean foundPrivateWork = false;
        for(WorkGroup group : summary.getWorks().getWorkGroup()) {
            assertNotNull(group.getWorkSummary());
            assertEquals(1, group.getWorkSummary().size());
            WorkSummary work = group.getWorkSummary().get(0);
            assertThat(work.getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(6)), is(Long.valueOf(7))));
            assertThat(work.getPath(),
                    anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
            assertThat(work.getTitle().getTitle().getContent(),
                    anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
            if(work.getPutCode().equals(Long.valueOf(7))) {
                assertEquals(Visibility.PRIVATE, work.getVisibility());
                foundPrivateWork = true;
            } 
        }
        
        assertTrue(foundPrivateWork);
        
        // Check fundings
        assertNotNull(summary.getFundings());
        assertEquals(3, summary.getFundings().getFundingGroup().size());
        boolean foundPrivateFunding = false;
        for (FundingGroup group : summary.getFundings().getFundingGroup()) {
            assertNotNull(group.getFundingSummary());
            assertEquals(1, group.getFundingSummary().size());
            FundingSummary funding = group.getFundingSummary().get(0);
            assertThat(funding.getPutCode(), anyOf(is(Long.valueOf(4)), is(Long.valueOf(5)), is(Long.valueOf(8))));
            assertThat(funding.getPath(),
                    anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5"), is("/4444-4444-4444-4446/funding/8")));
            assertThat(funding.getTitle().getTitle().getContent(),
                    anyOf(is("Private Funding"), is("Public Funding"), is("Limited Funding")));
            if(funding.getPutCode().equals(4L)) {
                assertEquals(Visibility.PRIVATE, funding.getVisibility());                
                foundPrivateFunding = true;
            }
        }
        
        assertTrue(foundPrivateFunding);
        
        // Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(3, summary.getEducations().getSummaries().size());
        
        boolean foundPrivateEducation = false;
        for(EducationSummary education : summary.getEducations().getSummaries()) {
            assertThat(education.getPutCode(), anyOf(is(Long.valueOf(6)), is(Long.valueOf(7)), is(Long.valueOf(9))));
            assertThat(education.getPath(),
                    anyOf(is("/4444-4444-4444-4446/education/6"), is("/4444-4444-4444-4446/education/7"), is("/4444-4444-4444-4446/education/9")));
            assertThat(education.getDepartmentName(),
                    anyOf(is("Education Dept # 1"), is("Education Dept # 2"), is("Education Dept # 3")));

            if(education.getPutCode().equals(6L)) {
                assertEquals(Visibility.PRIVATE, education.getVisibility());
                foundPrivateEducation = true;
            }
        }
        
        assertTrue(foundPrivateEducation);
        
        // Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(3, summary.getEmployments().getSummaries().size());
        
        boolean foundPrivateEmployment = false;
        
        for(EmploymentSummary employment : summary.getEmployments().getSummaries()) {
            assertThat(employment.getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(8)), is(Long.valueOf(11))));
            assertThat(employment.getPath(),
                    anyOf(is("/4444-4444-4444-4446/employment/5"), is("/4444-4444-4444-4446/employment/8"), is("/4444-4444-4444-4446/employment/11")));
            assertThat(employment.getDepartmentName(),
                    anyOf(is("Employment Dept # 1"), is("Employment Dept # 2"), is("Employment Dept # 4")));
            if(employment.getPutCode().equals(5L)) {
                assertEquals(Visibility.PRIVATE, employment.getVisibility());
                foundPrivateEmployment = true;
            }
        }       
        
        assertTrue(foundPrivateEmployment);
        
        // Check Peer reviews
        assertNotNull(summary.getPeerReviews());
        assertEquals(3, summary.getPeerReviews().getPeerReviewGroup().size());
        
        boolean foundPrivatePeerReview = false;
        for(PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            assertNotNull(group.getPeerReviewSummary());
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary peerReview = group.getPeerReviewSummary().get(0);
            assertThat(peerReview.getPutCode(), anyOf(is(Long.valueOf(1)), is(Long.valueOf(3)), is(Long.valueOf(4))));
            assertThat(peerReview.getGroupId(), anyOf(is("issn:0000001"), is("issn:0000002"), is("issn:0000003")));
            if(peerReview.getPutCode().equals(4L)) {
                assertEquals(Visibility.PRIVATE, peerReview.getVisibility());
                foundPrivatePeerReview = true;
            }
        }
        
        assertTrue(foundPrivatePeerReview);
    }

    @Test
    public void testCleanEmptyFieldsOnActivities() {
        Works works = new Works();
        WorkGroup group = new WorkGroup();
        for (int i = 0; i < 5; i++) {
            WorkSummary summary = new WorkSummary();
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
        assertNotNull(as.getWorks().getWorkGroup());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertNotNull(as.getWorks().getWorkGroup().get(0).getWorkSummary());
        assertEquals(5, as.getWorks().getWorkGroup().get(0).getWorkSummary().size());
        for (WorkSummary summary : as.getWorks().getWorkGroup().get(0).getWorkSummary()) {
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            assertTrue(summary.getTitle().getTitle().getContent().startsWith("Work "));
            assertNull(summary.getTitle().getTranslatedTitle());
        }

    }

    /**
     * TEST WORKS
     */
    @Test
    public void testViewPublicWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article A", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(5), work.getPutCode());
        assertEquals("/4444-4444-4444-4446/work/5", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.PUBLIC.value(), work.getVisibility().value());
    }

    @Test
    public void testViewLimitedWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", 6L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article B", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(6), work.getPutCode());
        assertEquals("/4444-4444-4444-4446/work/6", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.LIMITED.value(), work.getVisibility().value());
    }

    @Test
    public void testViewPrivateWork() {
        // Use the smallest scope in the pyramid to verify that you can read
        // your own limited and private data
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", 7L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article C", work.getWorkTitle().getTitle().getContent());
        assertEquals(Long.valueOf(7), work.getPutCode());
        assertEquals("/4444-4444-4444-4446/work/7", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
        assertEquals(Visibility.PRIVATE.value(), work.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateWorkYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork("4444-4444-4444-4446", 8L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewWorkThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        serviceDelegator.viewWork("4444-4444-4444-4443", 5L);
        fail();
    }

    @Test
    public void testCleanEmptyFieldsOnWorks() {
        Work work = new Work();
        work.setWorkCitation(new Citation(""));
        WorkTitle title = new WorkTitle();
        title.setTitle(new Title("My Work"));
        title.setSubtitle(new Subtitle("My subtitle"));
        title.setTranslatedTitle(new TranslatedTitle("", ""));
        work.setWorkTitle(title);

        ActivityUtils.cleanEmptyFields(work);

        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertNotNull(work.getWorkTitle().getSubtitle());
        assertEquals("My Work", work.getWorkTitle().getTitle().getContent());
        assertEquals("My subtitle", work.getWorkTitle().getSubtitle().getContent());

        assertNull(work.getWorkCitation());
        assertNull(work.getWorkTitle().getTranslatedTitle());
    }

    @Test
    public void testAddWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4445", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4445");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        // Check works
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getWorkGroup());
        assertEquals(1, summary.getWorks().getWorkGroup().size());
        assertNotNull(summary.getWorks().getWorkGroup().get(0));
        assertNotNull(summary.getWorks().getWorkGroup().get(0).getWorkSummary());
        assertEquals(1, summary.getWorks().getWorkGroup().get(0).getWorkSummary().size());

        Work work = getWork();
       
        response = serviceDelegator.createWork("4444-4444-4444-4445", work);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewActivities("4444-4444-4444-4445");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        // Check works
        assertNotNull(summary.getWorks());
        assertNotNull(summary.getWorks().getWorkGroup());
        assertEquals(2, summary.getWorks().getWorkGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (WorkGroup group : summary.getWorks().getWorkGroup()) {
            assertNotNull(group.getWorkSummary());
            assertNotNull(group.getWorkSummary().get(0));
            WorkSummary workSummary = group.getWorkSummary().get(0);
            assertNotNull(workSummary.getTitle());
            assertNotNull(workSummary.getTitle().getTitle());
            if ("A Book With Contributors JSON".equals(workSummary.getTitle().getTitle().getContent())) {
                haveOld = true;
            } else if ("A new work!".equals(workSummary.getTitle().getTitle().getContent())) {
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test
    public void testUpdateWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Long.valueOf(1), work.getPutCode());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("A day in the life", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.BOOK, work.getWorkType());
        assertEquals(Visibility.PUBLIC, work.getVisibility());
        
        work.setWorkType(WorkType.EDITED_BOOK);
        work.getWorkTitle().getTitle().setContent("Updated work title");

        response = serviceDelegator.updateWork("4444-4444-4444-4443", 1L, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewWork("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Long.valueOf(1), work.getPutCode());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Updated work title", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.EDITED_BOOK, work.getWorkType());

        // Rollback changes so we dont break other tests
        work.setWorkType(WorkType.BOOK);
        work.getWorkTitle().getTitle().setContent("A day in the life");
        response = serviceDelegator.updateWork("4444-4444-4444-4443", 1L, work);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateWorkYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertEquals(Long.valueOf(2), work.getPutCode());
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Another day in the life", work.getWorkTitle().getTitle().getContent());
        assertEquals(WorkType.BOOK, work.getWorkType());

        work.setWorkType(WorkType.EDITED_BOOK);
        work.getWorkTitle().getTitle().setContent("Updated work title");

        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.PART_OF);
        extId.setType(ExternalIDType.AGR.value());
        extId.setValue("ext-id-" + System.currentTimeMillis());
        extId.setUrl(new Url("http://thisIsANewUrl.com"));
        
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);

        serviceDelegator.updateWork("4444-4444-4444-4443", 2L, work);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateWorkChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4447", 10L);
        assertNotNull(response);
        Work work = (Work) response.getEntity(); 
        assertNotNull(work);
        assertEquals(Visibility.PUBLIC, work.getVisibility());
        
        work.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateWork("4444-4444-4444-4447", 10L, work);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testDeleteWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4447", 9L);
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);

        response = serviceDelegator.deleteWork("4444-4444-4444-4447", 9L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewWork("4444-4444-4444-4447", 9L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteWorkYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteWork("4444-4444-4444-4446", 8L);
        fail();
    }

    /**
     * TEST FUNDINGS
     */
    @Test
    public void testViewPublicFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(5), funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/5", funding.getPath());
        assertEquals("Public Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PUBLIC.value(), funding.getVisibility().value());
    }

    @Test
    public void testViewLimitedFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(1), funding.getPutCode());
        assertEquals("/4444-4444-4444-4443/funding/1", funding.getPath());
        assertEquals("Grant # 1", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.LIMITED.value(), funding.getVisibility().value());
    }

    @Test
    public void testViewPrivateFunding() {
        // Use the smallest scope in the pyramid to verify that you can read
        // your own limited and private data
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 4L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals(Long.valueOf(4), funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/4", funding.getPath());
        assertEquals("Private Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateFundingWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        serviceDelegator.viewFunding("4444-4444-4444-4443", 3L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewFundingThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Funding 1 belongs to 4444-4444-4444-4443
        serviceDelegator.viewFunding("4444-4444-4444-4446", 1L);
        fail();
    }

    @Test
    public void testAddFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertNotNull(summary.getFundings().getFundingGroup().get(0));
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary());
        assertEquals(1, summary.getFundings().getFundingGroup().get(0).getFundingSummary().size());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0));
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle());
        assertNotNull(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle());
        assertEquals("Public Funding # 1", summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent());

        Funding newFunding = getFunding();

        response = serviceDelegator.createFunding("4444-4444-4444-4447", newFunding);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getFundings());
        assertNotNull(summary.getFundings().getFundingGroup());
        assertEquals(2, summary.getFundings().getFundingGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (FundingGroup group : summary.getFundings().getFundingGroup()) {
            assertNotNull(group.getFundingSummary().get(0));
            assertNotNull(group.getFundingSummary().get(0).getTitle());
            assertNotNull(group.getFundingSummary().get(0).getTitle().getTitle());
            assertNotNull(group.getFundingSummary().get(0).getTitle().getTitle().getContent());
            if ("Public Funding # 1".equals(group.getFundingSummary().get(0).getTitle().getTitle().getContent())) {
                haveOld = true;
            } else if ("Public Funding # 2".equals(group.getFundingSummary().get(0).getTitle().getTitle().getContent())) {
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test
    public void testUpdateFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals("Public Funding # 1", funding.getTitle().getTitle().getContent());
        assertEquals("This is the description for funding with id 6", funding.getDescription());

        funding.getTitle().getTitle().setContent("Updated funding title");
        funding.setDescription("This is an updated description");
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(ExternalIDType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        funding.setExternalIdentifiers(fExtIds);

        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals("Updated funding title", funding.getTitle().getTitle().getContent());
        assertEquals("This is an updated description", funding.getDescription());

        // Rollback changes
        funding.getTitle().getTitle().setContent("Public Funding # 1");
        funding.setDescription("This is the description for funding with id 6");

        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateFundingYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);

        funding.getTitle().getTitle().setContent("Updated funding title");
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(ExternalIDType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        funding.setExternalIdentifiers(fExtIds);

        serviceDelegator.updateFunding("4444-4444-4444-4446", 5L, funding);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateFundingChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertEquals(Visibility.PUBLIC, funding.getVisibility());
        
        funding.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateFunding("4444-4444-4444-4447", 6L, funding);
        fail();       
    }

    @Test(expected = NoResultException.class)
    public void testDeleteFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4442", 7L);
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);

        response = serviceDelegator.deleteFunding("4444-4444-4444-4442", 7L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewFunding("4444-4444-4444-4442", 7L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteFundingYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteFunding("4444-4444-4444-4446", 5L);
        fail();
    }

    /**
     * TEST EDUCATIONS
     */
    @Test
    public void testViewPublicEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", 7L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Long.valueOf(7L), education.getPutCode());
        assertEquals("/4444-4444-4444-4446/education/7", education.getPath());
        assertEquals("Education Dept # 2", education.getDepartmentName());
        assertEquals(Visibility.PUBLIC.value(), education.getVisibility().value());
    }

    @Test
    public void testViewLimitedEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", 9L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Long.valueOf(9L), education.getPutCode());
        assertEquals("/4444-4444-4444-4446/education/9", education.getPath());
        assertEquals("Education Dept # 3", education.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), education.getVisibility().value());
    }

    @Test
    public void testViewPrivateEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", 6L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Long.valueOf(6L), education.getPutCode());
        assertEquals("/4444-4444-4444-4446/education/6", education.getPath());
        assertEquals("Education Dept # 1", education.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), education.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEducationWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEducation("4444-4444-4444-4446", 10L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEducationThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        // Education 1 belongs to 4444-4444-4444-4442
        serviceDelegator.viewEducation("4444-4444-4444-4446", 1L);
        fail();
    }

    @Test
    public void testAddEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4442");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertNotNull(summary.getEducations().getSummaries().get(0));
        assertEquals(Long.valueOf(1), summary.getEducations().getSummaries().get(0).getPutCode());

        Education education = new Education();
        education.setDepartmentName("My department name");
        education.setRoleTitle("My Role");
        education.setOrganization(getOrganization());

        response = serviceDelegator.createEducation("4444-4444-4444-4442", education);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities("4444-4444-4444-4442");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());

        boolean haveOld = false;
        boolean haveNew = false;

        for (EducationSummary educationSummary : summary.getEducations().getSummaries()) {
            assertNotNull(educationSummary.getPutCode());
            if (educationSummary.getPutCode() == 1L) {
                assertEquals("A Department", educationSummary.getDepartmentName());
                haveOld = true;
            } else {
                assertEquals(putCode, educationSummary.getPutCode());
                assertEquals("My department name", educationSummary.getDepartmentName());
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test
    public void testUpdateEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4443", 3L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals("Another Department", education.getDepartmentName());
        assertEquals("Student", education.getRoleTitle());

        education.setDepartmentName("Updated department name");
        education.setRoleTitle("The updated role title");

        response = serviceDelegator.updateEducation("4444-4444-4444-4443", 3L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewEducation("4444-4444-4444-4443", 3L);
        assertNotNull(response);
        education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals("Updated department name", education.getDepartmentName());
        assertEquals("The updated role title", education.getRoleTitle());

        // Rollback changes
        education.setDepartmentName("Another Department");
        education.setRoleTitle("Student");

        response = serviceDelegator.updateEducation("4444-4444-4444-4443", 3L, education);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEducationYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        education.setDepartmentName("Updated department name");
        education.setRoleTitle("The updated role title");
        serviceDelegator.updateEducation("4444-4444-4444-4442", 1L, education);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEducationChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4443", 3L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals(Visibility.PUBLIC, education.getVisibility());

        education.setVisibility(education.getVisibility().equals(Visibility.PRIVATE) ? Visibility.LIMITED : Visibility.PRIVATE);
        
        response = serviceDelegator.updateEducation("4444-4444-4444-4443", 3L, education);
        fail();
    }
    
    @Test(expected = NoResultException.class)
    public void testDeleteEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4447", 12L);
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);

        response = serviceDelegator.deleteAffiliation("4444-4444-4444-4447", 12L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewEducation("4444-4444-4444-4447", 12L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEducationYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation("4444-4444-4444-4446", 9L);
        fail();
    }

    /**
     * TEST EMPLOYMENTS
     */
    @Test
    public void testViewEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Long.valueOf(5L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/5", employment.getPath());
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }

    @Test
    public void testViewLimitedEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 11L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Long.valueOf(11L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/11", employment.getPath());
        assertEquals("Employment Dept # 4", employment.getDepartmentName());
        assertEquals(Visibility.LIMITED.value(), employment.getVisibility().value());
    }

    @Test
    public void testViewPrivateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Long.valueOf(5L), employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/5", employment.getPath());
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateEmploymentWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 10L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewEmploymentThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 4L);
        fail();
    }

    @Test
    public void testAddEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertNotNull(summary.getEmployments().getSummaries().get(0));
        assertEquals(Long.valueOf(13), summary.getEmployments().getSummaries().get(0).getPutCode());

        Employment employment = new Employment();
        employment.setDepartmentName("My department name");
        employment.setRoleTitle("My Role");
        employment.setOrganization(getOrganization());

        response = serviceDelegator.createEmployment("4444-4444-4444-4447", employment);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities("4444-4444-4444-4447");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());

        boolean haveOld = false;
        boolean haveNew = false;

        for (EmploymentSummary EmploymentSummary : summary.getEmployments().getSummaries()) {
            assertNotNull(EmploymentSummary.getPutCode());
            if (EmploymentSummary.getPutCode() == 13L) {
                assertEquals("Employment Dept # 1", EmploymentSummary.getDepartmentName());
                haveOld = true;
            } else {
                assertEquals(putCode, EmploymentSummary.getPutCode());
                assertEquals("My department name", EmploymentSummary.getDepartmentName());
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
    }

    @Test
    public void testUpdateEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals("Researcher", employment.getRoleTitle());

        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");

        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals("Updated department name", employment.getDepartmentName());
        assertEquals("The updated role title", employment.getRoleTitle());

        // Rollback changes
        employment.setDepartmentName("Employment Dept # 1");
        employment.setRoleTitle("Researcher");

        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateEmploymentYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 11L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        employment.setDepartmentName("Updated department name");
        employment.setRoleTitle("The updated role title");
        serviceDelegator.updateEmployment("4444-4444-4444-4446", 11L, employment);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateEmploymentChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);
        assertEquals(Visibility.PRIVATE, employment.getVisibility());

        employment.setVisibility(Visibility.LIMITED);
        
        response = serviceDelegator.updateEmployment("4444-4444-4444-4446", 5L, employment);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testDeleteEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4444", 14L);
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);

        response = serviceDelegator.deleteAffiliation("4444-4444-4444-4444", 14L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        serviceDelegator.viewEmployment("4444-4444-4444-4444", 14L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteEmploymentYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deleteAffiliation("4444-4444-4444-4446", 11L);
        fail();
    }

    /**
     * TEST PEER REVIEWS
     */
    @Test
    public void testViewPublicPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 1L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(1L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("reviewer", peerReview.getRole().value());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());
        assertEquals("review", peerReview.getType().value());
        assertEquals("http://peer_review.com", peerReview.getUrl().getValue());
        assertEquals("Peer Review # 1", peerReview.getSubjectName().getTitle().getContent());
        assertEquals("es", peerReview.getSubjectName().getTranslatedTitle().getLanguageCode());
        assertEquals("artistic-performance", peerReview.getSubjectType().value());
        assertEquals("http://work.com", peerReview.getSubjectUrl().getValue());
        assertEquals("Peer Review # 1 container name", peerReview.getSubjectContainerName().getContent());
        assertEquals("peer-review:subject-external-identifier-id#1", peerReview.getSubjectExternalIdentifier().getValue());
        assertEquals("agr", peerReview.getSubjectExternalIdentifier().getType());
        assertEquals("issn:0000001", peerReview.getGroupId());
    }

    @Test
    public void testViewLimitedPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 3L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(3L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#2", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("limited", peerReview.getVisibility().value());
        assertEquals("issn:0000002", peerReview.getGroupId());
    }

    @Test
    public void testViewPrivatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 4L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(4L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#3", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("private", peerReview.getVisibility().value());
        assertEquals("issn:0000003", peerReview.getGroupId());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivatePeerReviewWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4446", 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewPeerReviewThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewPeerReview("4444-4444-4444-4446", 2L);
        fail();
    }

    @Test
    public void testViewPeerReviewSummary() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReviewSummary("4444-4444-4444-4446", Long.valueOf(1));
        assertNotNull(response);
        PeerReviewSummary peerReview = (PeerReviewSummary) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf("1"), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());
    }

    @Test
    public void testUpdatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        PeerReview updatedPeerReview = (PeerReview) response.getEntity();
        assertNotNull(updatedPeerReview);
        assertEquals("http://updated.com/url", updatedPeerReview.getUrl().getValue());
        assertEquals("Updated Title", updatedPeerReview.getSubjectName().getTitle().getContent());
    }

    @Test
    public void testUpdatePeerReviewWhenYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());

        // Update the info
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        peerReview.getExternalIdentifiers().getExternalIdentifier().iterator().next().setValue("different");
        
        try {
            response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 2L, peerReview);
            fail();
        } catch (WrongSourceException wse) {

        }
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", Long.valueOf(2));
        peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());
    }
    
    @Test(expected = VisibilityMismatchException.class)    
    public void testUpdatePeerReviewChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());
        
        peerReview.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        fail();
    }        
    
    @Test
    public void testAddPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4444");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(1, summary.getPeerReviews().getPeerReviewGroup().size());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0));
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0));
        assertEquals("issn:0000001", summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());

        PeerReview peerReview = getPeerReview();

        response = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewActivities("4444-4444-4444-4444");
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(2, summary.getPeerReviews().getPeerReviewGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            if ("issn:0000001".equals(group.getPeerReviewSummary().get(0).getGroupId())) {
                haveOld = true;
            } else {
                assertEquals("issn:0000003", group.getPeerReviewSummary().get(0).getGroupId());
                haveNew = true;
            }
        }

        assertTrue(haveOld);
        assertTrue(haveNew);
        
        //Delete the new so it doesn't affect other tests
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode);
    }
    
    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddPeerReviewDuplicateFails() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        
        peerReview.setPutCode(null);
        
        response = serviceDelegator.createPeerReview("4444-4444-4444-4447", peerReview);
    }
    
    @Test
    public void testAddPeerReviewWithSameExtIdValueButDifferentExtIdType() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);

        PeerReview peerReview1 = new PeerReview();
        ExternalIDs weis1 = new ExternalIDs();
        ExternalID wei1 = new ExternalID();
        wei1.setRelationship(null);
        wei1.setValue("same_but_different_type");
        wei1.setType(ExternalIDType.DOI.value());
        weis1.getExternalIdentifier().add(wei1);
        peerReview1.setExternalIdentifiers(weis1);
        peerReview1.setGroupId("issn:0000003");
        peerReview1.setOrganization(getOrganization());
        peerReview1.setRole(Role.CHAIR);
        peerReview1.setSubjectContainerName(new Title("subject-container-name"));
        peerReview1.setSubjectExternalIdentifier(wei1);
        WorkTitle workTitle1 = new WorkTitle();
        workTitle1.setTitle(new Title("work-title"));
        peerReview1.setSubjectName(workTitle1);
        peerReview1.setSubjectType(WorkType.DATA_SET);
        peerReview1.setType(PeerReviewType.EVALUATION);

        Response response1 = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview1);
        assertNotNull(response1);
        assertEquals(Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Map<?, ?> map = response1.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode1 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
                
        PeerReview peerReview2 = new PeerReview();
        ExternalIDs weis2 = new ExternalIDs();
        ExternalID wei2 = new ExternalID();
        wei2.setRelationship(null);
        wei2.setValue("same_but_different_type"); // Same value
        wei2.setType(ExternalIDType.ARXIV.value()); // But different type
        weis2.getExternalIdentifier().add(wei2);
        peerReview2.setExternalIdentifiers(weis2);
        peerReview2.setGroupId("issn:0000003");
        peerReview2.setOrganization(getOrganization());
        peerReview2.setRole(Role.CHAIR);
        peerReview2.setSubjectContainerName(new Title("subject-container-name"));
        peerReview2.setSubjectExternalIdentifier(wei2);
        WorkTitle workTitle2 = new WorkTitle();
        workTitle2.setTitle(new Title("work-title"));
        peerReview2.setSubjectName(workTitle2);
        peerReview2.setSubjectType(WorkType.DATA_SET);
        peerReview2.setType(PeerReviewType.EVALUATION);

        Response response2 = serviceDelegator.createPeerReview("4444-4444-4444-4444", peerReview2);
        assertNotNull(response2);
        assertEquals(Response.Status.CREATED.getStatusCode(), response2.getStatus());
        map = response2.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        resultWithPutCode = (List<?>) map.get("Location");
        Long putCode2 = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
                
        //Delete new peer reviews so they don't affect other tests
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode1);
        serviceDelegator.deletePeerReview("4444-4444-4444-4444", putCode2);        
    }

    @Test
    public void testDeletePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        PeerReview review = (PeerReview) response.getEntity();
        assertNotNull(review);
        assertNotNull(review.getSubjectName());
        assertNotNull(review.getSubjectName().getTitle());
        assertEquals("Peer Review # 3", review.getSubjectName().getTitle().getContent());

        response = serviceDelegator.deletePeerReview("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

    }

    @Test(expected = WrongSourceException.class)
    public void testDeletePeerReviewYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        serviceDelegator.deletePeerReview("4444-4444-4444-4447", 2L);
        fail();
    }

    /**
     * TEST GROUP ID
     */
    @Test
    public void testGetGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.viewGroupIdRecord(Long.valueOf("2"));
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        assertEquals(Long.valueOf(2), groupIdRecord.getPutCode());
        assertEquals("issn:0000002", groupIdRecord.getGroupId());
        assertEquals("TestGroup2", groupIdRecord.getName());
        assertEquals("TestDescription2", groupIdRecord.getDescription());
        assertEquals("publisher", groupIdRecord.getType());
    }

    @Test
    public void testCreateGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        GroupIdRecord newRecord = new GroupIdRecord();
        newRecord.setGroupId("issn:0000005");
        newRecord.setName("TestGroup5");
        newRecord.setDescription("TestDescription5");
        newRecord.setType("publisher");
        Response response = serviceDelegator.createGroupIdRecord(newRecord);
        // Response created with location as the group-id
        assertNotNull(response.getMetadata().get("Location").get(0));
        assertEquals(response.getMetadata().get("Location").get(0).toString(), "5");
    }

    @Test
    public void testUpdateGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        Response response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        // Verify the name
        assertEquals(groupIdRecord.getName(), "TestGroup3");
        // Set a new name for update
        groupIdRecord.setName("TestGroup33");
        serviceDelegator.updateGroupIdRecord(groupIdRecord, Long.valueOf("3"));

        // Get the entity again and verify the name
        response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecordNew = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecordNew);
        // Verify the name
        assertEquals(groupIdRecordNew.getName(), "TestGroup33");

    }

    @Test(expected = GroupIdRecordNotFoundException.class)
    public void testDeleteGroupIdRecord() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        // Verify if the record exists
        Response response = serviceDelegator.viewGroupIdRecord(4L);
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        // Delete the record
        serviceDelegator.deleteGroupIdRecord(Long.valueOf("4"));
        // Throws a record not found exception
        serviceDelegator.viewGroupIdRecord(Long.valueOf("4"));
    }

    @Test
    public void testGetGroupIdRecords() {
        SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
        /*
         * At this point there should be at least 3 group ids and no more than
         * 5, since we are not sure if testDeleteGroupIdRecord and
         * testCreateGroupIdRecord have ran or not
         */

        // So, get a page with all
        Response response = serviceDelegator.viewGroupIdRecords("5", "1");
        assertNotNull(response);
        GroupIdRecords groupIdRecords1 = (GroupIdRecords) response.getEntity();
        assertNotNull(groupIdRecords1);
        assertNotNull(groupIdRecords1.getGroupIdRecord());

        int total = groupIdRecords1.getTotal();
        if (total < 3 || total > 5) {
            fail("There are more group ids than the expected, we are expecting between 3 and 5, total: " + total);
        }
    }

    /**
     * TEST RESEARCHER URLS
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testViewResearcherUrls() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4443");
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertEquals("/4444-4444-4444-4443/researcher-urls", researcherUrls.getPath());
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(5, researcherUrls.getResearcherUrls().size());
        for (ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            assertThat(rUrl.getPutCode(),
                    anyOf(equalTo(Long.valueOf(2)), equalTo(Long.valueOf(3)), equalTo(Long.valueOf(5)), equalTo(Long.valueOf(7)), equalTo(Long.valueOf(8))));
            assertNotNull(rUrl.getSource());
            assertFalse(PojoUtil.isEmpty(rUrl.getSource().retrieveSourcePath()));
            assertNotNull(rUrl.getUrl());
            assertNotNull(rUrl.getUrlName());
            assertNotNull(rUrl.getVisibility());
            if (rUrl.getPutCode().equals(Long.valueOf(5)) || rUrl.getPutCode().equals(Long.valueOf(7))) {
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=1", researcherUrl.getUrl().getValue());
        assertEquals("443_1", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
    }

    @Test
    public void testViewLimitedResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
    }

    @Test
    public void testViewPrivateResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 7L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);

        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=7", researcherUrl.getUrl().getValue());
        assertEquals("443_5", researcherUrl.getUrlName());
        assertEquals(Visibility.PRIVATE, researcherUrl.getVisibility());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateResearcherUrlWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 6L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewResearcherUrlThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 1L);
        fail();
    }

    @Test
    public void testAddResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://www.myRUrl.com"));
        rUrl.setUrlName("My researcher Url");
        rUrl.setVisibility(Visibility.LIMITED);

        Response response = serviceDelegator.createResearcherUrl("4444-4444-4444-4441", rUrl);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4441", putCode);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.myRUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My researcher Url", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
    }

    @Test
    public void testUpdateResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://www.researcherurl2.com?id=5", researcherUrl.getUrl().getValue());
        assertEquals("443_3", researcherUrl.getUrlName());

        researcherUrl.setUrl(new Url("http://theNewResearcherUrl.com"));
        researcherUrl.setUrlName("My Updated Researcher Url");

        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 5L, researcherUrl);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://theNewResearcherUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My Updated Researcher Url", researcherUrl.getUrlName());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateResearcherUrlYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 8L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());

        researcherUrl.setUrlName("Updated Name");
        serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 8L, researcherUrl);
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateResearcherUrlChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", 5L);
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl) response.getEntity();        
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
        
        researcherUrl.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", 5L, researcherUrl);
        fail();
    }        
    
    @Test
    public void testDeleteResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4445", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4445");
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertFalse(researcherUrls.getResearcherUrls().isEmpty());
        ResearcherUrl toDelete = null;

        for (ResearcherUrl rurl : researcherUrls.getResearcherUrls()) {
            if (rurl.getSource().retrieveSourcePath().equals("APP-5555555555555555")) {
                toDelete = rurl;
                break;
            }
        }

        assertNotNull(toDelete);

        response = serviceDelegator.deleteResearcherUrl("4444-4444-4444-4445", toDelete.getPutCode());
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4445");
        assertNotNull(response);
        researcherUrls = (ResearcherUrls) response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(0, researcherUrls.getResearcherUrls().size());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteResearcherUrlYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteResearcherUrl("4444-4444-4444-4443", 8L);
        fail();
    }

    /**
     * TEST EMAILS
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testViewEmails() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewEmails("4444-4444-4444-4443");
        assertNotNull(response);
        Emails emails = (Emails) response.getEntity();
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(3, emails.getEmails().size());
        for (Email email : emails.getEmails()) {
            assertThat(email.getEmail(), anyOf(is("teddybass2@semantico.com"), is("teddybass3public@semantico.com"), is("teddybass3private@semantico.com")));
            switch (email.getEmail()) {
            case "teddybass2@semantico.com":
                assertEquals(Visibility.LIMITED, email.getVisibility());
                assertEquals("4444-4444-4444-4443", email.retrieveSourcePath());
                break;
            case "teddybass3public@semantico.com":
                assertEquals(Visibility.PUBLIC, email.getVisibility());
                assertEquals("4444-4444-4444-4443", email.retrieveSourcePath());
                break;
            case "teddybass3private@semantico.com":
                assertEquals(Visibility.PRIVATE, email.getVisibility());
                assertEquals("APP-5555555555555555", email.retrieveSourcePath());
                break;
            }
        }
    }

    /**
     * TEST OTHER NAMES
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testViewOtherNames() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewOtherNames("4444-4444-4444-4446");
        assertNotNull(response);
        OtherNames otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(3, otherNames.getOtherNames().size());
        for (OtherName otherName : otherNames.getOtherNames()) {
            assertThat(otherName.getPutCode(), anyOf(is(5L), is(6L), is(8L)));
            assertThat(otherName.getContent(), anyOf(is("Other Name # 1"), is("Other Name # 2"), is("Other Name # 4")));
            if (otherName.getPutCode() == 5L) {
                assertEquals(Visibility.PUBLIC, otherName.getVisibility());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
            } else if (otherName.getPutCode() == 6L) {
                assertEquals(Visibility.LIMITED, otherName.getVisibility());
                assertEquals("4444-4444-4444-4446", otherName.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, otherName.getVisibility());
                assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4446", 5L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Other Name # 1", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewLimitedOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4446", 6L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Other Name # 2", otherName.getContent());
        assertEquals(Visibility.LIMITED, otherName.getVisibility());
        assertEquals("4444-4444-4444-4446", otherName.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewPrivateOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4446", 8L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Other Name # 4", otherName.getContent());
        assertEquals(Visibility.PRIVATE, otherName.getVisibility());
        assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateOtherNameWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4446", 7L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewOtherNameThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4446", 1L);
        fail();
    }

    @Test
    public void testAddOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        OtherName otherName = new OtherName();
        otherName.setContent("New Other Name");
        otherName.setVisibility(Visibility.LIMITED);
        Response response = serviceDelegator.createOtherName("4444-4444-4444-4441", otherName);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewOtherName("4444-4444-4444-4441", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        OtherName newOtherName = (OtherName) response.getEntity();
        assertNotNull(newOtherName);
        assertEquals("New Other Name", newOtherName.getContent());
        assertEquals(Visibility.PUBLIC, newOtherName.getVisibility());
        assertNotNull(newOtherName.getSource());
        assertEquals("APP-5555555555555555", newOtherName.getSource().retrieveSourcePath());
        assertNotNull(newOtherName.getCreatedDate());
        assertNotNull(newOtherName.getLastModifiedDate());
    }

    @Test
    public void testUpdateOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Slibberdy Slabinah", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setContent("Updated Other Name");
        
        response = serviceDelegator.updateOtherName("4444-4444-4444-4443", 1L, otherName);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        OtherName updatedOtherName = (OtherName) response.getEntity();
        assertNotNull(updatedOtherName);
        assertEquals("Updated Other Name", updatedOtherName.getContent());
        assertEquals(Visibility.PUBLIC, updatedOtherName.getVisibility());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateOtherNameYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals("Flibberdy Flabinah", otherName.getContent());
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setContent("Updated Other Name " + System.currentTimeMillis());        

        serviceDelegator.updateOtherName("4444-4444-4444-4443", 2L, otherName);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateOtherNameChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherName("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        OtherName otherName = (OtherName) response.getEntity();
        assertNotNull(otherName);
        assertEquals(Visibility.PUBLIC, otherName.getVisibility());

        otherName.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateOtherName("4444-4444-4444-4443", 1L, otherName);               
    }

    @Test
    public void testDeleteOtherName() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewOtherNames("4444-4444-4444-4447");
        assertNotNull(response);
        OtherNames otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertEquals(1, otherNames.getOtherNames().size());
        response = serviceDelegator.deleteOtherName("4444-4444-4444-4447", 9L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewOtherNames("4444-4444-4444-4447");
        assertNotNull(response);
        otherNames = (OtherNames) response.getEntity();
        assertNotNull(otherNames);
        assertNotNull(otherNames.getOtherNames());
        assertTrue(otherNames.getOtherNames().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteOtherNameYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.PERSON_UPDATE, ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteOtherName("4444-4444-4444-4446", 6L);
        fail();
    }

    /**
     * TEST EXTERNAL IDENTIFIERS
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testViewExternalIdentifiers() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4442");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        List<PersonExternalIdentifier> extIdsList = extIds.getExternalIdentifier();
        assertNotNull(extIdsList);
        assertEquals(3, extIdsList.size());

        for (PersonExternalIdentifier extId : extIdsList) {
            assertThat(extId.getPutCode(), anyOf(is(2L), is(3L), is(5L)));
            assertThat(extId.getValue(), anyOf(is("abc123"), is("abc456"), is("abc012")));
            assertNotNull(extId.getUrl());
            assertThat(extId.getUrl().getValue(),
                    anyOf(is("http://www.facebook.com/abc123"), is("http://www.facebook.com/abc456"), is("http://www.facebook.com/abc012")));
            assertEquals("Facebook", extId.getType());
            assertNotNull(extId.getSource());
            if (extId.getPutCode().equals(2L)) {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
            } else if (extId.getPutCode().equals(3L)) {
                assertEquals(Visibility.LIMITED, extId.getVisibility());
                assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(2), extId.getPutCode());
        assertEquals("abc123", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
        assertEquals(Visibility.PUBLIC, extId.getVisibility());

        assertNotNull(extId.getSource());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
    }

    @Test
    public void testViewLimitedExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(3), extId.getPutCode());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
        assertEquals(Visibility.LIMITED, extId.getVisibility());

        assertNotNull(extId.getSource());
        assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
    }

    @Test
    public void testViewPrivateExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 5L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals(Long.valueOf(5), extId.getPutCode());
        assertEquals("abc012", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc012", extId.getUrl().getValue());
        assertEquals(Visibility.PRIVATE, extId.getVisibility());

        assertNotNull(extId.getSource());
        assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
        assertNotNull(extId.getCreatedDate());
        assertNotNull(extId.getLastModifiedDate());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateExternalIdentifierWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 4L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewExternalIdentifierThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 1L);
        fail();
    }

    @Test
    public void testAddExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4443");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifier());
        assertEquals(1, extIds.getExternalIdentifier().size());
        assertEquals(Long.valueOf(1), extIds.getExternalIdentifier().get(0).getPutCode());
        assertNotNull(extIds.getExternalIdentifier().get(0).getUrl());
        assertEquals("http://www.facebook.com/d3clan", extIds.getExternalIdentifier().get(0).getUrl().getValue());
        assertEquals("d3clan", extIds.getExternalIdentifier().get(0).getValue());
        assertEquals(Visibility.PUBLIC, extIds.getExternalIdentifier().get(0).getVisibility());

        PersonExternalIdentifier newExtId = new PersonExternalIdentifier();
        newExtId.setType("new-common-name");
        newExtId.setValue("new-reference");
        newExtId.setUrl(new Url("http://newUrl.com"));
        newExtId.setVisibility(Visibility.LIMITED);

        response = serviceDelegator.createExternalIdentifier("4444-4444-4444-4443", newExtId);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());

        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4443");
        assertNotNull(response);
        extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifier());
        assertEquals(2, extIds.getExternalIdentifier().size());

        for (PersonExternalIdentifier extId : extIds.getExternalIdentifier()) {
            assertNotNull(extId.getUrl());
            if (extId.getPutCode() != 1L) {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("new-common-name", extId.getType());
                assertEquals("new-reference", extId.getValue());
                assertEquals("http://newUrl.com", extId.getUrl().getValue());
                assertEquals(putCode, extId.getPutCode());
            } else {
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("Facebook", extId.getType());
                assertEquals("d3clan", extId.getValue());
                assertEquals("http://www.facebook.com/d3clan", extId.getUrl().getValue());
            }
        }
    }

    @Test
    public void testUpdateExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc123", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
        extId.setType("updated-common-name");
        extId.setValue("updated-reference");
        extId.setUrl(new Url("http://updatedUrl.com"));
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier updatedExtId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(updatedExtId);
        assertEquals("updated-common-name", updatedExtId.getType());
        assertEquals("updated-reference", updatedExtId.getValue());
        assertNotNull(updatedExtId.getUrl());
        assertEquals("http://updatedUrl.com", updatedExtId.getUrl().getValue());

        // Revert changes so other tests still works
        extId.setType("Facebook");
        extId.setValue("abc123");
        extId.setUrl(new Url("http://www.facebook.com/abc123"));
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateExaternalIdentifierYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
        extId.setType("other-common-name");
        extId.setValue("other-reference");
        extId.setUrl(new Url("http://otherUrl.com"));
        serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 3L, extId);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateExternalIdentifierChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 2L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals(Visibility.PUBLIC, extId.getVisibility());
        
        extId.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateExternalIdentifier("4444-4444-4444-4442", 2L, extId);
        fail();
    }
    
    @Test
    public void testDeleteExternalIdentifier() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4444", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4444");
        assertNotNull(response);
        PersonExternalIdentifiers extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifier());
        assertEquals(1, extIds.getExternalIdentifier().size());
        assertEquals(Long.valueOf(6), extIds.getExternalIdentifier().get(0).getPutCode());

        response = serviceDelegator.deleteExternalIdentifier("4444-4444-4444-4444", 6L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewExternalIdentifiers("4444-4444-4444-4444");
        assertNotNull(response);
        extIds = (PersonExternalIdentifiers) response.getEntity();
        assertNotNull(extIds);
        assertNotNull(extIds.getExternalIdentifier());
        assertTrue(extIds.getExternalIdentifier().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteExternalIdentifierYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewExternalIdentifier("4444-4444-4444-4442", 3L);
        assertNotNull(response);
        PersonExternalIdentifier extId = (PersonExternalIdentifier) response.getEntity();
        assertNotNull(extId);
        assertEquals("Facebook", extId.getType());
        assertEquals("abc456", extId.getValue());
        assertNotNull(extId.getUrl());
        assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());

        serviceDelegator.deleteExternalIdentifier("4444-4444-4444-4442", 3L);
        fail();
    }

    /**
     * TEST KEYWORDS
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testViewKeywords() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeywords("4444-4444-4444-4443");
        assertNotNull(response);
        Keywords keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(3, keywords.getKeywords().size());

        for (Keyword keyword : keywords.getKeywords()) {
            assertThat(keyword.getPutCode(), anyOf(is(1L), is(2L), is(4L)));
            assertThat(keyword.getContent(), anyOf(is("tea making"), is("coffee making"), is("what else can we make?")));
            if (keyword.getPutCode() == 1L) {
                assertEquals(Visibility.PUBLIC, keyword.getVisibility());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
            } else if (keyword.getPutCode() == 2L) {
                assertEquals(Visibility.LIMITED, keyword.getVisibility());
                assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());
            } else {
                assertEquals(Visibility.PRIVATE, keyword.getVisibility());
                assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 1L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("tea making", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewLimitedKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("coffee making", keyword.getContent());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());
    }

    @Test
    public void testViewPrivateKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 4L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("what else can we make?", keyword.getContent());
        assertEquals(Visibility.PRIVATE, keyword.getVisibility());
        assertEquals("APP-5555555555555555", keyword.getSource().retrieveSourcePath());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateKeywordWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewKeyword("4444-4444-4444-4443", 3L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewKeywordThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewOtherName("4444-4444-4444-4443", 5L);
        fail();
    }

    @Test
    public void testAddKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Keyword keyword = new Keyword();
        keyword.setContent("New keyword");
        keyword.setVisibility(Visibility.LIMITED);
        Response response = serviceDelegator.createKeyword("4444-4444-4444-4441", keyword);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewKeyword("4444-4444-4444-4441", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Keyword newKeyword = (Keyword) response.getEntity();
        assertNotNull(newKeyword);
        assertEquals("New keyword", newKeyword.getContent());
        assertEquals(Visibility.PUBLIC, newKeyword.getVisibility());
        assertNotNull(newKeyword.getSource());
        assertEquals("APP-5555555555555555", newKeyword.getSource().retrieveSourcePath());
        assertNotNull(newKeyword.getCreatedDate());
        assertNotNull(newKeyword.getLastModifiedDate());
    }

    @Test
    public void testUpdateKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("keyword-2", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setContent("Updated keyword");

        response = serviceDelegator.updateKeyword("4444-4444-4444-4441", 6L, keyword);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("Updated keyword", keyword.getContent());
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateKeywordYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4443", 2L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals("coffee making", keyword.getContent());
        assertEquals(Visibility.LIMITED, keyword.getVisibility());
        assertNotNull(keyword.getSource());
        assertEquals("4444-4444-4444-4443", keyword.getSource().retrieveSourcePath());

        keyword.setContent("Updated Keyword " + System.currentTimeMillis());        

        serviceDelegator.updateKeyword("4444-4444-4444-4443", 2L, keyword);
        fail();
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateKeywordChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeyword("4444-4444-4444-4441", 6L);
        assertNotNull(response);
        Keyword keyword = (Keyword) response.getEntity();
        assertNotNull(keyword);
        assertEquals(Visibility.PUBLIC, keyword.getVisibility());

        keyword.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateKeyword("4444-4444-4444-4441", 6L, keyword);
        fail();
    }
    
    @Test
    public void testDeleteKeyword() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4499", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewKeywords("4444-4444-4444-4499");
        assertNotNull(response);
        Keywords keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertEquals(1, keywords.getKeywords().size());
        response = serviceDelegator.deleteKeyword("4444-4444-4444-4499", 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewKeywords("4444-4444-4444-4499");
        assertNotNull(response);
        keywords = (Keywords) response.getEntity();
        assertNotNull(keywords);
        assertNotNull(keywords.getKeywords());
        assertTrue(keywords.getKeywords().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteKeywordYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteKeyword("4444-4444-4444-4443", 3L);
        fail();
    }

    /**
     * TEST ADDRESSES
     */
    @SuppressWarnings("unchecked")
    @Test
    public void testViewAddresses() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddresses("4444-4444-4444-4447");
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(3, addresses.getAddress().size());

        for (Address address : addresses.getAddress()) {
            assertThat(address.getPutCode(), anyOf(is(2L), is(3L), is(4L)));
            assertThat(address.getCountry().getValue(), anyOf(is(Iso3166Country.CR), is(Iso3166Country.US)));
            if (address.getPutCode() == 2L) {
                assertEquals(Visibility.PUBLIC, address.getVisibility());
                assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 3L) {
                assertEquals(Visibility.LIMITED, address.getVisibility());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
            } else if (address.getPutCode() == 4L) {
                assertEquals(Visibility.PRIVATE, address.getVisibility());
                assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
            }
        }
    }

    @Test
    public void testViewPublicAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
    }

    @Test
    public void testViewLimitedAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 3L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.LIMITED, address.getVisibility());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
    }

    @Test
    public void testViewPrivateAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 4L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PRIVATE, address.getVisibility());
        assertEquals("APP-5555555555555555", address.getSource().retrieveSourcePath());
        assertEquals(Iso3166Country.CR, address.getCountry().getValue());
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateAddressWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewAddress("4444-4444-4444-4447", 5L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewAddressThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED);
        serviceDelegator.viewAddress("4444-4444-4444-4447", 1L);
        fail();
    }

    @Test
    public void testAddAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);

        Address address = new Address();
        address.setVisibility(Visibility.PUBLIC);
        address.setCountry(new Country(Iso3166Country.ES));

        Response response = serviceDelegator.createAddress("4444-4444-4444-4442", address);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));

        response = serviceDelegator.viewAddress("4444-4444-4444-4442", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Address newAddress = (Address) response.getEntity();
        assertNotNull(newAddress);
        assertEquals(Iso3166Country.ES, newAddress.getCountry().getValue());
        assertEquals(Visibility.LIMITED, newAddress.getVisibility());
        assertNotNull(newAddress.getSource());
        assertEquals("APP-5555555555555555", newAddress.getSource().retrieveSourcePath());
        assertNotNull(newAddress.getCreatedDate());
        assertNotNull(newAddress.getLastModifiedDate());

        // Remove it
        response = serviceDelegator.deleteAddress("4444-4444-4444-4442", putCode);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test
    public void testUpdateAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.getCountry().setValue(Iso3166Country.PA);

        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

        response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Iso3166Country.PA, address.getCountry().getValue());

        // Set it back to US again
        address.getCountry().setValue(Iso3166Country.US);
        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testUpdateAddressYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Iso3166Country.US, address.getCountry().getValue());
        assertEquals(Visibility.PUBLIC, address.getVisibility());
        assertNotNull(address.getSource());
        assertEquals("4444-4444-4444-4447", address.getSource().retrieveSourcePath());

        address.getCountry().setValue(Iso3166Country.BR);

        serviceDelegator.updateAddress("4444-4444-4444-4447", 2L, address);
        fail();
    }
    
    @Test(expected = VisibilityMismatchException.class)
    public void testUpdateAddressChangingVisibilityTest() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddress("4444-4444-4444-4442", 1L);
        assertNotNull(response);
        Address address = (Address) response.getEntity();
        assertNotNull(address);
        assertEquals(Visibility.PUBLIC, address.getVisibility());

        address.setVisibility(Visibility.PRIVATE);
        
        response = serviceDelegator.updateAddress("4444-4444-4444-4442", 1L, address);
        fail();
    }    
    
    @Test
    public void testDeleteAddress() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4499", ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewAddresses("4444-4444-4444-4499");
        assertNotNull(response);
        Addresses addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertEquals(1, addresses.getAddress().size());
        response = serviceDelegator.deleteAddress("4444-4444-4444-4499", 6L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        response = serviceDelegator.viewAddresses("4444-4444-4444-4499");
        assertNotNull(response);
        addresses = (Addresses) response.getEntity();
        assertNotNull(addresses);
        assertNotNull(addresses.getAddress());
        assertTrue(addresses.getAddress().isEmpty());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeleteAddressYouAreNotTheSourceOf() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.PERSON_UPDATE);
        serviceDelegator.deleteAddress("4444-4444-4444-4447", 5L);
        fail();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testViewPerson() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4442", ScopePathType.PERSON_READ_LIMITED);
        Response response = serviceDelegator.viewPerson("4444-4444-4444-4442");
        assertNotNull(response);
        Person person = (Person) response.getEntity();
        assertNotNull(person);
        assertNotNull(person.getName());
        assertEquals(Visibility.PUBLIC, person.getName().getVisibility());
        assertEquals("Credit Name", person.getName().getCreditName().getContent());
        assertEquals("Family Name", person.getName().getFamilyName().getContent());
        assertEquals("Given Names", person.getName().getGivenNames().getContent());

        assertNotNull(person.getAddresses());
        assertNotNull(person.getAddresses().getAddress());
        assertEquals(1, person.getAddresses().getAddress().size());
        assertNotNull(person.getAddresses().getAddress().get(0).getCreatedDate());
        assertNotNull(person.getAddresses().getAddress().get(0).getLastModifiedDate());
        assertNotNull(person.getAddresses().getAddress().get(0).getCountry());
        assertEquals(Iso3166Country.US, person.getAddresses().getAddress().get(0).getCountry().getValue());
        assertTrue(person.getAddresses().getAddress().get(0).getPrimary());
        assertEquals(Long.valueOf(1), person.getAddresses().getAddress().get(0).getPutCode());
        assertNotNull(person.getAddresses().getAddress().get(0).getSource());
        assertEquals("APP-5555555555555555", person.getAddresses().getAddress().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", person.getAddresses().getAddress().get(0).getSource().retriveSourceUri());
        assertEquals(Visibility.PUBLIC, person.getAddresses().getAddress().get(0).getVisibility());

        assertNotNull(person.getBiography());
        assertEquals("Biography for 4444-4444-4444-4442",
                person.getBiography().getContent());
        assertEquals(Visibility.PUBLIC, person.getBiography().getVisibility());

        assertNotNull(person.getEmails());
        assertNotNull(person.getEmails().getEmails());
        assertEquals(1, person.getEmails().getEmails().size());
        assertEquals("michael@bentine.com", person.getEmails().getEmails().get(0).getEmail());
        assertEquals(Visibility.LIMITED, person.getEmails().getEmails().get(0).getVisibility());
        assertNotNull(person.getEmails().getEmails().get(0).getSource());
        assertEquals("4444-4444-4444-4442", person.getEmails().getEmails().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", person.getEmails().getEmails().get(0).getSource().retriveSourceUri());
        assertNull(person.getEmails().getEmails().get(0).getPutCode());
        assertNotNull(person.getEmails().getEmails().get(0).getLastModifiedDate());
        assertNotNull(person.getEmails().getEmails().get(0).getCreatedDate());

        assertNotNull(person.getExternalIdentifiers());
        assertNotNull(person.getExternalIdentifiers().getExternalIdentifier());
        assertEquals(3, person.getExternalIdentifiers().getExternalIdentifier().size());

        boolean found2 = false, found3 = false, found5 = false;

        List<PersonExternalIdentifier> extIds = person.getExternalIdentifiers().getExternalIdentifier();
        for (PersonExternalIdentifier extId : extIds) {
            assertThat(extId.getPutCode(), anyOf(is(2L), is(3L), is(5L)));
            assertNotNull(extId.getCreatedDate());
            assertNotNull(extId.getLastModifiedDate());
            assertNotNull(extId.getSource());
            if (extId.getPutCode() == 2L) {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc123", extId.getValue());
                assertEquals("http://www.facebook.com/abc123", extId.getUrl().getValue());
                assertEquals(Visibility.PUBLIC, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", extId.getSource().retriveSourceUri());
                found2 = true;
            } else if (extId.getPutCode() == 3L) {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc456", extId.getValue());
                assertEquals("http://www.facebook.com/abc456", extId.getUrl().getValue());
                assertEquals(Visibility.LIMITED, extId.getVisibility());
                assertEquals("4444-4444-4444-4442", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", extId.getSource().retriveSourceUri());
                found3 = true;
            } else {
                assertEquals("Facebook", extId.getType());
                assertEquals("abc012", extId.getValue());
                assertEquals("http://www.facebook.com/abc012", extId.getUrl().getValue());
                assertEquals(Visibility.PRIVATE, extId.getVisibility());
                assertEquals("APP-5555555555555555", extId.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", extId.getSource().retriveSourceUri());
                found5 = true;
            }
        }

        assertTrue(found2 && found3 && found5);

        assertNotNull(person.getKeywords());
        assertNotNull(person.getKeywords().getKeywords());
        assertEquals(1, person.getKeywords().getKeywords().size());
        assertEquals("My keyword", person.getKeywords().getKeywords().get(0).getContent());
        assertEquals(Long.valueOf(7), person.getKeywords().getKeywords().get(0).getPutCode());
        assertEquals("APP-5555555555555555", person.getKeywords().getKeywords().get(0).getSource().retrieveSourcePath());
        assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", person.getKeywords().getKeywords().get(0).getSource().retriveSourceUri());
        assertEquals(Visibility.PUBLIC, person.getKeywords().getKeywords().get(0).getVisibility());
        assertNotNull(person.getKeywords().getKeywords().get(0).getCreatedDate());
        assertNotNull(person.getKeywords().getKeywords().get(0).getLastModifiedDate());

        assertNotNull(person.getOtherNames());
        assertNotNull(person.getOtherNames().getOtherNames());
        assertEquals(2, person.getOtherNames().getOtherNames().size());

        boolean found9 = false, found10 = false;

        for (OtherName otherName : person.getOtherNames().getOtherNames()) {
            assertThat(otherName.getPutCode(), anyOf(is(10L), is(11L)));
            assertNotNull(otherName.getSource());
            assertEquals("APP-5555555555555555", otherName.getSource().retrieveSourcePath());
            assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", otherName.getSource().retriveSourceUri());
            if (otherName.getPutCode() == 10L) {
                assertEquals("Other Name # 1", otherName.getContent());
                assertEquals(Visibility.PUBLIC, otherName.getVisibility());
                found9 = true;
            } else {
                assertEquals("Other Name # 2", otherName.getContent());
                assertEquals(Visibility.PRIVATE, otherName.getVisibility());
                found10 = true;
            }
        }

        assertTrue(found9 && found10);

        assertNotNull(person.getResearcherUrls());
        assertNotNull(person.getResearcherUrls().getResearcherUrls());
        assertEquals(3, person.getResearcherUrls().getResearcherUrls().size());

        found9 = false;
        found10 = false;
        boolean found12 = false;

        for (ResearcherUrl rUrl : person.getResearcherUrls().getResearcherUrls()) {
            assertNotNull(rUrl.getCreatedDate());
            assertNotNull(rUrl.getLastModifiedDate());
            assertNotNull(rUrl.getSource());
            assertThat(rUrl.getPutCode(), anyOf(is(9L), is(10L), is(12L)));
            assertNotNull(rUrl.getUrl());
            if (rUrl.getPutCode().equals(9L)) {
                assertEquals("4444-4444-4444-4442", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=9", rUrl.getUrl().getValue());
                assertEquals("1", rUrl.getUrlName());
                assertEquals(Visibility.PUBLIC, rUrl.getVisibility());
                found9 = true;
            } else if (rUrl.getPutCode().equals(10L)) {
                assertEquals("4444-4444-4444-4442", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/4444-4444-4444-4442", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=10", rUrl.getUrl().getValue());
                assertEquals("2", rUrl.getUrlName());
                assertEquals(Visibility.LIMITED, rUrl.getVisibility());
                found10 = true;
            } else {
                assertEquals(Long.valueOf(12), rUrl.getPutCode());
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
                assertEquals("http://testserver.orcid.org/client/APP-5555555555555555", rUrl.getSource().retriveSourceUri());
                assertEquals("http://www.researcherurl.com?id=12", rUrl.getUrl().getValue());
                assertEquals("4", rUrl.getUrlName());
                assertEquals(Visibility.PRIVATE, rUrl.getVisibility());
                found12 = true;
            }
        }

        assertTrue(found9 && found10 && found12);
        // TODO: TEST APPLICATIONS AND DELEGATION
    }

    @Test
    public void testViewOtherProfileDontWork() {
        //Set all possible permissions to user 4444-4444-4444-4446
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE,
                ScopePathType.AFFILIATIONS_CREATE, ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.AFFILIATIONS_UPDATE, ScopePathType.AUTHENTICATE,
                ScopePathType.FUNDING_CREATE, ScopePathType.FUNDING_READ_LIMITED, ScopePathType.FUNDING_UPDATE, ScopePathType.ORCID_BIO_EXTERNAL_IDENTIFIERS_CREATE,
                ScopePathType.ORCID_BIO_READ_LIMITED, ScopePathType.ORCID_BIO_UPDATE, ScopePathType.ORCID_PATENTS_CREATE, ScopePathType.ORCID_PATENTS_READ_LIMITED,
                ScopePathType.ORCID_PATENTS_UPDATE, ScopePathType.ORCID_PROFILE_CREATE, ScopePathType.ORCID_PROFILE_READ_LIMITED, ScopePathType.ORCID_WORKS_CREATE,
                ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.ORCID_WORKS_UPDATE, ScopePathType.PEER_REVIEW_CREATE, ScopePathType.PEER_REVIEW_READ_LIMITED,
                ScopePathType.PEER_REVIEW_UPDATE, ScopePathType.PERSON_READ_LIMITED, ScopePathType.PERSON_UPDATE, ScopePathType.READ_LIMITED, ScopePathType.READ_PUBLIC);
        //Try to view anything on the user
        String orcid = "4444-4444-4444-4442";
        Long putCode = 1L;
        try {
            //Check activities
            serviceDelegator.viewActivities(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewAddress(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewAddresses(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewBiography(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEducation(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEducationSummary(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEmails(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEmployment(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewEmploymentSummary(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewExternalIdentifier(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewExternalIdentifiers(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewFunding(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewFundingSummary(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewKeyword(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewKeywords(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewOtherName(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewOtherNames(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewPeerReview(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewPeerReviewSummary(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewPerson(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewPersonalDetails(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewResearcherUrl(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewResearcherUrls(orcid);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewWork(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
        
        try {
            serviceDelegator.viewWorkSummary(orcid, putCode);
            fail();
        } catch(OrcidUnauthorizedException ou) {
            assertEquals("Access token is for a different record", ou.getMessage());
        } catch(Exception e) {
            fail();
        }
    }
    
    @Test    
    public void testAddWorkWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Work work = getWork();
        try {
            work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createWork(orcid, work);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("DOI");
            serviceDelegator.createWork(orcid, work);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Assert that it could be created with a valid value
        work.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        Response response = serviceDelegator.createWork(orcid, work);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        //Delete it to roll back the test data
        response = serviceDelegator.deleteWork(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void testAddPeerReviewWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        PeerReview peerReview = getPeerReview();
        
        //Set both to a correct value
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        peerReview.getSubjectExternalIdentifier().setType("doi");
        
        //Check it fail on external identifier type
        try {
            peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("DOI");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Set the ext id to a correct value to test the subject ext id
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        //Check it fail on subject external identifier type
        try {
            peerReview.getSubjectExternalIdentifier().setType("INVALID");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            peerReview.getSubjectExternalIdentifier().setType("DOI");
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        //Test it works with correct values
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        peerReview.getSubjectExternalIdentifier().setType("doi");
        Response response = serviceDelegator.createPeerReview(orcid, peerReview);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        //Delete it to roll back the test data
        response = serviceDelegator.deletePeerReview(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    
    @Test
    public void testAddFundingWithInvalidExtIdTypeFail() {
        String orcid = "4444-4444-4444-4499";
        SecurityContextTestUtils.setUpSecurityContext(orcid, ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        
        Funding funding = getFunding();
        
        try {
            funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createFunding(orcid, funding);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        try {
            funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("GRANT_NUMBER");
            serviceDelegator.createFunding(orcid, funding);
            fail();
        } catch(ActivityIdentifierValidationException e) {
            
        } catch(Exception e) {
            fail();
        }
        
        funding.getExternalIdentifiers().getExternalIdentifier().get(0).setType("grant_number");
        Response response = serviceDelegator.createFunding(orcid, funding);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map<?, ?> map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List<?> resultWithPutCode = (List<?>) map.get("Location");
        Long putCode = Long.valueOf(String.valueOf(resultWithPutCode.get(0)));
        
        //Delete it to roll back the test data
        response = serviceDelegator.deleteFunding(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }
    
    private Work getWork() {
        Work work = new Work();
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("A new work!"));
        work.setWorkTitle(workTitle);
        work.setWorkType(WorkType.BOOK);
        work.setVisibility(Visibility.PUBLIC);
        ExternalIDs extIds = new ExternalIDs();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.PART_OF);
        extId.setType(ExternalIDType.AGR.value());
        extId.setValue("ext-id-" + System.currentTimeMillis());
        extId.setUrl(new Url("http://thisIsANewUrl.com"));
        extIds.getExternalIdentifier().add(extId);
        work.setWorkExternalIdentifiers(extIds);
        return work;
    }
    
    private PeerReview getPeerReview() {
        PeerReview peerReview = new PeerReview();
        ExternalIDs weis = new ExternalIDs();
        ExternalID wei1 = new ExternalID();
        wei1.setRelationship(Relationship.PART_OF);
        wei1.setUrl(new Url("http://myUrl.com"));
        wei1.setValue("work-external-identifier-id");
        wei1.setType(ExternalIDType.DOI.value());
        weis.getExternalIdentifier().add(wei1);
        peerReview.setExternalIdentifiers(weis);
        peerReview.setGroupId("issn:0000003");
        peerReview.setOrganization(getOrganization());
        peerReview.setRole(Role.CHAIR);
        peerReview.setSubjectContainerName(new Title("subject-container-name"));
        peerReview.setSubjectExternalIdentifier(wei1);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work-title"));
        peerReview.setSubjectName(workTitle);
        peerReview.setSubjectType(WorkType.DATA_SET);
        peerReview.setType(PeerReviewType.EVALUATION);
        return peerReview;
    }
    
    private Funding getFunding(){
        Funding newFunding = new Funding();
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Public Funding # 2"));
        newFunding.setTitle(title);
        newFunding.setType(FundingType.AWARD);
        ExternalID fExtId = new ExternalID();
        fExtId.setRelationship(Relationship.PART_OF);
        fExtId.setType(ExternalIDType.GRANT_NUMBER.value());
        fExtId.setUrl(new Url("http://fundingExtId.com"));
        fExtId.setValue("new-funding-ext-id");
        ExternalIDs fExtIds = new ExternalIDs();
        fExtIds.getExternalIdentifier().add(fExtId);
        newFunding.setExternalIdentifiers(fExtIds);
        newFunding.setOrganization(getOrganization());
        return newFunding;
    }
    
    private Organization getOrganization() {
        Organization org = new Organization();
        org.setName("Org Name");
        OrganizationAddress add = new OrganizationAddress();
        add.setCity("city");
        add.setCountry(Iso3166Country.TT);
        org.setAddress(add);
        return org;
    }
}
