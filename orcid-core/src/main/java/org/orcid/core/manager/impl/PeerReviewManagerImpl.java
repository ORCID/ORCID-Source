package org.orcid.core.manager.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.exception.GroupIdRecordNotFoundException;
import org.orcid.core.groupIds.issn.IssnGroupIdPatternMatcher;
import org.orcid.core.locale.LocaleManager;
import org.orcid.core.manager.GroupIdRecordManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.PeerReviewManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.GroupIdRecordManagerReadOnly;
import org.orcid.core.manager.read_only.impl.PeerReviewManagerReadOnlyImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.manager.validator.ExternalIDValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.core.utils.v3.identifiers.normalizers.ISSNNormalizer;
import org.orcid.jaxb.model.common.ActionType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.groupid_v2.GroupIdRecord;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.ItemType;
import org.orcid.jaxb.model.record_v2.ExternalID;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.PeerReview;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.PeerReviewEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.transaction.annotation.Transactional;

public class PeerReviewManagerImpl extends PeerReviewManagerReadOnlyImpl implements PeerReviewManager {

    @Resource
    private ISSNNormalizer issnNormaliser;

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

    @Resource(name = "groupIdRecordManagerReadOnly")
    private GroupIdRecordManagerReadOnly groupIdRecordManagerReadOnly;

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
            if (!SourceEntityUtils.getSourceId(sourceEntity).equals(orcid)) {
                if (peerReviews != null) {
                    for (PeerReviewEntity entity : peerReviews) {
                        PeerReview existing = jpaJaxbPeerReviewAdapter.toPeerReview(entity);
                        activityValidator.checkExternalIdentifiersForDuplicates(peerReview, existing, existing.getSource(), sourceEntity);
                    }
                }
            } else {
                // check vocab of external identifiers
                externalIDValidator.validateWorkOrPeerReview(peerReview.getExternalIdentifiers());
                externalIDValidator.validateWorkOrPeerReview(peerReview.getSubjectExternalIdentifier());
            }

