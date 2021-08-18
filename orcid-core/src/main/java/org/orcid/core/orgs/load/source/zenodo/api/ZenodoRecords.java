package org.orcid.core.orgs.load.source.zenodo.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(value = { "aggregations", "links" })
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
