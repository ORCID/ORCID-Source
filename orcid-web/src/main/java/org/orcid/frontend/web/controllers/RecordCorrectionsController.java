package org.orcid.frontend.web.controllers;

import java.util.Collections;
import java.util.Optional;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.OrcidSecurityManager;
import org.orcid.core.manager.read_only.RecordCorrectionsManagerReadOnly;
import org.orcid.model.record_correction.RecordCorrectionsPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author Liz Krznarich
 */
@Controller
public class RecordCorrectionsController extends BaseController {

    @Resource
    private RecordCorrectionsManagerReadOnly manager;

    @Resource(name = "orcidSecurityManagerV3")
    private OrcidSecurityManager securityMgr;
    
    private static final Long PAGE_SIZE = 10L;

    @RequestMapping(value = { "/about/trust/integrity/record-corrections", "/record-corrections" })
    public ModelAndView recordCorrections() {
        return new ModelAndView("record-corrections");
    }

    @RequestMapping(value = { "/record-corrections/next", "/record-corrections/next/{id}" }, method = RequestMethod.GET)
    public @ResponseBody RecordCorrectionsPage getNextDescending(@PathVariable Optional<Long> id) {
        try {
            return manager.getInvalidRecordDataChangesDescending(id.orElse(null), PAGE_SIZE);
        } catch (IllegalArgumentException e) {

        }
        return new RecordCorrectionsPage();
    }

    @RequestMapping(value = { "/record-corrections/previous", "/record-corrections/previous/{id}" }, method = RequestMethod.GET)
    public @ResponseBody RecordCorrectionsPage getPreviousDescending(@PathVariable Optional<Long> id) {
        try {
            RecordCorrectionsPage page = manager.getInvalidRecordDataChangesAscending(id.orElse(null), PAGE_SIZE);
            // Reverse the elements
            Collections.reverse(page.getRecordCorrections());
            // Reverse the next and previous elements
            boolean newNext = page.getHavePrevious();
            boolean newPrevious = page.getHaveNext();
            page.setHaveNext(newNext);
            page.setHavePrevious(newPrevious);
            Long newFirstElement = page.getLastElementId();
            Long newLastElement = page.getFirstElementId();
            page.setFirstElementId(newFirstElement);
            page.setLastElementId(newLastElement);            
            return page;
        } catch (IllegalArgumentException e) {

        }
        return new RecordCorrectionsPage();
    }
    
    @RequestMapping(value = { "/record-corrections/evict" }, method = RequestMethod.GET)    
    public ModelAndView evictCache() {
        if(securityMgr.isAdmin()) {
            manager.cacheEvict();
        }        
        return new ModelAndView("record-corrections");
    }
}
