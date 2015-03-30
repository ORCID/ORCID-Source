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
package org.orcid.core.manager.impl;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.peer_review.PeerReview;
import org.orcid.jaxb.model.record.peer_review.PeerReviewSummary;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;

public class PeerReviewManagerImpl implements PeerReviewManager {

    @Resource
    private PeerReviewDao peerReviewDao;
    
    @Resource
    private JpaJaxbPeerReviewAdapter jpaJaxbPeerReviewAdapter;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Resource
    private OrgManager orgManager;
    
    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private ProfileDao profileDao;
    
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
        //TODO: Check for duplicates
        PeerReviewEntity entity = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(peerReview);
        
        //Updates the give organization with the latest organization from database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(peerReview);
        entity.setOrg(updatedOrganization);
        
        entity.setSource(sourceManager.retrieveSourceEntity());
        ProfileEntity profile = profileDao.find(orcid);
        entity.setProfile(profile);
        setIncomingPrivacy(entity, profile);
        
        peerReviewDao.persist(entity);        
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
    
    private void setIncomingPrivacy(PeerReviewEntity entity, ProfileEntity profile) {
        Visibility incomingVisibility = entity.getVisibility();
        Visibility defaultVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {
            if (defaultVisibility.isMoreRestrictiveThan(incomingVisibility)) {
                entity.setVisibility(defaultVisibility);
            }
        } else if (incomingVisibility == null) {
            entity.setVisibility(Visibility.PRIVATE);
        }
    }
}
