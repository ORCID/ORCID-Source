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

import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonInstitutionFormDetails {

    private String dateFormat = "dd/MM/yyyy"; // can set this to be overriden if
    // needed in future

    private String orcid;
    private boolean institutionNamePublic;
    private String institutionName;
    private boolean isAddressPublic;
    private Map<String, String> departments;
    private List<String> selectedDepartments;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private Boolean formSelected = false;

    private String registrationRole;

    private List<String> allDates;
    private String startDate;
    private String endDate;
    private String country;

    private static final String COMMA_SPACE_DELIMITER = ", ";

    public CommonInstitutionFormDetails() {
        departments = new HashMap<String, String>();
        selectedDepartments = new ArrayList<String>();
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public boolean isInstitutionNamePublic() {
        return institutionNamePublic;
    }

    public void setInstitutionNamePublic(boolean institutionNamePublic) {
        this.institutionNamePublic = institutionNamePublic;
    }

    public String getInstitutionName() {
        return institutionName;
    }

    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }

    public boolean isAddressPublic() {
        return isAddressPublic;
    }

    public void setAddressPublic(boolean isAddressPublic) {
        this.isAddressPublic = isAddressPublic;
    }

    public Map<String, String> getDepartments() {
        return departments;
    }

    public void setDepartments(Map<String, String> departments) {
        this.departments = departments;
    }

    public List<String> getSelectedDepartments() {
        return selectedDepartments;
    }

    public void setSelectedDepartments(List<String> selectedDepartments) {
        this.selectedDepartments = selectedDepartments;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getAllDates() {
        return allDates;
    }

    public void setAllDates(List<String> allDates) {
        this.allDates = allDates;
    }

    public String getRegistrationRole() {
        return registrationRole;
    }

    public void setRegistrationRole(String registrationRole) {
        this.registrationRole = registrationRole;
    }

    public String getFormattedDepartments() {
        if (departments != null && !departments.isEmpty()) {
            return StringUtils.collectionToDelimitedString(departments.values(), COMMA_SPACE_DELIMITER);
        }

        return "";
    }

    public void setFormattedDepartments(String formattedDepartments) {
        String[] departments = StringUtils.delimitedListToStringArray(formattedDepartments, COMMA_SPACE_DELIMITER);
        if (departments != null) {
            this.setSelectedDepartments(Arrays.asList(departments));
        }

    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFormattedAddress() {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.hasText(addressLine1) ? addressLine1 + COMMA_SPACE_DELIMITER : "");
        sb.append(StringUtils.hasText(addressLine2) ? addressLine2 + COMMA_SPACE_DELIMITER : "");
        sb.append(StringUtils.hasText(city) ? city + COMMA_SPACE_DELIMITER : "");
        sb.append(StringUtils.hasText(state) ? state + COMMA_SPACE_DELIMITER : "");
        sb.append(StringUtils.hasText(zipCode) ? zipCode + COMMA_SPACE_DELIMITER : "");
        sb.append(StringUtils.hasText(country) ? country : "");
        return sb.toString();

    }

    public Boolean getFormSelected() {
        return formSelected;
    }

    public void setFormSelected(Boolean formSelected) {
        this.formSelected = formSelected;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

}