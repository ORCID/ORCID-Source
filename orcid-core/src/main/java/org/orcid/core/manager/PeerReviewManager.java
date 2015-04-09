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
package org.orcid.core.manager;

import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.summary.PeerReviewSummary;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public interface PeerReviewManager {
    /**
     * Get a peerReview based on the orcid and peerReview id
     * 
     * @param orcid
     *            The peerReview owner
     * @param peerReviewId
     *            The peerReview id
     * @return the PeerReview
     * */
    PeerReview getPeerReview(String orcid, String peerReviewId);

    /**
     * Get a peerReview summary based on the orcid and peerReview id
     * 
     * @param orcid
     *            The peerReview owner
     * @param peerReviewId
     *            The peerReview id
     * @return the PeerReviewSummary
     * */
    PeerReviewSummary getSummary(String orcid, String peerReviewId);

    /**
     * Add a new peerReview to the given user
     * 
     * @param orcid
     *            The user to add the peerReview
     * @param peerReview
     *            The peerReview to add
     * @return the added peerReview
     * */
    PeerReview createPeerReview(String orcid, PeerReview peerReview);

    /**
     * Updates a peerReview that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param peerReview
     *            The peerReview to update
     * @return the updated peerReview
     * */
    PeerReview updatePeerReview(String orcid, PeerReview peerReview);

    /**
     * Deletes a given peerReview, if and only if, the client that requested the
     * delete is the source of the peerReview
     * 
     * @param orcid
     *            the peerReview owner
     * @param peerReviewId
     *            The peerReview id
     * @return true if the peerReview was deleted, false otherwise
     * */
    boolean checkSourceAndDelete(String orcid, String peerReviewId);

    /**
     * Transforms a collection of peer review entities into a PeerReview list
     * 
     * @param peerReviews
     *            The collection of PeerReviewEntities
     * @return a list of PeerReview objects
     * */
    List<PeerReview> toPeerReviewList(Collection<PeerReviewEntity> peerReviews);
}
