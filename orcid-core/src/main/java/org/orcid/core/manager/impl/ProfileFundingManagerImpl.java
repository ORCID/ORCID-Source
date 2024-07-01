package org.orcid.core.manager.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.ProfileFundingManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.ProfileFundingManagerReadOnlyImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.jaxb.model.common.ActionType;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.ItemType;
import org.orcid.jaxb.model.record_v2.ExternalIDs;
import org.orcid.jaxb.model.record_v2.Funding;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.ProfileFundingEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

public class ProfileFundingManagerImpl extends ProfileFundingManagerReadOnlyImpl implements ProfileFundingManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileFundingManagerImpl.class);

    @Resource
    private OrgManager orgManager;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private ActivityValidator activityValidator;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private NotificationManager notificationManager;

    /**
     * Removes the relationship that exists between a funding and a profile.
     * 
     * @param profileFundingId
     *            The id of the profileFunding that will be removed from the
     *            client profile
     * @param clientOrcid
     *            The client orcid
     * @return true if the relationship was deleted
     */
    public boolean removeProfileFunding(String clientOrcid, Long profileFundingId) {
        return profileFundingDao.removeProfileFunding(clientOrcid, profileFundingId);
    }

    /**
     * Updates the visibility of an existing profile funding relationship
     * 
     * @param clientOrcid
     *            The client orcid
     * 
     * @param profileFundingId
     *            The id of the profile funding that will be updated
     * 
     * @param visibility
     *            The new visibility value for the profile profileFunding object
     * 
     * @return true if the relationship was updated
     */
    public boolean updateProfileFundingVisibility(String clientOrcid, Long profileFundingId, Visibility visibility) {
        return profileFundingDao.updateProfileFundingVisibility(clientOrcid, profileFundingId, visibility.name());
    }

    public boolean updateToMaxDisplay(String orcid, Long fundingId) {
        return profileFundingDao.updateToMaxDisplay(orcid, fundingId);
    }

    /**
     * Add a new funding to the given user
     * 
     * @param orcid
     *            The user to add the funding
     * @param funding
     *            The funding to add
     * @return the added funding
     */
    @Override
    @Transactional
    public Funding createFunding(String orcid, Funding funding, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        activityValidator.validateFunding(funding, sourceEntity, true, isApiRequest, null);

        // Check for duplicates
        List<ProfileFundingEntity> existingFundings = profileFundingDao.getByUser(orcid, getLastModified(orcid));
        List<Funding> fundings = jpaJaxbFundingAdapter.toFunding(existingFundings);
        if (fundings != null && isApiRequest) {
            for (Funding exstingFunding : fundings) {
                activityValidator.checkExternalIdentifiersForDuplicates(funding, exstingFunding, exstingFunding.getSource(), sourceEntity);
            }
        }

        ProfileFundingEntity profileFundingEntity = jpaJaxbFundingAdapter.toProfileFundingEntity(funding);
        profileFundingEntity.setOrcid(orcid);
        
        // Updates the give organization with the latest organization from
        // database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(funding);
        profileFundingEntity.setOrg(updatedOrganization);

        // Set the source
        if (sourceEntity.getSourceProfile() != null) {
            profileFundingEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        if (sourceEntity.getSourceClient() != null) {
            profileFundingEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        }

        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);        
        setIncomingWorkPrivacy(profileFundingEntity, profile);
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(profileFundingEntity, isApiRequest);
        profileFundingDao.persist(profileFundingEntity);
        profileFundingDao.flush();
        if (isApiRequest) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.FUNDING, createItemList(profileFundingEntity, funding.getExternalIdentifiers(), ActionType.CREATE));
        }
        return jpaJaxbFundingAdapter.toFunding(profileFundingEntity);
    }

    private void setIncomingWorkPrivacy(ProfileFundingEntity profileFundingEntity, ProfileEntity profile) {
        String incomingWorkVisibility = profileFundingEntity.getVisibility();
        String defaultWorkVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {
            profileFundingEntity.setVisibility(defaultWorkVisibility);
        } else if (incomingWorkVisibility == null) {
            profileFundingEntity.setVisibility(Visibility.PRIVATE.name());
        }
    }

    /**
     * Updates a funding that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param funding
     *            The funding to update
     * @return the updated funding
     */
    @Override
    public Funding updateFunding(String orcid, Funding funding, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        ProfileFundingEntity pfe = profileFundingDao.getProfileFunding(orcid, funding.getPutCode());
        Visibility originalVisibility = Visibility.valueOf(pfe.getVisibility());

        // Save the original source
        String existingSourceId = pfe.getSourceId();
        String existingClientSourceId = pfe.getClientSourceId();

        activityValidator.validateFunding(funding, sourceEntity, false, isApiRequest, originalVisibility);
        if (isApiRequest) {
            List<ProfileFundingEntity> existingFundings = profileFundingDao.getByUser(orcid, getLastModified(orcid));
            for (ProfileFundingEntity existingFunding : existingFundings) {
                Funding existing = jpaJaxbFundingAdapter.toFunding(existingFunding);
                if (!existing.getPutCode().equals(funding.getPutCode())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(funding, existing, existing.getSource(), sourceEntity);
                }
            }
        }

        orcidSecurityManager.checkSource(pfe);

        jpaJaxbFundingAdapter.toProfileFundingEntity(funding, pfe);
        pfe.setVisibility(originalVisibility.name());

        // Be sure it doesn't overwrite the source
        pfe.setSourceId(existingSourceId);
        pfe.setClientSourceId(existingClientSourceId);

        // Updates the give organization with the latest organization from
        // database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(funding);
        pfe.setOrg(updatedOrganization);

        pfe = profileFundingDao.merge(pfe);
        profileFundingDao.flush();
        if (isApiRequest) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.FUNDING, createItemList(pfe, funding.getExternalIdentifiers(), ActionType.UPDATE));
        }
        return jpaJaxbFundingAdapter.toFunding(pfe);
    }

    /**
     * Deletes a given funding, if and only if, the client that requested the
     * delete is the source of the funding
     * 
     * @param orcid
     *            the funding owner
     * @param fundingId
     *            The funding id
     * @return true if the funding was deleted, false otherwise
     */
    @Override
    @Transactional
    public boolean checkSourceAndDelete(String orcid, Long fundingId) {
        ProfileFundingEntity pfe = profileFundingDao.getProfileFunding(orcid, fundingId);
        orcidSecurityManager.checkSource(pfe);
        boolean result = profileFundingDao.removeProfileFunding(orcid, fundingId);
        Funding funding = jpaJaxbFundingAdapter.toFunding(pfe);
        notificationManager.sendAmendEmail(orcid, AmendedSection.FUNDING, createItemList(pfe, funding.getExternalIdentifiers(), ActionType.DELETE));
        return result;
    }

    private List<Item> createItemList(ProfileFundingEntity profileFundingEntity, ExternalIDs extIds, ActionType type) {
        Item item = new Item();
        item.setItemName(profileFundingEntity.getTitle());
        item.setItemType(ItemType.FUNDING);
        item.setPutCode(String.valueOf(profileFundingEntity.getId()));
        item.setActionType(type);

        if(extIds != null) {
            Map<String, Object> additionalInfo = new HashMap<String, Object>();
            additionalInfo.put("external_identifiers", extIds);
            item.setAdditionalInfo(additionalInfo);
        }

        return Arrays.asList(item);
    }

    @Override
    public void removeAllFunding(String orcid) {
        profileFundingDao.removeAllFunding(orcid);
    }
}
