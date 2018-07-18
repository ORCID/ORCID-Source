package org.orcid.pojo;

import java.util.List;

import org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceSummary;

public class ResearchResourceGroupPojo extends GroupPojo<ResearchResourceSummary> {

    public ResearchResourceGroupPojo(org.orcid.jaxb.model.v3.rc1.record.summary.ResearchResourceGroup g, int id, String orcid) {
        super(g.getResearchResourceSummary(), id, orcid, g.getIdentifiers());
    }

    /** Method adds getter for correct serialization name; 
     * 
     */
    public List<ResearchResourceSummary> getResearchResources(){
        return super.getActivities();
    }
    
}
