package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.Jaxb2JpaPeerReviewAdapter;
import org.orcid.jaxb.model.record.peer_review.PeerReview;
import org.orcid.jaxb.model.record.peer_review.PeerReviewSummary;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public class Jaxb2JpaPeerReviewAdapterImpl implements Jaxb2JpaPeerReviewAdapter {

    @Override
    public PeerReviewEntity toPeerReviewEntity(PeerReview peerReview) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PeerReview toPeerReview(PeerReviewEntity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PeerReviewSummary toPeerReviewSummary(PeerReviewEntity entity) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PeerReview> toPeerReview(Collection<PeerReviewEntity> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<PeerReviewSummary> toPeerReviewSummary(Collection<PeerReviewEntity> entities) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PeerReviewEntity toPeerReviewEntity(PeerReview peerReview, PeerReviewEntity existing) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
