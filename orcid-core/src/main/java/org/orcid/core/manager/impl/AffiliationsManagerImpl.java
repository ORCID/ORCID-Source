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
import org.orcid.core.exception.WrongSourceException;
import org.orcid.core.manager.AffiliationsManager;
import org.orcid.core.manager.OrgManager;
import org.orcid.core.manager.SourceManager;
import org.orcid.jaxb.model.message.Visibility;
import org.orcid.jaxb.model.record.AffiliationType;
import org.orcid.jaxb.model.record.Education;
import org.orcid.jaxb.model.record.Employment;
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
    private OrgManager orgManager;
    
    @Resource
    private SourceManager sourceManager;
    
    @Resource
    private ProfileDao profileDao;
    
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
        affiliationsDao.merge(educationEntity);
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
        checkSource(existingSource);
        
        jpaJaxbEducationAdapter.toOrgAffiliationRelationEntity(education, educationEntity);
        educationEntity.setVisibility(originalVisibility);
        educationEntity.setSource(existingSource);
        
        //Updates the give organization with the latest organization from database, or, create a new one
        OrgEntity updatedOrganization = orgManager.getOrgEntity(education);
        educationEntity.setOrg(updatedOrganization);        
        
        educationEntity.setAffiliationType(org.orcid.jaxb.model.message.AffiliationType.fromValue(AffiliationType.EDUCATION.value()));
        affiliationsDao.merge(educationEntity);
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
        return null;
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
        return null;
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
        return null;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    /**
     * Deletes a given education, if and only if, the client that requested the delete is the source of the education
     * @param orcid
     *          the education owner
     * @param affiliationId
     *          The education id                 
     * @return true if the education was deleted, false otherwise
     * */
    @Override
    public boolean checkSourceAndDelete(String orcid, String affiliationId) {
        OrgAffiliationRelationEntity affiliationEntity = affiliationsDao.getOrgAffiliationRelation(orcid, affiliationId);
        checkSource(affiliationEntity.getSource());
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
    
    private void checkSource(SourceEntity existingSource) {
        String sourceIdOfUpdater = sourceManager.retrieveSourceOrcid();
        if (sourceIdOfUpdater != null && (existingSource == null || !sourceIdOfUpdater.equals(existingSource.getSourceId()))) {
            throw new WrongSourceException("You are not the source of the affiliation, so you are not allowed to update it");
        }
    }
}
