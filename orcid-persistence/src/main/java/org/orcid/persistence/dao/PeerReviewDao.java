package org.orcid.persistence.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface PeerReviewDao extends GenericDao<PeerReviewEntity, Long> {

    /**
     * Find and retrieve a peer review that have the given id and belongs to the given user
     * 
     * @param userOrcid
     *            The owner of the peerReview
     * @param peerReviewId
     *            The id of the element
     * @return a peer review entity that have the give id and belongs to the given user 
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    PeerReviewEntity getPeerReview(String userOrcid, Long peerReviewId);
    
    /**
     * Removes the relationship that exists between a peerReview and a profile.
     * 
     * @param peerReviewId
     *            The id of the peerReview that will be removed from the
     *            client profile
     * @param userOrcid
     *            The user orcid
     * @return true if the relationship was deleted
     * */
    @Transactional(propagation = Propagation.REQUIRED)
    boolean removePeerReview(String userOrcid, Long peerReviewId);     
    
    
    /**
     * Find and retrieve all peer reviews that belongs to a user
     * 
     * @param userOrcid
     *            The owner of the peerReview
     * @return a list will all peer reviews associated with the given user 
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<PeerReviewEntity> getByUser(String userOrcid, long lastModified);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<Object[]> getPeerReviewsByOrcid(String orcid, boolean justPublic);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<PeerReviewEntity> getPeerReviewsByOrcidAndGroupId(String orcid, String groupId, boolean justPublic);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateToMaxDisplay(String orcid, Long peerReviewId);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibilities(String orcid, ArrayList<Long> peerReviewIds, String visibility);

    @Transactional(propagation = Propagation.REQUIRED)
    boolean updateVisibilityByGroupId(String orcid, String groupId, String visibility);
    
    /**
     * Returns a list of  ids of peer reviews that still have old external identifiers
     * @param limit
     *          The batch number to fetch
     * @return a list of peer review ids with old ext ids          
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<BigInteger> getPeerReviewWithOldExtIds(long limit);
    
    @Transactional(propagation = Propagation.REQUIRED)
    boolean increaseDisplayIndexOnAllElements(String orcid);
    
    /**
     * Removes all peer reviews that belongs to a given record. Careful!
     * 
     * @param orcid
     *            The ORCID iD of the record from which all peer reviews will be
     *            removed.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    void removeAllPeerReviews(String orcid);
    
    /**
     * Indicates if the record have public peer reviews
     * 
     * @param orcid
     * */
    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    Boolean hasPublicPeerReviews(String orcid);

    @Transactional(value = "transactionManagerReadOnly", readOnly = true)
    List<PeerReviewEntity> getPeerReviewsReferencingOrgs(List<Long> orgIds);
}
