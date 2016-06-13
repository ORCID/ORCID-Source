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
import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.manager.validator.ExternalIDValidator;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.notification.amended_rc2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_rc2.Item;
import org.orcid.jaxb.model.notification.permission_rc2.ItemType;
import org.orcid.jaxb.model.record.summary_rc2.PeerReviewSummary;
import org.orcid.jaxb.model.record_rc2.PeerReview;
import org.orcid.persistence.dao.PeerReviewDao;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;

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
    private LocaleManager localeManager;

    @Resource
    private OrcidUrlManager orcidUrlManager;

    @Resource
    private GroupIdRecordManager groupIdRecordManager;

    @Resource
    private NotificationManager notificationManager;

    @Resource
    private ExternalIDValidator externalIDValidator;
    
    @Resource 
    private ActivityValidator activityValidator;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
    
    @Override
    public void setSourceManager(SourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }

    @Override
    public PeerReview getPeerReview(String orcid, Long peerReviewId) {
        PeerReviewEntity peerReviewEntity = peerReviewDao.getPeerReview(orcid, peerReviewId);                       
        return jpaJaxbPeerReviewAdapter.toPeerReview(peerReviewEntity);
    }

    @Override
    public PeerReviewSummary getPeerReviewSummary(String orcid, Long peerReviewId) {
        PeerReviewEntity peerReviewEntity = peerReviewDao.getPeerReview(orcid, peerReviewId);
        return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntity);
    }

    @Override
    public List<PeerReview> toPeerReviewList(Collection<PeerReviewEntity> peerReviews) {
        return jpaJaxbPeerReviewAdapter.toPeerReview(peerReviews);
    }

    @Override
    @Transactional
    public PeerReview createPeerReview(String orcid, PeerReview peerReview, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // If request comes from the API, perform the validations
        if (isApiRequest) {
            // Validate it have at least one ext id
            activityValidator.validatePeerReview(peerReview, sourceEntity, true, isApiRequest, null);

            List<PeerReviewEntity> peerReviews = peerReviewDao.getByUser(orcid);
            // If it is the user adding the peer review, allow him to add
            // duplicates
            if (!sourceEntity.getSourceId().equals(orcid)) {
                if (peerReviews != null) {
                    for (PeerReviewEntity entity : peerReviews) {
                        PeerReview existing = jpaJaxbPeerReviewAdapter.toPeerReview(entity);
                        activityValidator.checkExternalIdentifiersForDuplicates(peerReview.getExternalIdentifiers(), existing.getExternalIdentifiers(), existing.getSource(),
                                sourceEntity);
                    }
                }
            }else{
                //check vocab of external identifiers
                externalIDValidator.validateWorkOrPeerReview(peerReview.getExternalIdentifiers());
                externalIDValidator.validateWorkOrPeerReview(peerReview.getSubjectExternalIdentifier());
            }

            validateGroupId(peerReview);
        }

        PeerReviewEntity entity = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(peerReview);

        // Updates the give organization with the latest organization from
        // database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(peerReview);
        entity.setOrg(updatedOrganization);
        
        //Set the source
        if(sourceEntity.getSourceProfile() != null) {
            entity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        if(sourceEntity.getSourceClient() != null) {
            entity.setClientSourceId(sourceEntity.getSourceClient().getId());
        } 
        
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);        
        entity.setProfile(profile);        
        setIncomingPrivacy(entity, profile);
        entity.setDisplayIndex(0L);
        peerReviewDao.increaseDisplayIndexOnAllElements(orcid);
        peerReviewDao.persist(entity);
        peerReviewDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItem(entity));
        return jpaJaxbPeerReviewAdapter.toPeerReview(entity);
    }

    @Override
    public PeerReview updatePeerReview(String orcid, PeerReview peerReview, boolean isApiRequest) {
        PeerReviewEntity existingEntity = peerReviewDao.getPeerReview(orcid, peerReview.getPutCode());        
        Visibility originalVisibility = existingEntity.getVisibility();
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        
        //Save the original source
        String existingSourceId = existingEntity.getSourceId();
        String existingClientSourceId = existingEntity.getClientSourceId();
        
        // If request comes from the API perform validations
        if (isApiRequest) {
            activityValidator.validatePeerReview(peerReview, sourceEntity, false, isApiRequest, originalVisibility);
            validateGroupId(peerReview);
            List<PeerReview> existingReviews = this.findPeerReviews(orcid, System.currentTimeMillis());
            for (PeerReview existing : existingReviews) {
                // Dont compare the updated peer review with the DB version
                if (!existing.getPutCode().equals(peerReview.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(peerReview.getExternalIdentifiers(), existing.getExternalIdentifiers(), existing.getSource(),
                            sourceManager.retrieveSourceEntity());
                }
            }
        }else{
            //check vocab of external identifiers
            externalIDValidator.validateWorkOrPeerReview(peerReview.getExternalIdentifiers());
            externalIDValidator.validateWorkOrPeerReview(peerReview.getSubjectExternalIdentifier());
        }
        PeerReviewEntity updatedEntity = new PeerReviewEntity();        
        
        orcidSecurityManager.checkSource(existingEntity);        
        
        jpaJaxbPeerReviewAdapter.toPeerReviewEntity(peerReview, updatedEntity);
        updatedEntity.setProfile(new ProfileEntity(orcid));
        updatedEntity.setVisibility(originalVisibility);
        
        //Be sure it doesn't overwrite the source
        updatedEntity.setSourceId(existingSourceId);
        updatedEntity.setClientSourceId(existingClientSourceId);
        
        OrgEntity updatedOrganization = orgManager.getOrgEntity(peerReview);
        updatedEntity.setOrg(updatedOrganization);
        updatedEntity = peerReviewDao.merge(updatedEntity);
        peerReviewDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItem(updatedEntity));
        return jpaJaxbPeerReviewAdapter.toPeerReview(updatedEntity);
    }

    @Override
    public boolean checkSourceAndDelete(String orcid, Long peerReviewId) {
        PeerReviewEntity pr = peerReviewDao.getPeerReview(orcid, peerReviewId);
        orcidSecurityManager.checkSource(pr);
        Item item = createItem(pr);
        boolean result = deletePeerReview(pr, orcid);
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, item);
        return result;
    }

    @Transactional
    private boolean deletePeerReview(PeerReviewEntity entity, String orcid) {        
        return peerReviewDao.removePeerReview(orcid, entity.getId());
    }

    private void setIncomingPrivacy(PeerReviewEntity entity, ProfileEntity profile) {
        Visibility incomingVisibility = entity.getVisibility();
        Visibility defaultVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {            
            entity.setVisibility(defaultVisibility);            
        } else if (incomingVisibility == null) {
            entity.setVisibility(Visibility.PRIVATE);
        }
    }

    @Override
    public void removePeerReview(String orcid, Long peerReviewId) {
        peerReviewDao.removePeerReview(orcid, peerReviewId);
    }

    @Override
    public boolean updateToMaxDisplay(String orcid, Long peerReviewId) {
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
     *            Last modified date used to check the cache
     * @return the list of peer reviews that belongs to this user
     * */
    @Override
    @Cacheable(value = "peer-reviews-summaries", key = "#orcid.concat('-').concat(#lastModified)")
    public List<PeerReviewSummary> getPeerReviewSummaryList(String orcid, long lastModified) {
        List<PeerReviewEntity> peerReviewEntities = peerReviewDao.getByUser(orcid);
        return jpaJaxbPeerReviewAdapter.toPeerReviewSummary(peerReviewEntities);
    }

    private void validateGroupId(PeerReview peerReview) {
        if (!PojoUtil.isEmpty(peerReview.getGroupId())) {
            if (!groupIdRecordManager.exists(peerReview.getGroupId())) {
                throw new OrcidValidationException(localeManager.resolveMessage("peer_review.group_id.not_valid"));
            }
        }
    }

    private Item createItem(PeerReviewEntity peerReviewEntity) {
        Item item = new Item();
        item.setItemName(peerReviewEntity.getSubjectName());
        item.setItemType(ItemType.PEER_REVIEW);
        item.setPutCode(String.valueOf(peerReviewEntity.getId()));
        return item;
    }

}
