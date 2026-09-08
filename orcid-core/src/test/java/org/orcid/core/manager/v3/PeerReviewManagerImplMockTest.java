package org.orcid.core.manager.v3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.v3.impl.PeerReviewManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

/**
 * The v3 peer review delete refuses a client that is not the source of the peer
 * review.
 *
 * <p>
 * {@code PeerReviewManagerImpl.checkSourceAndDelete} reads the peer review, then
 * calls {@code orcidSecurityManager.checkSourceAndThrow(pr)} <em>before</em>
 * {@code deletePeerReview}, which is the only caller of
 * {@code peerReviewDao.removePeerReview} on this path.
 *
 * <p>
 * The rule used to be proved only by the real-chain
 * {@code MemberV3ApiServiceDelegator_PeerReviewsTest}, which now stubs the
 * manager and therefore cannot see the guard at all.
 *
 * <p>
 * No actor is placed in the security context. At this layer the security manager
 * is a mock, so nothing under test reads {@code SecurityContextHolder}; the
 * fixture that carries the rule is the entity's source -- {@code Actors.CLIENT_B}
 * created it, and the client the guard refuses is the one acting. Setting an
 * actor here would also fail the class: {@code Actors.memberClient} builds a
 * stubbed token mock, and the strict Mockito runner reports stubbings nothing
 * reads as unnecessary.
 *
 * @see OrcidSecurityManager_SourceTest the other half of TESTING.md R2
 */
@RunWith(MockitoJUnitRunner.class)
public class PeerReviewManagerImplMockTest {

    private static final Long PEER_REVIEW_ID = 30L;

    @Mock
    private PeerReviewDao peerReviewDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private NotificationManager notificationManager;

    @InjectMocks
    private PeerReviewManagerImpl peerReviewManager = new PeerReviewManagerImpl();

    /**
     * Catches: deleting {@code orcidSecurityManager.checkSourceAndThrow(pr)} at
     * {@code manager/v3/impl/PeerReviewManagerImpl.java:187}, and moving it below
     * the {@code deletePeerReview(pr, orcid)} call at {@code :188}.
     */
    @Test
    public void checkSourceAndDeleteRefusesAPeerReviewSourcedByAnotherClient() {
        PeerReviewEntity peerReview = new PeerReviewEntity();
        peerReview.setId(PEER_REVIEW_ID);
        peerReview.setClientSourceId(Actors.CLIENT_B);
        when(peerReviewDao.getPeerReview(Actors.USER_A, PEER_REVIEW_ID)).thenReturn(peerReview);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSourceAndThrow(peerReview);

        try {
            peerReviewManager.checkSourceAndDelete(Actors.USER_A, PEER_REVIEW_ID);
            fail("a client must not be able to delete a peer review another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(peerReviewDao, never()).removePeerReview(anyString(), anyLong());
        verify(notificationManager, never()).sendAmendEmail(anyString(), any(AmendedSection.class), anyCollection());
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }
}
