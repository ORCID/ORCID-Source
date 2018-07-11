package org.orcid.frontend.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.ResearchResourceManager;
import org.orcid.frontend.web.pagination.Page;
import org.orcid.jaxb.model.v3.rc1.record.ResearchResource;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;
import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResources;
import org.orcid.pojo.ResearchResourceGroup;
import org.orcid.pojo.ajaxForm.ResearchResourceForm;
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
    
    @Resource
    ResearchResourceManager researchResourceManager;
    
    /**
     * List research resources associated with a profile
     * */
    @RequestMapping(value = "/researchResourcePage.json", method = RequestMethod.GET)
    public @ResponseBody Page<ResearchResourceGroup> getresearchResourcePage(@RequestParam("offset") int offset) {
        String orcid = getCurrentUserOrcid();
        
        List<ResearchResourceSummary> r = researchResourceManager.getResearchResourceSummaryList(orcid);
        ResearchResources rr = researchResourceManager.groupResearchResources(r, false);
        Page<ResearchResourceGroup> page = new Page<ResearchResourceGroup>();
        page.setGroups(new ArrayList<ResearchResourceGroup>());
        
        for (int i = offset; i < Math.min(offset + PAGE_SIZE, rr.getResearchResourceGroup().size()); i++) {
            org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceGroup group = rr.getResearchResourceGroup().get(i);
            page.getGroups().add(ResearchResourceGroup.valueOf(group, i, orcid));
        }
        page.setTotalGroups(rr.getResearchResourceGroup().size());
        page.setNextOffset(offset+PAGE_SIZE);
        //TODO: translation stuff
        return page;
    }
    
    @RequestMapping(value = "/researchResource.json", method = RequestMethod.GET)
    public @ResponseBody ResearchResourceForm getResearchResource(@RequestParam("id") long id) {
        String orcid = getCurrentUserOrcid();
        ResearchResource r = researchResourceManager.getResearchResource(orcid, id);
        //TODO: translation stuff
        return ResearchResourceForm.valueOf(r);
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
    
    //what does it need to do?
    //list of groups
    //Return a paginated list of groups like works.
    //affiliation for comparisson: https://github.com/ORCID/ORCID-Source/tree/CreatingActivityGroup
    //READ ONLY
    
    //create group pojo, form class, 
    
    //work out how translation works (see funding for example) ?? Can't find it now...
    //update publicProfileController to do public stuff.
    
    //QUESTION - Text vs string..
    //QUESTION - how the hell do o.getDisambiguatedOrganization().getExternalIdentifiers() work?
    
    //ANSWER - liz not bothered either way.
}
