package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Predicate;

import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.orcid.api.common.util.v3.ActivityUtils;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.jaxb.model.common.FundingType;
import org.orcid.jaxb.model.common.PeerReviewType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.Role;
import org.orcid.jaxb.model.common.WorkType;
import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.FundingTitle;
import org.orcid.jaxb.model.v3.release.record.WorkTitle;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.release.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Distinctions;
import org.orcid.jaxb.model.v3.release.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Educations;
import org.orcid.jaxb.model.v3.release.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Employments;
import org.orcid.jaxb.model.v3.release.record.summary.FundingGroup;
import org.orcid.jaxb.model.v3.release.record.summary.FundingSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Fundings;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.release.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.release.record.summary.Works;
import org.orcid.test.helper.v3.Utils;
import org.orcid.utils.DateUtils;

/**
 * Mocked boundary tests for the activities endpoint of the member V3 API.
 *
 * <p>
 * {@code viewActivities} hands the whole summary to
 * {@code OrcidSecurityManager.checkAndFilter(orcid, activities)}, which removes
 * elements in place. Which elements a given scope leaves behind is the security
 * manager's table and is proved by orcid-core's
 * {@code OrcidSecurityManager_ActivitiesSummaryTest}; with the manager mocked,
 * asserting that table here would only be asserting what this test built. The
 * {@code _NoSource} tests therefore install a stand-in for the filter and assert
 * the delegator's own half: that it returns the summary the filter mutated, with
 * every section's path stamped and every source name resolved.
 */
public class MemberV3ApiServiceDelegator_ActivitiesSummaryTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private ActivitiesSummary fullActivities(Source source) {
        ActivitiesSummary activities = emptyActivitiesSummary();

        Distinctions distinctions = new Distinctions();
        distinctions.retrieveGroups().add(affiliationGroup(distinctionSummary(30L, Visibility.LIMITED, source), distinctionSummary(27L, Visibility.PUBLIC, source)));
        distinctions.retrieveGroups().add(affiliationGroup(distinctionSummary(28L, Visibility.LIMITED, source)));
        distinctions.retrieveGroups().add(affiliationGroup(distinctionSummary(29L, Visibility.PRIVATE, source)));
        activities.setDistinctions(distinctions);

        Educations educations = new Educations();
        educations.retrieveGroups().add(affiliationGroup(educationSummary(25L, Visibility.LIMITED, source), educationSummary(20L, Visibility.PUBLIC, source)));
        educations.retrieveGroups().add(affiliationGroup(educationSummary(21L, Visibility.LIMITED, source)));
        educations.retrieveGroups().add(affiliationGroup(educationSummary(22L, Visibility.PRIVATE, source)));
        activities.setEducations(educations);

        Employments employments = new Employments();
        employments.retrieveGroups().add(affiliationGroup(employmentSummary(23L, Visibility.LIMITED, source), employmentSummary(17L, Visibility.PUBLIC, source)));
        employments.retrieveGroups().add(affiliationGroup(employmentSummary(18L, Visibility.LIMITED, source)));
        employments.retrieveGroups().add(affiliationGroup(employmentSummary(19L, Visibility.PRIVATE, source)));
        activities.setEmployments(employments);

        Fundings fundings = new Fundings();
        fundings.getFundingGroup().add(fundingGroup("1", fundingSummary(10L, Visibility.PUBLIC, source)));
        fundings.getFundingGroup().add(fundingGroup("2", fundingSummary(11L, Visibility.LIMITED, source)));
        fundings.getFundingGroup().add(fundingGroup("3", fundingSummary(12L, Visibility.PRIVATE, source)));
        activities.setFundings(fundings);

        PeerReviews peerReviews = new PeerReviews();
        peerReviews.getPeerReviewGroup().add(peerReviewGroup("issn:0000-0009", peerReviewSummary(9L, Visibility.PUBLIC, source)));
        peerReviews.getPeerReviewGroup().add(peerReviewGroup("issn:0000-0010", peerReviewSummary(10L, Visibility.LIMITED, source)));
        peerReviews.getPeerReviewGroup().add(peerReviewGroup("issn:0000-0011", peerReviewSummary(11L, Visibility.PRIVATE, source)));
        activities.setPeerReviews(peerReviews);

        Works works = new Works();
        works.getWorkGroup().add(workGroup("1", workSummary(11L, "PUBLIC", Visibility.PUBLIC, source)));
        works.getWorkGroup().add(workGroup("2", workSummary(12L, "LIMITED", Visibility.LIMITED, source)));
        works.getWorkGroup().add(workGroup("3", workSummary(13L, "PRIVATE", Visibility.PRIVATE, source)));
        activities.setWorks(works);

