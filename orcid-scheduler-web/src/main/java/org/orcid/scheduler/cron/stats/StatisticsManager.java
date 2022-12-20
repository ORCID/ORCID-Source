package org.orcid.scheduler.cron.stats;

public interface StatisticsManager {
    
    /**
     * Generate a new set of stats and store them in the database
     * 
     * @param statistics
     *          List of statistics to store
     * */
    public void generateStatistics();
}
