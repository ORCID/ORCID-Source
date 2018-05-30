package org.orcid.core.manager.v3.impl;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.core.manager.v3.SourceManager;
import org.orcid.core.manager.v3.read_only.impl.ResearchResourceManagerReadOnlyImpl;
import org.orcid.jaxb.model.common_v2.Visibility;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;

public class ResearchResourceManagerImpl extends ResearchResourceManagerReadOnlyImpl implements ResearchResourceManager {

    @Resource(name = "sourceManagerV3")
    private SourceManager sourceManager;

    @Override
    public ResearchResource createResearchResource(String orcid, ResearchResource rr, boolean isApiRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ResearchResource updateResearchResource(String orcid, ResearchResource rr, boolean isApiRequest) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void removeResearchResource(String orcid, Long researchResourceId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean updateToMaxDisplay(String orcid, Long researchResourceId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean updateVisibilities(String orcid, ArrayList<Long> researchResourceIds, Visibility visibility) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void removeAllResearchResources(String orcid) {
        // TODO Auto-generated method stub
        
    }

}
