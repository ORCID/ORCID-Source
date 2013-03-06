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

import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.RegistrationForm;

public class RegistrationFormValidationTest extends AbstractConstraintValidator<RegistrationForm> {

    Validator validator;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void allMandatoryValuesPopulated() {
        RegistrationForm form = new RegistrationForm();
        form.setGivenNames("Will");
        form.setFamilyName("Simpson");
        form.setEmail("will@semantico.com");
        form.setConfirmedEmail("will@semantico.com");
        form.setPassword("p4$$w0rd");
        form.setConfirmedPassword("p4$$w0rd");
        form.setAcceptTermsAndConditions(true);
        Set<ConstraintViolation<RegistrationForm>> errors = validator.validate(form);
        assertEquals("Should be no errors", 0, errors.size());
    }

    @Test
    public void noMandatoryValuesPopulated() {
        RegistrationForm form = new RegistrationForm();
        Set<ConstraintViolation<RegistrationForm>> errors = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(errors);
        assertEquals("Should be 6 errors", 5, errors.size());

        String givenNamesMissing = allErrorValues.get("givenNames");
        String email = allErrorValues.get("email");
        String password = allErrorValues.get("password");
        String retypedPassword = allErrorValues.get("confirmedPassword");
        String termsAndConditions = allErrorValues.get("acceptTermsAndConditions");

        assertEquals("Please enter your first name.", givenNamesMissing);
        assertEquals("Please enter your e-mail address.", email);
        assertEquals("Please enter a password", password);
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", retypedPassword);
        assertEquals("You must accept the terms and conditions.", termsAndConditions);

    }

    @Test
    public void crossFieldFormValidation() {
        RegistrationForm registrationForm = new RegistrationForm();
        String password = "a Password";
        String confirmedPassword = "which doesn't match";

        String email1 = "email1@email.com";
        String email2 = "email2@email.com";

        registrationForm.setEmail(email1);
        registrationForm.setConfirmedEmail(email2);

        registrationForm.setPassword(password);
        registrationForm.setConfirmedPassword(confirmedPassword);

        Set<ConstraintViolation<RegistrationForm>> violations = validator.validate(registrationForm);
        Set<String> allErrorValues = retrieveErrorValuesOnly(violations);

        assertTrue(allErrorValues.contains("The password and confirmed password must match"));
        assertTrue(allErrorValues.contains("The email and confirmed email must match"));

        registrationForm.setConfirmedEmail(email1);
        registrationForm.setConfirmedPassword(password);

        violations = validator.validate(registrationForm);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertFalse(allErrorValues.contains("The password and confirmed password must match"));
        assertFalse(allErrorValues.contains("The email and confirmed email must match"));
    }

    @Test
    public void spamValidationFailed() {
        RegistrationForm form = new RegistrationForm();
        form.setGivenNames("Will");
        form.setFamilyName("Simpson");
        form.setEmail("will@semantico.com");
        form.setConfirmedEmail("will@semantico.com");
        form.setReferRegOrcid("Fell for honeypot");
        form.setPassword("p4$$w0rd");
        form.setConfirmedPassword("p4$$w0rd");
        form.setAcceptTermsAndConditions(true);
        Set<ConstraintViolation<RegistrationForm>> errors = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(errors);
        assertEquals("Should be a single error", 1, allErrorValues.size());
        String spamErrorMessage = allErrorValues.get("referRegOrcid");
        assertEquals("There was an error validating your input", spamErrorMessage);
    }

}
