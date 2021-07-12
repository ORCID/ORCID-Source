package org.orcid.core.orgs.load.source.zenodo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ZenodoRecordsFileLinks {
    
    @JsonProperty("self")
    private String self;

    @JsonProperty("self")
    public String getSelf() {
        return self;
    }

    @JsonProperty("self")
    public void setSelf(String self) {
        this.self = self;
    }

}
