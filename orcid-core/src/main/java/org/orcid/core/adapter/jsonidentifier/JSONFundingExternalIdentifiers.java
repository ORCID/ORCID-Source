package org.orcid.core.adapter.jsonidentifier;

import java.util.ArrayList;
import java.util.List;

public class JSONFundingExternalIdentifiers {

    private List<JSONExternalIdentifier> fundingExternalIdentifier;

    public List<JSONExternalIdentifier> getFundingExternalIdentifier() {
        if (fundingExternalIdentifier == null) {
            fundingExternalIdentifier = new ArrayList<>();
        }
        return fundingExternalIdentifier;
    }

    public void setFundingExternalIdentifier(List<JSONExternalIdentifier> fundingExternalIdentifier) {
        this.fundingExternalIdentifier = fundingExternalIdentifier;
    }
    
}
