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
package org.orcid.core.manager.read_only.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.core.adapter.JpaJaxbEmploymentAdapter;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.pojo.ajaxForm.AffiliationForm;
import org.orcid.pojo.ajaxForm.PojoUtil;
import org.springframework.cache.annotation.Cacheable;

public class AffiliationsManagerReadOnlyImpl implements AffiliationsManagerReadOnly {
    @Resource
    protected JpaJaxbEducationAdapter jpaJaxbEducationAdapter;

    @Resource
    protected JpaJaxbEmploymentAdapter jpaJaxbEmploymentAdapter;
    
    protected OrgAffiliationRelationDao orgAffiliationRelationDao;    
        
    protected SourceNameCacheManager sourceNameCacheManager;
    
    public void setOrgAffiliationRelationDao(OrgAffiliationRelationDao orgAffiliationRelationDao) {
        this.orgAffiliationRelationDao = orgAffiliationRelationDao;
    }

    public void setSourceNameCacheManager(SourceNameCacheManager sourceNameCacheManager) {
		this.sourceNameCacheManager = sourceNameCacheManager;
	}

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
    
    @Deprecated
    @Override
    public List<AffiliationForm> getAffiliations(String orcid) {
        List<OrgAffiliationRelationEntity> affiliations = orgAffiliationRelationDao.getByUser(orcid);        
        List<AffiliationForm> result = new ArrayList<AffiliationForm>();
        
        if(affiliations != null) {
            for (OrgAffiliationRelationEntity affiliation : affiliations) {
                AffiliationForm affiliationForm = AffiliationForm.valueOf(affiliation);
                //Get the name from the name cache
                if(!PojoUtil.isEmpty(affiliationForm.getSource())) {
                    String sourceName = sourceNameCacheManager.retrieve(affiliationForm.getSource());
                    affiliationForm.setSourceName(sourceName);
                }
                result.add(affiliationForm);
            }
        }
        
        return result;
    }
}
