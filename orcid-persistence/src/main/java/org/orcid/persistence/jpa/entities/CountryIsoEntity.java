/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.persistence.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;


@Entity
@Table(name="country_reference_data")
public class CountryIsoEntity extends BaseEntity<String> {
            
        
    /**
     * 
     */
    private static final long serialVersionUID = -4295048540595089114L;   
    private String countryIsoCode;
    private String countryName;
    
    
    @Column(name="country_iso_code") 
    @Id
    public String getCountryIsoCode() {
        return countryIsoCode;
    }
    
    public void setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
    }
    
    
    @Column(name="country_name")
    public String getCountryName() {
        return countryName;
    }
    
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    @Override
    @Transient
    public String getId() {
        return countryIsoCode;
    } 
    
    
    
    
        
    
}
