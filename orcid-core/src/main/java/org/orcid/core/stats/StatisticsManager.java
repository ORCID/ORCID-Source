package org.orcid.core.stats;

public interface StatisticsManager {
    
    /**
     * Generate a new set of stats and store them in the database
     * 
     * @param statistics
     *          List of statistics to store
     * */
    public void generateStatistics();
        
    /**
     * Fetch the number of live ids 
     * */
    public long getLiveIds();
}
