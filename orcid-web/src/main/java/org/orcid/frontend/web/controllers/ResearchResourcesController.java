package org.orcid.frontend.web.controllers;

import java.util.ArrayList;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.frontend.web.pagination.Page;
import org.orcid.frontend.web.pagination.ResearchResourcePaginator;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.pojo.ResearchResourceGroupPojo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller("researchResourceController")
@RequestMapping(value = { "/research-resources" })
public class ResearchResourcesController extends BaseWorkspaceController {
    
    static final int PAGE_SIZE = 50;
    
    @Resource(name = "researchResourceManagerV3")
    ResearchResourceManager researchResourceManager;
    
    @Resource
    ResearchResourcePaginator paginator;
    
    /**
     * List research resources associated with a profile
     * */
    @RequestMapping(value = "/researchResourcePage.json", method = RequestMethod.GET)
    public @ResponseBody Page<ResearchResourceGroupPojo> getresearchResourcePage(@RequestParam("offset") int offset, @RequestParam("sort") String sort, @RequestParam("sortAsc") boolean sortAsc) {
        String orcid = getCurrentUserOrcid();        
        return paginator.getPage(orcid, offset,false, sort, sortAsc);
    }
    
    @RequestMapping(value = "/researchResource.json", method = RequestMethod.GET)
    public @ResponseBody ResearchResource getResearchResource(@RequestParam("id") long id) {
        String orcid = getCurrentUserOrcid();
        ResearchResource r = researchResourceManager.getResearchResource(orcid, id);
        return r;
    }
    
    @RequestMapping(value = "/{researchResourceIdsStr}", method = RequestMethod.DELETE)
    public @ResponseBody ArrayList<Long> removeWork(@PathVariable("researchResourceIdsStr") String researchResourceIdsStr) {
        ArrayList<Long> rrIds = new ArrayList<Long>();
        for (String workId : researchResourceIdsStr.split(","))
            rrIds.add(new Long(workId));
        researchResourceManager.removeResearchResources(getEffectiveUserOrcid(), rrIds);
        return rrIds;
    }
        
    /**
     * updates visibility of a research resource
     * */
    @RequestMapping(value = "/{researchResourceIdsStr}/visibility/{visibilityStr}", method = RequestMethod.GET)
    public @ResponseBody ArrayList<Long> updateVisibilitys(@PathVariable("researchResourceIdsStr") String researchResourceIdsStr, @PathVariable("visibilityStr") String visibilityStr) {
        String orcid = getEffectiveUserOrcid();
        ArrayList<Long> rrIds = new ArrayList<Long>();
        for (String workId : researchResourceIdsStr.split(","))
            rrIds.add(new Long(workId));
        researchResourceManager.updateVisibilities(orcid, rrIds, org.orcid.jaxb.model.v3.rc1.common.Visibility.fromValue(visibilityStr));
        return rrIds;
    }
    
    @RequestMapping(value = "/updateToMaxDisplay.json", method = RequestMethod.GET)
    public @ResponseBody boolean updateToMaxDisplay(@RequestParam(value = "putCode") Long putCode) {
        String orcid = getEffectiveUserOrcid();
        return researchResourceManager.updateToMaxDisplay(orcid, putCode);
    }
   
}
