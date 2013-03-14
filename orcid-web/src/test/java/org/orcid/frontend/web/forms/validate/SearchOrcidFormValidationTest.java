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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.SearchOrcidBioForm;

public class SearchOrcidFormValidationTest extends AbstractConstraintValidator<SearchOrcidBioForm> {

    Validator validator;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void allValuesPopulatedHappyPath() {
        SearchOrcidBioForm form = new SearchOrcidBioForm();
        form.setOrcid("1234");
        Set<ConstraintViolation<SearchOrcidBioForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }

    @Test
    public void mandatoryFieldsMissing() {
        SearchOrcidBioForm form = new SearchOrcidBioForm();
        Set<ConstraintViolation<SearchOrcidBioForm>> violations = validator.validate(form);
        Set<String> allErrorValues = retrieveErrorValuesOnly(violations);
        String errorMessage = "Please enter an ORCID ID to search for or alternatively any of the name fields - at least one of given name, family name, or institution name must have 2 characters";
        assertTrue(allErrorValues.size() == 1 && allErrorValues.contains(errorMessage));
    }

}
