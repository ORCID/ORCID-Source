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
package org.orcid.core.cron.impl;

import java.util.Date;
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

    // 30 Minutes in millis
    private long offset = 30 * 60 * 1000;
    
    public StatisticsGeneratorCronJobImpl(){
    }
    
    /**
     * Cron job that will generate statistics and store them on database
     * */
    @Override
    public void generateStatistics() {        
        LOG.debug("About to run statistics generator thread");
        boolean run = true;
        StatisticKeyEntity lastStatisticsKey = statisticsManager.getLatestKey();
        
        if(lastStatisticsKey != null && lastStatisticsKey.getGenerationDate() != null){
            LOG.info("Last time the statistics were generated: {}", lastStatisticsKey.getGenerationDate());
            
            long currentTime = System.currentTimeMillis();
            long lastRunInMillis = lastStatisticsKey.getGenerationDate().getTime();
            
            if(currentTime - lastRunInMillis < offset)
                run = false;
        }                                
        
        if(run){
            LOG.info("Last time the statistics cron job ran: {}", new Date());
            Map<String, Long> statistics = statisticsGeneratorManager.generateStatistics();
            StatisticKeyEntity statisticKey = statisticsManager.createKey();
    
            // Store statistics on database
            for (String key : statistics.keySet()) {
                statisticsManager.saveStatistic(statisticKey, key, statistics.get(key));
            }
        }
    }        
}
