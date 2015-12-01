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
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common.Subtitle;
import org.orcid.jaxb.model.common.Title;
import org.orcid.jaxb.model.common.TranslatedTitle;
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.groupid.GroupIdRecord;
import org.orcid.jaxb.model.groupid.GroupIdRecords;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_rc1.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_rc1.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc1.WorkGroup;
import org.orcid.jaxb.model.record.summary_rc1.WorkSummary;
import org.orcid.jaxb.model.record.summary_rc1.Works;
import org.orcid.jaxb.model.record_rc1.Citation;
import org.orcid.jaxb.model.record_rc1.Education;
import org.orcid.jaxb.model.record_rc1.Email;
import org.orcid.jaxb.model.record_rc1.Emails;
import org.orcid.jaxb.model.record_rc1.Employment;
import org.orcid.jaxb.model.record_rc1.Funding;
import org.orcid.jaxb.model.record_rc1.PeerReview;
import org.orcid.jaxb.model.record_rc1.Work;
import org.orcid.jaxb.model.record_rc1.WorkTitle;
import org.orcid.jaxb.model.record_rc1.WorkType;
import org.orcid.jaxb.model.record_rc2.ResearcherUrl;
import org.orcid.jaxb.model.record_rc2.ResearcherUrls;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml",
            "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml", "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewEntityData.xml", "/data/GroupIdRecordEntityData.xml");

    @Resource(name = "memberV2ApiServiceDelegator")
    private MemberV2ApiServiceDelegator serviceDelegator;

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
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode(),
                anyOf(is(Long.valueOf(5)), is(Long.valueOf(6)), is(Long.valueOf(7))));
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
        assertThat(summary.getWorks().getWorkGroup().get(1).getWorkSummary().get(0).getPutCode(),
                anyOf(is(Long.valueOf(5)), is(Long.valueOf(6)), is(Long.valueOf(7))));
        assertThat(summary.getWorks().getWorkGroup().get(1).getWorkSummary().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(1).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));                        
        assertThat(summary.getWorks().getWorkGroup().get(2).getWorkSummary().get(0).getPutCode(),
                anyOf(is(Long.valueOf(5)), is(Long.valueOf(6)), is(Long.valueOf(7))));
        assertThat(summary.getWorks().getWorkGroup().get(2).getWorkSummary().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(2).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));

        // Check fundings
        assertNotNull(summary.getFundings());
        assertEquals(2, summary.getFundings().getFundingGroup().size());
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode(),
                anyOf(is(Long.valueOf(4)), is(Long.valueOf(5))));        
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath(),
                anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5")));
        
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Private Funding"), is("Public Funding")));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getPutCode(),
                anyOf(is(Long.valueOf(4)), is(Long.valueOf(5))));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getPath(),
                anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5")));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Private Funding"), is("Public Funding")));

        // Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(3, summary.getEducations().getSummaries().size());
        assertThat(summary.getEducations().getSummaries().get(0).getPutCode(), anyOf(is(Long.valueOf(6)), is(Long.valueOf(7))));
        assertThat(summary.getEducations().getSummaries().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/education/6"), is("/4444-4444-4444-4446/education/7"), is("/4444-4444-4444-4446/education/9")));
        assertThat(summary.getEducations().getSummaries().get(0).getDepartmentName(), anyOf(is("Education Dept # 1"), is("Education Dept # 2"), is("Education Dept # 3")));

        // Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(3, summary.getEmployments().getSummaries().size());
        assertThat(summary.getEmployments().getSummaries().get(0).getPutCode(), anyOf(is(Long.valueOf(5)), is(Long.valueOf(8))));
        assertThat(summary.getEmployments().getSummaries().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/employment/5"), is("/4444-4444-4444-4446/employment/8"), is("/4444-4444-4444-4446/employment/11")));
        assertThat(summary.getEmployments().getSummaries().get(0).getDepartmentName(), anyOf(is("Employment Dept # 1"), is("Employment Dept # 2"), is("Employment Dept # 4")));
        
        // Check Peer reviews
        assertNotNull(summary.getPeerReviews());
        assertEquals(4, summary.getPeerReviews().getPeerReviewGroup().size());
        PeerReviewSummary peerReviewSummary = summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0);
        
        assertThat(peerReviewSummary.getPutCode(), anyOf(is(Long.valueOf(1)), is(Long.valueOf(3)), is(Long.valueOf(6))));      
    }

    @Test
    public void testCleanEmptyFieldsOnActivities() {
        Works works = new Works();
        WorkGroup group = new WorkGroup();
        for(int i = 0; i < 5; i++) {
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
        for(WorkSummary summary : as.getWorks().getWorkGroup().get(0).getWorkSummary()) {
            assertNotNull(summary.getTitle());
            assertNotNull(summary.getTitle().getTitle());
            assertTrue(summary.getTitle().getTitle().getContent().startsWith("Work "));
            assertNull(summary.getTitle().getTranslatedTitle());
        }
        
    }
    
    /**
     * TEST WORKS
     * */            
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
        //Use the smallest scope in the pyramid to verify that you can read your own limited and private data
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_PUBLIC);
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
        
    /**
     * TEST FUNDINGS
     * */         
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
        //Use the smallest scope in the pyramid to verify that you can read your own limited and private data
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_PUBLIC);
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
        //Funding 1 belongs to 4444-4444-4444-4443
        serviceDelegator.viewFunding("4444-4444-4444-4446", 1L);
        fail();        
    }
    
    /**
     * TEST EDUCATIONS
     * */     
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
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_PUBLIC);
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
        //Education 1 belongs to 4444-4444-4444-4442
        serviceDelegator.viewEducation("4444-4444-4444-4446", 1L);
        fail();
    }
    
    /**
     * TEST EMPLOYMENTS
     * */
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
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_PUBLIC);
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
    }
    
    @Test(expected = NoResultException.class)
    public void testViewEmploymentThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        serviceDelegator.viewEmployment("4444-4444-4444-4446", 4L);
    }
    
    /**
     * TEST PEER REVIEWS
     * */
    @Test
    public void testViewPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 1L);
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(1L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
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
        assertEquals("peer-review:subject-external-identifier-id#1", peerReview.getSubjectExternalIdentifier().getWorkExternalIdentifierId().getContent());
        assertEquals("agr", peerReview.getSubjectExternalIdentifier().getWorkExternalIdentifierType().value());
        assertEquals("issn:0000001", peerReview.getGroupId());
    }
    
    @Test
    public void testViewLimitedPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 3L);
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(3L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#2", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertEquals("limited", peerReview.getVisibility().value());
        assertEquals("issn:0000002", peerReview.getGroupId());
    }
    
    @Test
    public void testViewPrivatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.READ_PUBLIC);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", 4L);
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf(4L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#3", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertEquals("limited", peerReview.getVisibility().value());
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
        PeerReviewSummary peerReview= (PeerReviewSummary) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Long.valueOf("1"), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());        
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());                
    }
    
    @Test
    public void testUpdatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        serviceDelegator.updatePeerReview("4444-4444-4444-4447", 6L, peerReview);
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 6L);
        PeerReview updatedPeerReview= (PeerReview) response.getEntity();
        assertNotNull(updatedPeerReview);
        assertEquals("http://updated.com/url", updatedPeerReview.getUrl().getValue());
        assertEquals("Updated Title", updatedPeerReview.getSubjectName().getTitle().getContent());
    }
    
    @Test
    public void testUpdatePeerReviewWhenNotSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", 2L);
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());
        
        //Update the info
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        
        //Try to update it
        try {
            response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", 2L, peerReview);
            fail();
        } catch(WrongSourceException wse) {
            
        }
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", Long.valueOf(2));
        peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());
    }
        
    /**
     * TEST GROUP ID
     * */    
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
    	//Response created with location as the group-id
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
        //Verify the name
        assertEquals(groupIdRecord.getName(), "TestGroup3");
        //Set a new name for update
        groupIdRecord.setName("TestGroup33");
        serviceDelegator.updateGroupIdRecord(groupIdRecord, Long.valueOf("3"));
        
        //Get the entity again and verify the name
        response = serviceDelegator.viewGroupIdRecord(Long.valueOf("3"));
        assertNotNull(response);
        GroupIdRecord groupIdRecordNew = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecordNew);
        //Verify the name
        assertEquals(groupIdRecordNew.getName(), "TestGroup33");
        
    }
    
    @Test(expected=GroupIdRecordNotFoundException.class)
    public void testDeleteGroupIdRecord() {
    	SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
    	//Verify if the record exists
    	Response response = serviceDelegator.viewGroupIdRecord(4L);
        assertNotNull(response);
        GroupIdRecord groupIdRecord = (GroupIdRecord) response.getEntity();
        assertNotNull(groupIdRecord);
        //Delete the record
        serviceDelegator.deleteGroupIdRecord(Long.valueOf("4"));
        //Throws a record not found exception
        serviceDelegator.viewGroupIdRecord(Long.valueOf("4"));
    }
    
    @Test
    public void testGetGroupIdRecords() {
    	SecurityContextTestUtils.setUpSecurityContextForGroupIdClientOnly();
    	/*
    	 * At this point there should be at least 3 group ids and no more than 5, 
    	 * since we are not sure if testDeleteGroupIdRecord and testCreateGroupIdRecord have ran or not
    	 */
    	
    	//So, get a page with all
    	Response response = serviceDelegator.viewGroupIdRecords("5", "1");
        assertNotNull(response);
        GroupIdRecords groupIdRecords1 = (GroupIdRecords) response.getEntity();
        assertNotNull(groupIdRecords1);
        assertNotNull(groupIdRecords1.getGroupIdRecord());
        
        int total = groupIdRecords1.getTotal();
        if(total < 3 || total > 4) {
            fail("There are more group ids than the expected, we are expecting between 3 and 5, total: " + total);
        }                
    }
    
    /**
     * TEST RESEARCHER URLS
     * */
    @SuppressWarnings("unchecked")
    @Test
    public void testViewResearcherUrls() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4443");
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls)response.getEntity();
        assertNotNull(researcherUrls);
        assertEquals("/4444-4444-4444-4443/researcher-urls", researcherUrls.getPath());
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(5, researcherUrls.getResearcherUrls().size());
        for(ResearcherUrl rUrl : researcherUrls.getResearcherUrls()) {
            assertThat(rUrl.getPutCode(), anyOf(equalTo(Long.valueOf(2)), equalTo(Long.valueOf(3)), equalTo(Long.valueOf(5)), equalTo(Long.valueOf(7)), equalTo(Long.valueOf(8))));
            assertNotNull(rUrl.getSource());
            assertFalse(PojoUtil.isEmpty(rUrl.getSource().retrieveSourcePath()));
            assertNotNull(rUrl.getUrl());
            assertNotNull(rUrl.getUrlName());
            assertNotNull(rUrl.getVisibility());
            if(rUrl.getPutCode().equals(Long.valueOf(5)) || rUrl.getPutCode().equals(Long.valueOf(7))) {
                assertEquals("APP-5555555555555555", rUrl.getSource().retrieveSourcePath());
            }
        }
    }
    
    @Test
    public void testViewResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "2");
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=1", researcherUrl.getUrl().getValue());
        assertEquals("443_1", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
        
        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "3");
        assertNotNull(response);
        researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=2", researcherUrl.getUrl().getValue());
        assertEquals("443_2", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
        
        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "5");
        assertNotNull(response);
        researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
        
        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "7");
        assertNotNull(response);
        researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());        
        assertEquals("http://www.researcherurl2.com?id=7", researcherUrl.getUrl().getValue());
        assertEquals("443_5", researcherUrl.getUrlName());
        assertEquals(Visibility.PRIVATE, researcherUrl.getVisibility());        
    }
    
    @Test
    public void testViewPublicResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "2");
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=1", researcherUrl.getUrl().getValue());
        assertEquals("443_1", researcherUrl.getUrlName());
        assertEquals(Visibility.PUBLIC, researcherUrl.getVisibility());
    }
    
    @Test
    public void testViewLimitedResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "8");
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("4444-4444-4444-4443", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=8", researcherUrl.getUrl().getValue());
        assertEquals("443_6", researcherUrl.getUrlName());
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
    }
    
    @Test
    public void testViewPrivateResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "7");
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        
        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.researcherurl2.com?id=7", researcherUrl.getUrl().getValue());
        assertEquals("443_5", researcherUrl.getUrlName());
        assertEquals(Visibility.PRIVATE, researcherUrl.getVisibility());
    }
    
    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivateResearcherUrlWhereYouAreNotTheSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "6");
        fail();
    }
    
    @Test(expected = NoResultException.class)
    public void testViewResearcherUrlThatDontBelongToTheUser() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "1");
        fail();
    }
    
    
    @SuppressWarnings("rawtypes")
    @Test
    public void testAddResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4441", ScopePathType.READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        ResearcherUrl rUrl = new ResearcherUrl();
        rUrl.setUrl(new Url("http://www.myRUrl.com"));
        rUrl.setUrlName("My researcher Url");
        rUrl.setVisibility(Visibility.LIMITED);
        
        Response response = serviceDelegator.createResearcherUrl("4444-4444-4444-4441", rUrl);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Map map = response.getMetadata();
        assertNotNull(map);
        assertTrue(map.containsKey("Location"));
        List resultWithPutCode = (List)map.get("Location");
        String putCode = String.valueOf(resultWithPutCode.get(0));
                
        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4441", putCode);        
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertEquals("APP-5555555555555555", researcherUrl.getSource().retrieveSourcePath());
        assertEquals("http://www.myRUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My researcher Url", researcherUrl.getUrlName());
        assertEquals(Visibility.LIMITED, researcherUrl.getVisibility());
    }
    
    @Test
    public void testUpdateResearcherUrl() {
    	SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.PERSON_UPDATE);
    	Response response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "5");        
        assertNotNull(response);
        ResearcherUrl researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://www.researcherurl2.com?id=5", researcherUrl.getUrl().getValue());
        assertEquals("443_3", researcherUrl.getUrlName());
        
        researcherUrl.setUrl(new Url("http://theNewResearcherUrl.com"));
        researcherUrl.setUrlName("My Updated Researcher Url");
        
        response = serviceDelegator.updateResearcherUrl("4444-4444-4444-4443", "5", researcherUrl);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        response = serviceDelegator.viewResearcherUrl("4444-4444-4444-4443", "5");        
        assertNotNull(response);
        researcherUrl = (ResearcherUrl)response.getEntity();
        assertNotNull(researcherUrl);
        assertNotNull(researcherUrl.getUrl());
        assertEquals("http://theNewResearcherUrl.com", researcherUrl.getUrl().getValue());
        assertEquals("My Updated Researcher Url", researcherUrl.getUrlName());
    }
    
    @Test
    public void testDeleteResearcherUrl() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4445", ScopePathType.READ_LIMITED, ScopePathType.PERSON_UPDATE);
        Response response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4445");
        assertNotNull(response);
        ResearcherUrls researcherUrls = (ResearcherUrls)response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertFalse(researcherUrls.getResearcherUrls().isEmpty());
        ResearcherUrl toDelete = null;
        
        for(ResearcherUrl rurl : researcherUrls.getResearcherUrls()) {
            if(rurl.getSource().retrieveSourcePath().equals("APP-5555555555555555")) {
                toDelete = rurl;
                break;
            }
        }
        
        assertNotNull(toDelete);
        
        response = serviceDelegator.deleteResearcherUrl("4444-4444-4444-4445", String.valueOf(toDelete.getPutCode()));
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        
        response = serviceDelegator.viewResearcherUrls("4444-4444-4444-4445");
        assertNotNull(response);
        researcherUrls = (ResearcherUrls)response.getEntity();
        assertNotNull(researcherUrls);
        assertNotNull(researcherUrls.getResearcherUrls());
        assertEquals(0, researcherUrls.getResearcherUrls().size());               
    }
    
    /**
     * TEST EMAILS
     * */
    @SuppressWarnings("unchecked")
    @Test
    public void testViewEmails() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4443", ScopePathType.READ_LIMITED);
        Response response = serviceDelegator.viewEmails("4444-4444-4444-4443");
        assertNotNull(response);
        Emails emails = (Emails)response.getEntity();
        assertNotNull(emails);
        assertNotNull(emails.getEmails());
        assertEquals(3, emails.getEmails().size());
        for(Email email : emails.getEmails()) {
            assertThat(email.getEmail(), anyOf(is("teddybass2@semantico.com"), is("teddybass3public@semantico.com"), is("teddybass3private@semantico.com")));
            switch(email.getEmail()) {
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
}
