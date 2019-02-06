package org.orcid.pojo.grouping;

import java.io.Serializable;

public class WorkGroupingSuggestionsCount implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int count;
    
    private String orcid;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }
    

}
