package org.orcid.frontend.web.controllers;

import java.util.List;

import javax.annotation.Resource;

import org.orcid.core.manager.v3.ProfileHistoryEventManager;
import org.orcid.persistence.jpa.entities.ProfileHistoryEventEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ProfileHistoryController extends BaseController {

    @Resource(name = "profileHistoryEventManagerV3")
    private ProfileHistoryEventManager profileHistoryEventManager;

    @RequestMapping(value = "/profile-history/{orcid}", method = RequestMethod.GET)
    public ModelAndView register(@PathVariable("orcid") String orcid) {
        List<ProfileHistoryEventEntity> history = profileHistoryEventManager.getProfileHistoryForOrcid(orcid);
        ModelAndView mav = new ModelAndView("profile_history");
        mav.addObject("profileHistory", history);
        return mav;
    }
    
}
