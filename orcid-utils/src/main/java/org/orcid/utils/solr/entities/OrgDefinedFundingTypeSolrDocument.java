package org.orcid.utils.solr.entities;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrgDefinedFundingTypeSolrDocument {

    @Field(SolrConstants.ORG_DEFINED_FUNDING_TYPE)
    private String orgDefinedFundingType;

    public String getOrgDefinedFundingType() {
        return orgDefinedFundingType;
    }

    public void setOrgDefinedFundingType(String orgDefinedFundingType) {
        this.orgDefinedFundingType = orgDefinedFundingType;
    }      
}