            createIssnGroupIdIfNecessary(peerReview);
            validateGroupId(peerReview);
        }

        PeerReviewEntity entity = jpaJaxbPeerReviewAdapter.toPeerReviewEntity(peerReview);
        entity.setOrcid(orcid);
        
        // Updates the give organization with the latest organization from
        // database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(peerReview);
        entity.setOrg(updatedOrganization);

        // Set the source
        if (sourceEntity.getSourceProfile() != null) {
            entity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        if (sourceEntity.getSourceClient() != null) {
            entity.setClientSourceId(sourceEntity.getSourceClient().getId());
        }

        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);        
        setIncomingPrivacy(entity, profile);
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(entity, isApiRequest);

        peerReviewDao.persist(entity);
        peerReviewDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItemList(entity, ActionType.CREATE, peerReview.getExternalIdentifiers(), peerReview.getSubjectExternalIdentifier()));
        return jpaJaxbPeerReviewAdapter.toPeerReview(entity);
    }

    @Override
    public PeerReview updatePeerReview(String orcid, PeerReview peerReview, boolean isApiRequest) {
        PeerReviewEntity existingEntity = peerReviewDao.getPeerReview(orcid, peerReview.getPutCode());
        String originalVisibility = existingEntity.getVisibility();
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Save the original source
        String existingSourceId = existingEntity.getSourceId();
        String existingClientSourceId = existingEntity.getClientSourceId();

        orcidSecurityManager.checkSource(existingEntity);
        
        // If request comes from the API perform validations
        if (isApiRequest) {
            activityValidator.validatePeerReview(peerReview, sourceEntity, false, isApiRequest, Visibility.valueOf(originalVisibility));
            validateGroupId(peerReview);
            List<PeerReview> existingReviews = this.findPeerReviews(orcid);
            for (PeerReview existing : existingReviews) {
                // Dont compare the updated peer review with the DB version
                if (!existing.getPutCode().equals(peerReview.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(peerReview, existing, existing.getSource(), sourceManager.retrieveSourceEntity());
                }
            }
        } else {
            // check vocab of external identifiers
            externalIDValidator.validateWorkOrPeerReview(peerReview.getExternalIdentifiers());
            externalIDValidator.validateWorkOrPeerReview(peerReview.getSubjectExternalIdentifier());
        }             
        
        jpaJaxbPeerReviewAdapter.toPeerReviewEntity(peerReview, existingEntity);        
        existingEntity.setVisibility(originalVisibility);
        
        //Be sure it doesn't overwrite the source
        existingEntity.setSourceId(existingSourceId);
        existingEntity.setClientSourceId(existingClientSourceId);        

        createIssnGroupIdIfNecessary(peerReview);
        OrgEntity updatedOrganization = orgManager.getOrgEntity(peerReview);
        existingEntity.setOrg(updatedOrganization);
        
        existingEntity = peerReviewDao.merge(existingEntity);
        peerReviewDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItemList(existingEntity, ActionType.UPDATE, peerReview.getExternalIdentifiers(), peerReview.getSubjectExternalIdentifier()));
        return jpaJaxbPeerReviewAdapter.toPeerReview(existingEntity);
    }

    private void createIssnGroupIdIfNecessary(PeerReview peerReview) {
        if (IssnGroupIdPatternMatcher.isIssnGroupType(peerReview.getGroupId())) {
            String normalisedIssn = issnNormaliser.normalise("issn", peerReview.getGroupId());
            String normalisedGroupId = "issn:" + normalisedIssn;
            if (!groupIdRecordManager.exists(normalisedGroupId)) {
                groupIdRecordManager.createOrcidSourceIssnGroupIdRecord(normalisedGroupId, normalisedIssn);
            }
            peerReview.setGroupId(normalisedGroupId);
        }
    }

    @Override
    public boolean checkSourceAndDelete(String orcid, Long peerReviewId) {
        PeerReviewEntity pr = peerReviewDao.getPeerReview(orcid, peerReviewId);
        orcidSecurityManager.checkSource(pr);
        boolean result = deletePeerReview(pr, orcid);
        PeerReview model = jpaJaxbPeerReviewAdapter.toPeerReview(pr);
        notificationManager.sendAmendEmail(orcid, AmendedSection.PEER_REVIEW, createItemList(pr, ActionType.DELETE, model.getExternalIdentifiers(), model.getSubjectExternalIdentifier()));
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
            entity.setVisibility(Visibility.PRIVATE.name());
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
                throw new GroupIdRecordNotFoundException(localeManager.resolveMessage("peer_review.group_id.not_valid"));
            }
        }
    }

    private List<Item> createItemList(PeerReviewEntity peerReviewEntity, ActionType type, ExternalIDs extIds, ExternalID subjectExtId) {
        Item item = new Item();
        item.setItemType(ItemType.PEER_REVIEW);
        item.setPutCode(String.valueOf(peerReviewEntity.getId()));
        item.setActionType(type);
        Map<String, Object> additionalInfo = new HashMap<String, Object>();
        additionalInfo.put("subject_container_name", peerReviewEntity.getSubjectContainerName());

        String itemName = null;

        Optional<GroupIdRecord> optional = groupIdRecordManagerReadOnly.findByGroupId(peerReviewEntity.getGroupId());
        if (optional.isPresent()) {
            GroupIdRecord groupId = optional.get();
            if (!StringUtils.isBlank(groupId.getName())) {
                additionalInfo.put("group_name", optional.get().getName());
                itemName = optional.get().getName();
            }
        }

        if(extIds != null) {
            additionalInfo.put("external_identifiers", extIds);
        }

        if(subjectExtId != null) {
            additionalInfo.put("subject_external_identifiers", subjectExtId);
        }

        item.setItemName(itemName);
        item.setAdditionalInfo(additionalInfo);
        return Arrays.asList(item);
    }

    @Override
    public void removeAllPeerReviews(String orcid) {
        peerReviewDao.removeAllPeerReviews(orcid);
    }

}