        return activities;
    }

    @SafeVarargs
    private final <T extends org.orcid.jaxb.model.v3.release.record.summary.AffiliationSummary> AffiliationGroup<T> affiliationGroup(T... elements) {
        AffiliationGroup<T> group = new AffiliationGroup<>();
        for (T element : elements) {
            group.getActivities().add(element);
        }
        return group;
    }

    private DistinctionSummary distinctionSummary(long putCode, Visibility visibility, Source source) {
        DistinctionSummary element = new DistinctionSummary();
        element.setPutCode(putCode);
        element.setDepartmentName("Department " + putCode);
        element.setOrganization(Utils.getOrganization());
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private EducationSummary educationSummary(long putCode, Visibility visibility, Source source) {
        EducationSummary element = new EducationSummary();
        element.setPutCode(putCode);
        element.setDepartmentName("Department " + putCode);
        element.setOrganization(Utils.getOrganization());
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private EmploymentSummary employmentSummary(long putCode, Visibility visibility, Source source) {
        EmploymentSummary element = new EmploymentSummary();
        element.setPutCode(putCode);
        element.setDepartmentName("Department " + putCode);
        element.setOrganization(Utils.getOrganization());
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private FundingSummary fundingSummary(long putCode, Visibility visibility, Source source) {
        FundingSummary element = new FundingSummary();
        element.setPutCode(putCode);
        FundingTitle title = new FundingTitle();
        title.setTitle(new Title("Funding " + putCode));
        element.setTitle(title);
        element.setType(FundingType.AWARD);
        element.setOrganization(Utils.getOrganization());
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private PeerReviewSummary peerReviewSummary(long putCode, Visibility visibility, Source source) {
        PeerReviewSummary element = new PeerReviewSummary();
        element.setPutCode(putCode);
        element.setGroupId("issn:000" + putCode);
        element.setOrganization(Utils.getOrganization());
        element.setRole(Role.REVIEWER);
        element.setType(PeerReviewType.REVIEW);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private WorkSummary workSummary(long putCode, String title, Visibility visibility, Source source) {
        WorkSummary element = new WorkSummary();
        element.setPutCode(putCode);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title(title));
        element.setTitle(workTitle);
        element.setType(WorkType.JOURNAL_ARTICLE);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private ExternalID externalId(String value) {
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://doi/" + value));
        extId.setValue(value);
        return extId;
    }

    private FundingGroup fundingGroup(String externalIdValue, FundingSummary summary) {
        FundingGroup group = new FundingGroup();
        group.getIdentifiers().getExternalIdentifier().add(externalId(externalIdValue));
        group.getFundingSummary().add(summary);
        return group;
    }

    private PeerReviewGroup peerReviewGroup(String groupId, PeerReviewSummary summary) {
        PeerReviewGroup group = new PeerReviewGroup();
        group.getIdentifiers().getExternalIdentifier().add(externalId(groupId));
        PeerReviewDuplicateGroup duplicateGroup = new PeerReviewDuplicateGroup();
        duplicateGroup.getPeerReviewSummary().add(summary);
        group.getPeerReviewGroup().add(duplicateGroup);
        return group;
    }

    private WorkGroup workGroup(String externalIdValue, WorkSummary summary) {
        WorkGroup group = new WorkGroup();
        group.getIdentifiers().getExternalIdentifier().add(externalId(externalIdValue));
        group.getWorkSummary().add(summary);
        return group;
    }

    /**
     * Stands in for {@code OrcidSecurityManagerImpl}'s in-place filter over an
     * activities summary. {@code keep} decides what survives; which visibilities
     * a given scope actually keeps is proved in orcid-core.
     */
    private void filterActivitiesWith(Predicate<Visibility> keep) {
        doAnswer(invocation -> {
            ActivitiesSummary activities = invocation.getArgument(1);
            activities.getDistinctions().retrieveGroups().forEach(g -> g.getActivities().removeIf(e -> !keep.test(e.getVisibility())));
            activities.getDistinctions().retrieveGroups().removeIf(g -> g.getActivities().isEmpty());
            activities.getEducations().retrieveGroups().forEach(g -> g.getActivities().removeIf(e -> !keep.test(e.getVisibility())));
            activities.getEducations().retrieveGroups().removeIf(g -> g.getActivities().isEmpty());
            activities.getEmployments().retrieveGroups().forEach(g -> g.getActivities().removeIf(e -> !keep.test(e.getVisibility())));
            activities.getEmployments().retrieveGroups().removeIf(g -> g.getActivities().isEmpty());
            activities.getFundings().getFundingGroup().forEach(g -> g.getFundingSummary().removeIf(e -> !keep.test(e.getVisibility())));
            activities.getFundings().getFundingGroup().removeIf(g -> g.getFundingSummary().isEmpty());
            activities.getPeerReviews().getPeerReviewGroup()
                    .forEach(g -> g.getPeerReviewGroup().forEach(d -> d.getPeerReviewSummary().removeIf(e -> !keep.test(e.getVisibility()))));
            activities.getPeerReviews().getPeerReviewGroup().removeIf(g -> g.getPeerReviewGroup().get(0).getPeerReviewSummary().isEmpty());
            activities.getWorks().getWorkGroup().forEach(g -> g.getWorkSummary().removeIf(e -> !keep.test(e.getVisibility())));
            activities.getWorks().getWorkGroup().removeIf(g -> g.getWorkSummary().isEmpty());
            return null;
        }).when(orcidSecurityManager).checkAndFilter(eq(ORCID), any(ActivitiesSummary.class));
    }

    private static final Predicate<Visibility> PUBLIC_ONLY = Visibility.PUBLIC::equals;

    private static final Predicate<Visibility> PUBLIC_AND_LIMITED = v -> Visibility.PUBLIC.equals(v) || Visibility.LIMITED.equals(v);

    @Test
    public void testViewActivitiesReadPublic() {
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_1)));
        filterActivitiesWith(PUBLIC_ONLY);

        Response r = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary element = (ActivitiesSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/activities", element.getPath());
        assertEquals(1, element.getDistinctions().retrieveGroups().size());
        assertEquals(1, element.getEducations().retrieveGroups().size());
        assertEquals(1, element.getEmployments().retrieveGroups().size());
        assertEquals(1, element.getFundings().getFundingGroup().size());
        assertEquals(1, element.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, element.getWorks().getWorkGroup().size());
        assertEquals("/0000-0000-0000-0003/work/11", element.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getPath());
        assertEquals(CLIENT_1_NAME, element.getWorks().getWorkGroup().get(0).getWorkSummary().get(0).getSource().getSourceName().getContent());
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewActivitiesWrongToken() {
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(eq(ORCID),
                any(ActivitiesSummary.class));

        serviceDelegator.viewActivities(ORCID);
    }

    @Test
    public void testViewActitivies() {
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewActivities(ORCID);
        assertNotNull(response);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals("/0000-0000-0000-0003/activities", as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());

        assertEquals(3, as.getDistinctions().retrieveGroups().size());
        assertEquals(3, as.getEducations().retrieveGroups().size());
        assertEquals(3, as.getEmployments().retrieveGroups().size());
        assertEquals(3, as.getFundings().getFundingGroup().size());
        assertEquals(3, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(3, as.getWorks().getWorkGroup().size());

        assertEquals("/0000-0000-0000-0003/distinctions", as.getDistinctions().getPath());
        assertEquals("/0000-0000-0000-0003/educations", as.getEducations().getPath());
        assertEquals("/0000-0000-0000-0003/employments", as.getEmployments().getPath());
        assertEquals("/0000-0000-0000-0003/fundings", as.getFundings().getPath());
        assertEquals("/0000-0000-0000-0003/peer-reviews", as.getPeerReviews().getPath());
        assertEquals("/0000-0000-0000-0003/works", as.getWorks().getPath());

        verify(orcidSecurityManager).checkProfile(ORCID);
        verify(orcidSecurityManager).checkAndFilter(ORCID, as);
        verify(activitiesSummaryManagerReadOnly).getActivitiesSummary(ORCID, false);
    }

    /**
     * Touches no collaborator: {@code ActivityUtils.cleanEmptyFields} is static
     * and runs for real, so this stays exactly as it was.
     */
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
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_1)));
        filterActivitiesWith(PUBLIC_ONLY);

        Response r = serviceDelegator.viewActivities(ORCID);
        assertNotNull(r);
        assertEquals(ActivitiesSummary.class.getName(), r.getEntity().getClass().getName());
        ActivitiesSummary as = (ActivitiesSummary) r.getEntity();
        assertNotNull(as);
        Utils.verifyLastModified(as.getLastModifiedDate());
        for (AffiliationGroup<DistinctionSummary> group : as.getDistinctions().retrieveGroups()) {
            for (DistinctionSummary element : group.getActivities()) {
                assertEquals(Visibility.PUBLIC, element.getVisibility());
            }
        }
        for (WorkGroup group : as.getWorks().getWorkGroup()) {
            for (WorkSummary element : group.getWorkSummary()) {
                assertEquals(Visibility.PUBLIC, element.getVisibility());
            }
        }
        assertEquals(1, as.getWorks().getWorkGroup().size());
    }

    @Test
    public void testViewActitivies_AffiliationsReadLimited_NoSource() {
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_2)));
        filterActivitiesWith(PUBLIC_AND_LIMITED);

        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertNotNull(as.getPath());
        Utils.verifyLastModified(as.getLastModifiedDate());

        assertNotNull(as.getDistinctions());
        assertEquals(2, as.getDistinctions().retrieveGroups().size());
        boolean found1 = false, found2 = false;
        for (AffiliationGroup<DistinctionSummary> group : as.getDistinctions().retrieveGroups()) {
            DistinctionSummary element0 = group.getActivities().get(0);
            if (element0.getPutCode() == 30L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                assertEquals(2, group.getActivities().size());
                assertEquals(Long.valueOf(27), group.getActivities().get(1).getPutCode());
                assertEquals(Visibility.PUBLIC, group.getActivities().get(1).getVisibility());
                found1 = true;
            } else if (element0.getPutCode() == 28L) {
                assertEquals(Visibility.LIMITED, element0.getVisibility());
                found2 = true;
            }
        }
        assertTrue(found1);
        assertTrue(found2);

        assertNotNull(as.getEducations());
        assertEquals(2, as.getEducations().retrieveGroups().size());
        assertEquals("/0000-0000-0000-0003/educations", as.getEducations().getPath());
        assertNotNull(as.getEmployments());
        assertEquals(2, as.getEmployments().retrieveGroups().size());
        verify(orcidSecurityManager).checkAndFilter(ORCID, as);
    }

    @Test
    public void testViewActitivies_FundingReadLimited_NoSource() {
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_2)));
        filterActivitiesWith(PUBLIC_AND_LIMITED);

        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertNotNull(as.getFundings());
        assertEquals(2, as.getFundings().getFundingGroup().size());
        assertEquals("/0000-0000-0000-0003/fundings", as.getFundings().getPath());
        for (FundingGroup group : as.getFundings().getFundingGroup()) {
            FundingSummary summary = group.getFundingSummary().get(0);
            assertTrue(Visibility.PUBLIC.equals(summary.getVisibility()) || Visibility.LIMITED.equals(summary.getVisibility()));
            assertEquals("/0000-0000-0000-0003/funding/" + summary.getPutCode(), summary.getPath());
        }
        verify(orcidSecurityManager).checkAndFilter(ORCID, as);
    }

    @Test
    public void testViewActitivies_PeerReviewReadLimited_NoSource() {
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_2)));
        filterActivitiesWith(PUBLIC_AND_LIMITED);

        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertNotNull(as.getPeerReviews());
        assertEquals(2, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals("/0000-0000-0000-0003/peer-reviews", as.getPeerReviews().getPath());
        for (PeerReviewGroup group : as.getPeerReviews().getPeerReviewGroup()) {
            PeerReviewSummary summary = group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0);
            assertTrue(Visibility.PUBLIC.equals(summary.getVisibility()) || Visibility.LIMITED.equals(summary.getVisibility()));
            assertEquals("/0000-0000-0000-0003/peer-review/" + summary.getPutCode(), summary.getPath());
        }
        verify(orcidSecurityManager).checkAndFilter(ORCID, as);
    }

    @Test
    public void testViewActitivies_WorksReadLimited_NoSource() {
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_2)));
        filterActivitiesWith(PUBLIC_AND_LIMITED);

        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertNotNull(as.getWorks());
        assertEquals(2, as.getWorks().getWorkGroup().size());
        assertEquals("/0000-0000-0000-0003/works", as.getWorks().getPath());
        for (WorkGroup group : as.getWorks().getWorkGroup()) {
            WorkSummary summary = group.getWorkSummary().get(0);
            assertTrue(Visibility.PUBLIC.equals(summary.getVisibility()) || Visibility.LIMITED.equals(summary.getVisibility()));
            assertEquals("/0000-0000-0000-0003/work/" + summary.getPutCode(), summary.getPath());
        }
        verify(orcidSecurityManager).checkAndFilter(ORCID, as);
    }

    @Test
    public void testViewActitivies_NoReadLimited_NoSource() {
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(ORCID, false)).thenReturn(fullActivities(clientSource(CLIENT_2)));
        filterActivitiesWith(PUBLIC_ONLY);

        Response response = serviceDelegator.viewActivities(ORCID);
        ActivitiesSummary as = (ActivitiesSummary) response.getEntity();
        assertNotNull(as);
        assertEquals(1, as.getDistinctions().retrieveGroups().size());
        assertEquals(1, as.getEducations().retrieveGroups().size());
        assertEquals(1, as.getEmployments().retrieveGroups().size());
        assertEquals(1, as.getFundings().getFundingGroup().size());
        assertEquals(1, as.getPeerReviews().getPeerReviewGroup().size());
        assertEquals(1, as.getWorks().getWorkGroup().size());
        for (AffiliationGroup<DistinctionSummary> group : as.getDistinctions().retrieveGroups()) {
            for (DistinctionSummary element : group.getActivities()) {
                assertEquals(Visibility.PUBLIC, element.getVisibility());
            }
        }
        verify(orcidSecurityManager).checkAndFilter(ORCID, as);
    }
}
