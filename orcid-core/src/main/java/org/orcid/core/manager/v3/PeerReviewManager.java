package org.orcid.core.manager.v3;

import java.util.ArrayList;

import org.orcid.core.manager.v3.read_only.PeerReviewManagerReadOnly;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.record.PeerReview;

public interface PeerReviewManager extends PeerReviewManagerReadOnly {
    
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
     * Removes all peer reviews that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all peer reviews will be
     *            removed.
     */
    void removeAllPeerReviews(String orcid);
}
