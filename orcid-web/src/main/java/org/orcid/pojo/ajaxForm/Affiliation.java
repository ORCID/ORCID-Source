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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.message.AffiliationAddress;
import org.orcid.jaxb.model.message.AffiliationCity;
import org.orcid.jaxb.model.message.AffiliationCountry;
import org.orcid.jaxb.model.message.Iso3166Country;

public class Affiliation implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Visibility visibility;

    private Text affiliationName;

    private Text city;

    private Text region;

    private Text country;

    private Text department;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Text getAffiliationName() {
        return affiliationName;
    }

    public void setAffiliationName(Text affiliationName) {
        this.affiliationName = affiliationName;
    }

    public Text getCity() {
        return city;
    }

    public void setCity(Text city) {
        this.city = city;
    }

    public Text getRegion() {
        return region;
    }

    public void setRegion(Text region) {
        this.region = region;
    }

    public Text getCountry() {
        return country;
    }

    public void setCountry(Text country) {
        this.country = country;
    }

    public Text getDepartment() {
        return department;
    }

    public void setDepartment(Text department) {
        this.department = department;
    }

    public org.orcid.jaxb.model.message.Affiliation toAffiliation() {
        org.orcid.jaxb.model.message.Affiliation affiliation = new org.orcid.jaxb.model.message.Affiliation();
        affiliation.setVisibility(visibility.getVisibility());
        affiliation.setAffiliationName(affiliationName.getValue());
        AffiliationAddress affiliationAddress = new AffiliationAddress();
        affiliation.setAffiliationAddress(affiliationAddress);
        affiliationAddress.setAffiliationCity(new AffiliationCity(city.getValue()));
        affiliationAddress.setAffiliationCountry(new AffiliationCountry(Iso3166Country.fromValue(country.getValue())));
        if (department != null) {
            affiliation.setDepartmentName(department.getValue());
        }
        return affiliation;
    }

}
