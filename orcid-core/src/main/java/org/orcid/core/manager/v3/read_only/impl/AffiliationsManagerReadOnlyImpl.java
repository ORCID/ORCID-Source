package org.orcid.core.manager.v3.read_only.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.orcid.core.adapter.v3.JpaJaxbDistinctionAdapter;
import org.orcid.core.adapter.v3.JpaJaxbEducationAdapter;
import org.orcid.core.adapter.v3.JpaJaxbEmploymentAdapter;
import org.orcid.core.adapter.v3.JpaJaxbInvitedPositionAdapter;
import org.orcid.core.adapter.v3.JpaJaxbMembershipAdapter;
import org.orcid.core.adapter.v3.JpaJaxbQualificationAdapter;
import org.orcid.core.adapter.v3.JpaJaxbServiceAdapter;
import org.orcid.core.manager.SourceNameCacheManager;
import org.orcid.core.manager.v3.OrgAffiliationRelationEntityCacheManager;
import org.orcid.core.manager.v3.read_only.AffiliationsManagerReadOnly;
import org.orcid.core.utils.v3.activities.ActivitiesGroup;
import org.orcid.core.utils.v3.activities.ActivitiesGroupGenerator;
import org.orcid.core.utils.v3.activities.GroupableActivityComparator;
import org.orcid.jaxb.model.v3.rc1.record.Affiliation;
import org.orcid.jaxb.model.v3.rc1.record.AffiliationType;
import org.orcid.jaxb.model.v3.rc1.record.Distinction;
import org.orcid.jaxb.model.v3.rc1.record.Education;
import org.orcid.jaxb.model.v3.rc1.record.Employment;
import org.orcid.jaxb.model.v3.rc1.record.ExternalID;
import org.orcid.jaxb.model.v3.rc1.record.GroupAble;
import org.orcid.jaxb.model.v3.rc1.record.GroupableActivity;
import org.orcid.jaxb.model.v3.rc1.record.InvitedPosition;
import org.orcid.jaxb.model.v3.rc1.record.Membership;
import org.orcid.jaxb.model.v3.rc1.record.Qualification;
import org.orcid.jaxb.model.v3.rc1.record.Service;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationGroup;
import org.orcid.jaxb.model.v3.rc1.record.summary.AffiliationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.DistinctionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EducationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.EmploymentSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.InvitedPositionSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.MembershipSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.QualificationSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ServiceSummary;
import org.orcid.persistence.dao.OrgAffiliationRelationDao;
import org.orcid.persistence.jpa.entities.OrgAffiliationRelationEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class AffiliationsManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements AffiliationsManagerReadOnly {
    
    @Resource(name = "jpaJaxbDistinctionAdapterV3")
    protected JpaJaxbDistinctionAdapter jpaJaxbDistinctionAdapter;
    
    @Resource(name = "jpaJaxbEducationAdapterV3")
    protected JpaJaxbEducationAdapter jpaJaxbEducationAdapter;

    @Resource(name = "jpaJaxbEmploymentAdapterV3")
    protected JpaJaxbEmploymentAdapter jpaJaxbEmploymentAdapter;
    
    @Resource(name = "jpaJaxbInvitedPositionAdapterV3")
    protected JpaJaxbInvitedPositionAdapter jpaJaxbInvitedPositionAdapter;
    
    @Resource(name = "jpaJaxbMembershipAdapterV3")
    protected JpaJaxbMembershipAdapter jpaJaxbMembershipAdapter;
    
    @Resource(name = "jpaJaxbQualificationAdapterV3")
    protected JpaJaxbQualificationAdapter jpaJaxbQualificationAdapter;
    
    @Resource(name = "jpaJaxbServiceAdapterV3")
    protected JpaJaxbServiceAdapter jpaJaxbServiceAdapter;
    
    @Resource(name = "orgAffiliationRelationEntityCacheManager")
    protected OrgAffiliationRelationEntityCacheManager orgAffiliationRelationEntityCacheManager;
    
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
        checkType(entity, AffiliationType.EDUCATION);
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
        checkType(entity, AffiliationType.EDUCATION);
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
        checkType(entity, AffiliationType.EMPLOYMENT);
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
        checkType(entity, AffiliationType.EMPLOYMENT);
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
        List<OrgAffiliationRelationEntity> educationEntities = orgAffiliationRelationDao.getEducationSummaries(userOrcid, getLastModified(userOrcid));
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
    public Distinction getDistinctionAffiliation(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.DISTINCTION);
        return jpaJaxbDistinctionAdapter.toDistinction(entity);
    }

    @Override
    public DistinctionSummary getDistinctionSummary(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.DISTINCTION);
        return jpaJaxbDistinctionAdapter.toDistinctionSummary(entity);
    }

    @Override
    public List<DistinctionSummary> getDistinctionSummaryList(String userOrcid) {
        List<OrgAffiliationRelationEntity> entities = orgAffiliationRelationDao.getDistinctionSummaries(userOrcid, getLastModified(userOrcid));
        List<DistinctionSummary> elements = jpaJaxbDistinctionAdapter.toDistinctionSummary(entities);
        // UI sort it descending first, so,lets do the same for the API
        elements.sort((DistinctionSummary e1, DistinctionSummary e2) -> {
                String sortString1 = PojoUtil.createDateSortString(e1);
                String sortString2 = PojoUtil.createDateSortString(e2);
                return sortString2.compareTo(sortString1);
        });
        return elements;
    }

    @Override
    public InvitedPosition getInvitedPositionAffiliation(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.INVITED_POSITION);
        return jpaJaxbInvitedPositionAdapter.toInvitedPosition(entity);
    }

    @Override
    public InvitedPositionSummary getInvitedPositionSummary(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.INVITED_POSITION);
        return jpaJaxbInvitedPositionAdapter.toInvitedPositionSummary(entity);
    }

    @Override
    public List<InvitedPositionSummary> getInvitedPositionSummaryList(String userOrcid) {
        List<OrgAffiliationRelationEntity> entities = orgAffiliationRelationDao.getInvitedPositionSummaries(userOrcid, getLastModified(userOrcid));
        List<InvitedPositionSummary> elements = jpaJaxbInvitedPositionAdapter.toInvitedPositionSummary(entities);
        // UI sort it descending first, so,lets do the same for the API
        elements.sort((InvitedPositionSummary e1, InvitedPositionSummary e2) -> {
                String sortString1 = PojoUtil.createDateSortString(e1);
                String sortString2 = PojoUtil.createDateSortString(e2);
                return sortString2.compareTo(sortString1);
        });
        return elements;
    }

    @Override
    public Membership getMembershipAffiliation(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.MEMBERSHIP);
        return jpaJaxbMembershipAdapter.toMembership(entity);
    }

    @Override
    public MembershipSummary getMembershipSummary(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.MEMBERSHIP);
        return jpaJaxbMembershipAdapter.toMembershipSummary(entity);
    }

    @Override
    public List<MembershipSummary> getMembershipSummaryList(String userOrcid) {
        List<OrgAffiliationRelationEntity> entities = orgAffiliationRelationDao.getMembershipSummaries(userOrcid, getLastModified(userOrcid));
        List<MembershipSummary> elements = jpaJaxbMembershipAdapter.toMembershipSummary(entities);
        // UI sort it descending first, so,lets do the same for the API
        elements.sort((MembershipSummary e1, MembershipSummary e2) -> {
                String sortString1 = PojoUtil.createDateSortString(e1);
                String sortString2 = PojoUtil.createDateSortString(e2);
                return sortString2.compareTo(sortString1);
        });
        return elements;
    }

    @Override
    public Qualification getQualificationAffiliation(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.QUALIFICATION);
        return jpaJaxbQualificationAdapter.toQualification(entity);
    }

    @Override
    public QualificationSummary getQualificationSummary(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.QUALIFICATION);
        return jpaJaxbQualificationAdapter.toQualificationSummary(entity);
    }

    @Override
    public List<QualificationSummary> getQualificationSummaryList(String userOrcid) {
        List<OrgAffiliationRelationEntity> entities = orgAffiliationRelationDao.getQualificationSummaries(userOrcid, getLastModified(userOrcid));
        List<QualificationSummary> elements = jpaJaxbQualificationAdapter.toQualificationSummary(entities);
        // UI sort it descending first, so,lets do the same for the API
        elements.sort((QualificationSummary e1, QualificationSummary e2) -> {
                String sortString1 = PojoUtil.createDateSortString(e1);
                String sortString2 = PojoUtil.createDateSortString(e2);
                return sortString2.compareTo(sortString1);
        });
        return elements;
    }

    @Override
    public Service getServiceAffiliation(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.SERVICE);
        return jpaJaxbServiceAdapter.toService(entity);
    }

    @Override
    public ServiceSummary getServiceSummary(String userOrcid, Long affiliationId) {
        OrgAffiliationRelationEntity entity = orgAffiliationRelationDao.getOrgAffiliationRelation(userOrcid, affiliationId);
        checkType(entity, AffiliationType.SERVICE);
        return jpaJaxbServiceAdapter.toServiceSummary(entity);
    }

    @Override
    public List<ServiceSummary> getServiceSummaryList(String userOrcid) {
        List<OrgAffiliationRelationEntity> entities = orgAffiliationRelationDao.getServiceSummaries(userOrcid, getLastModified(userOrcid));
        List<ServiceSummary> elements = jpaJaxbServiceAdapter.toServiceSummary(entities);
        // UI sort it descending first, so,lets do the same for the API
        elements.sort((ServiceSummary e1, ServiceSummary e2) -> {
                String sortString1 = PojoUtil.createDateSortString(e1);
                String sortString2 = PojoUtil.createDateSortString(e2);
                return sortString2.compareTo(sortString1);
        });
        return elements;
    }
    
    @Override
    public List<Affiliation> getAffiliations(String orcid) {
        List<OrgAffiliationRelationEntity> affiliations = orgAffiliationRelationEntityCacheManager.getAffiliationEntities(orcid);        
        List<Affiliation> result = new ArrayList<Affiliation>();
        
        if(affiliations != null) {
            for (OrgAffiliationRelationEntity affiliation : affiliations) {
                if (AffiliationType.DISTINCTION.name().equals(affiliation.getAffiliationType())) {
                    result.add(jpaJaxbDistinctionAdapter.toDistinction(affiliation));
                } else if (AffiliationType.EDUCATION.name().equals(affiliation.getAffiliationType())) {
                    result.add(jpaJaxbEducationAdapter.toEducation(affiliation));
                } else if (AffiliationType.EMPLOYMENT.name().equals(affiliation.getAffiliationType())) {
                    result.add(jpaJaxbEmploymentAdapter.toEmployment(affiliation));
                } else if (AffiliationType.INVITED_POSITION.name().equals(affiliation.getAffiliationType())) {
                    result.add(jpaJaxbInvitedPositionAdapter.toInvitedPosition(affiliation));                    
                } else if (AffiliationType.MEMBERSHIP.name().equals(affiliation.getAffiliationType())) {
                    result.add(jpaJaxbMembershipAdapter.toMembership(affiliation));
                } else if (AffiliationType.QUALIFICATION.name().equals(affiliation.getAffiliationType())) {
                    result.add(jpaJaxbQualificationAdapter.toQualification(affiliation));
                } else if (AffiliationType.SERVICE.name().equals(affiliation.getAffiliationType())) {
                    result.add(jpaJaxbServiceAdapter.toService(affiliation));
                }
            }
        }
        
        return result;
    }
    
    @Override
    public <T extends AffiliationSummary> Map<AffiliationType, List<AffiliationGroup<T>>> getGroupedAffiliations(String orcid, boolean justPublic) {
        List<OrgAffiliationRelationEntity> affiliations = orgAffiliationRelationEntityCacheManager.getAffiliationEntities(orcid);
        ActivitiesGroupGenerator distinctionsGroupGenerator = new ActivitiesGroupGenerator();
        ActivitiesGroupGenerator educationsGroupGenerator = new ActivitiesGroupGenerator();
        ActivitiesGroupGenerator employmentsGroupGenerator = new ActivitiesGroupGenerator();
        ActivitiesGroupGenerator invitedPositionsGroupGenerator = new ActivitiesGroupGenerator();
        ActivitiesGroupGenerator membershipsGroupGenerator = new ActivitiesGroupGenerator();
        ActivitiesGroupGenerator qualificationsGroupGenerator = new ActivitiesGroupGenerator();
        ActivitiesGroupGenerator servicesGroupGenerator = new ActivitiesGroupGenerator();
        
        if(affiliations != null) {
            for (OrgAffiliationRelationEntity affiliation : affiliations) {
                if(!justPublic || org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC.name().equals(affiliation.getVisibility())) {
                    if (AffiliationType.DISTINCTION.name().equals(affiliation.getAffiliationType())) {
                        distinctionsGroupGenerator.group(jpaJaxbDistinctionAdapter.toDistinctionSummary(affiliation));
                    } else if (AffiliationType.EDUCATION.name().equals(affiliation.getAffiliationType())) {
                        educationsGroupGenerator.group(jpaJaxbEducationAdapter.toEducationSummary(affiliation));
                    } else if (AffiliationType.EMPLOYMENT.name().equals(affiliation.getAffiliationType())) {
                        employmentsGroupGenerator.group(jpaJaxbEmploymentAdapter.toEmploymentSummary(affiliation));
                    } else if (AffiliationType.INVITED_POSITION.name().equals(affiliation.getAffiliationType())) {
                        invitedPositionsGroupGenerator.group(jpaJaxbInvitedPositionAdapter.toInvitedPositionSummary(affiliation));                    
                    } else if (AffiliationType.MEMBERSHIP.name().equals(affiliation.getAffiliationType())) {
                        membershipsGroupGenerator.group(jpaJaxbMembershipAdapter.toMembershipSummary(affiliation));
                    } else if (AffiliationType.QUALIFICATION.name().equals(affiliation.getAffiliationType())) {
                        qualificationsGroupGenerator.group(jpaJaxbQualificationAdapter.toQualificationSummary(affiliation));
                    } else if (AffiliationType.SERVICE.name().equals(affiliation.getAffiliationType())) {
                        servicesGroupGenerator.group(jpaJaxbServiceAdapter.toServiceSummary(affiliation));
                    }
                }
            }
        }
        
        Map<AffiliationType, List<AffiliationGroup<T>>> map = new HashMap<AffiliationType, List<AffiliationGroup<T>>>();
        map.put(AffiliationType.DISTINCTION, group(distinctionsGroupGenerator));
        map.put(AffiliationType.EDUCATION, group(educationsGroupGenerator));
        map.put(AffiliationType.EMPLOYMENT, group(employmentsGroupGenerator));
        map.put(AffiliationType.INVITED_POSITION, group(invitedPositionsGroupGenerator));
        map.put(AffiliationType.MEMBERSHIP, group(membershipsGroupGenerator));
        map.put(AffiliationType.QUALIFICATION, group(qualificationsGroupGenerator));
        map.put(AffiliationType.SERVICE, group(servicesGroupGenerator));
        
        return map;
    }
        
    @Override
    public <T extends AffiliationSummary> List<AffiliationGroup<T>> groupAffiliations(List<T> summaries, boolean justPublic) {
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        for (AffiliationSummary summary : summaries) {
            if (!justPublic || summary.getVisibility().equals(org.orcid.jaxb.model.v3.rc1.common.Visibility.PUBLIC)) {
                groupGenerator.group(summary);
            }
        }

        return group(groupGenerator);
    }
    
    @SuppressWarnings("unchecked")
    private <T extends AffiliationSummary> List<AffiliationGroup<T>> group(ActivitiesGroupGenerator groupGenerator) {
        List<ActivitiesGroup> groups = groupGenerator.getGroups();
        List<AffiliationGroup<T>> affiliationGroups = new ArrayList<>();

        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            AffiliationGroup<T> affiliationGroup = new AffiliationGroup<>();
            
            // Fill with the external identifiers
            if(externalIdentifiers == null || externalIdentifiers.isEmpty()) {
                // Initialize the ids as an empty list
                affiliationGroup.getIdentifiers().getExternalIdentifier();
            } else {
                for (GroupAble extId : externalIdentifiers) {
                    ExternalID workExtId = (ExternalID) extId;
                    affiliationGroup.getIdentifiers().getExternalIdentifier().add(workExtId.clone());
                }
            }
            
            // Fill with the list of activities
            for (GroupableActivity activity : activities) {
                T affiliationSummary = (T) activity;
                affiliationGroup.getActivities().add(affiliationSummary);
            }

            // Sort
            affiliationGroup.getActivities().sort(new GroupableActivityComparator());
            affiliationGroups.add(affiliationGroup);
        }
        return affiliationGroups;
    }
    
    private void checkType(OrgAffiliationRelationEntity entity, AffiliationType type) {
        if(!entity.getAffiliationType().equals(type.name())) {
            throw new IllegalArgumentException("Given affiliation " + entity.getId() + " doesn't match the desired type " + type.value());
        }
    }
}
