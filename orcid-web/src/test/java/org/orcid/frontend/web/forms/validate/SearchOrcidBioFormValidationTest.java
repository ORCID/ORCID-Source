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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.orcid.frontend.web.forms.SearchOrcidBioForm;

public class SearchOrcidBioFormValidationTest extends AbstractConstraintValidator<SearchOrcidBioForm> {

    private static final String MISSING_DATA_MESSAGE = "Please enter an ORCID ID to search for or alternatively any of the name fields - at least one of given name, family name, or institution name must have 2 characters";

    @Test
    public void testEmptyValidatedOnceOrcidProvided() throws Exception {
        SearchOrcidBioForm form = new SearchOrcidBioForm();
        Set<ConstraintViolation<SearchOrcidBioForm>> violations = validator.validate(form);
        Set<String> allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 1);
        form.setOrcid("1234");
        violations = validator.validate(form);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 0);

    }

    @Test
    public void testAtLeastOneFieldPopulatedValidatesForm() throws Exception {
        SearchOrcidBioForm form = new SearchOrcidBioForm();
        Set<ConstraintViolation<SearchOrcidBioForm>> violations = validator.validate(form);
        Set<String> allErrorValues = retrieveErrorValuesOnly(violations);
        assertNotNull(allErrorValues.size() == 1);
        assertTrue(allErrorValues.contains(MISSING_DATA_MESSAGE));
        form.setFamilyName("familyName");
        violations = validator.validate(form);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 0);
    }

    @Test
    public void testFormFieldsCanBeEmptyButNoLessThan2Chars() throws Exception {

        SearchOrcidBioForm form = new SearchOrcidBioForm();
        resetFormFields(form);
        Set<ConstraintViolation<SearchOrcidBioForm>> violations = validator.validate(form);
        Set<String> allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 1);
        assertTrue(allErrorValues.contains(MISSING_DATA_MESSAGE));

        form.setFamilyName("fn");
        violations = validator.validate(form);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 0);
        resetFormFields(form);
        violations = validator.validate(form);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 1);
        assertTrue(allErrorValues.contains(MISSING_DATA_MESSAGE));

        form.setGivenName("gn");
        violations = validator.validate(form);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 0);
        resetFormFields(form);
        violations = validator.validate(form);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 1);
        assertTrue(allErrorValues.contains(MISSING_DATA_MESSAGE));

        form.setInstitutionName("in");
        violations = validator.validate(form);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertTrue(allErrorValues.size() == 0);
    }

    private void resetFormFields(SearchOrcidBioForm form) {
        form.setFamilyName("f");
        form.setGivenName("g");
        form.setInstitutionName("i");
    }

}
