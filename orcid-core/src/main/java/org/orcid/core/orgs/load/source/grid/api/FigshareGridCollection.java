package org.orcid.core.orgs.load.source.grid.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FigshareGridCollection {

    @JsonProperty("timeline")
    private GridCollectionTimeline timeline;

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("timeline")
    public GridCollectionTimeline getTimeline() {
        return timeline;
    }

    @JsonProperty("timeline")
    public void setTimeline(GridCollectionTimeline timeline) {
        this.timeline = timeline;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

}
