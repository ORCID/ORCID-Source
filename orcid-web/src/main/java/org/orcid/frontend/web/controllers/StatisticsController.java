package org.orcid.frontend.web.controllers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.manager.impl.StatisticsCacheManager;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.pojo.StatsSummary;
import org.springframework.context.MessageSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/statistics")
public class StatisticsController extends BaseController {    
    @Resource
    private StatisticsCacheManager statisticsCacheManager;
    
    @Resource
    MessageSource messageSource;
    
    @Scheduled(fixedDelayString = "${statistics.key.interval.delay:600000}")
    public void updateToLatestStatisticsSummary() {  
        statisticsCacheManager.setLatestStatisticsSummary();
    }
            
    @RequestMapping
    public ModelAndView getStatistics() {        
        ModelAndView mav = new ModelAndView("statistics");
        if (!domainsAllowingRobots.contains(orcidUrlManager.getBaseDomainRmProtocall())) {
            mav.addObject("noIndex", true);
        }
        return mav;
    }
    
    @RequestMapping(value = "/statistics.json", method = RequestMethod.GET)
    public @ResponseBody StatsSummary getStatsSummary() {
        StatisticsSummary statisticsSummary = statisticsCacheManager.retrieve();
        StatsSummary statsSummary = new StatsSummary();
        if (statisticsSummary != null) {
            mapProperties(statisticsSummary.getStatistics(), statsSummary);
            statsSummary.setStatisticsDate(formatStatisticsDate(statisticsSummary.getDate()));
        }
        return statsSummary;
    }
    
    private void mapProperties(Map<String, Long> statistics, StatsSummary statsSummary) {
        statsSummary.setLiveIds(statistics.get("liveIds"));
        statsSummary.setIdsWithExternalId(statistics.get("idsWithExternalId"));
        statsSummary.setIdsWithEducation(statistics.get("idsWithEducation"));
        statsSummary.setNumEducations(statistics.get("education"));
        statsSummary.setEducationUniqueOrgs(statistics.get("educationUniqueOrg"));
        statsSummary.setIdsWithEmployment(statistics.get("idsWithEmployment"));
        statsSummary.setNumEmployments(statistics.get("employment"));
        statsSummary.setEmploymentUniqueOrgs(statistics.get("employmentUniqueOrg"));
        statsSummary.setIdsWithFunding(statistics.get("idsWithFunding"));
        statsSummary.setNumFundings(statistics.get("funding"));
        statsSummary.setFundingUniqueOrgs(statistics.get("fundingUniqueOrg"));
        statsSummary.setIdsWithPeerReview(statistics.get("idsWithPeerReview"));
        statsSummary.setNumPeerReviews(statistics.get("peerReview"));
        statsSummary.setIdsWithPersonId(statistics.get("idsWithPersonId"));
        statsSummary.setNumPersonIds(statistics.get("personId"));
        statsSummary.setIdsWithWork(statistics.get("idsWithWork"));
        statsSummary.setNumWorks(statistics.get("work"));
        statsSummary.setUniqueDois(statistics.get("uniqueDois"));
    }

    /**
     * Formats the date when the statistic was added
     * */
    private String formatStatisticsDate(Date date){
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd"); 
        return dt.format(date);
    }
    
}
