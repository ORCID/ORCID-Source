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

import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.core.adapter.JpaJaxbEmploymentAdapter;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.OrcidSecurityManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
import org.orcid.jaxb.model.record.summary.EducationSummary;
import org.orcid.jaxb.model.record.summary.EmploymentSummary;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.dao.ProfileDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.persistence.jpa.entities.OrgEntity;
import org.orcid.persistence.jpa.entities.ProfileEntity;
import org.orcid.persistence.jpa.entities.SourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class AffiliationsManagerImpl implements AffiliationsManager {

    @Resource
    OrgAffiliationRelationDao affiliationsDao;
    
    @Resource 
    JpaJaxbEducationAdapter jpaJaxbEducationAdapter;
    
    @Resource 
    JpaJaxbEmploymentAdapter jpaJaxbEmploymentAdapter;
    
    @Resource
    private OrgManager orgManager;
    
    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private ProfileDao profileDao;
    
    @Resource
    private OrcidSecurityManager orcidSecurityManager;
    
    @Override
    public OrgAffiliationRelationEntity findAffiliationByUserAndId(String userOrcid, String affiliationId) {
        if(PojoUtil.isEmpty(userOrcid) || PojoUtil.isEmpty(affiliationId))
            return null;
        return affiliationsDao.getOrgAffiliationRelation(userOrcid, affiliationId);
    }

    @Override
    public List<OrgAffiliationRelationEntity> findAffiliationsByType(AffiliationType type) {
        if(type == null)
            return null;
        return affiliationsDao.getByType(type);
    }

    @Override
    public List<OrgAffiliationRelationEntity> findAffiliationsByUserAndType(String userOrcid, AffiliationType type) {
        if(PojoUtil.isEmpty(userOrcid) || type == null)
            return null;
        return affiliationsDao.getByUserAndType(userOrcid, type);
    }
    
    /**
     * Get an education based on the orcid and education id
     * @param orcid
     *          The education owner
     * @param affiliationId
     *          The affiliation id
     * @return the education
     * */
    @Override
    public Education getEducationAffiliation(String userOrcid, String affiliationId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, affiliationId);
        return jpaJaxbEducationAdapter.toEducation(entity);
    }
    
    /**
     * Get a summary of an education affiliation based on the orcid and education id
     * @param orcid
     *          The education owner
     * @param affiliationId
     *          The affiliation id
     * @return the education summary
     * */
    @Override
    public EducationSummary getEducationSummary(String userOrcid, String affiliationId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, affiliationId);
        return jpaJaxbEducationAdapter.toEducationSummary(entity);
    }
    
    /**
     * Add a new education to the given user
     * @param orcid
     *          The user to add the education
     * @param education
     *          The education to add
     * @return the added education
     * */
    @Override
    public Education createEducationAffiliation(String orcid, Education education) {
        OrgAffiliationRelationEntity educationEntity = jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(education);
        
        //Updates the give organization with the latest organization from database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(education);
        educationEntity.setOrg(updatedOrganization);
        
        educationEntity.setSource(sourceManager.retrieveSourceEntity());
        ProfileEntity profile = profileDao.find(orcid);
        educationEntity.setProfile(profile);
        setIncomingWorkPrivacy(educationEntity, profile);
        educationEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EDUCATION.value()));
        affiliationsDao.persist(educationEntity);
        return jpaJaxbEducationAdapter.toEducation(educationEntity);
    }
    
    /**
     * Updates a education that belongs to the given user
     * @param orcid
     *          The user
     * @param education
     *          The education to update
     * @return the updated education
     * */
    @Override
    public Education updateEducationAffiliation(String orcid, Education education) {
        OrgAffiliationRelationEntity educationEntity = affiliationsDao.getOrgAffiliationRelation(orcid, education.getPutCode());
        Visibility originalVisibility = educationEntity.getVisibility();
        SourceEntity existingSource = educationEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        
        jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(education, educationEntity);
        educationEntity.setVisibility(originalVisibility);
        educationEntity.setSource(existingSource);
        
        //Updates the give organization with the latest organization from database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(education);
        educationEntity.setOrg(updatedOrganization);        
        
        educationEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EDUCATION.value()));
        educationEntity = affiliationsDao.merge(educationEntity);
        return jpaJaxbEducationAdapter.toEducation(educationEntity);
    }
    
    /**
     * Get an employment based on the orcid and education id
     * @param orcid
     *          The employment owner
     * @param employmentId
     *          The employment id
     * @return the employment
     * */
    @Override
    public Employment getEmploymentAffiliation(String userOrcid, String employmentId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, employmentId);
        return jpaJaxbEmploymentAdapter.toEmployment(entity);
    }
    
    /**
     * Get a summary of an employment affiliation based on the orcid and education id
     * @param orcid
     *          The employment owner
     * @param employmentId
     *          The employment id
     * @return the employment summary
     * */
    public EmploymentSummary getEmploymentSummary(String userOrcid, String employmentId) {
        OrgAffiliationRelationEntity entity = findAffiliationByUserAndId(userOrcid, employmentId);
        return jpaJaxbEmploymentAdapter.toEmploymentSummary(entity);
    }        
    
    /**
     * Add a new employment to the given user
     * @param orcid
     *          The user to add the employment
     * @param employment
     *          The employment to add
     * @return the added employment
     * */
    @Override
    public Employment createEmploymentAffiliation(String orcid, Employment employment) {
        OrgAffiliationRelationEntity employmentEntity = jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(employment);
        
        //Updates the give organization with the latest organization from database
        OrgEntity updatedOrganization = orgManager.getOrgEntity(employment);
        employmentEntity.setOrg(updatedOrganization);
        
        employmentEntity.setSource(sourceManager.retrieveSourceEntity());
        ProfileEntity profile = profileDao.find(orcid);
        employmentEntity.setProfile(profile);
        setIncomingWorkPrivacy(employmentEntity, profile);
        employmentEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EMPLOYMENT.value()));
        affiliationsDao.persist(employmentEntity);
        return jpaJaxbEmploymentAdapter.toEmployment(employmentEntity);
    }
    
    /**
     * Updates a employment that belongs to the given user
     * @param orcid
     *          The user
     * @param employment
     *          The employment to update
     * @return the updated employment
     * */
    @Override
    public Employment updateEmploymentAffiliation(String orcid, Employment employment) {
        OrgAffiliationRelationEntity employmentEntity = affiliationsDao.getOrgAffiliationRelation(orcid, employment.getPutCode());
        Visibility originalVisibility = employmentEntity.getVisibility();
        SourceEntity existingSource = employmentEntity.getSource();
        orcidSecurityManager.checkSource(existingSource);
        
        jpaJaxbEmploymentAdapter.toOrgAffiliationRelationEntity(employment, employmentEntity);
        employmentEntity.setVisibility(originalVisibility);
        employmentEntity.setSource(existingSource);
        
        //Updates the give organization with the latest organization from database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(employment);
        employmentEntity.setOrg(updatedOrganization);        
        
        employmentEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EMPLOYMENT.value()));
        employmentEntity = affiliationsDao.merge(employmentEntity);
        return jpaJaxbEmploymentAdapter.toEmployment(employmentEntity);
    }
    
    /**
     * Deletes a given affiliation, if and only if, the client that requested the delete is the source of the affiliation
     * @param orcid
     *          the affiliation owner
     * @param affiliationId
     *          The affiliation id                 
     * @return true if the affiliation was deleted, false otherwise
     * */
    @Override
    public boolean checkSourceAndDelete(String orcid, String affiliationId) {
        OrgAffiliationRelationEntity affiliationEntity = affiliationsDao.getOrgAffiliationRelation(orcid, affiliationId);
        orcidSecurityManager.checkSource(affiliationEntity.getSource());
        return affiliationsDao.removeOrgAffiliationRelation(orcid, affiliationId);
    } 
    
    private void setIncomingWorkPrivacy(OrgAffiliationRelationEntity orgAffiliationRelationEntity, ProfileEntity profile) {
        Visibility incomingWorkVisibility = orgAffiliationRelationEntity.getVisibility();
        Visibility defaultWorkVisibility = profile.getActivitiesVisibilityDefault();
        if (profile.getClaimed()) {
            if (defaultWorkVisibility.isMoreRestrictiveThan(incomingWorkVisibility)) {
                orgAffiliationRelationEntity.setVisibility(defaultWorkVisibility);
            }
        } else if (incomingWorkVisibility == null) {
            orgAffiliationRelationEntity.setVisibility(Visibility.PRIVATE);
        }
    }
    
}
