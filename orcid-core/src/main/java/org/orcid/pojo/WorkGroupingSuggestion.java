package org.orcid.pojo;

import java.io.Serializable;

import org.orcid.core.adapter.jsonidentifier.JSONWorkPutCodes;

public class WorkGroupingSuggestion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private JSONWorkPutCodes putCodes;
    
    private Long id;
    
    private String orcid;
    
    public WorkGroupingSuggestion() {
        
    }
    
    public WorkGroupingSuggestion(JSONWorkPutCodes putCodes) {
        this.putCodes = putCodes;
    }

    public JSONWorkPutCodes getPutCodes() {
        return putCodes;
    }

    public void setPutCodes(JSONWorkPutCodes putCodes) {
        this.putCodes = putCodes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
}
