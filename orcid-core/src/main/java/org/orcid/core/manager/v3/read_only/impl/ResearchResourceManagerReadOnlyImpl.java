package org.orcid.core.manager.v3.read_only.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.orcid.core.adapter.v3.JpaJaxbResearchResourceAdapter;
import org.orcid.core.manager.v3.read_only.ResearchResourceManagerReadOnly;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResources;
import org.orcid.persistence.dao.ResearchResourceDao;
import org.orcid.persistence.jpa.entities.ResearchResourceEntity;

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
        ResearchResources rr = new ResearchResources();
        // TODO GROUPS
        return rr;
    }

}
