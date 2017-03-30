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

import java.util.HashMap;
import java.util.Map;

import org.orcid.persistence.jpa.entities.CountryIsoEntity;

public class OrgDisambiguated {

    public String value;
    public String city;
    public String region;
    public String country;
    public String orgType;
    public String countryForDisplay;
    public String sourceId;
    public String sourceType;
    public String disambiguatedAffiliationIdentifier;
    
    public String getDisambiguatedAffiliationIdentifier() {
        return disambiguatedAffiliationIdentifier;
    }
    public void setDisambiguatedAffiliationIdentifier(String disambiguatedAffiliationIdentifier) {
        this.disambiguatedAffiliationIdentifier = disambiguatedAffiliationIdentifier;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getRegion() {
        return region;
    }
    public void setRegion(String region) {
        this.region = region;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getOrgType() {
        return orgType;
    }
    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }
    public String getCountryForDisplay() {
        return CountryIsoEntity.class.getName() + '.' + this.getCountry();
    }
    public String getSourceId() {
        return sourceId;
    }
    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public String getSourceType() {
        return sourceType;
    }
    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }
    
    public Map<String,String> toMap(){
        HashMap<String,String> datum = new HashMap<String,String>();
        datum.put("value", this.getValue());
        datum.put("city", this.getCity());
        datum.put("region", this.getRegion());
        datum.put("country", this.getCountry());
        datum.put("orgType", this.getOrgType());
        datum.put("sourceId", this.getSourceId());
        datum.put("sourceType", this.getSourceType());
        datum.put("countryForDisplay", this.getCountryForDisplay());
        datum.put("disambiguatedAffiliationIdentifier", this.getCountryForDisplay());
        return datum;
    }
    
    public String toString(){
        return this.toMap().toString();
    }
}
