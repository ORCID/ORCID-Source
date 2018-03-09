package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.PeerReviewManagerReadOnlyImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.manager.validator.ExternalIDValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.ItemType;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.transaction.annotation.Transactional;

public class PeerReviewManagerImpl extends PeerReviewManagerReadOnlyImpl implements PeerReviewManager {

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private OrgManager orgManager;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

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
    public PeerReview createPeerReview(String orcid, PeerReview peerReview, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // If request comes from the API, perform the validations
        if (isApiRequest) {
            // Validate it have at least one ext id
            activityValidator.validatePeerReview(peerReview, sourceEntity, true, isApiRequest, null);

            List<PeerReviewEntity> peerReviews = peerReviewDao.getByUser(orcid, getLastModified(orcid));
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
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(entity, isApiRequest);
        peerReviewDao.persist(entity);
        peerReviewDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItemList(entity));
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
            List<PeerReview> existingReviews = this.findPeerReviews(orcid);
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
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItemList(updatedEntity));
        return jpaJaxbPeerReviewAdapter.toPeerReview(updatedEntity);
    }

    @Override
    public boolean checkSourceAndDelete(String orcid, Long peerReviewId) {
        PeerReviewEntity pr = peerReviewDao.getPeerReview(orcid, peerReviewId);
        orcidSecurityManager.checkSource(pr);        
        boolean result = deletePeerReview(pr, orcid);
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItemList(pr));
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

    private void validateGroupId(PeerReview peerReview) {
        if (!PojoUtil.isEmpty(peerReview.getGroupId())) {
            if (!groupIdRecordManager.exists(peerReview.getGroupId())) {
                throw new GroupIdRecordNotFoundException(localeManager.resolveMessage("peer_review.group_id.not_valid"));
            }
        }
    }

    private List<Item> createItemList(PeerReviewEntity peerReviewEntity) {
        Item item = new Item();
        item.setItemName(peerReviewEntity.getSubjectName());
        item.setItemType(ItemType.PEER_REVIEW);
        item.setPutCode(String.valueOf(peerReviewEntity.getId()));
        return Arrays.asList(item);
    }

    @Override
    public void removeAllPeerReviews(String orcid) {
        peerReviewDao.removeAllPeerReviews(orcid);
    }
}