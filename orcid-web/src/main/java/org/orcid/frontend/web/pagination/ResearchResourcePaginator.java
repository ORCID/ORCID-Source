package org.orcid.frontend.web.pagination;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResources;
import org.orcid.pojo.ResearchResourceGroupPojo;

public class ResearchResourcePaginator {

    static final int PAGE_SIZE = 50;
    
    @Resource(name = "researchResourceManagerV3")
    ResearchResourceManager researchResourceManager;

    public Page<ResearchResourceGroupPojo> getPage(String orcid, int offset, boolean justPublic) {
        List<ResearchResourceSummary> r = researchResourceManager.getResearchResourceSummaryList(orcid);
        ResearchResources rr = researchResourceManager.groupResearchResources(r, justPublic);
        Page<ResearchResourceGroupPojo> page = new Page<ResearchResourceGroupPojo>();
        page.setGroups(new ArrayList<ResearchResourceGroupPojo>());
        
        for (int i = offset; i < Math.min(offset + PAGE_SIZE, rr.getResearchResourceGroup().size()); i++) {
            org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceGroup group = rr.getResearchResourceGroup().get(i);
            page.getGroups().add(new ResearchResourceGroupPojo(group, i, orcid));
        }
        page.setTotalGroups(rr.getResearchResourceGroup().size());
        page.setNextOffset(offset+PAGE_SIZE);
        return page;
    }
    
    public int getPublicCount(String orcid) {
        List<ResearchResourceSummary> r = researchResourceManager.getResearchResourceSummaryList(orcid);
        ResearchResources publicResources = researchResourceManager.groupResearchResources(r, true);
        return publicResources.getResearchResourceGroup().size();
    }
}
