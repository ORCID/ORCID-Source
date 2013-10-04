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

import org.apache.commons.lang3.StringUtils;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.AffiliationAddress;
import org.orcid.jaxb.model.message.AffiliationCity;
import org.orcid.jaxb.model.message.AffiliationCountry;
import org.orcid.jaxb.model.message.AffiliationRegion;
import org.orcid.jaxb.model.message.AffiliationType;
import org.orcid.jaxb.model.message.Iso3166Country;

public class AffiliationForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text putCode;

    private Visibility visibility;

    private Text affiliationName;

    private Text city;

    private Text region;

    private Text country;

    private Text departmentName;

    private Text affiliationType;

    private String affiliationTypeForDisplay;

    private Date startDate;

    private Date endDate;

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getPutCode() {
        return putCode;
    }

    public void setPutCode(Text putCode) {
        this.putCode = putCode;
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

    public Text getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(Text departmentName) {
        this.departmentName = departmentName;
    }

    public Text getAffiliationType() {
        return affiliationType;
    }

    public void setAffiliationType(Text affiliationType) {
        this.affiliationType = affiliationType;
    }

    public String getAffiliationTypeForDisplay() {
        return affiliationTypeForDisplay;
    }

    public void setAffiliationTypeForDisplay(String affiliationTypeForDisplay) {
        this.affiliationTypeForDisplay = affiliationTypeForDisplay;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public static AffiliationForm valueOf(Affiliation affiliation) {
        AffiliationForm form = new AffiliationForm();
        form.setPutCode(Text.valueOf(affiliation.getPutCode()));
        form.setVisibility(Visibility.valueOf(affiliation.getVisibility()));
        form.setAffiliationName(Text.valueOf(affiliation.getAffiliationName()));
        AffiliationAddress address = affiliation.getAffiliationAddress();
        form.setCity(Text.valueOf(address.getAffiliationCity().getContent()));
        if (address.getAffiliationRegion() != null) {
            form.setRegion(Text.valueOf(address.getAffiliationRegion().getContent()));
        }
        form.setCountry(Text.valueOf(address.getAffiliationCountry().getValue().value()));
        if (affiliation.getDepartmentName() != null) {
            form.setDepartmentName(Text.valueOf(affiliation.getDepartmentName()));
        }
        if (affiliation.getAffiliationType() != null) {
            form.setAffiliationType(Text.valueOf(affiliation.getAffiliationType().value()));
        }
        if (affiliation.getStartDate() != null) {
            form.setStartDate(Date.valueOf(affiliation.getStartDate()));
        }
        if (affiliation.getEndDate() != null) {
            form.setEndDate(Date.valueOf(affiliation.getEndDate()));
        }
        return form;
    }

    public org.orcid.jaxb.model.message.Affiliation toAffiliation() {
        org.orcid.jaxb.model.message.Affiliation affiliation = new org.orcid.jaxb.model.message.Affiliation();
        if (putCode != null && StringUtils.isNotBlank(putCode.getValue())) {
            affiliation.setPutCode(putCode.getValue());
        }
        affiliation.setVisibility(visibility.getVisibility());
        affiliation.setAffiliationName(affiliationName.getValue());
        AffiliationAddress affiliationAddress = new AffiliationAddress();
        affiliation.setAffiliationAddress(affiliationAddress);
        affiliationAddress.setAffiliationCity(new AffiliationCity(city.getValue()));
        if (region != null && StringUtils.isNotBlank(region.getValue())) {
            affiliationAddress.setAffiliationRegion(new AffiliationRegion(region.getValue()));
        }
        affiliationAddress.setAffiliationCountry(new AffiliationCountry(Iso3166Country.fromValue(country.getValue())));
        if (departmentName != null) {
            affiliation.setDepartmentName(departmentName.getValue());
        }
        if (affiliationType != null && StringUtils.isNotBlank(affiliationType.getValue())) {
            affiliation.setAffiliationType(AffiliationType.fromValue(affiliationType.getValue()));
        }
        if (startDate != null) {
            affiliation.setStartDate(startDate.toFuzzyDate());
        }
        if (endDate != null) {
            affiliation.setEndDate(endDate.toFuzzyDate());
        }
        return affiliation;
    }

}
