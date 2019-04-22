package org.orcid.core.adapter.v3.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.v3.JpaJaxbPeerReviewAdapter;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.jaxb.model.v3.release.record.summary.PeerReviewSummary;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public class JpaJaxbPeerReviewAdapterImpl implements JpaJaxbPeerReviewAdapter {

    private MapperFacade mapperFacade;

    public void setMapperFacade(MapperFacade mapperFacade) {
        this.mapperFacade = mapperFacade;
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
