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
package org.orcid.pojo;

import java.util.List;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceConsortium {

    private List<SalesForceOpportunity> opportunities;

    public List<SalesForceOpportunity> getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(List<SalesForceOpportunity> opportunities) {
        this.opportunities = opportunities;
    }

    @Override
    public String toString() {
        return "SalesForceConsortium [opportunities=" + opportunities + "]";
    }

}
