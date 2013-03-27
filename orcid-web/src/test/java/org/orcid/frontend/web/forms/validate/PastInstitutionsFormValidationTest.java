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
package org.orcid.frontend.web.forms.validate;

import org.junit.Test;
import org.orcid.frontend.web.forms.PastInstitutionsForm;

import javax.validation.ConstraintViolation;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PastInstitutionsFormValidationTest extends AbstractConstraintValidator<PastInstitutionsForm> {

    @Test
    public void validatePastInstMissingFields() throws Exception {
        PastInstitutionsForm form = new PastInstitutionsForm();
        Set<ConstraintViolation<PastInstitutionsForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);
        assertEquals(5, allErrorValues.size());
        String institutionName = allErrorValues.get("institutionName");
        assertEquals("Please enter the name of the institution.", institutionName);
        String city = allErrorValues.get("city");
        assertEquals("Please enter a city.", city);

        String endDate = allErrorValues.get("endDate");
        assertEquals("Please select an end date.", endDate);

        String startDate = allErrorValues.get("startDate");
        assertEquals("Please select a start date.", startDate);

        form.setInstitutionName("institutionName");
        form.setEndDate("2007");
        form.setCity("Lewes");
        form.setStartDate("2006");
        form.setCountry("England");

        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        assertEquals(0, allErrorValues.size());

    }

    @Test
    public void validatePastInstFieldsInvalidValues() throws Exception {

        PastInstitutionsForm form = new PastInstitutionsForm();
        form.setCity("city");
        form.setInstitutionName("institutionName");

        form.setStartDate("15/10/2011");
        form.setEndDate("15/10/2010");
        Set<ConstraintViolation<PastInstitutionsForm>> violations = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);
        assertEquals(3, allErrorValues.size());

        String startDate = allErrorValues.get("startDate");
        assertEquals("Please select a start date.", startDate);

        form.setStartDate("2011");
        form.setEndDate("2010");

        violations = validator.validate(form);

        Set<String> fieldLevelErrors = retrieveErrorValuesOnly(violations);
        assertTrue(fieldLevelErrors.contains("End date must be after the start date."));
        form.setStartDate("2011");
        form.setEndDate("2011"); // can be same date just not before
        form.setCountry("England");
        violations = validator.validate(form);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        assertEquals(0, allErrorValues.size());
    }
}
