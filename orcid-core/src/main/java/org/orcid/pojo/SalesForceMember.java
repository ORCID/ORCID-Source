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

import java.io.Serializable;
import java.net.URL;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceMember implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private URL websiteUrl;
    private String researchCommunity;
    private String country;
    private String parentName;
    private String description;
    private URL logoUrl;
    private String consortiumLeadId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public URL getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(URL websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getResearchCommunity() {
        return researchCommunity;
    }

    public void setResearchCommunity(String researchCommunity) {
        this.researchCommunity = researchCommunity;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
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

    public String getConsortiumLeadId() {
        return consortiumLeadId;
    }

    public void setConsortiumLeadId(String consortiumLeadId) {
        this.consortiumLeadId = consortiumLeadId;
    }

    @Override
    public String toString() {
        return "SalesForceMember [id=" + id + ", name=" + name + ", websiteUrl=" + websiteUrl + ", researchCommunity=" + researchCommunity + ", country=" + country
                + ", parentName=" + parentName + ", description=" + description + ", logoUrl=" + logoUrl + ", consortiumLeadId=" + consortiumLeadId + "]";
    }

}
