package org.orcid.pojo.grouping;

import java.io.Serializable;
import java.util.List;

public class WorkGroupingSuggestion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<Long> putCodes;
    
    private Long id;
    
    private String orcid;
    
    public WorkGroupingSuggestion() {
        
    }
    
    public WorkGroupingSuggestion(List<Long> putCodes) {
        this.putCodes = putCodes;
    }

    public List<Long> getPutCodes() {
        return putCodes;
    }

    public void setPutCodes(List<Long> putCodes) {
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
