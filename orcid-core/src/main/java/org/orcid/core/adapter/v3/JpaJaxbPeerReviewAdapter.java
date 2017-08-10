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
package org.orcid.core.adapter.v3;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.dev1.record.PeerReview;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public interface JpaJaxbPeerReviewAdapter {
    PeerReviewEntity toPeerReviewEntity(PeerReview peerReview);

    PeerReview toPeerReview(PeerReviewEntity entity);
    
    PeerReviewSummary toPeerReviewSummary(PeerReviewEntity entity);

    List<PeerReview> toPeerReview(Collection<PeerReviewEntity> entities);
    
    List<PeerReviewSummary> toPeerReviewSummary(Collection<PeerReviewEntity> entities);
    
    PeerReviewEntity toPeerReviewEntity(PeerReview peerReview, PeerReviewEntity existing);
}
