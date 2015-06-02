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
import static org.junit.Assert.assertThat;
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
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.common.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.Funding;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.Subject;
import org.orcid.jaxb.model.record.Work;
import org.orcid.jaxb.model.record.WorkType;
import org.orcid.jaxb.model.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary.PeerReviewSummary;
import org.orcid.test.DBUnitTest;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-api-web-context.xml", "classpath:orcid-api-security-context.xml" })
public class MemberV2ApiServiceDelegatorTest extends DBUnitTest {
    private static final List<String> DATA_FILES = Arrays.asList("/data/EmptyEntityData.xml", "/data/SecurityQuestionEntityData.xml",
            "/data/SourceClientDetailsEntityData.xml", "/data/ProfileEntityData.xml", "/data/WorksEntityData.xml", "/data/ProfileWorksEntityData.xml",
            "/data/ClientDetailsEntityData.xml", "/data/Oauth2TokenDetailsData.xml", "/data/OrgsEntityData.xml", "/data/ProfileFundingEntityData.xml",
            "/data/OrgAffiliationEntityData.xml", "/data/PeerReviewSubjectEntityData.xml", "/data/PeerReviewEntityData.xml");

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
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewActivities("4444-4444-4444-4446");
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        // Check works
        assertNotNull(summary.getWorks());
        assertEquals(3, summary.getWorks().getWorkGroup().size());
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPutCode(),
                anyOf(is("5"), is("6"), is("7")));
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));
        assertThat(summary.getWorks().getWorkGroup().get(1).getWorkSummary().get(0).getPutCode(),
                anyOf(is("5"), is("6"), is("7")));
        assertThat(summary.getWorks().getWorkGroup().get(1).getWorkSummary().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(1).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));                        
        assertThat(summary.getWorks().getWorkGroup().get(2).getWorkSummary().get(0).getPutCode(),
                anyOf(is("5"), is("6"), is("7")));
        assertThat(summary.getWorks().getWorkGroup().get(2).getWorkSummary().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/work/5"), is("/4444-4444-4444-4446/work/6"), is("/4444-4444-4444-4446/work/7")));
        assertThat(summary.getWorks().getWorkGroup().get(2).getWorkSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Journal article A"), is("Journal article B"), is("Journal article C")));

        // Check fundings
        assertNotNull(summary.getFundings());
        assertEquals(2, summary.getFundings().getFundingGroup().size());
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPutCode(),
                anyOf(is("4"), is("5")));        
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getPath(),
                anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5")));
        
        assertThat(summary.getFundings().getFundingGroup().get(0).getFundingSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Private Funding"), is("Public Funding")));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getPutCode(),
                anyOf(is("4"), is("5")));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getPath(),
                anyOf(is("/4444-4444-4444-4446/funding/4"), is("/4444-4444-4444-4446/funding/5")));
        assertThat(summary.getFundings().getFundingGroup().get(1).getFundingSummary().get(0).getTitle().getTitle().getContent(),
                anyOf(is("Private Funding"), is("Limited Funding")));

        // Check Educations
        assertNotNull(summary.getEducations());
        assertNotNull(summary.getEducations().getSummaries());
        assertEquals(2, summary.getEducations().getSummaries().size());
        assertThat(summary.getEducations().getSummaries().get(0).getPutCode(), anyOf(is("6"), is("7")));
        assertThat(summary.getEducations().getSummaries().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/education/6"), is("/4444-4444-4444-4446/education/7")));
        assertThat(summary.getEducations().getSummaries().get(0).getDepartmentName(), anyOf(is("Education Dept # 1"), is("Education Dept # 2")));

        // Check Employments
        assertNotNull(summary.getEmployments());
        assertNotNull(summary.getEmployments().getSummaries());
        assertEquals(2, summary.getEmployments().getSummaries().size());
        assertThat(summary.getEmployments().getSummaries().get(0).getPutCode(), anyOf(is("5"), is("8")));
        assertThat(summary.getEmployments().getSummaries().get(0).getPath(), anyOf(is("/4444-4444-4444-4446/employment/5"), is("/4444-4444-4444-4446/employment/8")));
        assertThat(summary.getEmployments().getSummaries().get(0).getDepartmentName(), anyOf(is("Employment Dept # 1"), is("Employment Dept # 2")));
        
        // Check Peer reviews
        assertNotNull(summary.getPeerReviews());
        assertEquals(1, summary.getPeerReviews().getPeerReviewGroup().size());
        PeerReviewSummary peerReviewSummary = summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewSummary().get(0); 
        assertEquals("1", peerReviewSummary.getPutCode());
        assertNotNull(peerReviewSummary.getCompletionDate());
        assertEquals("01", peerReviewSummary.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReviewSummary.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReviewSummary.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReviewSummary.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertEquals("reviewer", peerReviewSummary.getRole().value());
        assertEquals("APP-5555555555555555", peerReviewSummary.getSource().retrieveSourcePath());
        assertEquals("public", peerReviewSummary.getVisibility().value());        
    }

    @Test
    public void testViewWork() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewWork("4444-4444-4444-4446", "5");
        assertNotNull(response);
        Work work = (Work) response.getEntity();
        assertNotNull(work);
        assertNotNull(work.getWorkTitle());
        assertNotNull(work.getWorkTitle().getTitle());
        assertEquals("Journal article A", work.getWorkTitle().getTitle().getContent());
        assertEquals("5", work.getPutCode());
        assertEquals("/4444-4444-4444-4446/work/5", work.getPath());
        assertEquals(WorkType.JOURNAL_ARTICLE, work.getWorkType());
    }

    @Test
    public void testViewFunding() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewFunding("4444-4444-4444-4446", "4");
        assertNotNull(response);
        Funding funding = (Funding) response.getEntity();
        assertNotNull(funding);
        assertNotNull(funding.getTitle());
        assertNotNull(funding.getTitle().getTitle());
        assertEquals("4", funding.getPutCode());
        assertEquals("/4444-4444-4444-4446/funding/4", funding.getPath());
        assertEquals("Private Funding", funding.getTitle().getTitle().getContent());
        assertEquals(Visibility.PRIVATE.value(), funding.getVisibility().value());
    }

    @Test
    public void testViewEducation() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEducation("4444-4444-4444-4446", "6");
        assertNotNull(response);
        Education education = (Education) response.getEntity();
        assertNotNull(education);
        assertEquals("6", education.getPutCode());
        assertEquals("/4444-4444-4444-4446/education/6", education.getPath());
        assertEquals("Education Dept # 1", education.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), education.getVisibility().value());
    }

    @Test
    public void testViewEmployment() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewEmployment("4444-4444-4444-4446", "5");
        assertNotNull(response);
        Employment employment = (Employment) response.getEntity();
        assertNotNull(employment);        
        assertEquals("5", employment.getPutCode());
        assertEquals("/4444-4444-4444-4446/employment/5", employment.getPath());
        assertEquals("Employment Dept # 1", employment.getDepartmentName());
        assertEquals(Visibility.PRIVATE.value(), employment.getVisibility().value());
    }
    
    @Test
    public void testViewPeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", "1");
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("1", peerReview.getPutCode());
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
        Subject subject = peerReview.getSubject();
        assertEquals("1", subject.getPutCode());
        assertEquals("Peer Review # 1", subject.getTitle().getTitle().getContent());
        assertEquals("Peer Review # 1 translated title", subject.getTitle().getTranslatedTitle().getContent());
        assertEquals("es", subject.getTitle().getTranslatedTitle().getLanguageCode());
        assertEquals("Peer Review # 1 subtitle", subject.getTitle().getSubtitle().getContent());
        assertEquals("artistic-performance", subject.getType().value());
        assertEquals("http://work.com", subject.getUrl().getValue());
        assertEquals("Peer Review # 1 journal title", subject.getJournalTitle().getContent());        
        assertNotNull(subject.getExternalIdentifiers());
        assertEquals(1, subject.getExternalIdentifiers().getExternalIdentifier().size());
        assertEquals("peer-review-subject:external-identifier-id#1", subject.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertEquals("agr", subject.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierType().value());
    }
    
    @Test
    public void testViewPeerReviewSummary() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReviewSummary("4444-4444-4444-4446", "1");
        assertNotNull(response);
        PeerReviewSummary peerReview= (PeerReviewSummary) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("1", peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getWorkExternalIdentifierId().getContent());
        assertEquals("reviewer", peerReview.getRole().value());
        assertEquals("APP-5555555555555555", peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());                
    }
    
    @Test
    public void testUpdatePeerReview() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4446", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", "1");
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubject().getTitle().getTitle().setContent("Updated Title");
        serviceDelegator.updatePeerReview("4444-4444-4444-4446", "1", peerReview);
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4446", "1");
        PeerReview updatedPeerReview= (PeerReview) response.getEntity();
        assertNotNull(updatedPeerReview);
        assertEquals("http://updated.com/url", updatedPeerReview.getUrl().getValue());
        assertEquals("Updated Title", updatedPeerReview.getSubject().getTitle().getTitle().getContent());
    }
    
    @Test
    public void testUpdatePeerReviewWhenNotSource() {
        SecurityContextTestUtils.setUpSecurityContext("4444-4444-4444-4447", ScopePathType.ACTIVITIES_READ_LIMITED, ScopePathType.ACTIVITIES_UPDATE);
        Response response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", "2");
        assertNotNull(response);
        PeerReview peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());
        
        //Update the info
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubject().getTitle().getTitle().setContent("Updated Title");
        
        //Try to update it
        try {
            response = serviceDelegator.updatePeerReview("4444-4444-4444-4447", "2", peerReview);
            fail();
        } catch(WrongSourceException wse) {
            
        }
        response = serviceDelegator.viewPeerReview("4444-4444-4444-4447", "2");
        peerReview= (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());
    }
}
