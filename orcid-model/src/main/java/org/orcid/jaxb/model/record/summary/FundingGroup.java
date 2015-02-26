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
package org.orcid.jaxb.model.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * 
 * 
 */
public class FundingGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    private Identifiers identifiers;
    private List<FundingSummary> fundingSummary;

    public Identifiers getIdentifiers() {
        if (identifiers == null)
            identifiers = new Identifiers();
        return identifiers;
    }

    public List<FundingSummary> getFundingSummary() {
        if (fundingSummary == null)
            fundingSummary = new ArrayList<FundingSummary>();
        return fundingSummary;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fundingSummary == null) ? 0 : fundingSummary.hashCode());
        result = prime * result + ((identifiers == null) ? 0 : identifiers.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FundingGroup other = (FundingGroup) obj;
        if (fundingSummary == null) {
            if (other.fundingSummary != null)
                return false;
        } else if (!fundingSummary.equals(other.fundingSummary))
            return false;
        if (identifiers == null) {
            if (other.identifiers != null)
                return false;
        } else if (!identifiers.equals(other.identifiers))
            return false;
        return true;
    }
}
