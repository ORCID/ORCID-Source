package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.utils.SecurityContextTestUtils;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.ActivitiesSummary;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record.summary_v2.FundingSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.WorkSummary;

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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertTrue(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.LIMITED, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);

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
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(1, as.getEducations().getSummaries().size());
        assertFalse(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertFalse(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

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
        as.setEducations(createEducations(e1, e2, e3));
        as.setEmployments(createEmployments(em1, em2, em3));
        as.setFundings(createFundings(f1, f2, f3));
        as.setPeerReviews(createPeerReviews(p1, p2, p3));
        as.setWorks(createWorks(w1, w2, w3));

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(1, as.getEducations().getSummaries().size());
        assertFalse(as.getEducations().getSummaries().contains(e1));
        assertFalse(as.getEducations().getSummaries().contains(e2));
        assertTrue(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertFalse(as.getEmployments().getSummaries().contains(em1));
        assertFalse(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(0, as.getEducations().getSummaries().size());
        // Check employments
        assertEquals(0, as.getEmployments().getSummaries().size());
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
        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertTrue(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertTrue(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.LIMITED, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertTrue(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(1, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertFalse(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertFalse(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(0, as.getEducations().getSummaries().size());
        // Check employments
        assertEquals(0, as.getEmployments().getSummaries().size());
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
        EducationSummary e1 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary e2 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_1);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_1);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(3, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertTrue(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(3, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertTrue(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);
        assertNotNull(as);
        // Check educations
        assertEquals(2, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check employments
        assertEquals(2, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        assertNotNull(as);
        // Check it have limited Educations
        assertNotNull(as.getEducations());
        assertEquals(2, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check it have limited Employments
        assertNotNull(as.getEmployments());
        assertEquals(2, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check it have only public Educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertFalse(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertFalse(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check it have only public Educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertFalse(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertFalse(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check it have only public Educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertFalse(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertFalse(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check it have only public Educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertFalse(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertFalse(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check it have limited Educations
        assertNotNull(as.getEducations());
        assertEquals(2, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check it have limited Employments
        assertNotNull(as.getEmployments());
        assertEquals(2, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check it have limited Educations
        assertNotNull(as.getEducations());
        assertEquals(2, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertTrue(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check it have limited Employments
        assertNotNull(as.getEmployments());
        assertEquals(2, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertTrue(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
        EducationSummary e1 = createEducationSummary(Visibility.PUBLIC, CLIENT_2);
        EducationSummary e2 = createEducationSummary(Visibility.LIMITED, CLIENT_2);
        EducationSummary e3 = createEducationSummary(Visibility.PRIVATE, CLIENT_2);

        EmploymentSummary em1 = createEmploymentSummary(Visibility.PUBLIC, CLIENT_2);
        EmploymentSummary em2 = createEmploymentSummary(Visibility.LIMITED, CLIENT_2);
        EmploymentSummary em3 = createEmploymentSummary(Visibility.PRIVATE, CLIENT_2);

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

        orcidSecurityManager.checkAndFilter(ORCID_1, as);

        // Check it have only public Educations
        assertNotNull(as.getEducations());
        assertEquals(1, as.getEducations().getSummaries().size());
        assertTrue(as.getEducations().getSummaries().contains(e1));
        assertFalse(as.getEducations().getSummaries().contains(e2));
        assertFalse(as.getEducations().getSummaries().contains(e3));
        // Check it have only public Employments
        assertNotNull(as.getEmployments());
        assertEquals(1, as.getEmployments().getSummaries().size());
        assertTrue(as.getEmployments().getSummaries().contains(em1));
        assertFalse(as.getEmployments().getSummaries().contains(em2));
        assertFalse(as.getEmployments().getSummaries().contains(em3));
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
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p1));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p2));
        assertFalse(as.getPeerReviews().getPeerReviewGroup().get(0).getActivities().contains(p3));
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
}
