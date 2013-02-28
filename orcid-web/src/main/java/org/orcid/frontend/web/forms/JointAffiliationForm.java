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
import org.orcid.frontend.web.forms.validate.ValidJointAffiliation;
import org.orcid.jaxb.model.message.Address;
import org.orcid.jaxb.model.message.Affiliation;
import org.orcid.jaxb.model.message.OrcidBio;
import org.orcid.jaxb.model.message.OrcidProfile;
import org.orcid.jaxb.model.message.Visibility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Will Simpson
 */
@ValidJointAffiliation
public class JointAffiliationForm {

    private CommonInstitutionFormDetails commonInstitutionFormDetails;

    private boolean registrationRolePublic;

    public JointAffiliationForm() {
        commonInstitutionFormDetails = new CommonInstitutionFormDetails();
    }

    public boolean isInstitutionNamePublic() {
        return commonInstitutionFormDetails.isInstitutionNamePublic();
    }

    public void setInstitutionNamePublic(boolean institutionNamePublic) {
        this.commonInstitutionFormDetails.setInstitutionNamePublic(institutionNamePublic);
    }

    public String getInstitutionName() {
        return commonInstitutionFormDetails.getInstitutionName();
    }

    public void setInstitutionName(String institutionName) {
        this.commonInstitutionFormDetails.setInstitutionName(institutionName);
    }

    public boolean isAddressPublic() {
        return commonInstitutionFormDetails.isAddressPublic();
    }

    public void setAddressPublic(boolean isAddressPublic) {
        this.commonInstitutionFormDetails.setAddressPublic(isAddressPublic);
    }

    public List<String> getDepartments() {
        return commonInstitutionFormDetails.getSelectedDepartments();
    }

    public void setDepartments(List<String> departments) {
        this.commonInstitutionFormDetails.setSelectedDepartments(departments);
    }

    public Map<String, String> getDepartmentsMap() {
        return commonInstitutionFormDetails.getDepartments();
    }

    public String getAddressLine1() {
        return commonInstitutionFormDetails.getAddressLine1();
    }

    public void setAddressLine1(String addressLine1) {
        this.commonInstitutionFormDetails.setAddressLine1(addressLine1);
    }

    public String getAddressLine2() {
        return commonInstitutionFormDetails.getAddressLine2();
    }

    public void setAddressLine2(String addressLine2) {
        this.commonInstitutionFormDetails.setAddressLine2(addressLine2);
    }

    public String getCity() {
        return commonInstitutionFormDetails.getCity();
    }

    public void setCity(String city) {
        this.commonInstitutionFormDetails.setCity(city);
    }

    public String getState() {
        return commonInstitutionFormDetails.getState();
    }

    public void setState(String state) {
        this.commonInstitutionFormDetails.setState(state);
    }

    public String getZipCode() {
        return commonInstitutionFormDetails.getZipCode();
    }

    public void setZipCode(String zipCode) {
        this.commonInstitutionFormDetails.setZipCode(zipCode);
    }

    public String getCountry() {
        return commonInstitutionFormDetails.getCountry();
    }

    public void setCountry(String country) {
        this.commonInstitutionFormDetails.setCountry(country);
    }

    public String getStartDate() {
        return commonInstitutionFormDetails.getStartDate();
    }

    public void setStartDate(String startDate) {
        this.commonInstitutionFormDetails.setStartDate(startDate);
    }

    public String getRegistrationRole() {
        return commonInstitutionFormDetails.getRegistrationRole();
    }

    public void setRegistrationRole(String registrationRole) {
        this.commonInstitutionFormDetails.setRegistrationRole(registrationRole);
    }

    public boolean isRegistrationRolePublic() {
        return registrationRolePublic;
    }

    public void setRegistrationRolePublic(boolean registrationRolePublic) {
        this.registrationRolePublic = registrationRolePublic;
    }

    public JointAffiliationForm(OrcidProfile orcidProfile) {
        OrcidBio orcidBio = orcidProfile != null ? orcidProfile.getOrcidBio() : null;
        commonInstitutionFormDetails = new CommonInstitutionFormDetails();
        if (orcidBio != null && orcidBio.getAffiliations() != null && !orcidBio.getAffiliations().isEmpty()) {
            populateFromOrcidBio(orcidBio.getAffiliations().get(0));
        }
    }

    private void populateFromOrcidBio(Affiliation affiliateInstitution) {
        setInstitutionName(affiliateInstitution != null ? affiliateInstitution.getAffiliationName() : null);
        setInstitutionNamePublic(Visibility.PUBLIC.equals(affiliateInstitution.getVisibility()));
    }

    private void populateAddress(Address address) {
        if (address != null) {
            setCountry(address.getCountry() != null ? address.getCountry().getContent() : null);
        }
    }

    public List<Affiliation> getAffiliateInstitutions() {
        List<Affiliation> affiliateInstitutions = new ArrayList<Affiliation>();

        Affiliation affiliation = new Affiliation();
        affiliateInstitutions.add(affiliation);


        return affiliateInstitutions;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
