package org.orcid.core.adapter.jsonidentifier;

import java.util.ArrayList;
import java.util.List;

public class JSONExternalIdentifiers {
    
    private List<JSONExternalIdentifier> externalIdentifier;

    public List<JSONExternalIdentifier> getExternalIdentifier() {
        if (externalIdentifier == null) {
            externalIdentifier = new ArrayList<>();
        }
        return externalIdentifier;
    }

    public void setExternalIdentifier(List<JSONExternalIdentifier> externalIdentifier) {
        this.externalIdentifier = externalIdentifier;
    }

}
