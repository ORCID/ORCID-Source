package org.orcid.api.publicV3.server.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.orcid.core.exception.OrcidNonPublicElementException;
import org.orcid.jaxb.model.v3.rc1.common.Filterable;
import org.orcid.jaxb.model.v3.rc1.common.Visibility;
import org.orcid.jaxb.model.v3.rc1.common.VisibilityType;
import org.orcid.jaxb.model.v3.rc1.record.ActivitiesContainer;
import org.orcid.jaxb.model.v3.rc1.record.Address;
import org.orcid.jaxb.model.v3.rc1.record.Addresses;
import org.orcid.jaxb.model.v3.rc1.record.Biography;
import org.orcid.jaxb.model.v3.rc1.record.Email;
import org.orcid.jaxb.model.v3.rc1.record.Emails;
import org.orcid.jaxb.model.v3.rc1.record.Group;
import org.orcid.jaxb.model.v3.rc1.record.GroupsContainer;
import org.orcid.jaxb.model.v3.rc1.record.Keyword;
import org.orcid.jaxb.model.v3.rc1.record.Keywords;
import org.orcid.jaxb.model.v3.rc1.record.Name;
import org.orcid.jaxb.model.v3.rc1.record.OtherName;
import org.orcid.jaxb.model.v3.rc1.record.OtherNames;
import org.orcid.jaxb.model.v3.rc1.record.Person;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifier;
import org.orcid.jaxb.model.v3.rc1.record.PersonExternalIdentifiers;
import org.orcid.jaxb.model.v3.rc1.record.PersonalDetails;
import org.orcid.jaxb.model.v3.rc1.record.Record;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrl;
import org.orcid.jaxb.model.v3.rc1.record.ResearcherUrls;
import org.orcid.jaxb.model.v3.rc1.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Educations;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Employments;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Fundings;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositions;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Memberships;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Qualifications;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Services;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.Works;
import org.orcid.test.OrcidJUnit4ClassRunner;
import org.springframework.test.context.ContextConfiguration;

