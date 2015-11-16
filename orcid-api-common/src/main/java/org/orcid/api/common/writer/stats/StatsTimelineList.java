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
package org.orcid.api.common.writer.stats;

import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.statistics.StatisticsTimeline;

public class StatsTimelineList  {

    private List<StatisticsTimeline> statistics = new ArrayList<StatisticsTimeline>();

    public StatsTimelineList() {
    }

    public List<StatisticsTimeline> getTimelines() {
        return statistics;
    }

    public void setTimelines(List<StatisticsTimeline> timelines) {
        this.statistics = timelines;
    }
    
    
}
