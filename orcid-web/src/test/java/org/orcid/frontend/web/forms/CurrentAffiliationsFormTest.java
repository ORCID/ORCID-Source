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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.orcid.jaxb.model.message.OrcidProfile;

/**
 * 
 * @author Will Simpson
 * 
 */
public class CurrentAffiliationsFormTest {

    @Test
    public void testGetOrcidProfile() throws IOException {
        CurrentAffiliationsForm form = new CurrentAffiliationsForm();
        form.setOrcid("4444-4444-4444-4446");

        PrimaryInstitutionForm primaryInstitutionForm = form.getPrimaryInstitutionForm();
        primaryInstitutionForm.setInstitutionName("Semantico");
        primaryInstitutionForm.setAddressLine1("Lees House");
        primaryInstitutionForm.setAddressLine2("21-23, Dyke Road");
        primaryInstitutionForm.setCity("Brighton");
        primaryInstitutionForm.setState("East Sussex");
        primaryInstitutionForm.setCountry("United Kingdom");
        primaryInstitutionForm.setZipCode("BN1 3FE");
        primaryInstitutionForm.setStartDate("2008-04-16");
        primaryInstitutionForm.setRegistrationRole("Researcher");
        List<String> primaryInstitutionDepartments = new ArrayList<String>();
        primaryInstitutionDepartments.add("Stan Higgins Trust");
        primaryInstitutionDepartments.add("Edward Bass Institute");
        primaryInstitutionDepartments.add("Stan Higgins Trust");
        primaryInstitutionForm.setDepartments(primaryInstitutionDepartments);

        JointAffiliationForm jointAffiliationForm = form.getJointAffiliationForm();
        jointAffiliationForm.setInstitutionName("University College London");
        jointAffiliationForm.setAddressLine1("Gower Street");
        jointAffiliationForm.setCity("London");
        jointAffiliationForm.setCountry("United Kingdom");
        jointAffiliationForm.setZipCode("WCR1E 6BT");

        List<String> jointAffiliationDepartments = new ArrayList<String>();
        jointAffiliationForm.setDepartments(jointAffiliationDepartments);
        // show that duplicate enries aren't added
        jointAffiliationDepartments.add("The Slade School of Fine Art");
        jointAffiliationDepartments.add("The Slade School of Fine Art");
        jointAffiliationForm.setStartDate("2006-02-23");
        jointAffiliationForm.setRegistrationRole("Other");

        PastInstitutionsForm pastInstitutionsForm = form.getPastInstitutionsForm();
        List<String> pastAffiliationDepartments = new ArrayList<String>();

        pastInstitutionsForm.setInstitutionName("Dr Zaius Research Facility");
        pastInstitutionsForm.setAddressLine1("Planet Street");
        pastInstitutionsForm.setCity("Unknown");
        pastInstitutionsForm.setCountry("USA");
        pastInstitutionsForm.setZipCode("WCR1E 6BT");

        // show that duplicate enries aren't added
        pastAffiliationDepartments.add("The Peter Griffin School");
        pastAffiliationDepartments.add("Dr Zaius Research Facility");
        pastAffiliationDepartments.add("The Peter Griffin School");
        pastInstitutionsForm.setStartDate("2006-02-27");
        pastInstitutionsForm.setEndDate("2007-02-27");
        pastInstitutionsForm.setDepartments(pastAffiliationDepartments);
        pastInstitutionsForm.addFormSummary();

        jointAffiliationForm.setRegistrationRole("Other");
        List<SponsorIdPair> sponsorIds = new ArrayList<SponsorIdPair>();
        form.setSponsorIds(sponsorIds);
        sponsorIds.add(new SponsorIdPair("8888-8888-8888-8880", "123"));

        OrcidProfile profile = form.getOrcidProfile();

        assertNotNull(profile);
        // current_affiliations_orcid_profile.xml file has been masively doctored to temporarily pass tests
        String expected = IOUtils.toString(getClass().getResourceAsStream("current_affiliations_orcid_profile.xml"));
        String actual = profile.toString();
        assertEquals(expected, actual);
    }

}
