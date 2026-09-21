package org.orcid.core.adapter.mapstruct;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

@Mapper
public interface PeerReviewMapperV3 {

    PeerReviewMapperV3 INSTANCE = Mappers.getMapper(PeerReviewMapperV3.class);

    default void mapPeerReviewAtoB(PeerReview peerReview, PeerReviewEntity entity) {
        entity.setUrl(peerReview.getUrl() == null ? null : peerReview.getUrl().getValue());
        entity.setSubjectUrl(peerReview.getSubjectUrl() == null ? null : peerReview.getSubjectUrl().getValue());
        entity.setSubjectName((peerReview.getSubjectName() == null || peerReview.getSubjectName().getTitle() == null) ? null : peerReview.getSubjectName().getTitle().getContent());
        entity.setSubjectTranslatedName((peerReview.getSubjectName() == null || peerReview.getSubjectName().getTranslatedTitle() == null) ? null
                : peerReview.getSubjectName().getTranslatedTitle().getContent());
        entity.setSubjectTranslatedNameLanguageCode((peerReview.getSubjectName() == null || peerReview.getSubjectName().getTranslatedTitle() == null) ? null
                : peerReview.getSubjectName().getTranslatedTitle().getLanguageCode());
        entity.setSubjectContainerName(peerReview.getSubjectContainerName() == null ? null : peerReview.getSubjectContainerName().getContent());
    }
}
