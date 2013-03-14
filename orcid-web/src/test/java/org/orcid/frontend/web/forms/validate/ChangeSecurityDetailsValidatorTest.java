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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.ChangeSecurityQuestionForm;

public class ChangeSecurityDetailsValidatorTest extends AbstractConstraintValidator<ChangeSecurityQuestionForm> {

    Validator validator;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void allValuesPopulatedHappyPath() {
        ChangeSecurityQuestionForm form = new ChangeSecurityQuestionForm();
        form.setSecurityQuestionAnswer("My answer");
        form.setSecurityQuestionId(2);
        Set<ConstraintViolation<ChangeSecurityQuestionForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }

    @Test
    public void blankEntryPermittedWhenIndex0() {
        ChangeSecurityQuestionForm form = new ChangeSecurityQuestionForm();
        form.setSecurityQuestionAnswer("My answer");
        form.setSecurityQuestionId(0);
        Set<ConstraintViolation<ChangeSecurityQuestionForm>> violations = validator.validate(form);
        Set<String> fieldLevelErrors = retrieveErrorValuesOnly(violations);
        assertTrue(fieldLevelErrors.contains("Please provide an answer to your challenge question."));

        form.setSecurityQuestionAnswer("");
        form.setSecurityQuestionId(0);
        violations = validator.validate(form);
        fieldLevelErrors = retrieveErrorValuesOnly(violations);
        assertFalse(fieldLevelErrors.contains("Please provide an answer to your challenge question."));
    }

    @Test
    public void blankEntryNotPermittedWhenIndexNot0() {
        ChangeSecurityQuestionForm form = new ChangeSecurityQuestionForm();
        form.setSecurityQuestionAnswer("");
        form.setSecurityQuestionId(3);
        Set<ConstraintViolation<ChangeSecurityQuestionForm>> violations = validator.validate(form);
        Set<String> fieldLevelErrors = retrieveErrorValuesOnly(violations);
        assertTrue(fieldLevelErrors.contains("Please provide an answer to your challenge question."));

        form.setSecurityQuestionAnswer("An answer");
        form.setSecurityQuestionId(3);
        violations = validator.validate(form);
        fieldLevelErrors = retrieveErrorValuesOnly(violations);
        assertFalse(fieldLevelErrors.contains("Please provide an answer to your challenge question."));
    }

}
