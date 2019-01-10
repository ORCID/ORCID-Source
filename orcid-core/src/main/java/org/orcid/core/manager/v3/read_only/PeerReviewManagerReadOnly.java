package org.orcid.core.manager.v3.read_only;

import java.util.List;

import org.orcid.jaxb.model.v3.rc2.record.PeerReview;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviewSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.PeerReviews;

public interface PeerReviewManagerReadOnly {
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
     * Return the list of peer reviews that belongs to a specific user
     * 
     * @param orcid
     *            the peerReview owner
     * @param lastModified
     * @return a list containing the user peer reviews
     * */
    List<PeerReview> findPeerReviews(String orcid);
    
    /**
     * Get the list of peer reivews that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of peer reviews that belongs to this user
     * */
    List<PeerReviewSummary> getPeerReviewSummaryList(String orcid);
    
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
    
    /**
     * Indicates if the record have public peer reviews
     * 
     * @param orcid
     * */
    Boolean hasPublicPeerReviews(String orcid);
}
