package org.orcid.core.manager.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.NotificationManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.ProfileEntityCacheManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.core.manager.read_only.impl.AffiliationsManagerReadOnlyImpl;
import org.orcid.core.manager.validator.ActivityValidator;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.ItemType;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.v3.dev1.record.AffiliationType;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;

public class AffiliationsManagerImpl extends AffiliationsManagerReadOnlyImpl implements AffiliationsManager {
    @Resource
    private OrgManager orgManager;

    @Resource
    private SourceManager sourceManager;

    @Resource
    private ProfileEntityCacheManager profileEntityCacheManager;

    @Resource
    private OrcidSecurityManager orcidSecurityManager;

    @Resource
    private NotificationManager notificationManager;
    
    @Resource 
    private ActivityValidator activityValidator;

    /**
     * Add a new education to the given user
     * 
     * @param orcid
     *            The user to add the education
     * @param education
     *            The education to add
     * @return the added education
     * */
    @Override
    public Education createEducationAffiliation(String orcid, Education education, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        activityValidator.validateEducation(education, sourceEntity, true, isApiRequest, null);
        OrgAffiliationRelationEntity educationEntity = jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(education);

        // Updates the give organization with the latest organization from
        // database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(education);
        educationEntity.setOrg(updatedOrganization);

        // Set source id 
        if(sourceEntity.getSourceProfile() != null) {
            educationEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        
        if(sourceEntity.getSourceClient() != null) {
            educationEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        }        
                        
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        educationEntity.setProfile(profile);
        setIncomingWorkPrivacy(educationEntity, profile);
        educationEntity.setAffiliationType(AffiliationType.EDUCATION);
        orgAffiliationRelationDao.persist(educationEntity);
        orgAffiliationRelationDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItemList(educationEntity));
        return jpaJaxbEducationAdapter.toEducation(educationEntity);
    }

