package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.PeerReviewMapperV2;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.jaxb.model.record_v2.WorkType;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public class PeerReviewMapperV2Test {

    @Test
    public void mapPeerReviewAtoBShouldDowngradeDissertationSubjectType() {
        PeerReview peerReview = mock(PeerReview.class, RETURNS_DEEP_STUBS);
        when(peerReview.getSubjectType()).thenReturn(WorkType.DISSERTATION);

        PeerReviewEntity entity = new PeerReviewEntity();
        PeerReviewMapperV2.INSTANCE.mapPeerReviewAtoB(peerReview, entity);

        assertEquals(org.orcid.jaxb.model.common.WorkType.DISSERTATION_THESIS.name(), entity.getSubjectType());
    }
}
