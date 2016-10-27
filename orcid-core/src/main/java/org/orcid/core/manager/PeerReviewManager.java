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
import org.orcid.jaxb.model.record.summary_rc3.PeerReviewSummary;
import org.orcid.jaxb.model.record.summary_rc3.PeerReviews;
import org.orcid.jaxb.model.record_rc3.PeerReview;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public interface PeerReviewManager {
    
    void setSourceManager(SourceManager sourceManager);
    
    /**
     * Get a peerReview based on the orcid and peerReview id
     * 
     * @param orcid
     *            The peerReview owner
     * @param peerReviewId
     *            The peerReview id
     * @return the PeerReview
     * */
    PeerReview getPeerReview(String orcid, Long peerReviewId);

    /**
     * Get a peerReview summary based on the orcid and peerReview id
     * 
     * @param orcid
     *            The peerReview owner
     * @param peerReviewId
     *            The peerReview id
     * @return the PeerReviewSummary
     * */
    PeerReviewSummary getPeerReviewSummary(String orcid, Long peerReviewId);

    /**
     * Add a new peerReview to the given user
     * 
     * @param orcid
     *            The user to add the peerReview
     * @param peerReview
     *            The peerReview to add
     * @param isApiRequest
     *          Indicates if the request comes from the API or not           
     * @return the added peerReview
     * */
    PeerReview createPeerReview(String orcid, PeerReview peerReview, boolean isApiRequest);

    /**
     * Updates a peerReview that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param peerReview
     *            The peerReview to update
     * @param isApiRequest
     *          Indicates if the request comes from the API or not
     * @return the updated peerReview
     * */
    PeerReview updatePeerReview(String orcid, PeerReview peerReview, boolean isApiRequest);

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
    boolean checkSourceAndDelete(String orcid, Long peerReviewId);

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
    void removePeerReview(String orcid, Long peerReviewId);

    /**
     * Updates the display index of a given peer review
     * 
     * @param orcid
     *            The peerReview owner
     * @param peerReviewId
     *            The peerReview id
     * @return true if it was able to update the display index
     * */
    boolean updateToMaxDisplay(String orcid, Long peerReviewId);

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

    /**
     * Return the list of peer reviews that belongs to a specific user
     * 
     * @param orcid
     *            the peerReview owner
     * @param lastModified
     * @return a list containing the user peer reviews
     * */
    List<PeerReview> findPeerReviews(String orcid, long lastModified);
    
    /**
     * Get the list of peer reivews that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of peer reviews that belongs to this user
     * */
    List<PeerReviewSummary> getPeerReviewSummaryList(String orcid, long lastModified);
    
    /**
     * Generate a grouped list of peer reviews with the given list of peer reviews
     * 
     * @param peerReviews
     *          The list of peer reviews to group
     * @param justPublic
     *          Specify if we want to group only the public elements in the given list
     * @return PeerReviews element with the PeerReviewSummary elements grouped                  
     * */
    PeerReviews groupPeerReviews(List<PeerReviewSummary> peerReviews, boolean justPublic);
}
