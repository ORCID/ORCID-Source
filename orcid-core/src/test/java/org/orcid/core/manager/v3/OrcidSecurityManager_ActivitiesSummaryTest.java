package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Affiliations;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;

/**
 * 
 * @author Will Simpson
 *
 */
public class OrcidSecurityManager_ActivitiesSummaryTest extends OrcidSecurityManagerTestBase {

    // ---- ACTIVITIES ----
    @Test(expected = OrcidUnauthorizedException.class)
    public void testActivitiesSummary_When_TokenForOtherUser() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        ActivitiesSummary as = new ActivitiesSummary();
        orcidSecurityManager.checkAndFilter(ORCID_2, as);
        fail();
    }

    @Test
    public void testActivitiesSummary_When_AllPublic_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        
        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        
        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        
        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(),d2));
        assertTrue(affiliationsContainSummary(as.getDistinctions(),d3));
        // Check educations
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertTrue(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em3));
        // Check invited positions
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertTrue(affiliationsContainSummary(as.getServices(), s3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_SomeLimited_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.LIMITED, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.LIMITED, CLIENT_2);        
        
        FundingSummary f1 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(1, as.getInvitedPositions().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(1, as.getMemberships().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(1, as.getQualifications().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(1, as.getServices().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check educations
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_SomePrivate_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d1));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d2));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(1, as.getInvitedPositions().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(1, as.getMemberships().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getMemberships(), m1));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m2));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(1, as.getQualifications().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getQualifications(), q1));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q2));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(1, as.getServices().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getServices(), s1));
        assertFalse(affiliationsContainSummary(as.getServices(), s2));
        assertTrue(affiliationsContainSummary(as.getServices(), s3));
        // Check educations
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getEducations(), e1));
        assertFalse(affiliationsContainSummary(as.getEducations(), e2));
        assertTrue(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertFalse(affiliationsContainSummary(as.getEmployments(), em1));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em2));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_AllPrivate_NoSource_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(0, as.getDistinctions().retrieveGroups().size());
        // Check invited positions
        assertEquals(0, as.getInvitedPositions().retrieveGroups().size());
        // Check memberships
        assertEquals(0, as.getMemberships().retrieveGroups().size());
        // Check qualifications
        assertEquals(0, as.getQualifications().retrieveGroups().size());
        // Check services
        assertEquals(0, as.getServices().retrieveGroups().size());        
        // Check educations
        assertEquals(0, as.getEducations().retrieveGroups().size());
        // Check employments
        assertEquals(0, as.getEmployments().retrieveGroups().size());
        // Check fundings
        assertEquals(0, as.getFundings().getFundingGroup().size());
        // Check peer reviews
        assertEquals(0, as.getPeerReviews().getPeerReviewGroup().size());
        // Check works
        assertEquals(0, as.getWorks().getWorkGroup().size());
    }

    @Test
    public void testActivitiesSummary_When_AllPrivate_Source_ReadPublicToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.READ_PUBLIC);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_1);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_1);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_1);

        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_1);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_1);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_1);

        MembershipSummary m1 = createMembershipSummary(Visibility.PRIVATE, CLIENT_1);
        MembershipSummary m2 = createMembershipSummary(Visibility.PRIVATE, CLIENT_1);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_1);

        QualificationSummary q1 = createQualificationSummary(Visibility.PRIVATE, CLIENT_1);
        QualificationSummary q2 = createQualificationSummary(Visibility.PRIVATE, CLIENT_1);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_1);

        ServiceSummary s1 = createServiceSummary(Visibility.PRIVATE, CLIENT_1);
        ServiceSummary s2 = createServiceSummary(Visibility.PRIVATE, CLIENT_1);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_1);

        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check educations
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertTrue(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em3));
        // Check invited positions
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertTrue(affiliationsContainSummary(as.getServices(), s3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }
   
    @Test
    public void testActivitiesSummary_When_AllPublic_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertTrue(affiliationsContainSummary(as.getServices(), s3));
        // Check educations
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertTrue(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_SomeLimited_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.LIMITED, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        
        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertTrue(affiliationsContainSummary(as.getServices(), s3));
        // Check educations
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertTrue(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_SomePrivate_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(1, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(1, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(1, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(1, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertFalse(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check educations
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertFalse(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_AllPrivate_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        
        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        
        // Check distinctions
        assertEquals(0, as.getDistinctions().retrieveGroups().size());
        // Check invited positions
        assertEquals(0, as.getInvitedPositions().retrieveGroups().size());
        // Check memberships
        assertEquals(0, as.getMemberships().retrieveGroups().size());
        // Check services
        assertEquals(0, as.getServices().retrieveGroups().size());
        // Check qualifications
        assertEquals(0, as.getQualifications().retrieveGroups().size());
        // Check educations
        assertEquals(0, as.getEducations().retrieveGroups().size());
        // Check employments
        assertEquals(0, as.getEmployments().retrieveGroups().size());
        // Check fundings
        assertEquals(0, as.getFundings().getFundingGroup().size());
        // Check peer reviews
        assertEquals(0, as.getPeerReviews().getPeerReviewGroup().size());
        // Check works
        assertEquals(0, as.getWorks().getWorkGroup().size());
    }

    @Test
    public void testActivitiesSummary_When_AllPrivate_Source_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_1);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_1);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_1);

        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_1);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_1);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_1);

        MembershipSummary m1 = createMembershipSummary(Visibility.PRIVATE, CLIENT_1);
        MembershipSummary m2 = createMembershipSummary(Visibility.PRIVATE, CLIENT_1);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_1);

        QualificationSummary q1 = createQualificationSummary(Visibility.PRIVATE, CLIENT_1);
        QualificationSummary q2 = createQualificationSummary(Visibility.PRIVATE, CLIENT_1);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_1);

        ServiceSummary s1 = createServiceSummary(Visibility.PRIVATE, CLIENT_1);
        ServiceSummary s2 = createServiceSummary(Visibility.PRIVATE, CLIENT_1);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_1);
        
        FundingSummary f1 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_1, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertTrue(affiliationsContainSummary(as.getServices(), s3));
        // Check educations
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertTrue(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(4, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(4, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_MixedVisibility_NoSource_ReadLimitedToken() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);
        
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check distinctions
        assertEquals(2, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(2, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(2, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(2, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(2, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check educations
        assertEquals(2, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check employments
        assertEquals(2, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check fundings
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check peer reviews
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check works
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_ReadLimitedToken_EmptyElement() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ACTIVITIES_READ_LIMITED);
        ActivitiesSummary as = new ActivitiesSummary();
        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
    }

    @Test
    public void testActivitiesSummary_When_AffiliationsReadLimited_And_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        assertNotNull(as);
        // Check distinctions
        assertEquals(2, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(2, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(2, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(2, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(2, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check it have limited Educations
        assertNotNull(as.getEducations());
        assertEquals(2, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check it have limited Employments
        assertNotNull(as.getEmployments());
        assertEquals(2, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check it have only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check it have only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check it have only public works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_FundingReadLimited_And_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.FUNDING_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        assertNotNull(as.getEducations());
        
        // Check distinctions
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(1, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(1, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(1, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(1, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertFalse(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check it have only public Educations        
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertFalse(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check it have limited funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check it have only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check it have only public works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_PeerReviewReadLimited_And_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.PEER_REVIEW_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        assertNotNull(as.getEducations());
        // Check distinctions
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(1, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(1, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(1, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(1, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertFalse(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check it have only public Educations        
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertFalse(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check it have only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check it have limited peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check it have only public works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_WorksReadLimited_And_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        assertNotNull(as.getEducations());
        // Check distinctions
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(1, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(1, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(1, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(1, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertFalse(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check it have only public Educations        
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertFalse(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check it have only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check it have only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check it have limited works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_WorksAndFundingReadLimited_And_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_WORKS_READ_LIMITED, ScopePathType.FUNDING_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check distinctions
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(1, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(1, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(1, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(1, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertFalse(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));// Check it have only public Educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertFalse(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check it have limited funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check it have only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check it have limited works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_AffiliationsAndPeerReviewReadLimited_And_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.PEER_REVIEW_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check distinctions
        assertEquals(2, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(2, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(2, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(2, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(2, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check it have limited Educations
        assertNotNull(as.getEducations());
        assertEquals(2, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check it have limited Employments
        assertNotNull(as.getEmployments());
        assertEquals(2, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check it have only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check it have limited peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check it have only public works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
    }

    @Test
    public void testActivitiesSummary_When_AllReadLimited_And_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.AFFILIATIONS_READ_LIMITED, ScopePathType.FUNDING_READ_LIMITED,
                ScopePathType.PEER_REVIEW_READ_LIMITED, ScopePathType.ORCID_WORKS_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);
     
        // Check distinctions
        assertEquals(2, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(2, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(2, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertTrue(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(2, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertTrue(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(2, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertTrue(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check it have limited Educations
        assertNotNull(as.getEducations());
        assertEquals(2, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertTrue(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check it have limited Employments
        assertNotNull(as.getEmployments());
        assertEquals(2, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertTrue(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check it have limited funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(3, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check it have limited reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check it have limited works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(3, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));        
    }

    @Test
    public void testActivitiesSummary_When_OrcidBioReadLimited_And_NotSource() {
        SecurityContextTestUtils.setUpSecurityContext(ORCID_1, CLIENT_1, ScopePathType.ORCID_BIO_READ_LIMITED);
        DistinctionSummary d1 = createDistinctionSummary(Visibility.PUBLIC, CLIENT_2);
        DistinctionSummary d2 = createDistinctionSummary(Visibility.LIMITED, CLIENT_2);
        DistinctionSummary d3 = createDistinctionSummary(Visibility.PRIVATE, CLIENT_2);

        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

        InvitedPositionSummary i1 = createInvitedPositionSummary(Visibility.PUBLIC, CLIENT_2);
        InvitedPositionSummary i2 = createInvitedPositionSummary(Visibility.LIMITED, CLIENT_2);
        InvitedPositionSummary i3 = createInvitedPositionSummary(Visibility.PRIVATE, CLIENT_2);

        MembershipSummary m1 = createMembershipSummary(Visibility.PUBLIC, CLIENT_2);
        MembershipSummary m2 = createMembershipSummary(Visibility.LIMITED, CLIENT_2);
        MembershipSummary m3 = createMembershipSummary(Visibility.PRIVATE, CLIENT_2);

        QualificationSummary q1 = createQualificationSummary(Visibility.PUBLIC, CLIENT_2);
        QualificationSummary q2 = createQualificationSummary(Visibility.LIMITED, CLIENT_2);
        QualificationSummary q3 = createQualificationSummary(Visibility.PRIVATE, CLIENT_2);

        ServiceSummary s1 = createServiceSummary(Visibility.PUBLIC, CLIENT_2);
        ServiceSummary s2 = createServiceSummary(Visibility.LIMITED, CLIENT_2);
        ServiceSummary s3 = createServiceSummary(Visibility.PRIVATE, CLIENT_2);
        
        FundingSummary f1 = createFundingSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        FundingSummary f2 = createFundingSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        FundingSummary f3 = createFundingSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        PeerReviewSummary p1 = createPeerReviewSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        PeerReviewSummary p2 = createPeerReviewSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        PeerReviewSummary p3 = createPeerReviewSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        WorkSummary w1 = createWorkSummary(Visibility.PUBLIC, CLIENT_2, EXTID_1);
        WorkSummary w2 = createWorkSummary(Visibility.LIMITED, CLIENT_2, EXTID_2);
        WorkSummary w3 = createWorkSummary(Visibility.PRIVATE, CLIENT_2, EXTID_3);

        ActivitiesSummary as = new ActivitiesSummary();
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));
        as.setDistinctions(createDistinctions(d1, d2, d3));
        as.setInvitedPositions(createInvitedPositions(i1, i2, i3));
        as.setMemberships(createMemberships(m1, m2, m3));
        as.setQualifications(createQualifications(q1, q2, q3));
        as.setServices(createServices(s1, s2, s3));
        
        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check distinctions
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getDistinctions(), d1));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d2));
        assertFalse(affiliationsContainSummary(as.getDistinctions(), d3));
        // Check invited positions
        assertEquals(1, as.getInvitedPositions().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getInvitedPositions(), i1));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i2));
        assertFalse(affiliationsContainSummary(as.getInvitedPositions(), i3));
        // Check memberships
        assertEquals(1, as.getMemberships().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getMemberships(), m1));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m2));
        assertFalse(affiliationsContainSummary(as.getMemberships(), m3));
        // Check qualifications
        assertEquals(1, as.getQualifications().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getQualifications(), q1));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q2));
        assertFalse(affiliationsContainSummary(as.getQualifications(), q3));
        // Check services
        assertEquals(1, as.getServices().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getServices(), s1));
        assertFalse(affiliationsContainSummary(as.getServices(), s2));
        assertFalse(affiliationsContainSummary(as.getServices(), s3));
        // Check it have only public Educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEducations(), e1));
        assertFalse(affiliationsContainSummary(as.getEducations(), e2));
        assertFalse(affiliationsContainSummary(as.getEducations(), e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertTrue(affiliationsContainSummary(as.getEmployments(), em1));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em2));
        assertFalse(affiliationsContainSummary(as.getEmployments(), em3));
        // Check it have only public funding
        assertNotNull(as.getFundings());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getFundings().getFundingGroup().get(0).getActivities().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getActivities().contains(f1));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f2));
        assertFalse(as.getFundings().getFundingGroup().get(0).getActivities().contains(f3));
        assertEquals(2, as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getFundings().getFundingGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));
        // Check it have only public peer reviews
        assertNotNull(as.getPeerReviews());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().contains(p3));
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED, "peer-review")));
        // Check it have only public works
        assertNotNull(as.getWorks());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().get(0).getActivities().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getActivities().contains(w1));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w2));
        assertFalse(as.getWorks().getWorkGroup().get(0).getActivities().contains(w3));
        assertEquals(2, as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().size());
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_1)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_2)));
        assertFalse(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_3)));
        assertTrue(as.getWorks().getWorkGroup().get(0).getIdentifiers().getExternalIdentifier().contains(getExtId(EXTID_SHARED)));        
    }
    
    private <T extends AffiliationSummary> boolean affiliationsContainSummary(Affiliations<T> affiliations, T summary) {
        for (AffiliationGroup<T> group : affiliations.retrieveGroups()) {
            if (group.getActivities().contains(summary)) {
                return true;
            }
        }
        return false;
    }
}
