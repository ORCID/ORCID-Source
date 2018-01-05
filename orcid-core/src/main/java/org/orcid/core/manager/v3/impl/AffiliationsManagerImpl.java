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
package org.orcid.core.manager.v3.impl;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.AffiliationsManager;
import org.orcid.core.manager.v3.NotificationManager;
import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.v3.OrgManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.AffiliationsManagerReadOnlyImpl;
import org.orcid.core.manager.v3.validator.ActivityValidator;
import org.orcid.jaxb.model.v3.dev1.common.Visibility;
import org.orcid.jaxb.model.v3.dev1.notification.amended.AmendedSection;
import org.orcid.jaxb.model.v3.dev1.notification.permission.Item;
import org.orcid.jaxb.model.v3.dev1.notification.permission.ItemType;
import org.orcid.jaxb.model.v3.dev1.record.Affiliation;
import org.orcid.jaxb.model.v3.dev1.record.AffiliationType;
import org.orcid.jaxb.model.v3.dev1.record.Education;
import org.orcid.jaxb.model.v3.dev1.record.Employment;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

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
    
    private Affiliation createAffiliation(String orcid, Affiliation affiliation, boolean isApiRequest, AffiliationType type) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        activityValidator.validateAffiliation(affiliation, sourceEntity, true, isApiRequest, null);

        if (isApiRequest) {
                checkAffiliationExternalIDsForDuplicates(orcid, affiliation, sourceEntity);
        }

        OrgAffiliationRelationEntity entity = null;
        
        if(type.equals(AffiliationType.EDUCATION)) {
            entity = jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity((Education) affiliation);
        } else if(type.equals(AffiliationType.EMPLOYMENT)) {
            entity = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity((Employment) affiliation);
        }
        
        // Updates the give organization with the latest organization from
        // database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(affiliation);
        entity.setOrg(updatedOrganization);

        // Set source id
        if (sourceEntity.getSourceProfile() != null) {
            entity.setSourceId(sourceEntity.getSourceProfile().getId());
        }

        if (sourceEntity.getSourceClient() != null) {
            entity.setClientSourceId(sourceEntity.getSourceClient().getId());
        }

        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        entity.setProfile(profile);
        setIncomingWorkPrivacy(entity, profile);
        if(type.equals(AffiliationType.EDUCATION)) {
            entity.setAffiliationType(org.orcid.jaxb.model.record_v2.AffiliationType.EDUCATION);
        } else if(type.equals(AffiliationType.EMPLOYMENT)) {
            entity.setAffiliationType(org.orcid.jaxb.model.record_v2.AffiliationType.EMPLOYMENT);
        }
        orgAffiliationRelationDao.persist(entity);
        orgAffiliationRelationDao.flush();
        
        Affiliation result = null;
        if(type.equals(AffiliationType.EDUCATION)) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItem(entity));
            result = jpaJaxbEducationAdapter.toEducation(entity);
        } else if (type.equals(AffiliationType.EMPLOYMENT)) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItem(entity));
            result = jpaJaxbEmploymentAdapter.toEmployment(entity);
        }
        return result;
    }
    
    public Affiliation updateAffiliation(String orcid, Affiliation affiliation, boolean isApiRequest, AffiliationType type) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(orcid, affiliation.getPutCode());

        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();

        // Save the original source
        String existingSourceId = entity.getSourceId();
        String existingClientSourceId = entity.getClientSourceId();

        org.orcid.jaxb.model.common_v2.Visibility originalVisibility = entity.getVisibility();
        orcidSecurityManager.checkSource(entity);

        activityValidator.validateAffiliation(affiliation, sourceEntity, false, isApiRequest,
                org.orcid.jaxb.model.v3.dev1.common.Visibility.fromValue(originalVisibility.value()));

        if (isApiRequest) {
            checkAffiliationExternalIDsForDuplicates(orcid, affiliation, sourceEntity);
        }

        if(type.equals(AffiliationType.EDUCATION)) {
            jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity((Education) affiliation, entity);            
        } else if(type.equals(AffiliationType.EMPLOYMENT)) {
            jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity((Employment) affiliation, entity);            
        }
        
        entity.setVisibility(originalVisibility);

        // Be sure it doesn't overwrite the source
        entity.setSourceId(existingSourceId);
        entity.setClientSourceId(existingClientSourceId);

        // Updates the give organization with the latest organization from
        // database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(affiliation);
        entity.setOrg(updatedOrganization);

        if(type.equals(AffiliationType.EDUCATION)) {
            entity.setAffiliationType(org.orcid.jaxb.model.record_v2.AffiliationType.EDUCATION);            
        } else if(type.equals(AffiliationType.EMPLOYMENT)) {
            entity.setAffiliationType(org.orcid.jaxb.model.record_v2.AffiliationType.EMPLOYMENT);            
        }
        
        entity = orgAffiliationRelationDao.merge(entity);
        orgAffiliationRelationDao.flush();
        
        Affiliation result = null;
        if(type.equals(AffiliationType.EDUCATION)) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItem(entity));
            result = jpaJaxbEducationAdapter.toEducation(entity);
        } else if(type.equals(AffiliationType.EMPLOYMENT)) {
            notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItem(entity));
            result = jpaJaxbEmploymentAdapter.toEmployment(entity);
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
        orcidSecurityManager.checkSource(affiliationEntity);
        boolean result = orgAffiliationRelationDao.removeOrgAffiliationRelation(orcid, affiliationId);
        if (result)
            notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItem(affiliationEntity));
        return result;
    }

    private void setIncomingWorkPrivacy(OrgAffiliationRelationEntity orgAffiliationRelationEntity, ProfileEntity profile) {
        org.orcid.jaxb.model.common_v2.Visibility incomingElementVisibility = orgAffiliationRelationEntity.getVisibility();
        org.orcid.jaxb.model.common_v2.Visibility defaultElementVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {
            orgAffiliationRelationEntity.setVisibility(defaultElementVisibility);
        } else if (incomingElementVisibility == null) {
            orgAffiliationRelationEntity.setVisibility(org.orcid.jaxb.model.common_v2.Visibility.PRIVATE);
        }
    }

    private Item createItem(OrgAffiliationRelationEntity orgAffiliationEntity) {
        Item item = new Item();
        item.setItemName(orgAffiliationEntity.getOrg().getName());
        item.setItemType(AffiliationType.EDUCATION.equals(orgAffiliationEntity.getAffiliationType()) ? ItemType.EDUCATION : ItemType.EMPLOYMENT);
        item.setPutCode(String.valueOf(orgAffiliationEntity.getId()));
        return item;
    }

    @Override
    public boolean updateVisibility(String orcid, Long affiliationId, Visibility visibility) {
        return orgAffiliationRelationDao.updateVisibilityOnOrgAffiliationRelation(orcid, affiliationId,
                org.orcid.jaxb.model.common_v2.Visibility.fromValue(visibility.value()));
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
    
    private void checkAffiliationExternalIDsForDuplicates(String orcid, Affiliation incoming, SourceEntity sourceEntity) {
        List<Affiliation> affiliations = getAffiliations(orcid);
        if (affiliations != null) {
            for (Affiliation affiliation : affiliations) {
                if (incoming.getClass().isAssignableFrom(affiliation.getClass())) {
                    activityValidator.checkExternalIdentifiersForDuplicates(incoming.getExternalIDs(), affiliation.getExternalIDs(), affiliation.getSource(),
                            sourceEntity);
                }
            }
        }
    }
}
