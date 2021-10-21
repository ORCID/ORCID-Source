package org.orcid.core.orgs.load.source.zenodo.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZenodoRecordsHits {
    
    @JsonProperty("hits")
    private List<ZenodoRecordsHit> hits;
    
    @JsonProperty("total")
    private Integer total;

    @JsonProperty("hits")
    public List<ZenodoRecordsHit> getHits() {
        return hits;
    }

    @JsonProperty("hits")
    public void setHits(List<ZenodoRecordsHit> hits) {
        this.hits = hits;
    }

    @JsonProperty("total")
    public Integer getTotal() {
        return total;
    }

    @JsonProperty("total")
    public void setTotal(Integer total) {
        this.total = total;
    }
    
    

}
