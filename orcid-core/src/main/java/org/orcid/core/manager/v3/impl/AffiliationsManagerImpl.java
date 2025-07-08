package org.orcid.core.manager.v3.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.manager.ClientDetailsEntityCacheManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.OrgManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.AffiliationsManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.ActivityValidator;
import org.orcid.core.utils.DisplayIndexCalculatorHelper;
import org.orcid.core.utils.SourceEntityUtils;
import org.orcid.jaxb.model.common.ActionType;
import org.orcid.jaxb.model.v3.release.common.Source;
import org.orcid.jaxb.model.v3.release.common.Visibility;
import org.orcid.jaxb.model.v3.release.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.release.notification.permission.Item;
import org.orcid.jaxb.model.v3.release.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.release.record.*;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.pojo.ajaxForm.Member;

public class AffiliationsManagerImpl extends AffiliationsManagerReadOnlyImpl implements AffiliationsManager {

    @Resource(name = "orgManagerV3")
    private OrgManager orgManager;

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager orcidSecurityManager;

    @Resource(name = "notificationManagerV3")
    private NotificationManager notificationManager;

    @Resource(name = "activityValidatorV3")
    private ActivityValidator activityValidator;

    @Resource
    private ClientDetailsEntityCacheManager clientDetailsEntityCacheManager;

    /**
     * Add a new distinction to the given user
     * 
     * @param orcid
     *            The user to add the distinction
     * @param distinction
     *            The distinction to add
     * @return the added employment
     */
    @Override
    public Distinction createDistinctionAffiliation(String orcid, Distinction distinction, boolean isApiRequest) {
        return (Distinction) createAffiliation(orcid, distinction, isApiRequest, AffiliationType.DISTINCTION);
    }

    /**
     * Updates a distinction that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param distinction
     *            The distinction to update
     * @return the updated distinction
     */
    @Override
    public Distinction updateDistinctionAffiliation(String orcid, Distinction distinction, boolean isApiRequest) {
        return (Distinction) updateAffiliation(orcid, distinction, isApiRequest, AffiliationType.DISTINCTION);
    }

    /**
     * Add a new education to the given user
     * 
     * @param orcid
     *            The user to add the education
     * @param education
     *            The education to add
     * @return the added education
     */
    @Override
    public Education createEducationAffiliation(String orcid, Education education, boolean isApiRequest) {
        return (Education) createAffiliation(orcid, education, isApiRequest, AffiliationType.EDUCATION);
    }

    /**
     * Updates a education that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param education
     *            The education to update
     * @return the updated education
     */
    @Override
    public Education updateEducationAffiliation(String orcid, Education education, boolean isApiRequest) {
        return (Education) updateAffiliation(orcid, education, isApiRequest, AffiliationType.EDUCATION);
    }

    /**
     * Add a new employment to the given user
     * 
     * @param orcid
     *            The user to add the employment
     * @param employment
     *            The employment to add
     * @return the added employment
     */
    @Override
    public Employment createEmploymentAffiliation(String orcid, Employment employment, boolean isApiRequest) {
        return (Employment) createAffiliation(orcid, employment, isApiRequest, AffiliationType.EMPLOYMENT);
    }

    /**
     * Updates a employment that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param employment
     *            The employment to update
     * @return the updated employment
     */
    @Override
    public Employment updateEmploymentAffiliation(String orcid, Employment employment, boolean isApiRequest) {
        return (Employment) updateAffiliation(orcid, employment, isApiRequest, AffiliationType.EMPLOYMENT);
    }

    /**
     * Add a new invitedPosition to the given user
     * 
     * @param orcid
     *            The user to add the invitedPosition
     * @param invitedPosition
     *            The invitedPosition to add
     * @return the added invitedPosition
     */
    @Override
    public InvitedPosition createInvitedPositionAffiliation(String orcid, InvitedPosition invitedPosition, boolean isApiRequest) {
        return (InvitedPosition) createAffiliation(orcid, invitedPosition, isApiRequest, AffiliationType.INVITED_POSITION);
    }

    /**
     * Updates a invitedPosition that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param invitedPosition
     *            The invitedPosition to update
     * @return the updated invitedPosition
     */
    @Override
    public InvitedPosition updateInvitedPositionAffiliation(String orcid, InvitedPosition invitedPosition, boolean isApiRequest) {
        return (InvitedPosition) updateAffiliation(orcid, invitedPosition, isApiRequest, AffiliationType.INVITED_POSITION);
    }

