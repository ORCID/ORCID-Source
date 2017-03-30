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
package org.orcid.core.salesforce.model;

import java.io.Serializable;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.orcid.core.salesforce.adapter.SalesForceAdapter;

/**
 * 
 * @author Will Simpson
 *
 */
public class Member implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String publicDisplayName;
    private URL websiteUrl;
    private CommunityType researchCommunity;
    private String country;
    private String description;
    private URL logoUrl;
    private String publicDisplayEmail;
    private String mainOpportunityPath;
    private String consortiumLeadId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicDisplayName() {
        return StringUtils.isNotBlank(publicDisplayName) ? publicDisplayName : name;
    }

    public void setPublicDisplayName(String publicDisplayName) {
        this.publicDisplayName = publicDisplayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return SlugUtils.createSlug(getId(), getName());
    }

    public URL getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(URL websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public CommunityType getResearchCommunity() {
        return researchCommunity;
    }

    public void setResearchCommunity(CommunityType researchCommunity) {
        this.researchCommunity = researchCommunity;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URL getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(URL logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getPublicDisplayEmail() {
        return publicDisplayEmail;
    }

    public void setPublicDisplayEmail(String publicDisplayEmail) {
        this.publicDisplayEmail = publicDisplayEmail;
    }

    public String getMainOpportunityPath() {
        return mainOpportunityPath;
    }

    public String getMainOpportunityId() {
        return SalesForceAdapter.extractIdFromUrl(mainOpportunityPath);
    }

    public void setMainOpportunityPath(String mainOpportunityPath) {
        this.mainOpportunityPath = mainOpportunityPath;
    }

    public String getConsortiumLeadId() {
        return consortiumLeadId;
    }

    public void setConsortiumLeadId(String consortiumLeadId) {
        this.consortiumLeadId = consortiumLeadId;
    }

    @Override
    public String toString() {
        return "Member [id=" + id + ", name=" + name + ", publicDisplayName=" + publicDisplayName + ", websiteUrl=" + websiteUrl + ", researchCommunity="
                + researchCommunity + ", country=" + country + ", description=" + description + ", logoUrl=" + logoUrl + ", publicDisplayEmail=" + publicDisplayEmail
                + ", mainOpportunityPath=" + mainOpportunityPath + ", consortiumLeadId=" + consortiumLeadId + "]";
    }

}
