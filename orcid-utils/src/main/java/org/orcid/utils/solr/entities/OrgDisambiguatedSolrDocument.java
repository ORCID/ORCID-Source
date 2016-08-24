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
package org.orcid.utils.solr.entities;

import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrgDisambiguatedSolrDocument {

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

    @Field(SolrConstants.IS_FUNDING_ORG)
    private boolean isFundingOrg;

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
}
