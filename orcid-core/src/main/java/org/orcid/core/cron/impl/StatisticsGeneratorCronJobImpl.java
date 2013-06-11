package org.orcid.core.cron.impl;

import java.util.Map;

import javax.annotation.Resource;

import org.orcid.core.cron.StatisticsGeneratorCronJob;
import org.orcid.core.manager.StatisticsGeneratorManager;
import org.orcid.core.manager.StatisticsManager;
import org.orcid.persistence.jpa.entities.StatisticKeyEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsGeneratorCronJobImpl implements StatisticsGeneratorCronJob {

private static final Logger LOG = LoggerFactory.getLogger(StatisticsGeneratorCronJobImpl.class);
    
    @Resource
    private StatisticsGeneratorManager statisticsGeneratorManager;

    @Resource
    private StatisticsManager statisticsManager;
    
    /**
     * 
     * */
    @Override
    public void generateStatistics(){
        LOG.debug("About to run statistics generator thread");
        Map<String, Long> statistics = statisticsGeneratorManager.generateStatistics();
        StatisticKeyEntity statisticKey = statisticsManager.createHistory();
        
        //Store statistics on database
        for(String key : statistics.keySet()){
            statisticsManager.saveStatistic(statisticKey, key, statistics.get(key));
        }
    }

}
