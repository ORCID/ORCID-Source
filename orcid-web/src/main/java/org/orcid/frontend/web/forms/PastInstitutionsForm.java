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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;

import org.apache.commons.lang.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.orcid.frontend.web.forms.validate.DateCompare;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.Country;
import org.orcid.jaxb.model.message.EndDate;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.StartDate;
import org.orcid.utils.DateUtils;
import org.springframework.util.AutoPopulatingList;

/**
 * Form to represent the past institutions elements on the manage pages
 */
@DateCompare.List({ @DateCompare(startDate = "startDate", endDate = "endDate", boundFields = { "startDate", "endDate" }, dateFormat = "yyyy",
        message = "End date must be after the start date.") })
public class PastInstitutionsForm {

    private CommonInstitutionFormDetails currentForm;
    private static final String DATE_FORMAT_REGEX = "\\d{4}";

    @Valid
    public CommonInstitutionFormDetails getCurrentForm() {
        return currentForm;
    }

    private List<CommonInstitutionFormDetails> allFormSummaries;

    public void setAllFormSummaries(List<CommonInstitutionFormDetails> allFormSummaries) {
        this.allFormSummaries = allFormSummaries;
    }

    public PastInstitutionsForm() {
        super();
        currentForm = new CommonInstitutionFormDetails();
        currentForm.setDateFormat("yyyy");
        this.allFormSummaries = new AutoPopulatingList<CommonInstitutionFormDetails>(CommonInstitutionFormDetails.class);
    }

    public PastInstitutionsForm(OrcidProfile orcidProfile) {
        currentForm = new CommonInstitutionFormDetails();
        currentForm.setDateFormat("yyyy");
        currentForm.setDepartments(new TreeMap<String, String>());
        currentForm.setOrcid(currentForm.getOrcid());
        allFormSummaries = new ArrayList<CommonInstitutionFormDetails>();
        OrcidBio orcidBio = orcidProfile.getOrcidBio();
        List<Affiliation> affiliations = orcidBio.getAffiliations();
        if (affiliations != null && !affiliations.isEmpty()) {
            //TODO: Copy the visibilities over from the form
//            currentForm.setInstitutionNamePublic(Visibility.PUBLIC.equals(pastInstitutions.getVisibility()));
//            for (PastInstitution pastInstitution : pastInstitutions.getPastInstitution()) {
//                allFormSummaries.add(getCommonInstitutionDetails(pastInstitution));
//            }
        }

    }

    @Valid
    @Pattern(regexp = DATE_FORMAT_REGEX, message = "Please select a start date.")
    @NotEmpty(message = "Please select a start date.")
    public String getStartDate() {
        return currentForm.getStartDate();
    }

    public void setStartDate(String startDate) {
        this.currentForm.setStartDate(startDate);
    }

    @Valid
    @Pattern(regexp = DATE_FORMAT_REGEX, message = "Please select an end date.")
    @NotEmpty(message = "Please select an end date.")
    public String getEndDate() {
        return currentForm.getEndDate();
    }

    public void setEndDate(String endDate) {
        this.currentForm.setEndDate(endDate);
    }

    public boolean isInstitutionNamePublic() {
        return currentForm.isInstitutionNamePublic();
    }

    public void setInstitutionNamePublic(boolean institutionNamePublic) {
        currentForm.setInstitutionNamePublic(institutionNamePublic);
    }

    public void setInstitutionName(String institutionName) {
        currentForm.setInstitutionName(institutionName);
    }

    public void setAddressPublic(boolean isAddressPublic) {
        currentForm.setAddressPublic(isAddressPublic);
    }

    public String getAddressLine1() {
        return currentForm.getAddressLine1();
    }

    public void setAddressLine1(String addressLine1) {
        currentForm.setAddressLine1(addressLine1);
    }

    public String getAddressLine2() {
        return currentForm.getAddressLine2();
    }

    public void setAddressLine2(String addressLine2) {
        currentForm.setAddressLine2(addressLine2);
    }

    @NotBlank(message = "Please enter a city.")
    public String getCity() {
        return currentForm.getCity();
    }

    public void setCity(String city) {
        currentForm.setCity(city);
    }

    public String getZipCode() {
        return currentForm.getZipCode();
    }

    public Map<String, String> getDepartmentsMap() {
        return this.currentForm.getDepartments();
    }

