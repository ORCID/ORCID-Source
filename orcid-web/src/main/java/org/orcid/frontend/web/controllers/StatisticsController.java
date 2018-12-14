package org.orcid.frontend.web.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.impl.StatisticsCacheManager;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController extends BaseController {    
    @Resource
    private StatisticsCacheManager statisticsCacheManager;
    
    @Resource
    MessageSource messageSource;
    
    @Scheduled(fixedDelayString = "${statistics.key.interval.delay:60000000}")
    public void updateToLatestStatisticsSummary() {  
        statisticsCacheManager.setLatestStatisticsSummary();
    }
            
    @RequestMapping
    public ModelAndView getStatistics() {        
        ModelAndView mav = new ModelAndView("statistics");
        Map<String, Long> statisticsMap = null;
                
        StatisticsSummary statisticsSummary = statisticsCacheManager.retrieve();
        
        if(statisticsSummary != null) {
        	statisticsMap = statisticsSummary.getStatistics();
        	mav.addObject("statistics_date", formatStatisticsDate(statisticsSummary.getDate()));
        }
        if(statisticsMap == null) {
        	statisticsMap = new HashMap<String, Long>();
        }
        mav.addObject("statistics", statisticsMap);
        
        return mav;
    }
    
    /**
     * Formats the date when the statistic was added
     * */
    private String formatStatisticsDate(Date date){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd"); 
        return dt.format(date);
    }
    
    /** 
     * @return the total amount of Live iDs  
     * */
    @RequestMapping(value = "/liveids.json")    
    public @ResponseBody String getLiveIdsAmount(HttpServletRequest request) {        
        return statisticsCacheManager.retrieveLiveIds(request.getLocale());
    }
    
}
