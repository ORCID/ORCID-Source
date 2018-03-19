package org.orcid.core.adapter.jsonidentifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("scope")
public class JSONWorkExternalIdentifiers implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<JSONWorkExternalIdentifier> workExternalIdentifier;

    public List<JSONWorkExternalIdentifier> getWorkExternalIdentifier() {
        if (workExternalIdentifier == null) {
            workExternalIdentifier = new ArrayList<>();
        }
        return workExternalIdentifier;
    }

    public void setWorkExternalIdentifier(List<JSONWorkExternalIdentifier> workExternalIdentifier) {
        this.workExternalIdentifier = workExternalIdentifier;
    }
    
}
