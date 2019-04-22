package org.orcid.core.manager.v3.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.exception.OrcidValidationException;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.GroupIdRecordManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.OrgManager;
import org.orcid.core.manager.v3.PeerReviewManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.PeerReviewManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.ActivityValidator;
import org.orcid.core.manager.v3.validator.ExternalIDValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.release.record.PeerReview;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.transaction.annotation.Transactional;

public class PeerReviewManagerImpl extends PeerReviewManagerReadOnlyImpl implements PeerReviewManager {

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "orgManagerV3")
    private OrgManager orgManager;

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private LocaleManager localeManager;

    @Resource(name = "groupIdRecordManagerV3")
    private GroupIdRecordManager groupIdRecordManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource(name = "externalIDValidatorV3")
    private ExternalIDValidator externalIDValidator;
    
    @Resource(name = "activityValidatorV3")
    private ActivityValidator activityValidator;
    
    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;
        
    @Override    
    public PeerReview createPeerReview(String orcid, PeerReview peerReview, boolean isApiRequest) {
        Source activeSource = sourceManager.retrieveActiveSource();

        // If request comes from the API, perform the validations
        if (isApiRequest) {
            // Validate it have at least one ext id
            activityValidator.validatePeerReview(peerReview, activeSource, true, isApiRequest, null);

            List<PeerReviewEntity> peerReviews = peerReviewDao.getByUser(orcid, getLastModified(orcid));
            // If it is the user adding the peer review, allow him to add
            // duplicates
            if (!(activeSource.getSourceOrcid() != null && activeSource.getSourceOrcid().getPath().equals(orcid))) {
                if (peerReviews != null) {
                    for (PeerReviewEntity entity : peerReviews) {
                        PeerReview existing = jpaJaxbPeerReviewAdapter.toPeerReview(entity);
                        activityValidator.checkExternalIdentifiersForDuplicates(peerReview.getExternalIdentifiers(), existing.getExternalIdentifiers(), existing.getSource(),
                                activeSource);
                    }
                }
            }else{
                //check vocab of external identifiers
                externalIDValidator.validatePeerReview(peerReview.getExternalIdentifiers());
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
        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, entity);
        
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
        Visibility originalVisibility = Visibility.valueOf(existingEntity.getVisibility());
        Source activeSource = sourceManager.retrieveActiveSource();
        
        //Save the original source
        Source originalSource = SourceEntityUtils.extractSourceFromEntity(existingEntity);
        
        // If request comes from the API perform validations
        if (isApiRequest) {
            activityValidator.validatePeerReview(peerReview, activeSource, false, isApiRequest, originalVisibility);
            validateGroupId(peerReview);
            List<PeerReview> existingReviews = this.findPeerReviews(orcid);
            for (PeerReview existing : existingReviews) {
                // Dont compare the updated peer review with the DB version
                if (!existing.getPutCode().equals(peerReview.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(peerReview.getExternalIdentifiers(), existing.getExternalIdentifiers(), existing.getSource(),
                            activeSource);
                }
            }
        }else{
            //check vocab of external identifiers
            externalIDValidator.validatePeerReview(peerReview.getExternalIdentifiers());
            externalIDValidator.validateWorkOrPeerReview(peerReview.getSubjectExternalIdentifier());
        }
        PeerReviewEntity updatedEntity = new PeerReviewEntity();        
        
        orcidSecurityManager.checkSourceAndThrow(existingEntity);        
        
        jpaJaxbPeerReviewAdapter.toPeerReviewEntity(peerReview, updatedEntity);
        updatedEntity.setProfile(new ProfileEntity(orcid));
        updatedEntity.setVisibility(originalVisibility.name());
        
        //Be sure it doesn't overwrite the source
        SourceEntityUtils.populateSourceAwareEntityFromSource(originalSource, updatedEntity);
        
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
        orcidSecurityManager.checkSourceAndThrow(pr);
        boolean result = deletePeerReview(pr, orcid);
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItemList(pr));
        return result;
    }

    @Transactional
    private boolean deletePeerReview(PeerReviewEntity entity, String orcid) {        
        return peerReviewDao.removePeerReview(orcid, entity.getId());
    }

    private void setIncomingPrivacy(PeerReviewEntity entity, ProfileEntity profile) {
        String incomingVisibility = entity.getVisibility();
        String defaultVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {            
            entity.setVisibility(defaultVisibility);            
        } else if (incomingVisibility == null) {
            entity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
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
        return peerReviewDao.updateVisibilities(orcid, peerReviewIds, visibility.name());
    }

    private void validateGroupId(PeerReview peerReview) {
        if (!PojoUtil.isEmpty(peerReview.getGroupId())) {
            if (!groupIdRecordManager.exists(peerReview.getGroupId())) {
                throw new OrcidValidationException(localeManager.resolveMessage("peer_review.group_id.not_valid"));
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