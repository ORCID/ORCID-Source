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