    @NotBlank(message = "Please enter the name of the institution.")
    public String getInstitutionName() {
        return currentForm.getInstitutionName();
    }

    public void setDepartmentsMap(Map<String, String> departments) {
        this.currentForm.setDepartments(departments);
    }

    public List<String> getDepartments() {
        return currentForm.getSelectedDepartments();
    }

    public void setDepartments(List<String> selectedDepartments) {
        currentForm.setSelectedDepartments(selectedDepartments);
    }

    public String getState() {
        return currentForm.getState();
    }

    public void setState(String state) {
        currentForm.setState(state);
    }

    public void setZipCode(String zipCode) {
        currentForm.setZipCode(zipCode);
    }

    public void setCountry(String country) {
        currentForm.setCountry(country);
    }

    @NotBlank(message = "Please enter a country.")
    public String getCountry() {
        return currentForm.getCountry();
    }

    public List<CommonInstitutionFormDetails> getAllFormSummaries() {
        return allFormSummaries;
    }

    public void addFormSummary() {
        allFormSummaries.add(currentForm);
        currentForm = new CommonInstitutionFormDetails();
    }

    public List<Affiliation> getAffiliationsFromSelected() {

        List<Affiliation> affiliations = new ArrayList<Affiliation>();
        for (CommonInstitutionFormDetails commonInstitutionFormDetails : allFormSummaries) {
            if (commonInstitutionFormDetails.getFormSelected()) {
                affiliations.add(getAffiliation(commonInstitutionFormDetails));
            }
        }

        return affiliations;
    }

    public String getRegistrationRole() {
        return currentForm.getRegistrationRole();
    }

    public void setRegistrationRole(String registrationRole) {
        currentForm.setRegistrationRole(registrationRole);
    }

    public void removeLastSummary() {

        if (allFormSummaries != null && !allFormSummaries.isEmpty()) {
            currentForm = allFormSummaries.remove(allFormSummaries.size() - 1);
        }

    }

    public String getDateFormat() {
        return currentForm.getDateFormat();
    }

    public void setDateFormat(String dateFormat) {
        currentForm.setDateFormat(dateFormat);
    }

    public boolean isAddressPublic() {
        return currentForm.isAddressPublic();
    }

    public OrcidProfile getOrcidProfile() {
        OrcidProfile profile = new OrcidProfile();
        Affiliation affiliation = getAffiliation(currentForm);

        OrcidBio orcidBio = new OrcidBio();
        orcidBio.getAffiliations().add(affiliation);
        profile.setOrcidBio(orcidBio);
        return profile;
    }

    private void populateCommonInstitutionFormDetailsAddress(CommonInstitutionFormDetails commonInstitutionFormDetails, Address address) {
        if (address != null) {           
          
            String countryName = address.getCountry() == null ? null : address.getCountry().getContent();
            commonInstitutionFormDetails.setCountry(countryName);
        }
    }

    private Affiliation getAffiliation(CommonInstitutionFormDetails formSummary) {
        Affiliation affiliation = new Affiliation();

        if (StringUtils.isNotBlank(formSummary.getRegistrationRole())) {
            affiliation.setRoleTitle(formSummary.getRegistrationRole());
        }

        affiliation.setStartDate(new StartDate(DateUtils.convertToXMLGregorianCalendar(formSummary.getStartDate())));
        affiliation.setEndDate(new EndDate(DateUtils.convertToXMLGregorianCalendar(formSummary.getEndDate())));

        Address address = getAddress(formSummary);

        affiliation.setAddress(address);

        populateDepartmentNames(formSummary, affiliation);

        return affiliation;
    }

    private Address getAddress(CommonInstitutionFormDetails formSummary) {
        Address address = new Address();      
        address.setCountry(new Country(formSummary.getCountry()));
        return address;
    }

    private void populateDepartmentNames(CommonInstitutionFormDetails formSummary, Affiliation affiliation) {
        if (formSummary.getSelectedDepartments() != null && !formSummary.getSelectedDepartments().isEmpty()) {
            Map<String, String> departments = formSummary.getDepartments();
            if (departments != null && !departments.isEmpty()) {
                Iterator<String> iterator = departments.keySet().iterator();
                affiliation.setDepartmentName(iterator.next());
            }
        }
    }

}
