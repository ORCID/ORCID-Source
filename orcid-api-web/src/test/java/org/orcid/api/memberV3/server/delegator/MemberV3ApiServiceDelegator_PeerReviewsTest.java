package org.orcid.api.memberV3.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import jakarta.persistence.NoResultException;
import jakarta.ws.rs.core.Response;

import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.orcid.core.exception.ActivityIdentifierValidationException;
import org.orcid.core.exception.OrcidAccessControlException;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.exception.OrcidUnauthorizedException;
import org.orcid.core.exception.OrcidVisibilityException;
import org.orcid.core.exception.VisibilityMismatchException;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.jaxb.model.common.PeerReviewSubjectType;
import org.orcid.jaxb.model.common.PeerReviewType;
import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.common.Role;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.release.common.FuzzyDate;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Title;
import org.orcid.jaxb.model.v3.release.common.TranslatedTitle;
import org.orcid.jaxb.model.v3.release.common.Url;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.ExternalID;
import org.orcid.jaxb.model.v3.release.record.ExternalIDs;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.SubjectName;
import org.orcid.jaxb.model.v3.release.record.summary.ActivitiesSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewDuplicateGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewGroup;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviews;
import org.orcid.test.helper.v3.Utils;

/**
 * Mocked boundary tests for the peer-review endpoints of the member V3 API.
 *
 * <p>
 * {@code checkAndFilter} is void and filters in place, so a mocked security
 * manager filters nothing and any "only the public reviews came back" assertion
 * would hold vacuously; those tables are proved in orcid-core by
 * {@code OrcidSecurityManager_generalTest}. The ISSN group-id plumbing the old
 * fixture reached through {@code TargetProxyHelper} belongs to
 * {@code GroupIdRecordManager} and is exercised in
 * MemberV3ApiServiceDelegator_GroupIdTest, so it is gone from here along with
 * the Spring context.
 */
public class MemberV3ApiServiceDelegator_PeerReviewsTest extends MemberV3ApiServiceDelegatorMockTestBase {

    private static final ScopePathType SCOPE = ScopePathType.PEER_REVIEW_READ_LIMITED;

    private static final String USER_4443 = "4444-4444-4444-4443";
    private static final String USER_4444 = "4444-4444-4444-4444";
    private static final String USER_4446 = "4444-4444-4444-4446";
    private static final String USER_4447 = "4444-4444-4444-4447";
    private static final String USER_4499 = "4444-4444-4444-4499";

