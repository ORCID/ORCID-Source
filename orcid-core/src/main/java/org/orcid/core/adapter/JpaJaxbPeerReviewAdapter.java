package org.orcid.core.adapter;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public interface JpaJaxbPeerReviewAdapter {
    PeerReviewEntity toPeerReviewEntity(PeerReview peerReview);

    PeerReview toPeerReview(PeerReviewEntity entity);
    
    PeerReviewSummary toPeerReviewSummary(PeerReviewEntity entity);

    List<PeerReview> toPeerReview(Collection<PeerReviewEntity> entities);
    
    List<PeerReviewSummary> toPeerReviewSummary(Collection<PeerReviewEntity> entities);
    
    PeerReviewEntity toPeerReviewEntity(PeerReview peerReview, PeerReviewEntity existing);
}
