package org.orcid.utils.solr.entities;

import java.io.Serializable;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgDisambiguatedSolrDocument implements Serializable {

    private static final long serialVersionUID = -3668075852578170180L;

    @Field(SolrConstants.ORG_DISAMBIGUATED_ID)
    private Long orgDisambiguatedId;

    @Field(SolrConstants.ORG_DISAMBIGUATED_NAME)
    private String orgDisambiguatedName;

    @Field(SolrConstants.ORG_DISAMBIGUATED_CITY)
    private String orgDisambiguatedCity;

    @Field(SolrConstants.ORG_DISAMBIGUATED_REGION)
    private String orgDisambiguatedRegion;

    @Field(SolrConstants.ORG_DISAMBIGUATED_COUNTRY)
    private String orgDisambiguatedCountry;

    @Field(SolrConstants.ORG_DISAMBIGUATED_TYPE)
    private String orgDisambiguatedType;

    @Field(SolrConstants.ORG_DISAMBIGUATED_POPULARITY)
    private Integer orgDisambiguatedPopularity;

    @Field(SolrConstants.ORG_NAMES)
    private List<String> orgNames;

    @Field(SolrConstants.ORG_DISAMBIGUATED_ID_FROM_SOURCE)
    private String orgDisambiguatedIdFromSource;

    @Field(SolrConstants.ORG_DISAMBIGUATED_STATUS)
    private String orgDisambiguatedStatus;

    @Field(SolrConstants.ORG_DISAMBIGUATED_ID_SOURCE_TYPE)
    private String orgDisambiguatedIdSourceType;

    @Field(SolrConstants.IS_FUNDING_ORG)
    private boolean isFundingOrg;

    @Field(SolrConstants.ORG_CHOSEN_BY_MEMBER)
    private boolean isOrgChosenByMember;

    public Long getOrgDisambiguatedId() {
        return orgDisambiguatedId;
    }

    public void setOrgDisambiguatedId(Long orgDisambiguatedId) {
        this.orgDisambiguatedId = orgDisambiguatedId;
    }

    public String getOrgDisambiguatedName() {
        return orgDisambiguatedName;
    }

    public void setOrgDisambiguatedName(String orgDisambiguatedName) {
        this.orgDisambiguatedName = orgDisambiguatedName;
    }

    public String getOrgDisambiguatedCity() {
        return orgDisambiguatedCity;
    }

    public void setOrgDisambiguatedCity(String orgDisambiguatedCity) {
        this.orgDisambiguatedCity = orgDisambiguatedCity;
    }

    public String getOrgDisambiguatedRegion() {
        return orgDisambiguatedRegion;
    }

    public void setOrgDisambiguatedRegion(String orgDisambiguatedRegion) {
        this.orgDisambiguatedRegion = orgDisambiguatedRegion;
    }

    public String getOrgDisambiguatedCountry() {
        return orgDisambiguatedCountry;
    }

    public void setOrgDisambiguatedCountry(String orgDisambiguatedCountry) {
        this.orgDisambiguatedCountry = orgDisambiguatedCountry;
    }

    /**
     * The type of org, e.g. academic, government.
     */
    public String getOrgDisambiguatedType() {
        return orgDisambiguatedType;
    }

    public void setOrgDisambiguatedType(String orgDisambiguatedType) {
        this.orgDisambiguatedType = orgDisambiguatedType;
    }

    public Integer getOrgDisambiguatedPopularity() {
        return orgDisambiguatedPopularity;
    }

    public void setOrgDisambiguatedPopularity(Integer orgDisambiguatedPopularity) {
        this.orgDisambiguatedPopularity = orgDisambiguatedPopularity;
    }

    public List<String> getOrgNames() {
        return orgNames;
    }

    public void setOrgNames(List<String> orgNames) {
        this.orgNames = orgNames;
    }

    public boolean isFundingOrg() {
        return isFundingOrg;
    }

    public void setFundingOrg(boolean isFundingOrg) {
        this.isFundingOrg = isFundingOrg;
    }

    public String getOrgDisambiguatedIdFromSource() {
        return orgDisambiguatedIdFromSource;
    }

    public void setOrgDisambiguatedIdFromSource(String orgDisambiguatedIdFromSource) {
        this.orgDisambiguatedIdFromSource = orgDisambiguatedIdFromSource;
    }

    /**
     * The type of ID, e.g. RINGGOLD, FUNDREF
     */
    public String getOrgDisambiguatedIdSourceType() {
        return orgDisambiguatedIdSourceType;
    }

    public void setOrgDisambiguatedIdSourceType(String orgDisambiguatedIdSourceType) {
        this.orgDisambiguatedIdSourceType = orgDisambiguatedIdSourceType;
    }

    public boolean isOrgChosenByMember() {
        return isOrgChosenByMember;
    }

    public void setOrgChosenByMember(boolean isOrgChosenByMember) {
        this.isOrgChosenByMember = isOrgChosenByMember;
    }

    public String getOrgDisambiguatedStatus() {
        return orgDisambiguatedStatus;
    }

    public void setOrgDisambiguatedStatus(String orgDisambiguatedStatus) {
        this.orgDisambiguatedStatus = orgDisambiguatedStatus;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isFundingOrg ? 1231 : 1237);
        result = prime * result + (isOrgChosenByMember ? 1231 : 1237);
        result = prime * result + ((orgDisambiguatedCity == null) ? 0 : orgDisambiguatedCity.hashCode());
        result = prime * result + ((orgDisambiguatedCountry == null) ? 0 : orgDisambiguatedCountry.hashCode());
        result = prime * result + ((orgDisambiguatedId == null) ? 0 : orgDisambiguatedId.hashCode());
        result = prime * result + ((orgDisambiguatedIdFromSource == null) ? 0 : orgDisambiguatedIdFromSource.hashCode());
        result = prime * result + ((orgDisambiguatedIdSourceType == null) ? 0 : orgDisambiguatedIdSourceType.hashCode());
        result = prime * result + ((orgDisambiguatedName == null) ? 0 : orgDisambiguatedName.hashCode());
        result = prime * result + ((orgDisambiguatedPopularity == null) ? 0 : orgDisambiguatedPopularity.hashCode());
        result = prime * result + ((orgDisambiguatedRegion == null) ? 0 : orgDisambiguatedRegion.hashCode());
        result = prime * result + ((orgDisambiguatedStatus == null) ? 0 : orgDisambiguatedStatus.hashCode());
        result = prime * result + ((orgDisambiguatedType == null) ? 0 : orgDisambiguatedType.hashCode());
        result = prime * result + ((orgNames == null) ? 0 : orgNames.hashCode());
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
        OrgDisambiguatedSolrDocument other = (OrgDisambiguatedSolrDocument) obj;
        if (isFundingOrg != other.isFundingOrg)
            return false;
        if (isOrgChosenByMember != other.isOrgChosenByMember)
            return false;
        if (orgDisambiguatedCity == null) {
            if (other.orgDisambiguatedCity != null)
                return false;
        } else if (!orgDisambiguatedCity.equals(other.orgDisambiguatedCity))
            return false;
        if (orgDisambiguatedCountry == null) {
            if (other.orgDisambiguatedCountry != null)
                return false;
        } else if (!orgDisambiguatedCountry.equals(other.orgDisambiguatedCountry))
            return false;
        if (orgDisambiguatedId == null) {
            if (other.orgDisambiguatedId != null)
                return false;
        } else if (!orgDisambiguatedId.equals(other.orgDisambiguatedId))
            return false;
        if (orgDisambiguatedIdFromSource == null) {
            if (other.orgDisambiguatedIdFromSource != null)
                return false;
        } else if (!orgDisambiguatedIdFromSource.equals(other.orgDisambiguatedIdFromSource))
            return false;
        if (orgDisambiguatedIdSourceType == null) {
            if (other.orgDisambiguatedIdSourceType != null)
                return false;
        } else if (!orgDisambiguatedIdSourceType.equals(other.orgDisambiguatedIdSourceType))
            return false;
        if (orgDisambiguatedName == null) {
            if (other.orgDisambiguatedName != null)
                return false;
        } else if (!orgDisambiguatedName.equals(other.orgDisambiguatedName))
            return false;
        if (orgDisambiguatedPopularity == null) {
            if (other.orgDisambiguatedPopularity != null)
                return false;
        } else if (!orgDisambiguatedPopularity.equals(other.orgDisambiguatedPopularity))
            return false;
        if (orgDisambiguatedRegion == null) {
            if (other.orgDisambiguatedRegion != null)
                return false;
        } else if (!orgDisambiguatedRegion.equals(other.orgDisambiguatedRegion))
            return false;
        if (orgDisambiguatedStatus == null) {
            if (other.orgDisambiguatedStatus != null)
                return false;
        } else if (!orgDisambiguatedStatus.equals(other.orgDisambiguatedStatus))
            return false;
        if (orgDisambiguatedType == null) {
            if (other.orgDisambiguatedType != null)
                return false;
        } else if (!orgDisambiguatedType.equals(other.orgDisambiguatedType))
            return false;
        if (orgNames == null) {
            if (other.orgNames != null)
                return false;
        } else if (!orgNames.equals(other.orgNames))
            return false;
        return true;
    }
}
