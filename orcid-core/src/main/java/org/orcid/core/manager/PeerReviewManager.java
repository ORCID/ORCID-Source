package org.orcid.core.manager;

import org.orcid.jaxb.model.record.peer_review.PeerReview;
import org.orcid.jaxb.model.record.peer_review.PeerReviewSummary;

public interface PeerReviewManager {
    /**
     * Get a peerReview based on the orcid and peerReview id
     * @param orcid
     *          The peerReview owner
     * @param peerReviewId
     *          The peerReview id
     * @return the PeerReview          
     * */
    PeerReview getPeerReview(String orcid, String peerReviewId);
    
    /**
     * Get a peerReview summary based on the orcid and peerReview id
     * @param orcid
     *          The peerReview owner
     * @param peerReviewId
     *          The peerReview id
     * @return the PeerReviewSummary          
     * */
    PeerReviewSummary getSummary(String orcid, String peerReviewId);
    
    /**
     * Add a new peerReview to the given user
     * @param orcid
     *          The user to add the peerReview
     * @param peerReview
     *          The peerReview to add
     * @return the added peerReview                  
     * */
    PeerReview createPeerReview(String orcid, PeerReview peerReview);
    
    /**
     * Updates a peerReview that belongs to the given user
     * @param orcid
     *          The user
     * @param peerReview
     *          The peerReview to update
     * @return the updated peerReview                  
     * */
    PeerReview updatePeerReview(String orcid, PeerReview peerReview);
    
    /**
     * Deletes a given peerReview, if and only if, the client that requested the delete is the source of the peerReview
     * @param orcid
     *          the peerReview owner
     * @param peerReviewId
     *          The peerReview id                 
     * @return true if the peerReview was deleted, false otherwise
     * */
    boolean checkSourceAndDelete(String orcid, String peerReviewId);
}
