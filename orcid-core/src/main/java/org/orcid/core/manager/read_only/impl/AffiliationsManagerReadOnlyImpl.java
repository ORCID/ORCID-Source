package org.orcid.core.manager.read_only.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.adapter.JpaJaxbEducationAdapter;
import org.orcid.core.adapter.JpaJaxbEmploymentAdapter;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.read_only.AffiliationsManagerReadOnly;
import org.orcid.jaxb.model.record.summary_v2.EducationSummary;
import org.orcid.jaxb.model.record.summary_v2.EmploymentSummary;
import org.orcid.jaxb.model.record_v2.Affiliation;
import org.orcid.jaxb.model.record_v2.Education;
import org.orcid.jaxb.model.record_v2.Employment;
import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class AffiliationsManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements AffiliationsManagerReadOnly {
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
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
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
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
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
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, employmentId);
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
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, employmentId);
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
    public List<EmploymentSummary> getEmploymentSummaryList(String userOrcid) {
        List<OrgAffiliationRelationEntity> employmentEntities = orgAffiliationRelationDao.getEmploymentSummaries(userOrcid, getLastModified(userOrcid));
        List<EmploymentSummary> elements = jpaJaxbEmploymentAdapter.toEmploymentSummary(employmentEntities);
        // UI sort it descending first, so,lets do the same for the API
        elements.sort((EmploymentSummary e1, EmploymentSummary e2) -> {
            String sortString1 = PojoUtil.createDateSortString(e1);
            String sortString2 = PojoUtil.createDateSortString(e2);
            return sortString2.compareTo(sortString1);
        });
        return elements;
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
    public List<EducationSummary> getEducationSummaryList(String userOrcid) {
        List<OrgAffiliationRelationEntity> educationEntities = orgAffiliationRelationDao.getByUserAndType(userOrcid, AffiliationType.EDUCATION.name());
        List<EducationSummary> elements = jpaJaxbEducationAdapter.toEducationSummary(educationEntities);
        // UI sort it descending first, so,lets do the same for the API
        elements.sort((EducationSummary e1, EducationSummary e2) -> {
            String sortString1 = PojoUtil.createDateSortString(e1);
            String sortString2 = PojoUtil.createDateSortString(e2);
            return sortString2.compareTo(sortString1);
        });
        return elements;
    }    
    
    @Override
    public List<Affiliation> getAffiliations(String orcid) {
        List<OrgAffiliationRelationEntity> affiliations = orgAffiliationRelationDao.getByUser(orcid);        
        List<Affiliation> result = new ArrayList<Affiliation>();
        
        if(affiliations != null) {
            for (OrgAffiliationRelationEntity affiliation : affiliations) {
                if(AffiliationType.EDUCATION.equals(affiliation.getAffiliationType())) {
                    result.add(jpaJaxbEducationAdapter.toEducation(affiliation));
                } else {
                    result.add(jpaJaxbEmploymentAdapter.toEmployment(affiliation));
                }
            }
        }
        
        return result;
    }
}
