package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.jaxb.model.record.summary_v2.PeerReviewSummary;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public class JpaJaxbPeerReviewAdapterImpl implements JpaJaxbPeerReviewAdapter {

    private final MapperFacadeSupport mapperFacade = new MapperFacadeSupport();

    public void setMapperFacade(Object mapperFacade) {
        this.mapperFacade.setMapperFacade(mapperFacade);
    }
    
    @Override
    public PeerReviewEntity toPeerReviewEntity(PeerReview peerReview) {
        if (peerReview == null) {
            return null;
        }
        return mapperFacade.map(peerReview, PeerReviewEntity.class);
    }

    @Override
    public PeerReview toPeerReview(PeerReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        return mapperFacade.map(entity, PeerReview.class);
    }

    @Override
    public PeerReviewSummary toPeerReviewSummary(PeerReviewEntity entity) {
        if (entity == null) {
            return null;
        }
        
        return mapperFacade.map(entity, PeerReviewSummary.class);
    }

    @Override
    public List<PeerReview> toPeerReview(Collection<PeerReviewEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, PeerReview.class);
    }

    @Override
    public List<PeerReviewSummary> toPeerReviewSummary(Collection<PeerReviewEntity> entities) {
        if (entities == null) {
            return null;
        }
        return mapperFacade.mapAsList(entities, PeerReviewSummary.class);
    }

    @Override
    public PeerReviewEntity toPeerReviewEntity(PeerReview peerReview, PeerReviewEntity existing) {
        if (peerReview == null) {
            return null;
        }
        mapperFacade.map(peerReview, existing);
        return existing;
    }
}
