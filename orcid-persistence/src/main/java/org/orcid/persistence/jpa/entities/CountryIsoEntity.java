package org.orcid.persistence.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "country_reference_data")
public class CountryIsoEntity extends BaseEntity<String> {

    /**
     * 
     */
    private static final long serialVersionUID = -4295048540595089114L;
    private String countryIsoCode;
    private String countryName;

    @Column(name = "country_iso_code")
    @Id
    public String getCountryIsoCode() {
        return countryIsoCode;
    }

    public void setCountryIsoCode(String countryIsoCode) {
        this.countryIsoCode = countryIsoCode;
    }

    @Column(name = "country_name")
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
