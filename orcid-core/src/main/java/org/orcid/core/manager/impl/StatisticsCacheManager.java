package org.orcid.core.manager.impl;

import java.util.Locale;

import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;

/**
 * @author Shobhit Tyagi
 */
public interface StatisticsCacheManager {

    String retrieveLiveIds(Locale locale);
    
    StatisticsSummary retrieve();

    StatisticsTimeline getStatisticsTimelineModel(StatisticsEnum type);
            
    void setLatestStatisticsSummary();
            
    void setLatestStatisticsTimeline();
}
