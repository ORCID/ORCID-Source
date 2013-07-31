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

    private long halfHourInMillis = 30 * 60 * 1000;
    
    private long hourInMillis = halfHourInMillis * 2;
    
    private long dayInMillis = 24 * hourInMillis;
    
    private long weekInMillis = dayInMillis * 7;        
    
    /**
     * Cron job that will generate statistics and store them on database
     * */
    @Override
    public void generateStatistics() {
        LOG.debug("About to run statistics generator thread");
        boolean run = false;
        StatisticKeyEntity lastStatisticsKey = statisticsManager.getLatestKey();
        
        if(lastStatisticsKey != null && lastStatisticsKey.getGenerationDate() != null){
        	Date lastTimeJobRuns = lastStatisticsKey.getGenerationDate();        	
        	boolean isTimeToRun = isFridayNearMidnight();
        	long offset = System.currentTimeMillis() - lastTimeJobRuns.getTime();
        	LOG.info("Last time the statistics were generated: {}", lastTimeJobRuns);            
            LOG.info("Is time to run the scheduler? {}", isTimeToRun);
            
            if(offset > weekInMillis || (isTimeToRun && offset > halfHourInMillis) ){
            	run = true;
            }
            
        } else {
        	LOG.warn("There are no statistics generated yet.");
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
     * @return true if it is Friday 11:30 PM or later, false otherwise. 
     * */
    public boolean isFridayNearMidnight(){
    	Calendar c = Calendar.getInstance();
    	int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
    	// If it is friday
    	if(dayOfWeek == Calendar.FRIDAY){
    		// And it is 11 PM
    		if(c.get(Calendar.HOUR_OF_DAY) == 23){
    			// And it is later than 11:30 pm
    			if(c.get(Calendar.MINUTE) >= 30){
    				return true;
    			}
    		}
    	}
    	
    	return false;
    }
}
