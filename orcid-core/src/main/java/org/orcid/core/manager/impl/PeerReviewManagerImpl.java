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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbPeerReviewAdapter;
import org.orcid.core.exception.OrcidDuplicatedActivityException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.common.SourceClientId;
import org.orcid.jaxb.model.common.SourceOrcid;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.PeerReview;
import org.orcid.jaxb.model.record.summary.PeerReviewSummary;
import org.orcid.persistence.dao.GenericDao;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

public class PeerReviewManagerImpl implements PeerReviewManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(PeerReviewManagerImpl.class);

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

    @Resource
    private LocaleManager localeManager;

    @Resource
    private GenericDao<PeerReviewSubjectEntity, Long> peerReviewSubjectDao;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Override
    public void setSourceManager(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }
    
    @Override
    public PeerReview getPeerReview(String orcid, String peerReviewId) {
        PeerReviewEntity peerReviewEntity = peerReviewDao.getPeerReview(orcid, peerReviewId);
        return jpaJaxbPeerReviewAdapter.toPeerReview(peerReviewEntity);
    }

    @Override
    public PeerReviewSummary getPeerReviewSummary(String orcid, String peerReviewId) {
        PeerReviewEntity peerReviewEntity = peerReviewDao.getPeerReview(orcid, peerReviewId);
        return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntity);
    }

    @Override
    public List<PeerReview> toPeerReviewList(Collection<PeerReviewEntity> peerReviews) {
        return jpaJaxbPeerReviewAdapter.toPeerReview(peerReviews);
    }

    @Override
    public PeerReview createPeerReview(String orcid, PeerReview peerReview) {
        List<PeerReviewEntity> peerReviews = peerReviewDao.getByUser(orcid);
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Set the source to the peerReview before looking for duplicates
        if (sourceEntity != null) {
            Source source = new Source();
            if (sourceEntity.getSourceClient() != null) {
                source.setSourceClientId(new SourceClientId(sourceEntity.getSourceClient().getClientId()));
            } else if (sourceEntity.getSourceProfile() != null) {
                source.setSourceOrcid(new SourceOrcid(sourceEntity.getSourceProfile().getId()));
            }
            peerReview.setSource(source);
        }

        // If it is the user adding the peer review, allow him to add duplicates
        if(!sourceEntity.getSourceId().equals(orcid)) {
            if (peerReviews != null) {
                for (PeerReviewEntity entity : peerReviews) {
                    PeerReview existing = jpaJaxbPeerReviewAdapter.toPeerReview(entity);
                    if (existing.isDuplicated(peerReview)) {
                        LOGGER.error("Trying to create a funding that is duplicated with " + entity.getId());
                        throw new OrcidDuplicatedActivityException(localeManager.resolveMessage("api.error.duplicated"));
                    }
                }
            }
        }        

        PeerReviewEntity entity = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(peerReview);

        // Updates the give organization with the latest organization from
        // database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(peerReview);
        entity.setOrg(updatedOrganization);

        ProfileEntity profile = profileDao.find(orcid);
        entity.setProfile(profile);
        setIncomingPrivacy(entity, profile);
        peerReviewDao.persist(entity);
        return jpaJaxbPeerReviewAdapter.toPeerReview(entity);
    }

    @Override
    public PeerReview updatePeerReview(String orcid, PeerReview peerReview) {
        PeerReviewEntity existingEntity = peerReviewDao.getPeerReview(orcid, peerReview.getPutCode());
        PeerReviewEntity updatedEntity = new PeerReviewEntity();
        Visibility originalVisibility = existingEntity.getVisibility();
        SourceEntity existingSource = existingEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        jpaJaxbPeerReviewAdapter.toPeerReviewEntity(peerReview, updatedEntity);
        updatedEntity.setProfile(new ProfileEntity(orcid));
        updatedEntity.setVisibility(originalVisibility);
        updatedEntity.setSource(existingSource);
        
        
        OrgEntity updatedOrganization = orgManager.getOrgEntity(peerReview);
        updatedEntity.setOrg(updatedOrganization);

        updatedEntity = peerReviewDao.merge(updatedEntity); 
        
        return jpaJaxbPeerReviewAdapter.toPeerReview(updatedEntity);
    }

    @Override
    public boolean checkSourceAndDelete(String orcid, String peerReviewId) {
        PeerReviewEntity pr = peerReviewDao.getPeerReview(orcid, peerReviewId);
        orcidSecurityManager.checkSource(pr.getSource());

        return deletePeerReview(pr, orcid);
    }

    @Transactional
    private boolean deletePeerReview(PeerReviewEntity entity, String orcid) {
        Long subjectId = entity.getSubject().getId();
        // Delete the peer review
        boolean result = peerReviewDao.removePeerReview(orcid, entity.getId());
        if (result) {
            // Delete the subject
            peerReviewSubjectDao.remove(subjectId);
        }
        return result;
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

    @Override
    public void removePeerReview(String orcid, String peerReviewId) {
        peerReviewDao.removePeerReview(orcid, Long.valueOf(peerReviewId));
    }
    
    @Override
    public boolean updateToMaxDisplay(String orcid, String peerReviewId) {
        return peerReviewDao.updateToMaxDisplay(orcid, peerReviewId);
    }
    
    @Override
    public boolean updateVisibilities(String orcid, ArrayList<Long> peerReviewIds, Visibility visibility) {
        return peerReviewDao.updateVisibilities(orcid, peerReviewIds, visibility);
    }
    
    @Override
    @Cacheable(value = "peer-reviews", key = "#orcid.concat('-').concat(#lastModified)")
    public List<PeerReview> findPeerReviews(String orcid, long lastModified) {
        List<PeerReviewEntity> peerReviewEntities = peerReviewDao.getByUser(orcid);
        return toPeerReviewList(peerReviewEntities);
    }
    
    /**
     * Get the list of peer reivews that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *          Last modified date used to check the cache
     * @return the list of peer reviews that belongs to this user
     * */
     @Override
     @Cacheable(value = "peer-reviews-summaries", key = "#orcid.concat('-').concat(#lastModified)")
    public List<PeerReviewSummary> getPeerReviewSummaryList(String orcid, long lastModified) {
         List<PeerReviewEntity> peerReviewEntities = peerReviewDao.getByUser(orcid);
         return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntities);
     }
}
