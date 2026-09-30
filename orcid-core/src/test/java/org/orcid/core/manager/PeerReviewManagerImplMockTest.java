package org.orcid.core.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
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
import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.impl.PeerReviewManagerImpl;
import org.orcid.core.utils.Actors;
import org.orcid.jaxb.model.common_v2.Title;
import org.orcid.jaxb.model.common_v2.Url;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.PeerReviewType;
import org.orcid.jaxb.model.record_v2.Relationship;
import org.orcid.jaxb.model.record_v2.Role;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

/**
 * The source guard on {@link PeerReviewManagerImpl}, on mocks.
 *
 * <p>
 * A mocked sibling of the DBUnit {@code PeerReviewManagerTest}, which is left
 * alone. The guard runs before the validator on both paths here, so no
 * validator is needed to reach it.
 */
@RunWith(MockitoJUnitRunner.class)
public class PeerReviewManagerImplMockTest {

    private static final String ORCID = Actors.USER_A;

    private static final Long PUT_CODE = 1L;

    @InjectMocks
    private PeerReviewManagerImpl peerReviewManager = new PeerReviewManagerImpl();

    @Mock
    private PeerReviewDao peerReviewDao;

    @Mock
    private OrcidSecurityManager orcidSecurityManager;

    @Mock
    private SourceManager sourceManager;

    @Mock
    private JpaJaxbPeerReviewAdapter jpaJaxbPeerReviewAdapter;


    private static PeerReviewEntity storedPeerReview(Visibility visibility, String clientSourceId) {
        PeerReviewEntity entity = new PeerReviewEntity();
        entity.setId(PUT_CODE);
        entity.setOrcid(ORCID);
        entity.setVisibility(visibility.name());
        entity.setClientSourceId(clientSourceId);
        return entity;
    }

    private static PeerReview peerReview(Visibility visibility) {
        PeerReview peerReview = new PeerReview();
        peerReview.setPutCode(PUT_CODE);
        peerReview.setRole(Role.CHAIR);
        peerReview.setType(PeerReviewType.EVALUATION);
        peerReview.setSubjectContainerName(new Title("Peer review title"));

        ExternalID extId = new ExternalID();
        extId.setRelationship(Relationship.SELF);
        extId.setType("doi");
        extId.setUrl(new Url("http://orcid.org"));
        extId.setValue("ext-id-value");
        ExternalIDs extIds = new ExternalIDs();
        extIds.getExternalIdentifier().add(extId);
        peerReview.setExternalIdentifiers(extIds);

        peerReview.setVisibility(visibility);
        return peerReview;
    }

    private static WrongSourceException wrongSource() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("activity", "work");
        return new WrongSourceException(params);
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(existingEntity)}
     * ({@code PeerReviewManagerImpl.java:149}) below
     * {@code peerReviewDao.merge(existingEntity)}, or deleting it.
     */
    @Test
    public void updateRefusesAPeerReviewSourcedByAnotherClient() {
        PeerReviewEntity stored = storedPeerReview(Visibility.PUBLIC, Actors.CLIENT_B);
        when(peerReviewDao.getPeerReview(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            peerReviewManager.updatePeerReview(ORCID, peerReview(Visibility.PUBLIC), true);
            fail("a client must not update a peer review another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(peerReviewDao, never()).merge(any(PeerReviewEntity.class));
    }

    /**
     * Mutation caught: moving {@code orcidSecurityManager.checkSource(pr)}
     * ({@code PeerReviewManagerImpl.java:199}) below
     * {@code peerReviewDao.removePeerReview(...)}, or deleting it.
     */
    @Test
    public void deleteRefusesAPeerReviewSourcedByAnotherClient() {
        PeerReviewEntity stored = storedPeerReview(Visibility.PUBLIC, Actors.CLIENT_B);
        when(peerReviewDao.getPeerReview(ORCID, PUT_CODE)).thenReturn(stored);
        doThrow(wrongSource()).when(orcidSecurityManager).checkSource(stored);

        try {
            peerReviewManager.checkSourceAndDelete(ORCID, PUT_CODE);
            fail("a client must not delete a peer review another client is the source of");
        } catch (WrongSourceException expected) {
            assertEquals("work", expected.getParams().get("activity"));
        }

        verify(peerReviewDao, never()).removePeerReview(ORCID, PUT_CODE);
    }
}
