package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.adapter.v3.JpaJaxbResearchResourceAdapter;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.core.utils.v3.activities.ActivitiesGroup;
import org.orcid.core.utils.v3.activities.ActivitiesGroupGenerator;
import org.orcid.core.utils.v3.activities.GroupableActivityComparator;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;
import org.orcid.jaxb.model.v3.rc2.record.GroupAble;
import org.orcid.jaxb.model.v3.rc2.record.GroupableActivity;
import org.orcid.jaxb.model.v3.rc2.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceGroup;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc2.record.summary.ResearchResources;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;
import org.orcid.pojo.ajaxForm.PojoUtil;

public class ResearchResourceManagerReadOnlyImpl extends ManagerReadOnlyBaseImpl implements ResearchResourceManagerReadOnly{

    @Resource 
    private ResearchResourceDao rrDao;
    
    @Resource(name = "jpaJaxbResearchResourceAdapterV3")
    protected JpaJaxbResearchResourceAdapter jpaJaxbResearchResourceAdapter;
    
    @Override
    @Transactional
    public ResearchResource getResearchResource(String orcid, Long researchResourceId) {
        ResearchResourceEntity e = rrDao.getResearchResource(orcid, researchResourceId);
        return jpaJaxbResearchResourceAdapter.toModel(e);
    }

    @Override
    public ResearchResourceSummary getResearchResourceSummary(String orcid, Long researchResourceId) {
        ResearchResourceEntity e = rrDao.getResearchResource(orcid, researchResourceId);
        return jpaJaxbResearchResourceAdapter.toSummary(e);
    }

    @Override
    @Transactional
    public List<ResearchResource> findResearchResources(String orcid) {
        List<ResearchResourceEntity> e = rrDao.getByUser(orcid, getLastModified(orcid));
        return jpaJaxbResearchResourceAdapter.toModels(e);
    }

    @Override
    public List<ResearchResourceSummary> getResearchResourceSummaryList(String orcid) {
        List<ResearchResourceEntity> e = rrDao.getByUser(orcid, getLastModified(orcid));
        return jpaJaxbResearchResourceAdapter.toSummaries(e);
    }

    @Override
    public ResearchResources groupResearchResources(List<ResearchResourceSummary> researchResources, boolean justPublic) {
        //so much code duplication :(
        ActivitiesGroupGenerator groupGenerator = new ActivitiesGroupGenerator();
        ResearchResources rr = new ResearchResources();
        for (ResearchResourceSummary r : researchResources){
            if (justPublic && !r.getVisibility().equals(org.orcid.jaxb.model.v3.rc2.common.Visibility.PUBLIC)) {
            } else {
                groupGenerator.group(r);
            }
        }
        List<ActivitiesGroup> groups = groupGenerator.getGroups();
        for (ActivitiesGroup group : groups) {
            Set<GroupAble> externalIdentifiers = group.getGroupKeys();
            Set<GroupableActivity> activities = group.getActivities();
            ResearchResourceGroup rrGroup = new ResearchResourceGroup();
            // Fill the work groups with the external identifiers
            if(externalIdentifiers == null || externalIdentifiers.isEmpty()) {
                // Initialize the ids as an empty list
                rrGroup.getIdentifiers().getExternalIdentifier();
            } else {
                for (GroupAble extId : externalIdentifiers) {
                    ExternalID workExtId = (ExternalID) extId;
                    rrGroup.getIdentifiers().getExternalIdentifier().add(workExtId.clone());
                }
            }
            
            // Fill the work group with the list of activities
            for (GroupableActivity activity : activities) {
                ResearchResourceSummary rrSummary = (ResearchResourceSummary) activity;
                rrGroup.getResearchResourceSummary().add(rrSummary);
            }

            // Sort
            rrGroup.getResearchResourceSummary().sort(new GroupableActivityComparator());
            rr.getResearchResourceGroup().add(rrGroup);
        }
        return rr;
    }

    @Override
    public Boolean hasPublicResearchResources(String orcid) {
        if(PojoUtil.isEmpty(orcid)) {
            return false;
        }
        return rrDao.hasPublicResearchResources(orcid);
    }

}