    /**
     * Add a new membership to the given user
     * 
     * @param orcid
     *            The user to add the membership
     * @param membership
     *            The membership to add
     * @return the added membership
     */
    @Override
    public Membership createMembershipAffiliation(String orcid, Membership membership, boolean isApiRequest) {
        return (Membership) createAffiliation(orcid, membership, isApiRequest, AffiliationType.MEMBERSHIP);
    }

    /**
     * Updates a membership that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param membership
     *            The membership to update
     * @return the updated membership
     */
    @Override
    public Membership updateMembershipAffiliation(String orcid, Membership membership, boolean isApiRequest) {
        return (Membership) updateAffiliation(orcid, membership, isApiRequest, AffiliationType.MEMBERSHIP);
    }

    /**
     * Add a new qualification to the given user
     * 
     * @param orcid
     *            The user to add the qualification
     * @param qualification
     *            The qualification to add
     * @return the added qualification
     */
    @Override
    public Qualification createQualificationAffiliation(String orcid, Qualification qualification, boolean isApiRequest) {
        return (Qualification) createAffiliation(orcid, qualification, isApiRequest, AffiliationType.QUALIFICATION);
    }

    /**
     * Updates a qualification that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param qualification
     *            The qualification to update
     * @return the updated qualification
     */
    @Override
    public Qualification updateQualificationAffiliation(String orcid, Qualification qualification, boolean isApiRequest) {
        return (Qualification) updateAffiliation(orcid, qualification, isApiRequest, AffiliationType.QUALIFICATION);
    }

    /**
     * Add a new service to the given user
     * 
     * @param orcid
     *            The user to add the service
     * @param service
     *            The service to add
     * @return the added service
     */
    @Override
    public Service createServiceAffiliation(String orcid, Service service, boolean isApiRequest) {
        return (Service) createAffiliation(orcid, service, isApiRequest, AffiliationType.SERVICE);
    }

    /**
     * Updates a service that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param service
     *            The service to update
     * @return the updated service
     */
    @Override
    public Service updateServiceAffiliation(String orcid, Service service, boolean isApiRequest) {
        return (Service) updateAffiliation(orcid, service, isApiRequest, AffiliationType.SERVICE);
    }

    private Affiliation createAffiliation(String orcid, Affiliation affiliation, boolean isApiRequest, AffiliationType type) {
        Source activeSource = sourceManager.retrieveActiveSource();
        activityValidator.validateAffiliation(affiliation, activeSource, true, isApiRequest, null);

        if (isApiRequest) {
            checkAffiliationExternalIDsForDuplicates(orcid, affiliation, activeSource);
        }

        OrgAffiliationRelationEntity entity = null;

        switch (type) {
        case DISTINCTION:
            entity = jpaJaxbDistinctionAdapter.toOrgAffiliationRelationEntity((Distinction) affiliation);
            break;
        case EDUCATION:
            entity = jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity((Education) affiliation);
            break;
        case EMPLOYMENT:
            entity = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity((Employment) affiliation);
            break;
        case INVITED_POSITION:
            entity = jpaJaxbInvitedPositionAdapter.toOrgAffiliationRelationEntity((InvitedPosition) affiliation);
            break;
        case MEMBERSHIP:
            entity = jpaJaxbMembershipAdapter.toOrgAffiliationRelationEntity((Membership) affiliation);
            break;
        case QUALIFICATION:
            entity = jpaJaxbQualificationAdapter.toOrgAffiliationRelationEntity((Qualification) affiliation);
            break;
        case SERVICE:
            entity = jpaJaxbServiceAdapter.toOrgAffiliationRelationEntity((Service) affiliation);
            break;
        }

        entity.setOrcid(orcid);
        
        // Updates the give organization with the latest organization from
        // database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(affiliation);
        entity.setOrg(updatedOrganization);

        SourceEntityUtils.populateSourceAwareEntityFromSource(activeSource, entity);

        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);        
        setIncomingWorkPrivacy(entity, profile, isApiRequest);
        entity.setAffiliationType(type.name());
        DisplayIndexCalculatorHelper.setDisplayIndexOnNewEntity(entity, isApiRequest);

        orgAffiliationRelationDao.persist(entity);
        orgAffiliationRelationDao.flush();

