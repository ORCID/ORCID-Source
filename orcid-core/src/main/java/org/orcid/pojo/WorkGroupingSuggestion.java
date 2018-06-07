package org.orcid.pojo;

import java.io.Serializable;

import org.orcid.core.adapter.jsonidentifier.JSONWorkPutCodes;

public class WorkGroupingSuggestion implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private JSONWorkPutCodes putCodes;

    public JSONWorkPutCodes getPutCodes() {
        return putCodes;
    }

    public void setPutCodes(JSONWorkPutCodes putCodes) {
        this.putCodes = putCodes;
    }
    
}
