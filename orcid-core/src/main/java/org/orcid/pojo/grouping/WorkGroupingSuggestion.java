package org.orcid.pojo.grouping;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class WorkGroupingSuggestion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<Long> putCodes;
    
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
    
    public String getPutCodesAsString() {
        Collections.sort(putCodes);
        return StringUtils.join(putCodes, ',');
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    
}
