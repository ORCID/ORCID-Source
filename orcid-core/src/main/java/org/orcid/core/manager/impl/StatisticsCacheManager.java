package org.orcid.core.manager.impl;

import java.util.Locale;

import org.orcid.core.utils.statistics.StatisticsEnum;
import org.orcid.jaxb.model.statistics.StatisticsSummary;
import org.orcid.jaxb.model.statistics.StatisticsTimeline;

/**
 * @author Shobhit Tyagi
 */
public interface StatisticsCacheManager {

	public StatisticsSummary retrieve();
    
    public void remove();

	String retrieveLiveIds(Locale locale);

	public StatisticsTimeline getStatisticsTimelineModel(StatisticsEnum type);
}
