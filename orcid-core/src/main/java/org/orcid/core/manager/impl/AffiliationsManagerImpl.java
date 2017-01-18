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
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.notification.amended_v2.AmendedSection;
import org.orcid.jaxb.model.notification.permission_v2.Item;
import org.orcid.jaxb.model.notification.permission_v2.ItemType;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.cache.annotation.Cacheable;

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
    
    @Override
    public OrgAffiliationRelationEntity findAffiliationByUserAndId(String userOrcid, Long affiliationId) {
        if (PojoUtil.isEmpty(userOrcid) || affiliationId == null)
            return null;
        OrgAffiliationRelationEntity affiliation = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        return affiliation;
    }    

    @Override
    public List<OrgAffiliationRelationEntity> findAffiliationsByUserAndType(String userOrcid, AffiliationType type) {
        if (PojoUtil.isEmpty(userOrcid) || type == null)
            return null;
        return orgAffiliationRelationDao.getByUserAndType(userOrcid, type);
    }

    /**
     * Get an education based on the orcid and education id
     * 
     * @param orcid
     *            The education owner
     * @param affiliationId
     *            The affiliation id
     * @return the education
     * */
    @Override
    public Education getEducationAffiliation(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, affiliationId);
        return jpaJaxbEducationAdapter.toEducation(entity);
    }

    /**
     * Get a summary of an education affiliation based on the orcid and
     * education id
     * 
     * @param orcid
     *            The education owner
     * @param affiliationId
     *            The affiliation id
     * @return the education summary
     * */
    @Override
    public EducationSummary getEducationSummary(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, affiliationId);
        return jpaJaxbEducationAdapter.toEducationSummary(entity);
    }

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
        educationEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EDUCATION.value()));
        orgAffiliationRelationDao.persist(educationEntity);
        orgAffiliationRelationDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItem(educationEntity));
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

        educationEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EDUCATION.value()));
        educationEntity = orgAffiliationRelationDao.merge(educationEntity);
        orgAffiliationRelationDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.EDUCATION, createItem(educationEntity));
        return jpaJaxbEducationAdapter.toEducation(educationEntity);
    }

    /**
     * Get an employment based on the orcid and education id
     * 
     * @param orcid
     *            The employment owner
     * @param employmentId
     *            The employment id
     * @return the employment
     * */
    @Override
    public Employment getEmploymentAffiliation(String userOrcid, Long employmentId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, employmentId);
        return jpaJaxbEmploymentAdapter.toEmployment(entity);
    }

    /**
     * Get a summary of an employment affiliation based on the orcid and
     * education id
     * 
     * @param orcid
     *            The employment owner
     * @param employmentId
     *            The employment id
     * @return the employment summary
     * */
    public EmploymentSummary getEmploymentSummary(String userOrcid, Long employmentId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, employmentId);
        return jpaJaxbEmploymentAdapter.toEmploymentSummary(entity);
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
        employmentEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EMPLOYMENT.value()));
        orgAffiliationRelationDao.persist(employmentEntity);
        orgAffiliationRelationDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItem(employmentEntity));
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

        employmentEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EMPLOYMENT.value()));
        employmentEntity = orgAffiliationRelationDao.merge(employmentEntity);
        orgAffiliationRelationDao.flush();
        notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItem(employmentEntity));
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
            notificationManager.sendAmendEmail(orcid, AmendedSection.EMPLOYMENT, createItem(affiliationEntity));
        return result; 
    }

    private void setIncomingWorkPrivacy(OrgAffiliationRelationEntity orgAffiliationRelationEntity, ProfileEntity profile) {
        Visibility incomingWorkVisibility = orgAffiliationRelationEntity.getVisibility();
        Visibility defaultWorkVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {            
            orgAffiliationRelationEntity.setVisibility(defaultWorkVisibility);            
        } else if (incomingWorkVisibility == null) {
            orgAffiliationRelationEntity.setVisibility(Visibility.PRIVATE);
        }
    }

    /**
     * Get the list of employments that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *            Last modified date used to check the cache
     * @return the list of employments that belongs to this user
     * */
    @Override
    @Cacheable(value = "employments-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<EmploymentSummary> getEmploymentSummaryList(String userOrcid, long lastModified) {
        List<OrgAffiliationRelationEntity> employmentEntities = findAffiliationsByUserAndType(userOrcid, AffiliationType.EMPLOYMENT);
        return jpaJaxbEmploymentAdapter.toEmploymentSummary(employmentEntities);
    }

    /**
     * Get the list of educations that belongs to a user
     * 
     * @param userOrcid
     * @param lastModified
     *            Last modified date used to check the cache
     * @return the list of educations that belongs to this user
     * */
    @Override
    @Cacheable(value = "educations-summaries", key = "#userOrcid.concat('-').concat(#lastModified)")
    public List<EducationSummary> getEducationSummaryList(String userOrcid, long lastModified) {
        List<OrgAffiliationRelationEntity> educationEntities = findAffiliationsByUserAndType(userOrcid, AffiliationType.EDUCATION);
        return jpaJaxbEducationAdapter.toEducationSummary(educationEntities);
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
        return orgAffiliationRelationDao.updateVisibilityOnOrgAffiliationRelation(orcid, affiliationId, visibility);
    }        
}
