package org.orcid.core.salesforce.model;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @author Will Simpson
 *
 */
public class Consortium implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<Opportunity> opportunities;

    public List<Opportunity> getOpportunities() {
        return opportunities;
    }

    public void setOpportunities(List<Opportunity> opportunities) {
        this.opportunities = opportunities;
    }

    @Override
    public String toString() {
        return "SalesForceConsortium [opportunities=" + opportunities + "]";
    }

}
