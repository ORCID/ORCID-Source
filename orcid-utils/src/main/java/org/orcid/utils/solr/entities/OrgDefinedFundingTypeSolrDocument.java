package org.orcid.utils.solr.entities;

import java.io.Serializable;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrgDefinedFundingTypeSolrDocument implements Serializable {

    private static final long serialVersionUID = -8658219490445053157L;

    @Field(SolrConstants.ORG_DEFINED_FUNDING_TYPE)
    private String orgDefinedFundingType;

    public String getOrgDefinedFundingType() {
        return orgDefinedFundingType;
    }

    public void setOrgDefinedFundingType(String orgDefinedFundingType) {
        this.orgDefinedFundingType = orgDefinedFundingType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((orgDefinedFundingType == null) ? 0 : orgDefinedFundingType.hashCode());
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
        OrgDefinedFundingTypeSolrDocument other = (OrgDefinedFundingTypeSolrDocument) obj;
        if (orgDefinedFundingType == null) {
            if (other.orgDefinedFundingType != null)
                return false;
        } else if (!orgDefinedFundingType.equals(other.orgDefinedFundingType))
            return false;
        return true;
    }      
}
