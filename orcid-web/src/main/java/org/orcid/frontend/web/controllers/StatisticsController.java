package org.orcid.frontend.web.controllers;

import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.StatisticsManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class StatisticsController extends BaseController {

    @Resource
    private StatisticsManager statisticsManager;
    
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @Cacheable("statistics")
    public ModelAndView getStatistics(){
        ModelAndView mav = new ModelAndView("statistics");
        Map<String, Long> statistics = statisticsManager.getStatistics(); 
        mav.addObject("statistics", statistics);
        return mav;
    }
}
