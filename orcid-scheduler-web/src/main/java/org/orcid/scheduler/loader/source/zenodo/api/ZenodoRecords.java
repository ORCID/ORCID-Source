package org.orcid.scheduler.loader.source.zenodo.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ZenodoRecords {

    @JsonProperty("hits")
    private ZenodoRecordsHits hits;


    @JsonProperty("hits")
    public ZenodoRecordsHits getHits() {
        return hits;
    }

    @JsonProperty("hits")
    public void setHits(ZenodoRecordsHits hits) {
        this.hits = hits;
    }  

}