@RunWith(OrcidJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-t1-web-context.xml", "classpath:orcid-t1-security-context.xml" })
public class PublicAPISecurityManagerV3Test {

    @Resource
    PublicAPISecurityManagerV3 publicAPISecurityManagerV3;

    @Test
    public void checkIsPublicFilterableTest() {
        publicAPISecurityManagerV3.checkIsPublic(getFilterableElement(Visibility.PUBLIC));

        try {
            publicAPISecurityManagerV3.checkIsPublic(getFilterableElement(Visibility.LIMITED));
            fail();
        } catch (OrcidNonPublicElementException e) {

        }

        try {
            publicAPISecurityManagerV3.checkIsPublic(getFilterableElement(Visibility.PRIVATE));
            fail();
        } catch (OrcidNonPublicElementException e) {

        }
    }

    @Test
    public void checkIsPublicVisibilityTypeTest() {
        publicAPISecurityManagerV3.checkIsPublic(getVisibilityTypeElement(Visibility.PUBLIC));

        try {
            publicAPISecurityManagerV3.checkIsPublic(getVisibilityTypeElement(Visibility.LIMITED));
            fail();
        } catch (OrcidNonPublicElementException e) {

        }

        try {
            publicAPISecurityManagerV3.checkIsPublic(getVisibilityTypeElement(Visibility.PRIVATE));
            fail();
        } catch (OrcidNonPublicElementException e) {

        }
    }

    @Test
    public void checkIsPublicBiographyTest() {
        Biography b = new Biography();
        b.setVisibility(Visibility.PUBLIC);
        b.setContent("Bio test");
        publicAPISecurityManagerV3.checkIsPublic(b);

        try {
            b.setVisibility(Visibility.LIMITED);
            publicAPISecurityManagerV3.checkIsPublic(b);
            fail();
        } catch (OrcidNonPublicElementException e) {

        }

        try {
            b.setVisibility(Visibility.PRIVATE);
            publicAPISecurityManagerV3.checkIsPublic(b);
            fail();
        } catch (OrcidNonPublicElementException e) {

        }
    }

    @Test
    public void checkIsPublicNameTest() {
        Name n = new Name();
        n.setVisibility(Visibility.PUBLIC);
        publicAPISecurityManagerV3.checkIsPublic(n);

        try {
            n.setVisibility(Visibility.LIMITED);
            publicAPISecurityManagerV3.checkIsPublic(n);
            fail();
        } catch (OrcidNonPublicElementException e) {

        }

        try {
            n.setVisibility(Visibility.PRIVATE);
            publicAPISecurityManagerV3.checkIsPublic(n);
            fail();
        } catch (OrcidNonPublicElementException e) {
        }
    }

    @Test
    public void filterAddressesTest() {
        Addresses x = getAddressesElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, x.getAddress().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(3, x.getAddress().size());
        assertAllArePublic(x.getAddress());

        x = getAddressesElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, x.getAddress().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(2, x.getAddress().size());
        assertAllArePublic(x.getAddress());

        x = getAddressesElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getAddress().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(1, x.getAddress().size());
        assertAllArePublic(x.getAddress());

        x = getAddressesElement(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getAddress().size());
        publicAPISecurityManagerV3.filter(x);
        assertTrue(x.getAddress().isEmpty());
    }

    @Test
    public void filterEmailsTest() {
        Emails x = getEmailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, x.getEmails().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(3, x.getEmails().size());
        assertAllArePublic(x.getEmails());

        x = getEmailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, x.getEmails().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(2, x.getEmails().size());
        assertAllArePublic(x.getEmails());

        x = getEmailsElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getEmails().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(1, x.getEmails().size());
        assertAllArePublic(x.getEmails());

        x = getEmailsElement(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getEmails().size());
        publicAPISecurityManagerV3.filter(x);
        assertTrue(x.getEmails().isEmpty());

    }

    @Test
    public void filterExternalIdentifiersTest() {
        PersonExternalIdentifiers x = getPersonExternalIdentifiersElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, x.getExternalIdentifiers().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(3, x.getExternalIdentifiers().size());
        assertAllArePublic(x.getExternalIdentifiers());

        x = getPersonExternalIdentifiersElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, x.getExternalIdentifiers().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(2, x.getExternalIdentifiers().size());
        assertAllArePublic(x.getExternalIdentifiers());

        x = getPersonExternalIdentifiersElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getExternalIdentifiers().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(1, x.getExternalIdentifiers().size());
        assertAllArePublic(x.getExternalIdentifiers());

        x = getPersonExternalIdentifiersElement(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getExternalIdentifiers().size());
        publicAPISecurityManagerV3.filter(x);
        assertTrue(x.getExternalIdentifiers().isEmpty());
    }

    @Test
    public void filterKeywordsTest() {
        Keywords x = getKeywordsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, x.getKeywords().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(3, x.getKeywords().size());
        assertAllArePublic(x.getKeywords());

        x = getKeywordsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, x.getKeywords().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(2, x.getKeywords().size());
        assertAllArePublic(x.getKeywords());

        x = getKeywordsElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getKeywords().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(1, x.getKeywords().size());
        assertAllArePublic(x.getKeywords());

        x = getKeywordsElement(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getKeywords().size());
        publicAPISecurityManagerV3.filter(x);
        assertTrue(x.getKeywords().isEmpty());
    }

    @Test
    public void filterOtherNamesTest() {
        OtherNames x = getOtherNamesElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, x.getOtherNames().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(3, x.getOtherNames().size());
        assertAllArePublic(x.getOtherNames());

        x = getOtherNamesElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, x.getOtherNames().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(2, x.getOtherNames().size());
        assertAllArePublic(x.getOtherNames());

        x = getOtherNamesElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getOtherNames().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(1, x.getOtherNames().size());
        assertAllArePublic(x.getOtherNames());

        x = getOtherNamesElement(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getOtherNames().size());
        publicAPISecurityManagerV3.filter(x);
        assertTrue(x.getOtherNames().isEmpty());
    }

    @Test
    public void filterResearcherUrlsTest() {
        ResearcherUrls x = getResearcherUrlsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, x.getResearcherUrls().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(3, x.getResearcherUrls().size());
        assertAllArePublic(x.getResearcherUrls());

        x = getResearcherUrlsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, x.getResearcherUrls().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(2, x.getResearcherUrls().size());
        assertAllArePublic(x.getResearcherUrls());

        x = getResearcherUrlsElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getResearcherUrls().size());
        publicAPISecurityManagerV3.filter(x);
        assertEquals(1, x.getResearcherUrls().size());
        assertAllArePublic(x.getResearcherUrls());

        x = getResearcherUrlsElement(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, x.getResearcherUrls().size());
        publicAPISecurityManagerV3.filter(x);
        assertTrue(x.getResearcherUrls().isEmpty());
    }

    @Test
    public void filterEmploymentsTest() {
        Employments e = getEmployments(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(3, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getEmployments(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(2, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getEmployments(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(1, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getEmployments(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertTrue(e.retrieveGroups().isEmpty());
    }

    @Test
    public void filterEducationsTest() {
        Educations e = getEducations(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(3, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getEducations(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(2, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getEducations(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(1, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getEducations(Visibility.LIMITED, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertTrue(e.retrieveGroups().isEmpty());
    }

    @Test
    public void filterWorksTest() {
        Works w = getWorks(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, w.getWorkGroup().size());
        publicAPISecurityManagerV3.filter(w);
        assertEquals(3, w.getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(w);

        w = getWorks(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, w.getWorkGroup().size());
        publicAPISecurityManagerV3.filter(w);
        assertEquals(2, w.getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(w);

        w = getWorks(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, w.getWorkGroup().size());
        publicAPISecurityManagerV3.filter(w);
        assertEquals(1, w.getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(w);

        w = getWorks(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, w.getWorkGroup().size());
        publicAPISecurityManagerV3.filter(w);
        assertTrue(w.getWorkGroup().isEmpty());

    }

    @Test
    public void filterFundingsTest() {
        Fundings f = getFundings(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, f.getFundingGroup().size());
        publicAPISecurityManagerV3.filter(f);
        assertEquals(3, f.getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(f);

        f = getFundings(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, f.getFundingGroup().size());
        publicAPISecurityManagerV3.filter(f);
        assertEquals(2, f.getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(f);

        f = getFundings(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, f.getFundingGroup().size());
        publicAPISecurityManagerV3.filter(f);
        assertEquals(1, f.getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(f);

        f = getFundings(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, f.getFundingGroup().size());
        publicAPISecurityManagerV3.filter(f);
        assertTrue(f.getFundingGroup().isEmpty());
    }

    @Test
    public void filterPeerReviewsTest() {
        PeerReviews p = getPeerReviews(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, p.getPeerReviewGroup().size());
        publicAPISecurityManagerV3.filter(p);
        assertEquals(3, p.getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(p);

        p = getPeerReviews(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, p.getPeerReviewGroup().size());
        publicAPISecurityManagerV3.filter(p);
        assertEquals(2, p.getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(p);

        p = getPeerReviews(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, p.getPeerReviewGroup().size());
        publicAPISecurityManagerV3.filter(p);
        assertEquals(1, p.getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(p);

        p = getPeerReviews(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, p.getPeerReviewGroup().size());
        publicAPISecurityManagerV3.filter(p);
        assertTrue(p.getPeerReviewGroup().isEmpty());
    }

    @Test
    public void filterDistinctionsTest() {
        Distinctions e = getDistinctions(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(3, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getDistinctions(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(2, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getDistinctions(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(1, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getDistinctions(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertTrue(e.retrieveGroups().isEmpty());
    }

    @Test
    public void filterInvitedPositionsTest() {
        InvitedPositions e = getInvitedPositions(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(3, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getInvitedPositions(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(2, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getInvitedPositions(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(1, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getInvitedPositions(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertTrue(e.retrieveGroups().isEmpty());
    }

    @Test
    public void filterMembershipsTest() {
        Memberships e = getMemberships(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(3, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getMemberships(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(2, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getMemberships(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(1, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getMemberships(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertTrue(e.retrieveGroups().isEmpty());
    }

    @Test
    public void filterQualificationsTest() {
        Qualifications e = getQualifications(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(3, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getQualifications(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(2, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getQualifications(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(1, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getQualifications(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertTrue(e.retrieveGroups().isEmpty());
    }

    @Test
    public void filterServicesTest() {
        Services e = getServices(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(3, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getServices(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(2, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getServices(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertEquals(1, e.retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(e);

        e = getServices(Visibility.PRIVATE, Visibility.LIMITED, Visibility.PRIVATE);
        assertEquals(3, e.retrieveGroups().size());
        publicAPISecurityManagerV3.filter(e);
        assertTrue(e.retrieveGroups().isEmpty());
    }

    @Test
    public void checkIsPublicActivitiesSummaryTest() {
        ActivitiesSummary as = getActivitiesSummaryElement();
        publicAPISecurityManagerV3.filter(as);
        // Assert it have all activities
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters educations
        as = getActivitiesSummaryElement();
        setVisibility(as.getEducations(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);

        assertNotNull(as.getEducations());
        assertTrue(as.getEducations().retrieveGroups().isEmpty());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters employments
        as = getActivitiesSummaryElement();
        setVisibility(as.getEmployments(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);

        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertNotNull(as.getEmployments());
        assertTrue(as.getEmployments().retrieveGroups().isEmpty());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters distinctions
        as = getActivitiesSummaryElement();
        setVisibility(as.getDistinctions(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertTrue(as.getDistinctions().retrieveGroups().isEmpty());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters invited positions
        as = getActivitiesSummaryElement();
        setVisibility(as.getInvitedPositions(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertTrue(as.getInvitedPositions().retrieveGroups().isEmpty());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters memberships
        as = getActivitiesSummaryElement();
        setVisibility(as.getMemberships(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertTrue(as.getMemberships().retrieveGroups().isEmpty());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters qualifications
        as = getActivitiesSummaryElement();
        setVisibility(as.getQualifications(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertTrue(as.getQualifications().retrieveGroups().isEmpty());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters services
        as = getActivitiesSummaryElement();
        setVisibility(as.getServices(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertTrue(as.getServices().retrieveGroups().isEmpty());

        // Assert it filters funding
        as = getActivitiesSummaryElement();
        setVisibility(as.getFundings(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);

        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertTrue(as.getFundings().getFundingGroup().isEmpty());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters peer reviews
        as = getActivitiesSummaryElement();
        setVisibility(as.getPeerReviews(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);

        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().isEmpty());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Assert it filters works
        as = getActivitiesSummaryElement();
        setVisibility(as.getWorks(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(as);

        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertTrue(as.getWorks().getWorkGroup().isEmpty());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());
    }

    @Test
    public void checkIsPublicPersonalDetailsTest() {
        PersonalDetails p = getPersonalDetailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC);
        publicAPISecurityManagerV3.filter(p);
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        assertNotNull(p.getOtherNames().getOtherNames());
        p.getOtherNames().getOtherNames().forEach(e -> {
            assertIsPublic(e);
        });

        // Should not fail, but name should be empty
        p = getPersonalDetailsElement(Visibility.LIMITED, Visibility.PUBLIC, Visibility.PUBLIC);
        publicAPISecurityManagerV3.filter(p);
        assertNull(p.getName());
        assertNotNull(p.getBiography());
        assertNotNull(p.getOtherNames().getOtherNames());
        p.getOtherNames().getOtherNames().forEach(e -> {
            assertIsPublic(e);
        });

        // Should not fail, but bio should be null
        p = getPersonalDetailsElement(Visibility.PUBLIC, Visibility.LIMITED, Visibility.PUBLIC);
        publicAPISecurityManagerV3.filter(p);
        assertNotNull(p.getName());
        assertNull(p.getBiography());
        assertNotNull(p.getOtherNames().getOtherNames());
        p.getOtherNames().getOtherNames().forEach(e -> {
            assertIsPublic(e);
        });

        p = getPersonalDetailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.LIMITED);
        publicAPISecurityManagerV3.filter(p);
        assertNotNull(p.getName());
        assertNotNull(p.getBiography());
        assertNotNull(p.getOtherNames());
        assertTrue(p.getOtherNames().getOtherNames().isEmpty());

        p = getPersonalDetailsElement(Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PRIVATE);
        publicAPISecurityManagerV3.filter(p);
        assertNotNull(p.getName());
        assertNotNull(p.getBiography());
        assertNotNull(p.getOtherNames());
        assertTrue(p.getOtherNames().getOtherNames().isEmpty());
    }

    @Test
    public void checkIsPublicPersonTest() {
        Person p = getPersonElement();
        publicAPISecurityManagerV3.filter(p);

        // Nothing is filtered yet
        assertEquals(3, p.getAddresses().getAddress().size());
        p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        assertEquals(3, p.getEmails().getEmails().size());
        p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getKeywords().getKeywords().size());
        p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

        // Addresses filtered
        p = getPersonElement();
        setVisibility(p.getAddresses().getAddress(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(p);
        // --- filtered ---
        assertNotNull(p.getAddresses());
        assertTrue(p.getAddresses().getAddress().isEmpty());
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        assertEquals(3, p.getEmails().getEmails().size());
        p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getKeywords().getKeywords().size());
        p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

        // Bio filtered
        p = getPersonElement();
        p.getBiography().setVisibility(Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(p);
        assertEquals(3, p.getAddresses().getAddress().size());
        p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
        // --- filtered ---
        assertNull(p.getBiography());
        assertEquals(3, p.getEmails().getEmails().size());
        p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getKeywords().getKeywords().size());
        p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

        // Emails filtered
        p = getPersonElement();
        setVisibility(p.getEmails().getEmails(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(p);
        assertEquals(3, p.getAddresses().getAddress().size());
        p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        // --- filtered ---
        assertNotNull(p.getEmails());
        assertTrue(p.getEmails().getEmails().isEmpty());
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getKeywords().getKeywords().size());
        p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

        // External ids filtered
        p = getPersonElement();
        setVisibility(p.getExternalIdentifiers().getExternalIdentifiers(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(p);
        assertEquals(3, p.getAddresses().getAddress().size());
        p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        assertEquals(3, p.getEmails().getEmails().size());
        p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
        // --- filtered ---
        assertNotNull(p.getExternalIdentifiers());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().isEmpty());
        assertEquals(3, p.getKeywords().getKeywords().size());
        p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

        // Keywords filtered
        p = getPersonElement();
        setVisibility(p.getKeywords().getKeywords(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(p);
        assertEquals(3, p.getAddresses().getAddress().size());
        p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        assertEquals(3, p.getEmails().getEmails().size());
        p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
        // --- filtered ---
        assertNotNull(p.getKeywords());
        assertTrue(p.getKeywords().getKeywords().isEmpty());
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

        // Name filtered
        p = getPersonElement();
        p.getName().setVisibility(Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(p);
        assertEquals(3, p.getAddresses().getAddress().size());
        p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        assertEquals(3, p.getEmails().getEmails().size());
        p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getKeywords().getKeywords().size());
        p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
        // --- filtered ---
        assertNull(p.getName());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

        // Other names filtered
        p = getPersonElement();
        setVisibility(p.getOtherNames().getOtherNames(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(p);
        assertEquals(3, p.getAddresses().getAddress().size());
        p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        assertEquals(3, p.getEmails().getEmails().size());
        p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getKeywords().getKeywords().size());
        p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        // --- filtered ---
        assertNotNull(p.getOtherNames());
        assertTrue(p.getOtherNames().getOtherNames().isEmpty());
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        p.getResearcherUrls().getResearcherUrls().forEach(e -> assertIsPublic(e));

        // Researcher urls filtered
        p = getPersonElement();
        setVisibility(p.getResearcherUrls().getResearcherUrls(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(p);
        assertEquals(3, p.getAddresses().getAddress().size());
        p.getAddresses().getAddress().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getBiography().getVisibility());
        assertEquals(3, p.getEmails().getEmails().size());
        p.getEmails().getEmails().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        p.getExternalIdentifiers().getExternalIdentifiers().forEach(e -> assertIsPublic(e));
        assertEquals(3, p.getKeywords().getKeywords().size());
        p.getKeywords().getKeywords().forEach(e -> assertIsPublic(e));
        assertEquals(Visibility.PUBLIC, p.getName().getVisibility());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        p.getOtherNames().getOtherNames().forEach(e -> assertIsPublic(e));
        // --- filtered ---
        assertNotNull(p.getResearcherUrls());
        assertTrue(p.getResearcherUrls().getResearcherUrls().isEmpty());
    }

    @Test
    public void checkIsPublicRecordTest() {
        Record r = getRecordElement();
        publicAPISecurityManagerV3.filter(r);

        // Verify activities - nothing filtered
        ActivitiesSummary as = r.getActivitiesSummary();
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEducations());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getEmployments());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertGroupContainsOnlyPublicElements(as.getFundings());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertPeerReviewsContainsOnlyPublicElements(as.getPeerReviews());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertGroupContainsOnlyPublicElements(as.getWorks());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Verify bio sections - nothing filtered
        Person p = r.getPerson();
        assertEquals(3, p.getAddresses().getAddress().size());
        assertAllArePublic(p.getAddresses().getAddress());
        assertEquals(3, p.getEmails().getEmails().size());
        assertAllArePublic(p.getEmails().getEmails());
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertAllArePublic(p.getExternalIdentifiers().getExternalIdentifiers());
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertAllArePublic(p.getKeywords().getKeywords());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertAllArePublic(p.getOtherNames().getOtherNames());
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertAllArePublic(p.getResearcherUrls().getResearcherUrls());
        assertNotNull(p.getBiography());
        assertNotNull(p.getName());

        // Filter biography, name, educations and funding
        r = getRecordElement();
        r.getPerson().getBiography().setVisibility(Visibility.LIMITED);
        r.getPerson().getName().setVisibility(Visibility.LIMITED);
        setVisibility(r.getActivitiesSummary().getEducations(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        setVisibility(r.getActivitiesSummary().getFundings(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(r);

        // Verify activities - educations and funding filtered
        as = r.getActivitiesSummary();
        assertNotNull(as.getEducations());
        assertTrue(as.getEducations().retrieveGroups().isEmpty());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertTrue(as.getFundings().getFundingGroup().isEmpty());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Verify bio sections - bio and name filtered
        p = r.getPerson();
        assertEquals(3, p.getAddresses().getAddress().size());
        assertEquals(3, p.getEmails().getEmails().size());
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertNull(p.getBiography());
        assertNull(p.getName());

        // Filter emails, external identifiers, peer reviews and works
        r = getRecordElement();
        setVisibility(r.getPerson().getEmails().getEmails(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        setVisibility(r.getPerson().getExternalIdentifiers().getExternalIdentifiers(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        setVisibility(r.getActivitiesSummary().getPeerReviews(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        setVisibility(r.getActivitiesSummary().getWorks(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(r);

        // Verify activities - peer reviews and works filtered
        as = r.getActivitiesSummary();
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertTrue(as.getPeerReviews().getPeerReviewGroup().isEmpty());
        assertTrue(as.getWorks().getWorkGroup().isEmpty());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Verify bio sections - emails, external identifiers filtered
        p = r.getPerson();
        assertEquals(3, p.getAddresses().getAddress().size());
        assertNotNull(p.getEmails());
        assertTrue(p.getEmails().getEmails().isEmpty());
        assertNotNull(p.getExternalIdentifiers());
        assertTrue(p.getExternalIdentifiers().getExternalIdentifiers().isEmpty());
        assertEquals(3, p.getKeywords().getKeywords().size());
        assertEquals(3, p.getOtherNames().getOtherNames().size());
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertNotNull(p.getBiography());
        assertNotNull(p.getName());

        // Filter keywords and other names
        r = getRecordElement();
        setVisibility(r.getPerson().getOtherNames().getOtherNames(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);
        setVisibility(r.getPerson().getKeywords().getKeywords(), Visibility.LIMITED, Visibility.PRIVATE, Visibility.LIMITED);

        publicAPISecurityManagerV3.filter(r);

        // Verify activities - nothing filtered
        as = r.getActivitiesSummary();
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().size());
        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getDistinctions());
        assertEquals(3, as.getInvitedPositions().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getInvitedPositions());
        assertEquals(3, as.getMemberships().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getMemberships());
        assertEquals(3, as.getQualifications().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getQualifications());
        assertEquals(3, as.getServices().retrieveGroups().size());
        assertGroupContainsOnlyPublicElements(as.getServices());

        // Verify bio sections - keywords and other names filtered
        p = r.getPerson();
        assertEquals(3, p.getAddresses().getAddress().size());
        assertEquals(3, p.getEmails().getEmails().size());
        assertEquals(3, p.getExternalIdentifiers().getExternalIdentifiers().size());
        assertNotNull(p.getKeywords());
        assertTrue(p.getKeywords().getKeywords().isEmpty());
        assertNotNull(p.getOtherNames());
        assertTrue(p.getOtherNames().getOtherNames().isEmpty());
        assertEquals(3, p.getResearcherUrls().getResearcherUrls().size());
        assertNotNull(p.getBiography());
        assertNotNull(p.getName());
    }

    /**
     * Utilities
     */
    private Filterable getFilterableElement(Visibility v) {
        EducationSummary s = new EducationSummary();
        s.setVisibility(v);
        return s;
    }

    private VisibilityType getVisibilityTypeElement(Visibility v) {
        EducationSummary s = new EducationSummary();
        s.setVisibility(v);
        return s;
    }

    private Employments getEmployments(Visibility... vs) {
        Employments e = new Employments();
        for (Visibility v : vs) {
            AffiliationGroup<EmploymentSummary> g = new AffiliationGroup<EmploymentSummary>();
            EmploymentSummary s = new EmploymentSummary();
            s.setVisibility(v);
            g.getActivities().add(s);
            e.getEmploymentGroups().add(g);
        }
        return e;
    }

    private Educations getEducations(Visibility... vs) {
        Educations e = new Educations();
        for (Visibility v : vs) {
            AffiliationGroup<EducationSummary> g = new AffiliationGroup<EducationSummary>();
            EducationSummary s = new EducationSummary();
            s.setVisibility(v);
            g.getActivities().add(s);
            e.getEducationGroups().add(g);
        }
        return e;
    }

    private Distinctions getDistinctions(Visibility... vs) {
        Distinctions e = new Distinctions();
        for (Visibility v : vs) {
            AffiliationGroup<DistinctionSummary> g = new AffiliationGroup<DistinctionSummary>();
            DistinctionSummary s = new DistinctionSummary();
            s.setVisibility(v);
            g.getActivities().add(s);
            e.getDistinctionGroups().add(g);
        }
        return e;
    }

    private InvitedPositions getInvitedPositions(Visibility... vs) {
        InvitedPositions e = new InvitedPositions();
        for (Visibility v : vs) {
            AffiliationGroup<InvitedPositionSummary> g = new AffiliationGroup<InvitedPositionSummary>();
            InvitedPositionSummary s = new InvitedPositionSummary();
            s.setVisibility(v);
            g.getActivities().add(s);
            e.getInvitedPositionGroups().add(g);
        }
        return e;
    }

    private Memberships getMemberships(Visibility... vs) {
        Memberships e = new Memberships();
        for (Visibility v : vs) {
            AffiliationGroup<MembershipSummary> g = new AffiliationGroup<MembershipSummary>();
            MembershipSummary s = new MembershipSummary();
            s.setVisibility(v);
            g.getActivities().add(s);
            e.getMembershipGroups().add(g);
        }
        return e;
    }

    private Qualifications getQualifications(Visibility... vs) {
        Qualifications e = new Qualifications();
        for (Visibility v : vs) {
            AffiliationGroup<QualificationSummary> g = new AffiliationGroup<QualificationSummary>();
            QualificationSummary s = new QualificationSummary();
            s.setVisibility(v);
            g.getActivities().add(s);
            e.getQualificationGroups().add(g);
        }
        return e;
    }

    private Services getServices(Visibility... vs) {
        Services e = new Services();
        for (Visibility v : vs) {
            AffiliationGroup<ServiceSummary> g = new AffiliationGroup<ServiceSummary>();
            ServiceSummary s = new ServiceSummary();
            s.setVisibility(v);
            g.getActivities().add(s);
            e.getServiceGroups().add(g);
        }
        return e;
    }

    private Works getWorks(Visibility... vs) {
        Works works = new Works();
        for (Visibility v : vs) {
            WorkGroup g = new WorkGroup();
            WorkSummary s = new WorkSummary();
            s.setVisibility(v);
            g.getWorkSummary().add(s);
            works.getWorkGroup().add(g);
        }
        return works;
    }

    private Fundings getFundings(Visibility... vs) {
        Fundings fundings = new Fundings();
        for (Visibility v : vs) {
            FundingGroup g = new FundingGroup();
            FundingSummary s = new FundingSummary();
            s.setVisibility(v);
            g.getFundingSummary().add(s);
            fundings.getFundingGroup().add(g);
        }
        return fundings;
    }

    private PeerReviews getPeerReviews(Visibility... vs) {
        PeerReviews peerReviews = new PeerReviews();
        for (Visibility v : vs) {
            PeerReviewGroup g = new PeerReviewGroup();
            PeerReviewSummary s = new PeerReviewSummary();
            s.setVisibility(v);
            PeerReviewDuplicateGroup d = new PeerReviewDuplicateGroup();
            d.getPeerReviewSummary().add(s);
            g.getPeerReviewGroup().add(d);
            peerReviews.getPeerReviewGroup().add(g);
        }
        return peerReviews;
    }

    private ActivitiesSummary getActivitiesSummaryElement() {
        Visibility[] vs = { Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC };
        ActivitiesSummary s = new ActivitiesSummary();
        s.setDistinctions(getDistinctions(vs));
        s.setEducations(getEducations(vs));
        s.setEmployments(getEmployments(vs));
        s.setInvitedPositions(getInvitedPositions(vs));
        s.setMemberships(getMemberships(vs));
        s.setQualifications(getQualifications(vs));
        s.setServices(getServices(vs));
        s.setFundings(getFundings(vs));
        s.setPeerReviews(getPeerReviews(vs));
        s.setWorks(getWorks(vs));
        return s;
    }

    private OtherNames getOtherNamesElement(Visibility... vs) {
        OtherNames otherNames = new OtherNames();
        for (Visibility v : vs) {
            OtherName o = new OtherName();
            o.setVisibility(v);
            if (otherNames.getOtherNames() == null) {
                otherNames.setOtherNames(new ArrayList<OtherName>());
            }
            otherNames.getOtherNames().add(o);
        }
        return otherNames;
    }

    private Addresses getAddressesElement(Visibility... vs) {
        Addresses elements = new Addresses();
        for (Visibility v : vs) {
            Address element = new Address();
            element.setVisibility(v);
            if (elements.getAddress() == null) {
                elements.setAddress(new ArrayList<Address>());
            }
            elements.getAddress().add(element);
        }
        return elements;
    }

    private Emails getEmailsElement(Visibility... vs) {
        Emails elements = new Emails();
        for (Visibility v : vs) {
            Email element = new Email();
            element.setVisibility(v);
            if (elements.getEmails() == null) {
                elements.setEmails(new ArrayList<Email>());
            }
            elements.getEmails().add(element);
        }
        return elements;
    }

    private PersonExternalIdentifiers getPersonExternalIdentifiersElement(Visibility... vs) {
        PersonExternalIdentifiers elements = new PersonExternalIdentifiers();
        for (Visibility v : vs) {
            PersonExternalIdentifier element = new PersonExternalIdentifier();
            element.setVisibility(v);
            if (elements.getExternalIdentifiers() == null) {
                elements.setExternalIdentifiers(new ArrayList<PersonExternalIdentifier>());
            }
            elements.getExternalIdentifiers().add(element);
        }
        return elements;
    }

    private Keywords getKeywordsElement(Visibility... vs) {
        Keywords elements = new Keywords();
        for (Visibility v : vs) {
            Keyword element = new Keyword();
            element.setVisibility(v);
            if (elements.getKeywords() == null) {
                elements.setKeywords(new ArrayList<Keyword>());
            }
            elements.getKeywords().add(element);
        }
        return elements;
    }

    private ResearcherUrls getResearcherUrlsElement(Visibility... vs) {
        ResearcherUrls elements = new ResearcherUrls();
        for (Visibility v : vs) {
            ResearcherUrl element = new ResearcherUrl();
            element.setVisibility(v);
            if (elements.getResearcherUrls() == null) {
                elements.setResearcherUrls(new ArrayList<ResearcherUrl>());
            }
            elements.getResearcherUrls().add(element);
        }
        return elements;
    }

    private PersonalDetails getPersonalDetailsElement(Visibility nameVisibility, Visibility bioVisiblity, Visibility otherNamesVisibility) {
        PersonalDetails p = new PersonalDetails();
        Name name = new Name();
        name.setVisibility(nameVisibility);
        p.setName(name);
        Biography bio = new Biography();
        bio.setVisibility(bioVisiblity);
        bio.setContent("Bio test");
        p.setBiography(bio);
        p.setOtherNames(getOtherNamesElement(otherNamesVisibility));
        return p;
    }

    private Person getPersonElement() {
        Visibility[] vs = { Visibility.PUBLIC, Visibility.PUBLIC, Visibility.PUBLIC };
        Person p = new Person();
        p.setAddresses(getAddressesElement(vs));
        p.setEmails(getEmailsElement(vs));
        p.setExternalIdentifiers(getPersonExternalIdentifiersElement(vs));
        p.setKeywords(getKeywordsElement(vs));
        p.setOtherNames(getOtherNamesElement(vs));
        p.setResearcherUrls(getResearcherUrlsElement(vs));

        Name name = new Name();
        name.setVisibility(Visibility.PUBLIC);
        p.setName(name);

        Biography b = new Biography();
        b.setVisibility(Visibility.PUBLIC);
        b.setContent("Biography test");
        p.setBiography(b);

        return p;
    }

    private Record getRecordElement() {
        Record r = new Record();
        r.setActivitiesSummary(getActivitiesSummaryElement());
        r.setPerson(getPersonElement());
        return r;
    }

    private void setVisibility(List<? extends Filterable> elements, Visibility... vs) {
        assertEquals(elements.size(), vs.length);
        for (int i = 0; i < vs.length; i++) {
            elements.get(i).setVisibility(vs[i]);
        }
    }

    private void setVisibility(PeerReviews peerReviews, Visibility... vs) {
        assertEquals(peerReviews.retrieveGroups().size(), vs.length);
        int idx = 0;
        for (PeerReviewGroup g : peerReviews.retrieveGroups()) {
            for (PeerReviewDuplicateGroup duplicateGroup : g.getPeerReviewGroup()) {
                // Every group have just one element
                assertEquals(1, duplicateGroup.getActivities().size());
                for (Filterable f : duplicateGroup.getActivities()) {
                    f.setVisibility(vs[idx++]);
                }
            }
        }
    }

    private void setVisibility(GroupsContainer container, Visibility... vs) {
        assertEquals(container.retrieveGroups().size(), vs.length);
        int idx = 0;
        for (Group g : container.retrieveGroups()) {
            // Every group have just one element
            assertEquals(1, g.getActivities().size());
            for (Filterable f : g.getActivities()) {
                f.setVisibility(vs[idx++]);
            }
        }
    }

    /**
     * Assert helpers
     */
    private void assertIsPublic(Filterable a) {
        assertEquals(Visibility.PUBLIC, a.getVisibility());
    }

    private void assertAllArePublic(List<? extends Filterable> list) {
        if (list == null) {
            return;
        }
        list.forEach(e -> {
            assertIsPublic(e);
        });
    }

    private void assertGroupContainsOnlyPublicElements(GroupsContainer container) {
        if (container == null || container.retrieveGroups() == null || container.retrieveGroups().isEmpty()) {
            fail("No activities");
        }
        container.retrieveGroups().forEach(x -> {
            assertNotNull(x.getActivities());
            x.getActivities().forEach(e -> {
                assertIsPublic(e);
            });
        });
    }

    private void assertPeerReviewsContainsOnlyPublicElements(PeerReviews peerReviews) {
        if (peerReviews == null || peerReviews.retrieveGroups() == null || peerReviews.retrieveGroups().isEmpty()) {
            fail("No activities");
        }

        for (PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            for (PeerReviewDuplicateGroup duplicateGroup : group.getPeerReviewGroup()) {
                assertNotNull(duplicateGroup.getPeerReviewSummary());
                for (PeerReviewSummary summary : duplicateGroup.getPeerReviewSummary()) {
                    assertIsPublic(summary);
                }
            }
        }
    }

    private void assertContainerContainsOnlyPublicElements(ActivitiesContainer container) {
        if (container == null || container.retrieveActivities() == null || container.retrieveActivities().isEmpty()) {
            fail("No activities");
        }
        container.retrieveActivities().forEach(x -> {
            assertIsPublic(x);
        });
    }
}
