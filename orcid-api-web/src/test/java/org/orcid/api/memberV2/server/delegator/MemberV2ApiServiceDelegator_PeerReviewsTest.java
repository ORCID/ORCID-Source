package org.orcid.api.memberV2.server.delegator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import org.orcid.jaxb.model.common_v2.Source;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.message.ScopePathType;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewGroup;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_v2.PeerReviews;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.Role;
import org.orcid.jaxb.model.record_v2.WorkTitle;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.test.helper.Utils;

/**
 * The peer-review endpoints of the member v2 delegator, on mocks.
 *
 * <p>
 * The duplicate check, the external-identifier validation and the
 * source-ownership rule are all enforced inside {@code PeerReviewManagerImpl}
 * (the duplicate check runs a query on the external-id group), so at this
 * boundary they can only be observed as the manager refusing. Each such case
 * below says where the rule itself is proved. Visibility filtering belongs to
 * {@code OrcidSecurityManager_generalTest}, and the "(orcid, id)" predicate that
 * hides another record's peer review is SQL in {@code PeerReviewDaoImpl}.
 */
public class MemberV2ApiServiceDelegator_PeerReviewsTest extends MemberV2ApiServiceDelegatorMockBase {

    private static final String OTHER_ORCID = "4444-4444-4444-4447";
    private static final String MY_ORCID = "4444-4444-4444-4444";

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPeerReviewWrongToken() {
        PeerReview peerReview = peerReview(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 9L)).thenReturn(peerReview);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, peerReview,
                ScopePathType.PEER_REVIEW_READ_LIMITED);

        try {
            serviceDelegator.viewPeerReview(ORCID, 9L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", peerReview.getPath());
        }
    }

    @Test(expected = OrcidUnauthorizedException.class)
    public void testViewPeerReviewSummaryWrongToken() {
        PeerReviewSummary summary = peerReviewSummary(9L, Visibility.PUBLIC);
        when(peerReviewManagerReadOnly.getPeerReviewSummary(ORCID, 9L)).thenReturn(summary);
        doThrow(new OrcidUnauthorizedException("Access token is for a different record")).when(orcidSecurityManager).checkAndFilter(ORCID, summary,
                ScopePathType.PEER_REVIEW_READ_LIMITED);

        try {
            serviceDelegator.viewPeerReviewSummary(ORCID, 9L);
        } finally {
            assertNull("the element must not be decorated once the guard has refused", summary.getPath());
        }
    }

    @Test
    public void testViewPeerReviewReadPublic() {
        PeerReview peerReview = peerReview(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 9L)).thenReturn(peerReview);

        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);

        PeerReview element = (PeerReview) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/peer-review/9", element.getPath());
        assertEquals(CLIENT_1_NAME, element.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(ORCID, peerReview, ScopePathType.PEER_REVIEW_READ_LIMITED);
    }

    @Test
    public void testViewPeerReviewSummaryReadPublic() {
        PeerReviewSummary summary = peerReviewSummary(9L, Visibility.PUBLIC);
        when(peerReviewManagerReadOnly.getPeerReviewSummary(ORCID, 9L)).thenReturn(summary);

        Response r = serviceDelegator.viewPeerReviewSummary(ORCID, 9L);

        PeerReviewSummary element = (PeerReviewSummary) r.getEntity();
        assertNotNull(element);
        assertEquals("/0000-0000-0000-0003/peer-review/9", element.getPath());
        verify(orcidSecurityManager).checkAndFilter(ORCID, summary, ScopePathType.PEER_REVIEW_READ_LIMITED);
    }

    @Test
    public void testViewPublicPeerReview() {
        assertViewPeerReviewDecorated(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
    }

    @Test
    public void testViewLimitedPeerReview() {
        assertViewPeerReviewDecorated(10L, Visibility.LIMITED, clientSource(CLIENT_1));
    }

    @Test
    public void testViewPrivatePeerReview() {
        assertViewPeerReviewDecorated(11L, Visibility.PRIVATE, clientSource(CLIENT_1));
    }

    @Test(expected = OrcidVisibilityException.class)
    public void testViewPrivatePeerReviewWhereYouAreNotTheSource() {
        PeerReview peerReview = peerReview(12L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(peerReviewManagerReadOnly.getPeerReview(OTHER_ORCID, 12L)).thenReturn(peerReview);
        doThrow(new OrcidVisibilityException()).when(orcidSecurityManager).checkAndFilter(OTHER_ORCID, peerReview, ScopePathType.PEER_REVIEW_READ_LIMITED);

        serviceDelegator.viewPeerReview(OTHER_ORCID, 12L);
        fail();
    }

    @Test(expected = NoResultException.class)
    public void testViewPeerReviewThatDontBelongToTheUser() {
        // The (orcid, id) predicate is in PeerReviewDaoImpl's query.
        when(peerReviewManagerReadOnly.getPeerReview(OTHER_ORCID, 1L)).thenThrow(new NoResultException());

        try {
            serviceDelegator.viewPeerReview(OTHER_ORCID, 1L);
            fail();
        } finally {
            verifyNoInteractions(orcidSecurityManager);
        }
    }

    @Test
    public void testViewPeerReviewSummary() {
        PeerReviewSummary summary = peerReviewSummary(9L, Visibility.PUBLIC);
        when(peerReviewManagerReadOnly.getPeerReviewSummary(OTHER_ORCID, 9L)).thenReturn(summary);

        Response response = serviceDelegator.viewPeerReviewSummary(OTHER_ORCID, 9L);

        assertNotNull(response);
        PeerReviewSummary returned = (PeerReviewSummary) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4447/peer-review/9", returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(CLIENT_1_NAME, returned.getSource().getSourceName().getContent());
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, summary, ScopePathType.PEER_REVIEW_READ_LIMITED);
    }

    @Test
    public void testViewPeerReviews() {
        List<PeerReviewSummary> stored = new ArrayList<>(
                Arrays.asList(peerReviewSummary(9L, Visibility.PUBLIC), peerReviewSummary(10L, Visibility.LIMITED), peerReviewSummary(11L, Visibility.PRIVATE)));
        when(peerReviewManagerReadOnly.getPeerReviewSummaryList(ORCID)).thenReturn(stored);
        when(peerReviewManager.groupPeerReviews(anyList(), eq(false))).thenAnswer(invocation -> groupEachSeparately(invocation.getArgument(0)));

        Response response = serviceDelegator.viewPeerReviews(ORCID);

        assertNotNull(response);
        PeerReviews peerReviews = (PeerReviews) response.getEntity();
        assertNotNull(peerReviews);
        assertEquals("/0000-0000-0000-0003/peer-reviews", peerReviews.getPath());
        Utils.verifyLastModified(peerReviews.getLastModifiedDate());
        assertEquals(3, peerReviews.getPeerReviewGroup().size());
        for (PeerReviewGroup group : peerReviews.getPeerReviewGroup()) {
            assertEquals(1, group.getPeerReviewSummary().size());
            PeerReviewSummary summary = group.getPeerReviewSummary().get(0);
            assertEquals("/0000-0000-0000-0003/peer-review/" + summary.getPutCode(), summary.getPath());
            assertEquals(CLIENT_1_NAME, summary.getSource().getSourceName().getContent());
        }
        // the cached list must be copied before it reaches a filter that edits
        // in place
        ArgumentCaptor<List<PeerReviewSummary>> filtered = summaryListCaptor();
        verify(orcidSecurityManager).checkAndFilter(eq(ORCID), filtered.capture(), eq(ScopePathType.PEER_REVIEW_READ_LIMITED));
        assertNotSame(stored, filtered.getValue());
    }

    @Test
    public void testReadPublicScope_PeerReview() {
        // Refused per element, never with a blanket matcher.
        PeerReview nine = peerReview(9L, Visibility.PUBLIC, clientSource(CLIENT_1));
        PeerReview ten = peerReview(10L, Visibility.LIMITED, clientSource(CLIENT_1));
        PeerReview eleven = peerReview(11L, Visibility.PRIVATE, clientSource(CLIENT_2));
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 9L)).thenReturn(nine);
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 10L)).thenReturn(ten);
        when(peerReviewManagerReadOnly.getPeerReview(ORCID, 11L)).thenReturn(eleven);
        PeerReviewSummary summary = peerReviewSummary(9L, Visibility.PUBLIC);
        when(peerReviewManagerReadOnly.getPeerReviewSummary(ORCID, 9L)).thenReturn(summary);
        doThrow(new OrcidAccessControlException()).when(orcidSecurityManager).checkAndFilter(ORCID, eleven, ScopePathType.PEER_REVIEW_READ_LIMITED);

        Response r = serviceDelegator.viewPeerReview(ORCID, 9L);
        assertNotNull(r);
        assertEquals(PeerReview.class.getName(), r.getEntity().getClass().getName());

        r = serviceDelegator.viewPeerReviewSummary(ORCID, 9L);
        assertNotNull(r);
        assertEquals(PeerReviewSummary.class.getName(), r.getEntity().getClass().getName());

        // Limited where am the source should work
        serviceDelegator.viewPeerReview(ORCID, 10L);

        try {
            // Private am not the source should fail
            serviceDelegator.viewPeerReview(ORCID, 11L);
            fail();
        } catch (OrcidAccessControlException e) {

        } catch (Exception e) {
            fail();
        }
    }

    @Test
    public void testUpdatePeerReview() {
        PeerReview peerReview = peerReview(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        peerReview.setUrl(new Url("http://updated.com/url"));
        PeerReview updated = peerReview(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        updated.setUrl(new Url("http://updated.com/url"));
        when(peerReviewManager.updatePeerReview(eq(OTHER_ORCID), any(PeerReview.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updatePeerReview(OTHER_ORCID, 6L, peerReview);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals("http://updated.com/url", ((PeerReview) response.getEntity()).getUrl().getValue());
        verify(orcidSecurityManager).checkClientAccessAndScopes(OTHER_ORCID, ScopePathType.PEER_REVIEW_UPDATE);
        ArgumentCaptor<PeerReview> submitted = ArgumentCaptor.forClass(PeerReview.class);
        verify(peerReviewManager).updatePeerReview(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
    }

    @Test
    public void testUpdatePeerReviewWhenYouAreNotTheSourceOf() {
        // The refusal comes from PeerReviewManagerImpl, which asks
        // orcidSecurityManager.checkSource about the stored entity. What this
        // test still shows at the boundary is that the refusal reaches the caller
        // and that a later read is unaffected by the rejected update.
        // a fresh object per read: updatePeerReview clears the source of the
        // object it is handed, so returning the same instance twice would make
        // the re-read below observe the caller's own mutation rather than the
        // stored row
        when(peerReviewManagerReadOnly.getPeerReview(OTHER_ORCID, 2L)).thenAnswer(invocation -> peerReview(2L, Visibility.PUBLIC, clientSource(CLIENT_2)));
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "peer review"))).when(peerReviewManager).updatePeerReview(eq(OTHER_ORCID),
                any(PeerReview.class), anyBoolean());

        Response response = serviceDelegator.viewPeerReview(OTHER_ORCID, 2L);
        assertNotNull(response);
        PeerReview peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals("http://peer_review.com/2", peerReview.getUrl().getValue());
        assertEquals(CLIENT_2, peerReview.getSource().retrieveSourcePath());

        peerReview.setUrl(new Url("http://updated.com/url"));
        peerReview.getSubjectName().getTitle().setContent("Updated Title");
        peerReview.getExternalIdentifiers().getExternalIdentifier().iterator().next().setValue("different");

        try {
            response = serviceDelegator.updatePeerReview(OTHER_ORCID, 2L, peerReview);
            fail();
        } catch (WrongSourceException wse) {

        }

        // nothing was written: the manager refused before storing anything
        verify(peerReviewManager, times(1)).updatePeerReview(eq(OTHER_ORCID), any(PeerReview.class), anyBoolean());
        response = serviceDelegator.viewPeerReview(OTHER_ORCID, Long.valueOf(2));
        peerReview = (PeerReview) response.getEntity();
        assertNotNull(peerReview);
        assertEquals(CLIENT_2, peerReview.getSource().retrieveSourcePath());
    }

    @Test(expected = VisibilityMismatchException.class)
    public void testUpdatePeerReviewChangingVisibilityTest() {
        PeerReview peerReview = peerReview(6L, Visibility.PRIVATE, clientSource(CLIENT_1));
        doThrow(new VisibilityMismatchException()).when(peerReviewManager).updatePeerReview(eq(OTHER_ORCID), any(PeerReview.class), anyBoolean());

        serviceDelegator.updatePeerReview(OTHER_ORCID, 6L, peerReview);
        fail();
    }

    @Test
    public void testUpdatePeerReviewLeavingVisibilityNullTest() {
        PeerReview peerReview = peerReview(6L, null, clientSource(CLIENT_1));
        PeerReview updated = peerReview(6L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(peerReviewManager.updatePeerReview(eq(OTHER_ORCID), any(PeerReview.class), anyBoolean())).thenReturn(updated);

        Response response = serviceDelegator.updatePeerReview(OTHER_ORCID, 6L, peerReview);

        assertNotNull(response);
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(Visibility.PUBLIC, ((PeerReview) response.getEntity()).getVisibility());
        ArgumentCaptor<PeerReview> submitted = ArgumentCaptor.forClass(PeerReview.class);
        verify(peerReviewManager).updatePeerReview(eq(OTHER_ORCID), submitted.capture(), eq(true));
        assertNull("keeping the stored visibility is the manager's job, not the delegator's", submitted.getValue().getVisibility());
    }

    @Test
    public void testAddPeerReview() {
        PeerReview created = peerReview(100L, Visibility.PUBLIC, clientSource(CLIENT_1));
        when(peerReviewManager.createPeerReview(eq(MY_ORCID), any(PeerReview.class), anyBoolean())).thenReturn(created);

        Response response = serviceDelegator.createPeerReview(MY_ORCID, Utils.getPeerReview());

        assertNotNull(response);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(Long.valueOf(100), Utils.getPutCode(response));
        verify(orcidSecurityManager).checkClientAccessAndScopes(MY_ORCID, ScopePathType.PEER_REVIEW_CREATE, ScopePathType.PEER_REVIEW_UPDATE);
        ArgumentCaptor<PeerReview> submitted = ArgumentCaptor.forClass(PeerReview.class);
        verify(peerReviewManager).createPeerReview(eq(MY_ORCID), submitted.capture(), eq(true));
        assertNull("a client may not choose its own source", submitted.getValue().getSource());
    }

    @Test(expected = OrcidDuplicatedActivityException.class)
    public void testAddPeerReviewDuplicateFails() {
        // The duplicate check queries the external-id group inside
        // PeerReviewManagerImpl, so it is proved there. Here: the delegator does
        // not swallow it.
        PeerReview peerReview = peerReview(null, Visibility.PUBLIC, clientSource(CLIENT_1));
        doThrow(new OrcidDuplicatedActivityException(Collections.singletonMap("activity", "peer review"))).when(peerReviewManager)
                .createPeerReview(eq(OTHER_ORCID), any(PeerReview.class), anyBoolean());

        serviceDelegator.createPeerReview(OTHER_ORCID, peerReview);
    }

    @Test
    public void testAddPeerReviewWithSameExtIdValueButDifferentExtIdType() {
        // Two peer reviews whose external identifiers share a value but not a
        // type are not duplicates. Which pairs count as duplicates is
        // PeerReviewManagerImpl's rule; what is checked here is that the
        // delegator submits both, each with its own external identifier, and
        // reports both as created.
        when(peerReviewManager.createPeerReview(eq(MY_ORCID), any(PeerReview.class), anyBoolean()))
                .thenReturn(peerReview(101L, Visibility.PUBLIC, clientSource(CLIENT_1))).thenReturn(peerReview(102L, Visibility.PUBLIC, clientSource(CLIENT_1)));

        PeerReview peerReview1 = peerReviewWithExternalId("doi", "same_but_different_type");
        Response response1 = serviceDelegator.createPeerReview(MY_ORCID, peerReview1);
        assertNotNull(response1);
        assertEquals(Response.Status.CREATED.getStatusCode(), response1.getStatus());
        Long putCode1 = Utils.getPutCode(response1);

        PeerReview peerReview2 = peerReviewWithExternalId("arxiv", "same_but_different_type");
        Response response2 = serviceDelegator.createPeerReview(MY_ORCID, peerReview2);
        assertNotNull(response2);
        assertEquals(Response.Status.CREATED.getStatusCode(), response2.getStatus());
        Long putCode2 = Utils.getPutCode(response2);

        assertEquals(Long.valueOf(101), putCode1);
        assertEquals(Long.valueOf(102), putCode2);

        ArgumentCaptor<PeerReview> submitted = ArgumentCaptor.forClass(PeerReview.class);
        verify(peerReviewManager, times(2)).createPeerReview(eq(MY_ORCID), submitted.capture(), eq(true));
        assertEquals("doi", submitted.getAllValues().get(0).getExternalIdentifiers().getExternalIdentifier().get(0).getType());
        assertEquals("arxiv", submitted.getAllValues().get(1).getExternalIdentifiers().getExternalIdentifier().get(0).getType());

        serviceDelegator.deletePeerReview(MY_ORCID, putCode1);
        serviceDelegator.deletePeerReview(MY_ORCID, putCode2);
        verify(peerReviewManager).checkSourceAndDelete(MY_ORCID, putCode1);
        verify(peerReviewManager).checkSourceAndDelete(MY_ORCID, putCode2);
    }

    @Test
    public void testDeletePeerReview() {
        String orcid = "4444-4444-4444-4443";
        PeerReview stored = peerReview(8L, Visibility.PUBLIC, clientSource(CLIENT_1));
        stored.getSubjectName().getTitle().setContent("Peer Review # 3");
        when(peerReviewManagerReadOnly.getPeerReview(orcid, 8L)).thenReturn(stored);

        Response response = serviceDelegator.viewPeerReview(orcid, 8L);
        assertNotNull(response);
        PeerReview review = (PeerReview) response.getEntity();
        assertNotNull(review);
        assertNotNull(review.getSubjectName());
        assertNotNull(review.getSubjectName().getTitle());
        assertEquals("Peer Review # 3", review.getSubjectName().getTitle().getContent());

        response = serviceDelegator.deletePeerReview(orcid, 8L);
        assertNotNull(response);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
        verify(orcidSecurityManager).checkClientAccessAndScopes(orcid, ScopePathType.PEER_REVIEW_UPDATE);
        verify(peerReviewManager).checkSourceAndDelete(orcid, 8L);
    }

    @Test
    public void testAddPeerReviewWithInvalidExtIdTypeFail() {
        // The external identifier type is validated inside PeerReviewManagerImpl
        // (through ExternalIDValidator).
        String orcid = "4444-4444-4444-4499";
        when(peerReviewManager.createPeerReview(eq(orcid), any(PeerReview.class), anyBoolean())).thenAnswer(invocation -> {
            PeerReview submitted = invocation.getArgument(1);
            if ("INVALID".equals(submitted.getExternalIdentifiers().getExternalIdentifier().get(0).getType())) {
                throw new ActivityIdentifierValidationException();
            }
            return peerReview(100L, Visibility.PUBLIC, clientSource(CLIENT_1));
        });

        PeerReview peerReview = Utils.getPeerReview();
        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("INVALID");
        try {
            serviceDelegator.createPeerReview(orcid, peerReview);
            fail();
        } catch (ActivityIdentifierValidationException e) {

        } catch (Exception e) {
            fail();
        }

        peerReview.getExternalIdentifiers().getExternalIdentifier().get(0).setType("doi");
        Response response = serviceDelegator.createPeerReview(orcid, peerReview);
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        Long putCode = Utils.getPutCode(response);

        response = serviceDelegator.deletePeerReview(orcid, putCode);
        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    }

    @Test(expected = WrongSourceException.class)
    public void testDeletePeerReviewYouAreNotTheSourceOf() {
        doThrow(new WrongSourceException(Collections.singletonMap("activity", "peer review"))).when(peerReviewManager)
                .checkSourceAndDelete("4444-4444-4444-4446", 12L);

        serviceDelegator.deletePeerReview("4444-4444-4444-4446", 12L);
        fail();
    }

    // ------------------------------------------------------------- helpers

    private void assertViewPeerReviewDecorated(long putCode, Visibility visibility, Source source) {
        PeerReview peerReview = peerReview(putCode, visibility, source);
        when(peerReviewManagerReadOnly.getPeerReview(OTHER_ORCID, putCode)).thenReturn(peerReview);

        Response response = serviceDelegator.viewPeerReview(OTHER_ORCID, putCode);

        assertNotNull(response);
        PeerReview returned = (PeerReview) response.getEntity();
        assertNotNull(returned);
        assertEquals("/4444-4444-4444-4447/peer-review/" + putCode, returned.getPath());
        Utils.verifyLastModified(returned.getLastModifiedDate());
        assertEquals(visibility, returned.getVisibility());
        verify(orcidSecurityManager).checkAndFilter(OTHER_ORCID, peerReview, ScopePathType.PEER_REVIEW_READ_LIMITED);
    }

    /**
     * A stand-in for {@code groupPeerReviews}: one group per summary. The
     * grouping algorithm is the read-only manager's and is tested there.
     */
    private PeerReviews groupEachSeparately(List<PeerReviewSummary> summaries) {
        PeerReviews peerReviews = new PeerReviews();
        for (PeerReviewSummary summary : summaries) {
            PeerReviewGroup group = new PeerReviewGroup();
            group.getPeerReviewSummary().add(summary);
            group.getIdentifiers().getExternalIdentifier().add(externalId("doi", String.valueOf(summary.getPutCode())));
            peerReviews.getPeerReviewGroup().add(group);
        }
        return peerReviews;
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<List<PeerReviewSummary>> summaryListCaptor() {
        return ArgumentCaptor.forClass(List.class);
    }

    private PeerReview peerReviewWithExternalId(String type, String value) {
        PeerReview peerReview = new PeerReview();
        ExternalIDs externalIds = new ExternalIDs();
        ExternalID externalId = new ExternalID();
        externalId.setValue(value);
        externalId.setType(type);
        externalId.setRelationship(Relationship.SELF);
        externalIds.getExternalIdentifier().add(externalId);
        peerReview.setExternalIdentifiers(externalIds);
        peerReview.setGroupId("issn:0000-0003");
        peerReview.setOrganization(organization());
        peerReview.setRole(Role.CHAIR);
        peerReview.setSubjectContainerName(new Title("subject-container-name"));
        peerReview.setSubjectExternalIdentifier(externalId);
        WorkTitle workTitle = new WorkTitle();
        workTitle.setTitle(new Title("work-title"));
        peerReview.setSubjectName(workTitle);
        peerReview.setSubjectType(WorkType.DATA_SET);
        peerReview.setType(PeerReviewType.EVALUATION);
        return peerReview;
    }

    private PeerReview peerReview(Long putCode, Visibility visibility, Source source) {
        PeerReview peerReview = peerReviewWithExternalId("doi", "peer-review-" + putCode);
        peerReview.setPutCode(putCode);
        peerReview.setUrl(new Url("http://peer_review.com/" + putCode));
        peerReview.setVisibility(visibility);
        peerReview.setSource(source);
        peerReview.setCreatedDate(createdDate());
        peerReview.setLastModifiedDate(lastModified());
        return peerReview;
    }

    private PeerReviewSummary peerReviewSummary(Long putCode, Visibility visibility) {
        PeerReviewSummary summary = new PeerReviewSummary();
        summary.setPutCode(putCode);
        summary.setGroupId("issn:0000-0003");
        summary.setOrganization(organization());
        summary.setExternalIdentifiers(externalIds("doi", "peer-review-" + putCode));
        summary.setVisibility(visibility);
        summary.setSource(clientSource(CLIENT_1));
        summary.setCreatedDate(createdDate());
        summary.setLastModifiedDate(lastModified());
        return summary;
    }
}
