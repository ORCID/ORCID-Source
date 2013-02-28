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
package org.orcid.frontend.web.forms;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.OrcidProfile;

import java.util.List;
import java.util.Map;

/**
 * @author Will Simpson
 */
public class PrimaryInstitutionForm {

    private CommonInstitutionFormDetails data = new CommonInstitutionFormDetails();

    public PrimaryInstitutionForm() {
        super();
    }

    public PrimaryInstitutionForm(OrcidProfile orcidProfile) {

    }

    public Affiliation getPrimaryInstitution() {
       return null;
    }

    public boolean isInstitutionNamePublic() {
        return data.isInstitutionNamePublic();
    }

    public void setInstitutionNamePublic(boolean institutionNamePublic) {
        this.data.setInstitutionNamePublic(institutionNamePublic);
    }

    @NotEmpty
    public String getInstitutionName() {
        return data.getInstitutionName();
    }

    public void setInstitutionName(String institutionName) {
        this.data.setInstitutionName(institutionName);
    }

    public boolean isAddressPublic() {
        return data.isAddressPublic();
    }

    public void setAddressPublic(boolean isAddressPublic) {
        this.data.setAddressPublic(isAddressPublic);
    }

    public List<String> getDepartments() {
        return data.getSelectedDepartments();
    }

    public void setDepartments(List<String> departments) {
        this.data.setSelectedDepartments(departments);
    }

    public Map<String, String> getDepartmentsMap() {
        return data.getDepartments();
    }

    @NotEmpty
    public String getAddressLine1() {
        return data.getAddressLine1();
    }

    public void setAddressLine1(String addressLine1) {
        this.data.setAddressLine1(addressLine1);
    }

    public String getAddressLine2() {
        return data.getAddressLine2();
    }

    public void setAddressLine2(String addressLine2) {
        this.data.setAddressLine2(addressLine2);
    }

    @NotEmpty
    public String getCity() {
        return data.getCity();
    }

    public void setCity(String city) {
        this.data.setCity(city);
    }

    public String getState() {
        return data.getState();
    }

    public void setState(String state) {
        this.data.setState(state);
    }

    public String getZipCode() {
        return data.getZipCode();
    }

    public void setZipCode(String zipCode) {
        this.data.setZipCode(zipCode);
    }

    public String getStartDate() {
        return data.getStartDate();
    }

    public void setStartDate(String startDate) {
        this.data.setStartDate(startDate);
    }

    @NotEmpty
    public String getRegistrationRole() {
        return data.getRegistrationRole();
    }

    public void setRegistrationRole(String registrationRole) {
        this.data.setRegistrationRole(registrationRole);
    }

    @NotEmpty
    public String getCountry() {
        return data.getCountry();
    }

    public void setCountry(String country) {
        data.setCountry(country);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
