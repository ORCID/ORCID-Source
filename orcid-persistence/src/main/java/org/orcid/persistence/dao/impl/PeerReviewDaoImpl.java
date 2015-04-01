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
package org.orcid.persistence.dao.impl;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.springframework.transaction.annotation.Transactional;

public class PeerReviewDaoImpl extends GenericDaoImpl<PeerReviewEntity, Long> implements PeerReviewDao {

    public PeerReviewDaoImpl() {
        super(PeerReviewEntity.class);
    }

    @Override
    public PeerReviewEntity getPeerReview(String userOrcid, String peerReviewId) {
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
    public List<PeerReviewEntity> getByUser(String userOrcid) {
        TypedQuery<PeerReviewEntity> query = entityManager.createQuery("from PeerReviewEntity where profile.id=:userOrcid", PeerReviewEntity.class);
        query.setParameter("userOrcid", userOrcid);
        return query.getResultList();
    }
}