        Affiliation result = null;
        switch (type) {
        case DISTINCTION:
            notificationManager.sendAmendEmail(orcid, AmendedSection.DISTINCTION, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.CREATE));
            result = jpaJaxbDistinctionAdapter.toDistinction(entity);
            break;
        case EDUCATION:
            notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.CREATE));
            result = jpaJaxbEducationAdapter.toEducation(entity);
            break;
        case EMPLOYMENT:
            notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.CREATE));
            result = jpaJaxbEmploymentAdapter.toEmployment(entity);
            break;
        case INVITED_POSITION:
            notificationManager.sendAmendEmail(orcid, AmendedSection.INVITED_POSITION, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.CREATE));
            result = jpaJaxbInvitedPositionAdapter.toInvitedPosition(entity);
            break;
        case MEMBERSHIP:
            notificationManager.sendAmendEmail(orcid, AmendedSection.MEMBERSHIP, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.CREATE));
            result = jpaJaxbMembershipAdapter.toMembership(entity);
            break;
        case QUALIFICATION:
            notificationManager.sendAmendEmail(orcid, AmendedSection.QUALIFICATION, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.CREATE));
            result = jpaJaxbQualificationAdapter.toQualification(entity);
            break;
        case SERVICE:
            notificationManager.sendAmendEmail(orcid, AmendedSection.SERVICE, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.CREATE));
            result = jpaJaxbServiceAdapter.toService(entity);
            break;
        }
        return result;
    }

    public Affiliation updateAffiliation(String orcid, Affiliation affiliation, boolean isApiRequest, AffiliationType type) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(orcid, affiliation.getPutCode());

        Source activeSource = sourceManager.retrieveActiveSource();

        // Save the original source
        Source originalSource = SourceEntityUtils.extractSourceFromEntity(entity, clientDetailsEntityCacheManager);

        String originalVisibility = entity.getVisibility();
        orcidSecurityManager.checkSourceAndThrow(entity);

        activityValidator.validateAffiliation(affiliation, activeSource, false, isApiRequest,
                org.orcid.jaxb.model.v3.release.common.Visibility.valueOf(originalVisibility));

        if (isApiRequest) {
            checkAffiliationExternalIDsForDuplicates(orcid, affiliation, activeSource);
        }

        switch (type) {
        case DISTINCTION:
            jpaJaxbDistinctionAdapter.toOrgAffiliationRelationEntity((Distinction) affiliation, entity);
            break;
        case EDUCATION:
            jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity((Education) affiliation, entity);
            break;
        case EMPLOYMENT:
            jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity((Employment) affiliation, entity);
            break;
        case INVITED_POSITION:
            jpaJaxbInvitedPositionAdapter.toOrgAffiliationRelationEntity((InvitedPosition) affiliation, entity);
            break;
        case MEMBERSHIP:
            jpaJaxbMembershipAdapter.toOrgAffiliationRelationEntity((Membership) affiliation, entity);
            break;
        case QUALIFICATION:
            jpaJaxbQualificationAdapter.toOrgAffiliationRelationEntity((Qualification) affiliation, entity);
            break;
        case SERVICE:
            jpaJaxbServiceAdapter.toOrgAffiliationRelationEntity((Service) affiliation, entity);
            break;
        }

        if (isApiRequest) {
            entity.setVisibility(originalVisibility);
        }

        // Populate display index in case it is missing
        DisplayIndexCalculatorHelper.setDisplayIndexOnExistingEntity(entity, isApiRequest);

        SourceEntityUtils.populateSourceAwareEntityFromSource(originalSource, entity);

        // Updates the give organization with the latest organization from
        // database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(affiliation);
        entity.setOrg(updatedOrganization);

        entity.setAffiliationType(type.name());
        entity = orgAffiliationRelationDao.merge(entity);
        orgAffiliationRelationDao.flush();

        Affiliation result = null;
        switch (type) {
        case DISTINCTION:
            notificationManager.sendAmendEmail(orcid, AmendedSection.DISTINCTION, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.UPDATE));
            result = jpaJaxbDistinctionAdapter.toDistinction(entity);
            break;
        case EDUCATION:
            notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.UPDATE));
            result = jpaJaxbEducationAdapter.toEducation(entity);
            break;
        case EMPLOYMENT:
            notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.UPDATE));
            result = jpaJaxbEmploymentAdapter.toEmployment(entity);
            break;
        case INVITED_POSITION:
            notificationManager.sendAmendEmail(orcid, AmendedSection.INVITED_POSITION, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.UPDATE));
            result = jpaJaxbInvitedPositionAdapter.toInvitedPosition(entity);
            break;
        case MEMBERSHIP:
            notificationManager.sendAmendEmail(orcid, AmendedSection.MEMBERSHIP, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.UPDATE));
            result = jpaJaxbMembershipAdapter.toMembership(entity);
            break;
        case QUALIFICATION:
            notificationManager.sendAmendEmail(orcid, AmendedSection.QUALIFICATION, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.UPDATE));
            result = jpaJaxbQualificationAdapter.toQualification(entity);
            break;
        case SERVICE:
            notificationManager.sendAmendEmail(orcid, AmendedSection.SERVICE, createItemList(entity, affiliation.getExternalIdentifiers(), ActionType.UPDATE));
            result = jpaJaxbServiceAdapter.toService(entity);
            break;
        }
        return result;
    }

    /**
     * Deletes a given affiliation, if and only if, the client that requested
     * the delete is the source of the affiliation
     * 
     * @param orcid
     *            the affiliation owner
     * @param affiliationId
     *            The affiliation id
     * @return true if the affiliation was deleted, false otherwise
     */
    @Override
    public boolean checkSourceAndDelete(String orcid, Long affiliationId) {
        OrgAffiliationRelationEntity affiliationEntity = orgAffiliationRelationDao.getOrgAffiliationRelation(orcid, affiliationId);
        orcidSecurityManager.checkSourceAndThrow(affiliationEntity);
        boolean result = orgAffiliationRelationDao.removeOrgAffiliationRelation(orcid, affiliationId);
        if (result) {
            if (AffiliationType.DISTINCTION.name().equals(affiliationEntity.getAffiliationType())) {
                Distinction aff = jpaJaxbDistinctionAdapter.toDistinction(affiliationEntity);
                notificationManager.sendAmendEmail(orcid, AmendedSection.DISTINCTION, createItemList(affiliationEntity, aff.getExternalIdentifiers(), ActionType.DELETE));
            } else if (AffiliationType.EDUCATION.name().equals(affiliationEntity.getAffiliationType())) {
                Education aff = jpaJaxbEducationAdapter.toEducation(affiliationEntity);
                notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItemList(affiliationEntity, aff.getExternalIdentifiers(), ActionType.DELETE));
            } else if (AffiliationType.EMPLOYMENT.name().equals(affiliationEntity.getAffiliationType())) {
                Employment aff = jpaJaxbEmploymentAdapter.toEmployment(affiliationEntity);
                notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItemList(affiliationEntity, aff.getExternalIdentifiers(), ActionType.DELETE));
            } else if (AffiliationType.INVITED_POSITION.name().equals(affiliationEntity.getAffiliationType())) {
                InvitedPosition aff = jpaJaxbInvitedPositionAdapter.toInvitedPosition(affiliationEntity);
                notificationManager.sendAmendEmail(orcid, AmendedSection.INVITED_POSITION, createItemList(affiliationEntity, aff.getExternalIdentifiers(), ActionType.DELETE));
            } else if (AffiliationType.MEMBERSHIP.name().equals(affiliationEntity.getAffiliationType())) {
                Membership aff = jpaJaxbMembershipAdapter.toMembership(affiliationEntity);
                notificationManager.sendAmendEmail(orcid, AmendedSection.MEMBERSHIP, createItemList(affiliationEntity, aff.getExternalIdentifiers(), ActionType.DELETE));
            } else if (AffiliationType.QUALIFICATION.name().equals(affiliationEntity.getAffiliationType())) {
                Qualification aff = jpaJaxbQualificationAdapter.toQualification(affiliationEntity);
                notificationManager.sendAmendEmail(orcid, AmendedSection.QUALIFICATION, createItemList(affiliationEntity, aff.getExternalIdentifiers(), ActionType.DELETE));
            } else if (AffiliationType.SERVICE.name().equals(affiliationEntity.getAffiliationType())) {
                Service aff = jpaJaxbServiceAdapter.toService(affiliationEntity);
                notificationManager.sendAmendEmail(orcid, AmendedSection.SERVICE, createItemList(affiliationEntity, aff.getExternalIdentifiers(), ActionType.DELETE));
            }
        }
        return result;
    }

    private void setIncomingWorkPrivacy(OrgAffiliationRelationEntity orgAffiliationRelationEntity, ProfileEntity profile, boolean isApiRequest) {
        String incomingElementVisibility = orgAffiliationRelationEntity.getVisibility();
        String defaultElementVisibility = profile.getActivitiesVisibilityDefault();
        if ((isApiRequest && profile.getClaimed()) || (incomingElementVisibility == null && !isApiRequest)) {
            orgAffiliationRelationEntity.setVisibility(defaultElementVisibility);
        } else if (isApiRequest && !profile.getClaimed() && incomingElementVisibility == null) {
            orgAffiliationRelationEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE.name());
        }
    }

    private List<Item> createItemList(OrgAffiliationRelationEntity orgAffiliationEntity, ExternalIDs extIds, ActionType type) {
        Item item = new Item();
        if (!StringUtils.isBlank(orgAffiliationEntity.getTitle())) {
            item.setItemName(orgAffiliationEntity.getTitle());
        }
        ItemType itemType = null;
        if (AffiliationType.DISTINCTION.name().equals(orgAffiliationEntity.getAffiliationType())) {
            itemType = ItemType.DISTINCTION;
        } else if (AffiliationType.EDUCATION.name().equals(orgAffiliationEntity.getAffiliationType())) {
            itemType = ItemType.EDUCATION;
        } else if (AffiliationType.EMPLOYMENT.name().equals(orgAffiliationEntity.getAffiliationType())) {
            itemType = ItemType.EMPLOYMENT;
        } else if (AffiliationType.INVITED_POSITION.name().equals(orgAffiliationEntity.getAffiliationType())) {
            itemType = ItemType.INVITED_POSITION;
        } else if (AffiliationType.MEMBERSHIP.name().equals(orgAffiliationEntity.getAffiliationType())) {
            itemType = ItemType.MEMBERSHIP;
        } else if (AffiliationType.QUALIFICATION.name().equals(orgAffiliationEntity.getAffiliationType())) {
            itemType = ItemType.QUALIFICATION;
        } else if (AffiliationType.SERVICE.name().equals(orgAffiliationEntity.getAffiliationType())) {
            itemType = ItemType.SERVICE;
        }
        item.setActionType(type);
        item.setItemType(itemType);
        item.setPutCode(String.valueOf(orgAffiliationEntity.getId()));
        Map<String, Object> additionalInfo = new HashMap<String, Object>();
        additionalInfo.put("department", orgAffiliationEntity.getDepartment());
        additionalInfo.put("org_name", orgAffiliationEntity.getOrg().getName());

        if(extIds != null && extIds.getExternalIdentifier() != null && !extIds.getExternalIdentifier().isEmpty()) {
            additionalInfo.put("external_identifiers", extIds);
        }

        item.setAdditionalInfo(additionalInfo);
        return Arrays.asList(item);
    }

    @Override
    public boolean updateVisibility(String orcid, Long affiliationId, Visibility visibility) {
        return orgAffiliationRelationDao.updateVisibilityOnOrgAffiliationRelation(orcid, affiliationId, visibility.name());
    }

    @Override
    @Transactional
    public boolean updateVisibilities(String orcid, ArrayList<Long> affiliationIds, Visibility visibility) {
        return orgAffiliationRelationDao.updateVisibilitiesOnOrgAffiliationRelation(orcid, affiliationIds, visibility.name());
    }

    /**
     * Deletes an affiliation.
     * 
     * It doesn't check the source of the element before delete it, so, it is
     * intended to be used only by the user from the UI
     * 
     * @param userOrcid
     *            The client orcid
     *
     * @param affiliationId
     *            The affiliation id in the DB
     * @return true if the relationship was deleted
     */
    @Override
    public boolean removeAffiliation(String userOrcid, Long affiliationId) {
        return orgAffiliationRelationDao.removeOrgAffiliationRelation(userOrcid, affiliationId);
    }

    @Override
    public void removeAllAffiliations(String orcid) {
        orgAffiliationRelationDao.removeAllAffiliations(orcid);
    }

    private void checkAffiliationExternalIDsForDuplicates(String orcid, Affiliation incoming, Source activeSource) {
        List<Affiliation> affiliations = getAffiliations(orcid);
        if (affiliations != null) {
            for (Affiliation affiliation : affiliations) {
                // If it is the same element, ignore it, to prevent false
                // duplicate exceptions
                if (incoming.getPutCode() != null && incoming.getPutCode().equals(affiliation.getPutCode())) {
                    continue;
                }

                if (incoming.getClass().isAssignableFrom(affiliation.getClass())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(incoming, affiliation, affiliation.getSource(), activeSource);
                }
            }
        }
    }

    @Override
    public Boolean updateToMaxDisplay(String orcid, Long putCode) {
        return orgAffiliationRelationDao.updateToMaxDisplay(orcid, putCode);
    }
}
