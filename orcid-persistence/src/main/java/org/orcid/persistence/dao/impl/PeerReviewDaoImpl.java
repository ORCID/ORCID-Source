package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class PeerReviewDaoImpl extends GenericDaoImpl<PeerReviewEntity, Long> implements PeerReviewDao {

    public PeerReviewDaoImpl() {
        super(PeerReviewEntity.class);
    }

    @Override
    public PeerReviewEntity getPeerReview(String userOrcid, Long peerReviewId) {
        Query query = entityManager.createQuery("from PeerReviewEntity where profile.id=:userOrcid and id=:peerReviewId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("peerReviewId", Long.valueOf(peerReviewId));
        return (PeerReviewEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    public boolean removePeerReview(String userOrcid, Long peerReviewId) {
        Query query = entityManager.createQuery("delete from PeerReviewEntity where profile.id=:userOrcid and id=:peerReviewId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("peerReviewId", peerReviewId);
        return query.executeUpdate() > 0 ? true : false;
    }    
    
    @Override
    @Cacheable(value = "peer-reviews", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<PeerReviewEntity> getByUser(String userOrcid, long lastModified) {
        TypedQuery<PeerReviewEntity> query = entityManager.createQuery("from PeerReviewEntity where profile.id=:userOrcid", PeerReviewEntity.class);
        query.setParameter("userOrcid", userOrcid);
        return query.getResultList();
    }
    
    @Override
    @Transactional
    public boolean updateToMaxDisplay(String orcid, Long peerReviewId) {
        Query query = entityManager.createNativeQuery("UPDATE peer_review SET display_index = (select coalesce(MAX(display_index) + 1, 0) from peer_review where orcid=:orcid and id != :id ), last_modified=now() WHERE id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", peerReviewId);
        return query.executeUpdate() > 0 ? true : false;
    }
    
    @Override
    @Transactional
    public boolean updateVisibilities(String orcid, ArrayList<Long> peerReviewIds, Visibility visibility) {
        Query query = entityManager
                .createQuery("update PeerReviewEntity set visibility=:visibility, lastModified=now() where id in (:peerReviewIds) and  profile.id=:orcid");
        query.setParameter("peerReviewIds", peerReviewIds);
        query.setParameter("visibility", visibility);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0 ? true : false;
    }
    
    /**
     * Returns a list of  ids of peer reviews that still have old external identifiers
     * @param limit
     *          The batch number to fetch
     * @return a list of peer review ids with old ext ids          
     * */
    @Override
    @SuppressWarnings("unchecked") 
    public List<BigInteger> getPeerReviewWithOldExtIds(long limit) {
        Query query = entityManager.createNativeQuery("SELECT distinct(id) FROM (SELECT id, json_array_elements(json_extract_path(external_identifiers_json, 'workExternalIdentifier')) AS j FROM peer_review WHERE external_identifiers_json is not null limit :limit) AS a WHERE (j->'relationship') is null");
        query.setParameter("limit", limit);
        return query.getResultList();
    }
    
    @Override
    public boolean increaseDisplayIndexOnAllElements(String orcid) {
        Query query = entityManager.createNativeQuery("update peer_review set display_index=(display_index + 1), last_modified=now() where orcid=:orcid");                
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0;
    }
    
    @Override
    @Transactional
    public void removeAllPeerReviews(String orcid){
        Query query = entityManager.createQuery("delete from PeerReviewEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }
}
