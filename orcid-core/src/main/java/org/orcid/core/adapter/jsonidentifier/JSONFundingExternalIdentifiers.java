/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
