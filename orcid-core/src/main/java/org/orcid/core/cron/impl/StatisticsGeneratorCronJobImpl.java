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

import java.util.Calendar;
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

    private long dayInMillis = 24 * 60 * 60 * 1000;
    
    private int daysOffset;
    
    public StatisticsGeneratorCronJobImpl(){
        this.daysOffset = 1;
    }
    
    public StatisticsGeneratorCronJobImpl(int daysOffset){
        this.daysOffset = daysOffset;
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
            long currentDaysOffset = this.getDaysOffset(lastStatisticsKey.getGenerationDate());
            LOG.info("Days since the last time the statistics were generated: {}", currentDaysOffset);
            if(currentDaysOffset < this.daysOffset)
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
    
    /**
     * Get the number of days since the last time the cron job ran
     * @param lastRun The last time the cron ran
     * @return the number of days since the last time the cron ran
     * */
    private long getDaysOffset(Date lastRun){
        Calendar lastRunCalendar = Calendar.getInstance();
        lastRunCalendar.setTime(lastRun);
        long lastRunMillis = lastRunCalendar.getTimeInMillis();
        long todayMillis = System.currentTimeMillis();        
        return (todayMillis - lastRunMillis) / dayInMillis;
    }

}