    private PeerReview peerReview(long putCode, String groupId, String extIdValue, Visibility visibility, Source source) {
        PeerReview element = new PeerReview();
        element.setPutCode(putCode);
        element.setGroupId(groupId);
        element.setExternalIdentifiers(externalIds(extIdValue));
        element.setSubjectExternalIdentifier(subjectExternalIdentifier());
        element.setCompletionDate(FuzzyDate.valueOf(2015, 1, 1));
        element.setOrganization(Utils.getOrganization());
        element.setRole(Role.REVIEWER);
        element.setType(PeerReviewType.REVIEW);
        element.setUrl(new Url("http://peer_review.com"));
        element.setSubjectContainerName(new Title("Peer Review # " + putCode + " container name"));
        element.setSubjectType(PeerReviewSubjectType.ARTISTIC_PERFORMANCE);
        element.setSubjectUrl(new Url("http://work.com"));
        SubjectName subjectName = new SubjectName();
        subjectName.setTitle(new Title("Peer Review # " + putCode));
        subjectName.setTranslatedTitle(new TranslatedTitle("titulo", "es"));
        element.setSubjectName(subjectName);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private PeerReviewSummary summary(long putCode, String groupId, String extIdValue, Visibility visibility, Source source) {
        PeerReviewSummary element = new PeerReviewSummary();
        element.setPutCode(putCode);
        element.setGroupId(groupId);
        element.setExternalIdentifiers(externalIds(extIdValue));
        element.setCompletionDate(FuzzyDate.valueOf(2015, 1, 1));
        element.setOrganization(Utils.getOrganization());
        element.setRole(Role.REVIEWER);
        element.setType(PeerReviewType.REVIEW);
        element.setVisibility(visibility);
        element.setSource(source);
        element.setLastModifiedDate(lastModified());
        return element;
    }

    private ExternalIDs externalIds(String value) {
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType(WorkExternalIdentifierType.DOI.value());
        extId.setUrl(new Url("http://myUrl.com"));
        extId.setValue(value);
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        return extIds;
    }

    private ExternalID subjectExternalIdentifier() {
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("agr");
        extId.setUrl(new Url("http://myUrl.com"));
        extId.setValue("peer-review:subject-external-identifier-id#1");
        return extId;
    }

    private PeerReviewGroup group(String groupIdentifier, PeerReviewSummary summary) {
        PeerReviewGroup group = new PeerReviewGroup();
        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("peer-review");
        extId.setValue(groupIdentifier);
        group.getIdentifiers().getExternalIdentifier().add(extId);
        PeerReviewDuplicateGroup duplicateGroup = new PeerReviewDuplicateGroup();
        duplicateGroup.getPeerReviewSummary().add(summary);
        group.getPeerReviewGroup().add(duplicateGroup);
        return group;
    }

    private PeerReviews peerReviews(PeerReviewGroup... groups) {
        PeerReviews container = new PeerReviews();
        container.getPeerReviewGroup().addAll(Arrays.asList(groups));
        return container;
    }

    private ActivitiesSummary activitiesWithPeerReviews(PeerReviews peerReviews) {
        ActivitiesSummary activities = emptyActivitiesSummary();
        activities.setPeerReviews(peerReviews);
        return activities;
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPeerReviewWrongToken() {
        when(peerReviewManagerReadOnly.getPeerReview(USER_4447, 2L))
                .thenReturn(peerReview(2L, "issn:0000-0002", "work:external-identifier-id#2", Visibility.PUBLIC, clientSource(CLIENT_2)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(USER_4447), any(PeerReview.class), eq(SCOPE));

        serviceDelegator.viewPeerReview(USER_4447, 2L);
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPeerReviewSummaryWrongToken() {
        when(peerReviewManagerReadOnly.getPeerReviewSummary(USER_4446, Long.valueOf(1)))
                .thenReturn(summary(1L, "issn:0000-0001", "work:external-identifier-id#1", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidUnauthorizedException("Access token is for a different record"))
                .when(orcidSecurityManager).checkAndFilter(eq(USER_4446), any(PeerReviewSummary.class), eq(SCOPE));

        serviceDelegator.viewPeerReviewSummary(USER_4446, Long.valueOf(1));
    }

    @Test
    public void testViewPeerReviewReadPublic() {
        PeerReview stored = peerReview(2L, "issn:0000-0002", "work:external-identifier-id#2", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(peerReviewManagerReadOnly.getPeerReview(USER_4447, 2L)).thenReturn(stored);

        Response r = serviceDelegator.viewPeerReview(USER_4447, 2L);
        PeerReview element = (PeerReview) r.getEntity();
        assertNotNull(element);
        assertEquals("/4444-4444-4444-4447/peer-review/2", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(USER_4447, element, SCOPE);
    }

    @Test
    public void testViewPeerReviewSummaryReadPublic() {
        PeerReviewSummary stored = summary(1L, "issn:0000-0001", "work:external-identifier-id#1", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(peerReviewManagerReadOnly.getPeerReviewSummary(USER_4446, Long.valueOf(1))).thenReturn(stored);

        Response r = serviceDelegator.viewPeerReviewSummary(USER_4446, Long.valueOf(1));
        PeerReviewSummary element = (PeerReviewSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/4444-4444-4444-4446/peer-review/1", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, element, SCOPE);
    }

    @Test
    public void testViewPublicPeerReview() {
        when(peerReviewManagerReadOnly.getPeerReview(USER_4446, 1L))
                .thenReturn(peerReview(1L, "issn:0000-0001", "work:external-identifier-id#1", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewPeerReview(USER_4446, 1L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/4444-4444-4444-4446/peer-review/1", peerReview.getPath());
        Utils.verifyLastModified(peerReview.getLastModifiedDate());
        assertEquals(Long.valueOf(1L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("reviewer", peerReview.getRole().value());
        assertEquals(CLIENT_1, peerReview.getSource().retrieveSourcePath());
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
        assertEquals("issn:0000-0001", peerReview.getGroupId());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, peerReview, SCOPE);
    }

    @Test
    public void testViewLimitedPeerReview() {
        when(peerReviewManagerReadOnly.getPeerReview(USER_4446, 3L))
                .thenReturn(peerReview(3L, "issn:0000-0002", "work:external-identifier-id#2", Visibility.LIMITED, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewPeerReview(USER_4446, 3L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/4444-4444-4444-4446/peer-review/3", peerReview.getPath());
        Utils.verifyLastModified(peerReview.getLastModifiedDate());
        assertEquals(Long.valueOf(3L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#2", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("limited", peerReview.getVisibility().value());
        assertEquals("issn:0000-0002", peerReview.getGroupId());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, peerReview, SCOPE);
    }

    @Test
    public void testViewPrivatePeerReview() {
        when(peerReviewManagerReadOnly.getPeerReview(USER_4446, 4L))
                .thenReturn(peerReview(4L, "issn:0000-0003", "work:external-identifier-id#3", Visibility.PRIVATE, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewPeerReview(USER_4446, 4L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/4444-4444-4444-4446/peer-review/4", peerReview.getPath());
        Utils.verifyLastModified(peerReview.getLastModifiedDate());
        assertEquals(Long.valueOf(4L), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#3", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals("private", peerReview.getVisibility().value());
        assertEquals("issn:0000-0003", peerReview.getGroupId());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, peerReview, SCOPE);
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivatePeerReviewWhereYouAreNotTheSource() {
        PeerReview stored = peerReview(5L, "issn:0000-0004", "work:external-identifier-id#4", Visibility.PRIVATE, clientSource(CLIENT_2));
        when(peerReviewManagerReadOnly.getPeerReview(USER_4446, 5L)).thenReturn(stored);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(USER_4446, stored, SCOPE);

        serviceDelegator.viewPeerReview(USER_4446, 5L);
        fail();
    }

    /**
     * The rule this proves -- that peer review 2 cannot be read through record
     * 4446 -- lives in a SQL WHERE clause
     * ({@code PeerReviewDaoImpl.getPeerReview}), so with a mocked manager only
     * the pass-through survives here. The predicate itself is proved by
     * MemberV3ApiServiceDelegatorDatabaseRulesTest in the db-tests stage.
     */
    @Test(expected = NoResultException.class)
    public void testViewPeerReviewThatDontBelongToTheUser() {
        when(peerReviewManagerReadOnly.getPeerReview(USER_4446, 2L)).thenThrow(new NoResultException());

        serviceDelegator.viewPeerReview(USER_4446, 2L);
        fail();
    }

    @Test
    public void testViewPeerReviewSummary() {
        when(peerReviewManagerReadOnly.getPeerReviewSummary(USER_4446, Long.valueOf(1)))
                .thenReturn(summary(1L, "issn:0000-0001", "work:external-identifier-id#1", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewPeerReviewSummary(USER_4446, Long.valueOf(1));
        assertNotNull(response);
        PeerReviewSummary peerReview = (PeerReviewSummary) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("/4444-4444-4444-4446/peer-review/1", peerReview.getPath());
        Utils.verifyLastModified(peerReview.getLastModifiedDate());
        assertEquals(Long.valueOf("1"), peerReview.getPutCode());
        assertNotNull(peerReview.getCompletionDate());
        assertEquals("01", peerReview.getCompletionDate().getDay().getValue());
        assertEquals("01", peerReview.getCompletionDate().getMonth().getValue());
        assertEquals("2015", peerReview.getCompletionDate().getYear().getValue());
        assertEquals("work:external-identifier-id#1", peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).getValue());
        assertEquals(CLIENT_1, peerReview.getSource().retrieveSourcePath());
        assertEquals("public", peerReview.getVisibility().value());
        verify(orcidSecurityManager).checkAndFilter(USER_4446, peerReview, SCOPE);
    }

    @Test
    public void testViewPeerReviews() {
        PeerReviewSummary s9 = summary(9L, "issn:0000-0009", "ext-9", Visibility.PUBLIC, clientSource(CLIENT_1));
        PeerReviewSummary s10 = summary(10L, "issn:0000-0010", "ext-10", Visibility.LIMITED, clientSource(CLIENT_1));
        PeerReviewSummary s11 = summary(11L, "issn:0000-0011", "ext-11", Visibility.PRIVATE, clientSource(CLIENT_1));
        PeerReviewSummary s12 = summary(12L, "issn:0000-0012", "ext-12", Visibility.LIMITED, userSource(ORCID));
        List<PeerReviewSummary> summaries = Arrays.asList(s9, s10, s11, s12);
        when(peerReviewManagerReadOnly.getPeerReviewSummaryList(ORCID)).thenReturn(summaries);
        when(peerReviewManager.groupPeerReviews(summaries, false)).thenReturn(
                peerReviews(group("issn:0000-0009", s9), group("issn:0000-0010", s10), group("issn:0000-0011", s11), group("issn:0000-0012", s12)));

        Response r = serviceDelegator.viewPeerReviews(ORCID);
        assertNotNull(r);
        PeerReviews peerReviews = (PeerReviews) r.getEntity();
        assertNotNull(peerReviews);
        assertEquals("/0000-0000-0000-0003/peer-reviews", peerReviews.getPath());
        Utils.verifyLastModified(peerReviews.getLastModifiedDate());
        assertNotNull(peerReviews.getPeerReviewGroup());
        assertEquals(4, peerReviews.getPeerReviewGroup().size());
        boolean found1 = false, found2 = false, found3 = false, found4 = false;
        for (PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            assertNotNull(group.getIdentifiers());
            assertNotNull(group.getIdentifiers().getExternalIdentifier());
            assertEquals(1, group.getIdentifiers().getExternalIdentifier().size());
            assertNotNull(group.getPeerReviewGroup().get(0).getPeerReviewSummary());
            assertEquals(1, group.getPeerReviewGroup().get(0).getPeerReviewSummary().size());
            PeerReviewSummary summary = group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0);
            Utils.verifyLastModified(summary.getLastModifiedDate());
            switch (group.getIdentifiers().getExternalIdentifier().get(0).getValue()) {
            case "issn:0000-0009":
                assertEquals("issn:0000-0009", summary.getGroupId());
                assertEquals(Long.valueOf(9), summary.getPutCode());
                assertEquals("/0000-0000-0000-0003/peer-review/9", summary.getPath());
                found1 = true;
                break;
            case "issn:0000-0010":
                assertEquals("issn:0000-0010", summary.getGroupId());
                assertEquals(Long.valueOf(10), summary.getPutCode());
                found2 = true;
                break;
            case "issn:0000-0011":
                assertEquals("issn:0000-0011", summary.getGroupId());
                assertEquals(Long.valueOf(11), summary.getPutCode());
                found3 = true;
                break;
            case "issn:0000-0012":
                assertEquals("issn:0000-0012", summary.getGroupId());
                assertEquals(Long.valueOf(12), summary.getPutCode());
                found4 = true;
                break;
            default:
                fail("Invalid group id found: " + group.getIdentifiers().getExternalIdentifier().get(0).getValue());
                break;
            }
        }
        assertTrue(found1);
        assertTrue(found2);
        assertTrue(found3);
        assertTrue(found4);
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), anyList(), eq(SCOPE));
    }

    @Test
    public void testReadPublicScope_PeerReview() {
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 9L))
                .thenReturn(peerReview(9L, "issn:0000-0009", "ext-9", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(peerReviewManagerReadOnly.getPeerReviewSummary(ORCID, 9L))
                .thenReturn(summary(9L, "issn:0000-0009", "ext-9", Visibility.PUBLIC, clientSource(CLIENT_1)));
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 10L))
                .thenReturn(peerReview(10L, "issn:0000-0010", "ext-10", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(peerReviewManagerReadOnly.getPeerReviewSummary(ORCID, 10L))
                .thenReturn(summary(10L, "issn:0000-0010", "ext-10", Visibility.LIMITED, clientSource(CLIENT_1)));
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 11L))
                .thenReturn(peerReview(11L, "issn:0000-0011", "ext-11", Visibility.PRIVATE, clientSource(CLIENT_1)));
        when(peerReviewManagerReadOnly.getPeerReviewSummary(ORCID, 11L))
                .thenReturn(summary(11L, "issn:0000-0011", "ext-11", Visibility.PRIVATE, clientSource(CLIENT_1)));

        PeerReview limitedOtherSource = peerReview(12L, "issn:0000-0012", "ext-12", Visibility.LIMITED, userSource(ORCID));
        PeerReviewSummary limitedOtherSourceSummary = summary(12L, "issn:0000-0012", "ext-12", Visibility.LIMITED, userSource(ORCID));
        PeerReview privateOtherSource = peerReview(13L, "issn:0000-0013", "ext-13", Visibility.PRIVATE, userSource(ORCID));
        PeerReviewSummary privateOtherSourceSummary = summary(13L, "issn:0000-0013", "ext-13", Visibility.PRIVATE, userSource(ORCID));
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 12L)).thenReturn(limitedOtherSource);
        when(peerReviewManagerReadOnly.getPeerReviewSummary(ORCID, 12L)).thenReturn(limitedOtherSourceSummary);
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 13L)).thenReturn(privateOtherSource);
        when(peerReviewManagerReadOnly.getPeerReviewSummary(ORCID, 13L)).thenReturn(privateOtherSourceSummary);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, limitedOtherSourceSummary, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSource, SCOPE);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, privateOtherSourceSummary, SCOPE);

        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(r);
        assertEquals(PeerReview.class.getName(), r.getEntity().getClass().getName());
        r = serviceDelegator.viewPeerReviewSummary(ORCID, 9L);
        assertNotNull(r);
        assertEquals(PeerReviewSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited that am the source of should work
        serviceDelegator.viewPeerReview(ORCID, 10L);
        serviceDelegator.viewPeerReviewSummary(ORCID, 10L);
        // Limited that am not the source of should fail
        try {
            serviceDelegator.viewPeerReview(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 12L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }

        // Private that am the source of should work
        serviceDelegator.viewPeerReview(ORCID, 11L);
        serviceDelegator.viewPeerReviewSummary(ORCID, 11L);
        // Private that am not the source of should fail
        try {
            serviceDelegator.viewPeerReview(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 13L);
            fail();
        } catch (OrcidAccessControlException e) {
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdatePeerReview() {
        PeerReview stored = peerReview(6L, "issn:0000-0006", "ext-6", Visibility.PUBLIC, clientSource(CLIENT_1));
        PeerReview updated = peerReview(6L, "issn:0000-0006", "ext-6", Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setUrl(new Url("http://updated.com/url"));
        updated.getSubjectName().getTitle().setContent("Updated Title");
        when(peerReviewManagerReadOnly.getPeerReview(USER_4447, 6L)).thenReturn(stored).thenReturn(updated);
        when(peerReviewManager.updatePeerReview(eq(USER_4447), any(PeerReview.class), eq(true))).thenReturn(updated);

        Response response = serviceDelegator.viewPeerReview(USER_4447, 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        Utils.verifyLastModified(peerReview.getLastModifiedDate());

        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");

        response = serviceDelegator.updatePeerReview(USER_4447, 6L, peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4447, ScopePathType.PEER_REVIEW_UPDATE);
        // A client supplied source must never reach the manager.
        ArgumentCaptor<PeerReview> captor = ArgumentCaptor.forClass(PeerReview.class);
        verify(peerReviewManager).updatePeerReview(eq(USER_4447), captor.capture(), eq(true));
        assertNull(captor.getValue().getSource());

        response = serviceDelegator.viewPeerReview(USER_4447, 6L);
        PeerReview updatedPeerReview = (PeerReview) response.getEntity();
        assertNotNull(updatedPeerReview);
        Utils.verifyLastModified(updatedPeerReview.getLastModifiedDate());
        assertEquals("http://updated.com/url", updatedPeerReview.getUrl().getValue());
        assertEquals("Updated Title", updatedPeerReview.getSubjectName().getTitle().getContent());
    }

    @Test
    public void testUpdatePeerReviewWhenYouAreNotTheSourceOf() {
        // A fresh instance per call: the delegator clears the source on the
        // object it is handed, and a mock that returned the same instance twice
        // would show that mutation back as if it had been persisted.
        when(peerReviewManagerReadOnly.getPeerReview(USER_4447, 2L)).thenAnswer(invocation -> {
            PeerReview element = peerReview(2L, "issn:0000-0002", "work:external-identifier-id#2", Visibility.PUBLIC, clientSource("APP-6666666666666666"));
            element.setUrl(new Url("http://peer_review.com/2"));
            return element;
        });
        doThrow(new WrongSourceException(new HashMap<String, String>()))
                .when(peerReviewManager).updatePeerReview(eq(USER_4447), any(PeerReview.class), eq(true));

        Response response = serviceDelegator.viewPeerReview(USER_4447, 2L);
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
            serviceDelegator.updatePeerReview(USER_4447, 2L, peerReview);
            fail();
        } catch (WrongSourceException wse) {

        }

        // A subsequent read still shows the original source: the failed update
        // changed nothing.
        response = serviceDelegator.viewPeerReview(USER_4447, Long.valueOf(2));
        peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("APP-6666666666666666", peerReview.getSource().retrieveSourcePath());
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdatePeerReviewChangingVisibilityTest() {
        when(peerReviewManagerReadOnly.getPeerReview(USER_4447, 6L))
                .thenReturn(peerReview(6L, "issn:0000-0006", "ext-6", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new VisibilityMismatchException()).when(peerReviewManager).updatePeerReview(eq(USER_4447), any(PeerReview.class), eq(true));

        Response response = serviceDelegator.viewPeerReview(USER_4447, 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());

        peerReview.setVisibility(Visibility.PRIVATE);

        serviceDelegator.updatePeerReview(USER_4447, 6L, peerReview);
        fail();
    }

    @Test
    public void testUpdatePeerReviewLeavingVisibilityNullTest() {
        when(peerReviewManagerReadOnly.getPeerReview(USER_4447, 6L))
                .thenReturn(peerReview(6L, "issn:0000-0006", "ext-6", Visibility.PUBLIC, clientSource(CLIENT_1)));
        // Restoring the stored visibility is the manager's job and is proved
        // there; here the delegator must simply return what it produced.
        when(peerReviewManager.updatePeerReview(eq(USER_4447), any(PeerReview.class), eq(true)))
                .thenReturn(peerReview(6L, "issn:0000-0006", "ext-6", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewPeerReview(USER_4447, 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());

        peerReview.setVisibility(null);

        response = serviceDelegator.updatePeerReview(USER_4447, 6L, peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        peerReview = (PeerReview) response.getEntity();
        assertEquals(Visibility.PUBLIC, peerReview.getVisibility());
    }

    @Test
    public void testAddPeerReview() {
        PeerReviewSummary existing = summary(1L, "issn:0000-0001", "ext-1", Visibility.PUBLIC, clientSource(CLIENT_1));
        PeerReviewSummary added = summary(1000L, "issn:0000-0003", "ext-1000", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(activitiesSummaryManagerReadOnly.getActivitiesSummary(eq(USER_4444), eq(false)))
                .thenReturn(activitiesWithPeerReviews(peerReviews(group("issn:0000-0001", existing))))
                .thenReturn(activitiesWithPeerReviews(peerReviews(group("issn:0000-0001", existing), group("issn:0000-0003", added))));
        when(peerReviewManager.createPeerReview(eq(USER_4444), any(PeerReview.class), eq(true)))
                .thenReturn(peerReview(1000L, "issn:0000-0003", "ext-1000", Visibility.PUBLIC, clientSource(CLIENT_1)));

        Response response = serviceDelegator.viewActivities(USER_4444);
        assertNotNull(response);
        ActivitiesSummary summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(1, summary.getPeerReviews().getPeerReviewGroup().size());
        assertEquals("issn:0000-0001",
                summary.getPeerReviews().getPeerReviewGroup().get(0).getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());

        PeerReview peerReview = Utils.getPeerReview();

        response = serviceDelegator.createPeerReview(USER_4444, peerReview);
        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);
        assertEquals(Long.valueOf(1000L), putCode);
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4444, ScopePathType.PEER_REVIEW_CREATE, ScopePathType.PEER_REVIEW_UPDATE);

        response = serviceDelegator.viewActivities(USER_4444);
        assertNotNull(response);
        summary = (ActivitiesSummary) response.getEntity();
        assertNotNull(summary);
        Utils.verifyLastModified(summary.getLastModifiedDate());
        assertNotNull(summary.getPeerReviews());
        assertNotNull(summary.getPeerReviews().getPeerReviewGroup());
        assertEquals(2, summary.getPeerReviews().getPeerReviewGroup().size());

        boolean haveOld = false;
        boolean haveNew = false;

        for (PeerReviewGroup group : summary.getPeerReviews().getPeerReviewGroup()) {
            Utils.verifyLastModified(group.getLastModifiedDate());
            Utils.verifyLastModified(group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getLastModifiedDate());
            if ("issn:0000-0001".equals(group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId())) {
                haveOld = true;
            } else {
                assertEquals("issn:0000-0003", group.getPeerReviewGroup().get(0).getPeerReviewSummary().get(0).getGroupId());
                haveNew = true;
            }
        }
        assertTrue(haveOld);
        assertTrue(haveNew);

        // Delete the new so it doesn't affect other tests
        serviceDelegator.deletePeerReview(USER_4444, putCode);
        verify(peerReviewManager).checkSourceAndDelete(USER_4444, 1000L);
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddPeerReviewDuplicateFails() {
        when(peerReviewManagerReadOnly.getPeerReview(USER_4447, 6L))
                .thenReturn(peerReview(6L, "issn:0000-0006", "ext-6", Visibility.PUBLIC, clientSource(CLIENT_1)));
        doThrow(new OrcidDuplicatedActivityException(new HashMap<String, String>()))
                .when(peerReviewManager).createPeerReview(eq(USER_4447), any(PeerReview.class), eq(true));

        Response response = serviceDelegator.viewPeerReview(USER_4447, 6L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        peerReview.setPutCode(null);

        serviceDelegator.createPeerReview(USER_4447, peerReview);
    }

    @Test
    public void testAddPeerReviewWithSameExtIdValueButDifferentExtIdType() {
        PeerReview created1 = peerReview(1001L, "issn:0000-0003", "same_but_different_type", Visibility.PUBLIC, clientSource(CLIENT_1));
        PeerReview created2 = peerReview(1002L, "issn:0000-0003", "same_but_different_type", Visibility.PUBLIC, clientSource(CLIENT_1));
        when(peerReviewManager.createPeerReview(eq(USER_4444), any(PeerReview.class), eq(true))).thenReturn(created1).thenReturn(created2);

        PeerReview peerReview1 = new PeerReview();
        ExternalIDs weis1 = new ExternalIDs();
        ExternalID wei1 = new ExternalID();
        wei1.setRelationship(Relationship.SELF);
        wei1.setValue("same_but_different_type");
        wei1.setType(WorkExternalIdentifierType.DOI.value());
        weis1.getExternalIdentifier().add(wei1);
        peerReview1.setExternalIdentifiers(weis1);
        peerReview1.setGroupId("issn:0000-0003");
        peerReview1.setOrganization(Utils.getOrganization());
        peerReview1.setRole(Role.CHAIR);
        peerReview1.setSubjectContainerName(new Title("subject-container-name"));
        peerReview1.setSubjectExternalIdentifier(wei1);
        SubjectName subjectName1 = new SubjectName();
        subjectName1.setTitle(new Title("subject-names"));
        peerReview1.setSubjectName(subjectName1);
        peerReview1.setSubjectType(PeerReviewSubjectType.DATA_SET);
        peerReview1.setType(PeerReviewType.EVALUATION);

        Response response1 = serviceDelegator.createPeerReview(USER_4444, peerReview1);
        assertNotNull(response1);
        assertEquals(Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Long putCode1 = Utils.getPutCode(response1);

        PeerReview peerReview2 = new PeerReview();
        ExternalIDs weis2 = new ExternalIDs();
        ExternalID wei2 = new ExternalID();
        wei2.setRelationship(Relationship.SELF);
        wei2.setValue("same_but_different_type"); // Same value
        wei2.setType(WorkExternalIdentifierType.AGR.value()); // Different type
        weis2.getExternalIdentifier().add(wei2);
        peerReview2.setExternalIdentifiers(weis2);
        peerReview2.setGroupId("issn:0000-0003");
        peerReview2.setOrganization(Utils.getOrganization());
        peerReview2.setRole(Role.CHAIR);
        peerReview2.setSubjectContainerName(new Title("subject-container-name"));
        peerReview2.setSubjectExternalIdentifier(wei2);
        SubjectName subjectName2 = new SubjectName();
        subjectName2.setTitle(new Title("subject-names"));
        peerReview2.setSubjectName(subjectName2);
        peerReview2.setSubjectType(PeerReviewSubjectType.DATA_SET);
        peerReview2.setType(PeerReviewType.EVALUATION);

        Response response2 = serviceDelegator.createPeerReview(USER_4444, peerReview2);
        assertNotNull(response2);
        assertEquals(Response.Status.CREATED.getStatusCode(), response2.getStatus());
        Long putCode2 = Utils.getPutCode(response2);

        // The duplicate-external-id rule is ActivityValidator's and is proved by
        // ActivityValidatorTest; what the delegator owes is to pass both
        // creations through to the manager unchanged.
        ArgumentCaptor<PeerReview> captor = ArgumentCaptor.forClass(PeerReview.class);
        verify(peerReviewManager, times(2)).createPeerReview(eq(USER_4444), captor.capture(), eq(true));
        assertEquals(WorkExternalIdentifierType.DOI.value(), captor.getAllValues().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals(WorkExternalIdentifierType.AGR.value(), captor.getAllValues().get(1).getExternalIdentifiers().getExternalIdentifier().get(0).getType());

        serviceDelegator.deletePeerReview(USER_4444, putCode1);
        serviceDelegator.deletePeerReview(USER_4444, putCode2);
        verify(peerReviewManager).checkSourceAndDelete(USER_4444, putCode1);
        verify(peerReviewManager).checkSourceAndDelete(USER_4444, putCode2);
    }

    @Test
    public void testDeletePeerReview() {
        PeerReview stored = peerReview(8L, "issn:0000-0008", "ext-8", Visibility.PUBLIC, clientSource(CLIENT_1));
        stored.getSubjectName().getTitle().setContent("Peer Review # 3");
        when(peerReviewManagerReadOnly.getPeerReview(USER_4443, 8L)).thenReturn(stored);

        Response response = serviceDelegator.viewPeerReview(USER_4443, 8L);
        assertNotNull(response);
        PeerReview review = (PeerReview) response.getEntity();
        assertNotNull(review);
        assertNotNull(review.getSubjectName());
        assertNotNull(review.getSubjectName().getTitle());
        assertEquals("Peer Review # 3", review.getSubjectName().getTitle().getContent());

        response = serviceDelegator.deletePeerReview(USER_4443, 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(USER_4443, ScopePathType.PEER_REVIEW_UPDATE);
        verify(peerReviewManager).checkSourceAndDelete(USER_4443, 8L);
    }

    @Test
    public void testAddPeerReviewWithInvalidExtIdTypeFail() {
        PeerReview peerReview = Utils.getPeerReview();

        // Set both to a correct value
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        peerReview.getSubjectExternalIdentifier().setType("doi");

        // The external identifier type table is enforced by ExternalIDValidator
        // inside the manager and proved by ExternalIDValidatorTest; the
        // delegator's contract is to let the exception through untouched.
        PeerReview created = peerReview(1000L, "issn:0000-0003", "ext-1000", Visibility.PUBLIC, clientSource(CLIENT_1));
        doThrow(new ActivityIdentifierValidationException()).doThrow(new ActivityIdentifierValidationException()).doReturn(created)
                .when(peerReviewManager).createPeerReview(eq(USER_4499), any(PeerReview.class), eq(true));

        // Check it fail on external identifier type
        try {
            peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
            serviceDelegator.createPeerReview(USER_4499, peerReview);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        // Set the ext id to a correct value to test the subject ext id
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        // Check it fail on subject external identifier type
        try {
            peerReview.getSubjectExternalIdentifier().setType("INVALID");
            serviceDelegator.createPeerReview(USER_4499, peerReview);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        // Test it works with correct values
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        peerReview.getSubjectExternalIdentifier().setType("doi");
        Response response = serviceDelegator.createPeerReview(USER_4499, peerReview);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);

        // Delete it to roll back the test data
        response = serviceDelegator.deletePeerReview(USER_4499, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(peerReviewManager).checkSourceAndDelete(USER_4499, 1000L);
    }

    @Test(expected = WrongSourceException.class)
    public void testDeletePeerReviewYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(new HashMap<String, String>())).when(peerReviewManager).checkSourceAndDelete(USER_4447, 2L);

        serviceDelegator.deletePeerReview(USER_4447, 2L);
        fail();
    }
}
