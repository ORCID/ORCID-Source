package org.orcid.core.manager;

import java.util.Map;

public interface StatisticsManager {
    
    /**
     * Save a set of statistics to the database
     * 
     * @param statistics
     *          List of statistics to store
     * */
    public void saveStatistics(Map<String, Long> statistics);
}
