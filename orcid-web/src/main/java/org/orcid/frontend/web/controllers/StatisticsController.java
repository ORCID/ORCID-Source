/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.orcid.core.manager.StatisticsManager;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.orcid.persistence.jpa.entities.StatisticValuesEntity;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController extends BaseController {    
    @Resource
    private StatisticsManager statisticsManager;
    
    @Resource
    MessageSource messageSource;
    
    @RequestMapping
    public ModelAndView getStatistics() {        
        ModelAndView mav = new ModelAndView("statistics");
        Map<String, Long> statisticsMap = new HashMap<String, Long>();
                
        StatisticKeyEntity latestKey = null; //statisticsManager.getLatestKey();
        List<StatisticValuesEntity> statistics = statisticsManager.getLatestStatistics();
        
        if(statistics != null)
            for(StatisticValuesEntity statistic : statistics) {
                statisticsMap.put(statistic.getStatisticName(), statistic.getStatisticValue());
                if (latestKey == null) 
                    latestKey = statistic.getKey();
            }        
            
        mav.addObject("statistics", statisticsMap);
        
        if(latestKey != null)
            mav.addObject("statistics_date", formatStatisticsDate(latestKey.getGenerationDate()));
        
        return mav;
    }
    
    /**
     * Formats the date when the statistic was added
     * */
    private String formatStatisticsDate(Date date){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MMM-dd"); 
        return dt.format(date);
    }
    
    /** 
     * @return the total amount of Live iDs  
     * */
    @RequestMapping(value = "/liveids.json")    
    public @ResponseBody String getLiveIdsAmount(HttpServletRequest request) {        
        return statisticsManager.getLiveIds(request.getLocale());
    }
    
}