    /**
     * Updates a education that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param education
     *            The education to update
     * @return the updated education
     * */
    @Override
    public Education updateEducationAffiliation(String orcid, Education education, boolean isApiRequest) {
        OrgAffiliationRelationEntity educationEntity = orgAffiliationRelationDao.getOrgAffiliationRelation(orcid, education.getPutCode());                
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        //Save the original source
        String existingSourceId = educationEntity.getSourceId();
        String existingClientSourceId = educationEntity.getClientSourceId();
        
        Visibility originalVisibility = educationEntity.getVisibility();
        orcidSecurityManager.checkSource(educationEntity);

        activityValidator.validateEducation(education, sourceEntity, false, isApiRequest, originalVisibility);
        
        jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(education, educationEntity);
        educationEntity.setVisibility(originalVisibility);
        
        //Be sure it doesn't overwrite the source
        educationEntity.setSourceId(existingSourceId);
        educationEntity.setClientSourceId(existingClientSourceId);

        // Updates the give organization with the latest organization from
        // database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(education);
        educationEntity.setOrg(updatedOrganization);

        educationEntity.setAffiliationType(AffiliationType.EDUCATION);
        educationEntity = orgAffiliationRelationDao.merge(educationEntity);
        orgAffiliationRelationDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItemList(educationEntity));
        return jpaJaxbEducationAdapter.toEducation(educationEntity);
    }

    /**
     * Add a new employment to the given user
     * 
     * @param orcid
     *            The user to add the employment
     * @param employment
     *            The employment to add
     * @return the added employment
     * */
    @Override
    public Employment createEmploymentAffiliation(String orcid, Employment employment, boolean isApiRequest) {
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        activityValidator.validateEmployment(employment, sourceEntity, true, isApiRequest, null);
        OrgAffiliationRelationEntity employmentEntity = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(employment);

        // Updates the give organization with the latest organization from
        // database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(employment);
        employmentEntity.setOrg(updatedOrganization);

        // Set source id 
        if(sourceEntity.getSourceProfile() != null) {
            employmentEntity.setSourceId(sourceEntity.getSourceProfile().getId());
        }
        
        if(sourceEntity.getSourceClient() != null) {
            employmentEntity.setClientSourceId(sourceEntity.getSourceClient().getId());
        }
        
        ProfileEntity profile = profileEntityCacheManager.retrieve(orcid);
        employmentEntity.setProfile(profile);
        setIncomingWorkPrivacy(employmentEntity, profile);
        employmentEntity.setAffiliationType(AffiliationType.EMPLOYMENT);
        orgAffiliationRelationDao.persist(employmentEntity);
        orgAffiliationRelationDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItemList(employmentEntity));
        return jpaJaxbEmploymentAdapter.toEmployment(employmentEntity);
    }

    /**
     * Updates a employment that belongs to the given user
     * 
     * @param orcid
     *            The user
     * @param employment
     *            The employment to update
     * @return the updated employment
     * */
    @Override
    public Employment updateEmploymentAffiliation(String orcid, Employment employment, boolean isApiRequest) {
        OrgAffiliationRelationEntity employmentEntity = orgAffiliationRelationDao.getOrgAffiliationRelation(orcid, employment.getPutCode());        
        Visibility originalVisibility = employmentEntity.getVisibility();  
        SourceEntity sourceEntity = sourceManager.retrieveSourceEntity();
        
        //Save the original source
        String existingSourceId = employmentEntity.getSourceId();
        String existingClientSourceId = employmentEntity.getClientSourceId();
        
        orcidSecurityManager.checkSource(employmentEntity);

        activityValidator.validateEmployment(employment, sourceEntity, false, isApiRequest, originalVisibility);
        
        jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(employment, employmentEntity);
        employmentEntity.setVisibility(originalVisibility);
                
        //Be sure it doesn't overwrite the source
        employmentEntity.setSourceId(existingSourceId);
        employmentEntity.setClientSourceId(existingClientSourceId);
                
        // Updates the give organization with the latest organization from
        // database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(employment);
        employmentEntity.setOrg(updatedOrganization);

        employmentEntity.setAffiliationType(AffiliationType.EMPLOYMENT);
        employmentEntity = orgAffiliationRelationDao.merge(employmentEntity);
        orgAffiliationRelationDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItemList(employmentEntity));
        return jpaJaxbEmploymentAdapter.toEmployment(employmentEntity);
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
     * */
    @Override
    public boolean checkSourceAndDelete(String orcid, Long affiliationId) {
        OrgAffiliationRelationEntity affiliationEntity = orgAffiliationRelationDao.getOrgAffiliationRelation(orcid, affiliationId);                
        orcidSecurityManager.checkSource(affiliationEntity);
        boolean result = orgAffiliationRelationDao.removeOrgAffiliationRelation(orcid, affiliationId);
        if(result)
            notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItemList(affiliationEntity));
        return result; 
    }

    private void setIncomingWorkPrivacy(OrgAffiliationRelationEntity orgAffiliationRelationEntity, ProfileEntity profile) {
        Visibility incomingElementVisibility = orgAffiliationRelationEntity.getVisibility();
        Visibility defaultElementVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) { 
            orgAffiliationRelationEntity.setVisibility(defaultElementVisibility);            
        } else if (incomingElementVisibility == null) {
            orgAffiliationRelationEntity.setVisibility(Visibility.PRIVATE);
        }
    }    

    private List<Item> createItemList(OrgAffiliationRelationEntity orgAffiliationEntity) {
        Item item = new Item();
        item.setItemName(orgAffiliationEntity.getOrg().getName());
        item.setItemType(AffiliationType.EDUCATION.value().equals(orgAffiliationEntity.getAffiliationType().value()) ? ItemType.EDUCATION : ItemType.EMPLOYMENT);
        item.setPutCode(String.valueOf(orgAffiliationEntity.getId()));
        return Arrays.asList(item);
    }        

    @Override
    public boolean updateVisibility(String orcid, Long affiliationId, Visibility visibility) {
        return orgAffiliationRelationDao.updateVisibilityOnOrgAffiliationRelation(orcid, affiliationId, visibility);
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
}
