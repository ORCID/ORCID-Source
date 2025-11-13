package org.orcid.persistence.dao.impl;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.aop.UpdateProfileLastModified;
import org.orcid.persistence.aop.UpdateProfileLastModifiedAndIndexingStatus;
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
        Query query = entityManager.createQuery("from PeerReviewEntity where orcid=:userOrcid and id=:peerReviewId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("peerReviewId", Long.valueOf(peerReviewId));
        return (PeerReviewEntity) query.getSingleResult();
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean removePeerReview(String userOrcid, Long peerReviewId) {
        Query query = entityManager.createQuery("delete from PeerReviewEntity where orcid=:userOrcid and id=:peerReviewId");
        query.setParameter("userOrcid", userOrcid);
        query.setParameter("peerReviewId", peerReviewId);
        return query.executeUpdate() > 0 ? true : false;
    }    
    
    @Override
    @Cacheable(value = "peer-reviews", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<PeerReviewEntity> getByUser(String userOrcid, long lastModified) {
        TypedQuery<PeerReviewEntity> query = entityManager.createQuery("from PeerReviewEntity where orcid=:userOrcid order by completionDate.year desc, completionDate.month desc, completionDate.day desc", PeerReviewEntity.class);
        query.setParameter("userOrcid", userOrcid);
        return query.getResultList();
    }

    @Override
    public List<Object[]> getPeerReviewsByOrcid(String orcid, boolean justPublic) {
        String sqlString = null;
        if (justPublic) {
            sqlString = "SELECT g.id, p.group_id, p.id as put_code, p.visibility, g.group_name, p.source_id, p.client_source_id, p.assertion_origin_source_id FROM peer_review p, group_id_record g WHERE p.orcid=:orcid AND p.visibility='PUBLIC' AND lower(p.group_id)=lower(g.group_id) ORDER BY p.group_id, p.display_index, p.date_created";
        } else {
            sqlString = "SELECT g.id, p.group_id, p.id as put_code, p.visibility, g.group_name, p.source_id, p.client_source_id, p.assertion_origin_source_id FROM peer_review p, group_id_record g WHERE p.orcid=:orcid AND lower(p.group_id)=lower(g.group_id) ORDER BY p.group_id, p.display_index, p.date_created";
        }
        Query query = entityManager.createNativeQuery(sqlString);
        query.setParameter("orcid", orcid);

        return query.getResultList();
    }

    @Override
    public List<PeerReviewEntity> getPeerReviewsByOrcidAndGroupId(String orcid, String groupId, boolean justPublic) {
        String sqlString = null;
        if(justPublic) {
            sqlString = "from PeerReviewEntity where orcid=:orcid and groupId=:groupId and visibility='PUBLIC' order by completionDate.year desc, completionDate.month desc, completionDate.day desc";
        } else {
            sqlString = "from PeerReviewEntity where orcid=:orcid and groupId=:groupId order by completionDate.year desc, completionDate.month desc, completionDate.day desc";
        }
        TypedQuery<PeerReviewEntity> query = entityManager.createQuery(sqlString, PeerReviewEntity.class);
        query.setParameter("orcid", orcid);
        query.setParameter("groupId", groupId);
        return query.getResultList();
    }

    @Override
    @Transactional
    @UpdateProfileLastModified
    public boolean updateToMaxDisplay(String orcid, Long peerReviewId) {
        Query query = entityManager.createNativeQuery("UPDATE peer_review SET display_index = (select coalesce(MAX(display_index) + 1, 0) from peer_review where orcid=:orcid and id != :id ), last_modified=now() WHERE id = :id");
        query.setParameter("orcid", orcid);
        query.setParameter("id", peerReviewId);
        return query.executeUpdate() > 0 ? true : false;
    }
    
    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean updateVisibilities(String orcid, ArrayList<Long> peerReviewIds, String visibility) {
        Query query = entityManager
                .createQuery("update PeerReviewEntity set visibility=:visibility, lastModified=now() where id in (:peerReviewIds) and  orcid=:orcid");
        query.setParameter("peerReviewIds", peerReviewIds);
        query.setParameter("visibility", visibility);
        query.setParameter("orcid", orcid);
        return query.executeUpdate() > 0 ? true : false;
    }

    @Override
    @Transactional
    @UpdateProfileLastModifiedAndIndexingStatus
    public boolean updateVisibilityByGroupId(String orcid, String groupId, String visibility) {
        Query query = entityManager
                .createQuery("update PeerReviewEntity set visibility=:visibility, lastModified=now() where groupId=:groupId and  orcid=:orcid");
        query.setParameter("groupId", groupId);
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
    @UpdateProfileLastModifiedAndIndexingStatus
    public void removeAllPeerReviews(String orcid){
        Query query = entityManager.createQuery("delete from PeerReviewEntity where orcid = :orcid");
        query.setParameter("orcid", orcid);
        query.executeUpdate();
    }

    @Override
    public Boolean hasPublicPeerReviews(String orcid) {
        Query query = entityManager.createNativeQuery("select count(*) from peer_review where orcid=:orcid and visibility='PUBLIC'");
        query.setParameter("orcid", orcid);
        Long result = ((BigInteger)query.getSingleResult()).longValue();
        return (result != null && result > 0);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<PeerReviewEntity> getPeerReviewsReferencingOrgs(List<Long> orgIds) {
        Query query = entityManager.createQuery("from PeerReviewEntity where org.id in (:orgIds)");
        query.setParameter("orgIds", orgIds);
        return query.getResultList();
    }
    
    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public void persist(PeerReviewEntity entity) {
        super.persist(entity);
    }
    
    @Override
    @UpdateProfileLastModifiedAndIndexingStatus
    @Transactional
    public PeerReviewEntity merge(PeerReviewEntity entity) {
        return super.merge(entity);
    }
}
