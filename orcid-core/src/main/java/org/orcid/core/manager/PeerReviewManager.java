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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.orcid.jaxb.model.message.Visibility;
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
    PeerReviewSummary getPeerReviewSummary(String orcid, String peerReviewId);

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

    /**
     * Deletes a given peerReview if and only if it belongs to the given user.
     * If the peerReview exists but it doesn't belong to this user, it will not
     * delete it
     * 
     * @param orcid
     *            the peerReview owner
     * @param peerReviewId
     *            The peerReview id
     * */
    void removePeerReview(String orcid, String peerReviewId);

    /**
     * Updates the display index of a given peer review
     * 
     * @param orcid
     *            The peerReview owner
     * @param peerReviewId
     *            The peerReview id
     * @return true if it was able to update the display index
     * */
    boolean updateToMaxDisplay(String orcid, String peerReviewId);
    
    /**
     * Updates the visibility of a list of existing peer review
     * 
     * @param peerReviewIds
     *            The ids of the peerReview that will be updated
     * @param visibility
     *            The new visibility value for the peer review
     * @return true if the relationship was updated
     * */
    public boolean updateVisibilities(String orcid, ArrayList<Long> peerReviewIds, Visibility visibility);
}
