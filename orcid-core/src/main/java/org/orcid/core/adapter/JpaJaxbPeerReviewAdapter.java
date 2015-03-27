package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record.peer_review.PeerReview;
import org.orcid.jaxb.model.record.peer_review.PeerReviewSummary;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public interface JpaJaxbPeerReviewAdapter {
    PeerReviewEntity toPeerReviewEntity(PeerReview peerReview);

    PeerReview toPeerReview(PeerReviewEntity entity);
    
    PeerReviewSummary toPeerReviewSummary(PeerReviewEntity entity);

    List<PeerReview> toPeerReview(Collection<PeerReviewEntity> entities);
    
    List<PeerReviewSummary> toPeerReviewSummary(Collection<PeerReviewEntity> entities);
    
    PeerReviewEntity toPeerReviewEntity(PeerReview peerReview, PeerReviewEntity existing);
}
