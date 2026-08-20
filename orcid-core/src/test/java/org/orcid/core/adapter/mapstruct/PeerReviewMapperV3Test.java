package org.orcid.core.adapter.mapstruct;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.orcid.core.adapter.mapstruct.PeerReviewMapperV3;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public class PeerReviewMapperV3Test {

    @Test
    public void mapPeerReviewAtoBShouldMapSubjectNameAndUrls() {
        PeerReview peerReview = mock(PeerReview.class, RETURNS_DEEP_STUBS);
        when(peerReview.getUrl().getValue()).thenReturn("https://example.org/review");
        when(peerReview.getSubjectUrl().getValue()).thenReturn("https://example.org/subject");
        when(peerReview.getSubjectName().getTitle().getContent()).thenReturn("Subject Title");
        when(peerReview.getSubjectName().getTranslatedTitle().getContent()).thenReturn("Titulo");
        when(peerReview.getSubjectName().getTranslatedTitle().getLanguageCode()).thenReturn("es");
        when(peerReview.getSubjectContainerName().getContent()).thenReturn("Container");

        PeerReviewEntity entity = new PeerReviewEntity();
        PeerReviewMapperV3.INSTANCE.mapPeerReviewAtoB(peerReview, entity);

        assertEquals("https://example.org/review", entity.getUrl());
        assertEquals("https://example.org/subject", entity.getSubjectUrl());
        assertEquals("Subject Title", entity.getSubjectName());
        assertEquals("Titulo", entity.getSubjectTranslatedName());
        assertEquals("es", entity.getSubjectTranslatedNameLanguageCode());
        assertEquals("Container", entity.getSubjectContainerName());
    }
}
