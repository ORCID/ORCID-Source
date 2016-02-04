/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
