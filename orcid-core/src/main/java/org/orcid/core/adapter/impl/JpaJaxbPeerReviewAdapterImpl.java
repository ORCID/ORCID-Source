/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.core.adapter.impl;

import java.util.Collection;
import java.util.List;

import ma.glasnost.orika.MapperFacade;

import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.jaxb.model.record.summary_rc4.PeerReviewSummary;
import org.orcid.jaxb.model.record_rc4.PeerReview;
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
