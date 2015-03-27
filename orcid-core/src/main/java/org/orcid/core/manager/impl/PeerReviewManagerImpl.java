package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.jaxb.model.record.peer_review.PeerReview;
import org.orcid.jaxb.model.record.peer_review.PeerReviewSummary;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;

public class PeerReviewManagerImpl implements PeerReviewManager {

    @Resource
    private PeerReviewDao peerReviewDao;
    
    @Resource
    private JpaJaxbPeerReviewAdapter jpaJaxbPeerReviewAdapter;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Override
    public PeerReview getPeerReview(String orcid, String peerReviewId) {
        PeerReviewEntity peerReviewEntity = peerReviewDao.getPeerReview(orcid, peerReviewId); 
        return jpaJaxbPeerReviewAdapter.toPeerReview(peerReviewEntity);
    }

    @Override
    public PeerReviewSummary getSummary(String orcid, String peerReviewId) {
        PeerReviewEntity peerReviewEntity = peerReviewDao.getPeerReview(orcid, peerReviewId); 
        return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntity);
    }

    @Override
    public PeerReview createPeerReview(String orcid, PeerReview peerReview) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public PeerReview updatePeerReview(String orcid, PeerReview peerReview) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean checkSourceAndDelete(String orcid, String peerReviewId) {
        PeerReviewEntity pr = peerReviewDao.getPeerReview(orcid, peerReviewId);
        orcidSecurityManager.checkSource(pr.getSource());
        return peerReviewDao.removePeerReview(orcid, peerReviewId);
    }    
}
