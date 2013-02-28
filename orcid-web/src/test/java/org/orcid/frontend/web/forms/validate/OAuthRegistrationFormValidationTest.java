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
import org.orcid.frontend.web.forms.OAuthRegistrationForm;

public class OAuthRegistrationFormValidationTest extends AbstractConstraintValidator<OAuthRegistrationForm> {

    Validator validator;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void allValuesPopulatedHappyPath() {
        OAuthRegistrationForm oAuthRegistrationForm = new OAuthRegistrationForm();
        oAuthRegistrationForm.setGivenNames("Will");
        oAuthRegistrationForm.setFamilyName("Simpson");
        oAuthRegistrationForm.setEmail("will@semantico.com");
        oAuthRegistrationForm.setConfirmedEmail("will@semantico.com");
        oAuthRegistrationForm.setPassword("p4$$w0rd");
        oAuthRegistrationForm.setConfirmedPassword("p4$$w0rd");
        oAuthRegistrationForm.setTermsAccepted(true);
        Set<ConstraintViolation<OAuthRegistrationForm>> errors = validator.validate(oAuthRegistrationForm);
        assertEquals("Should be no errors", 0, errors.size());

    }

    @Test
    public void testRegistrationFormMissingData() {
        OAuthRegistrationForm oAuthRegistrationForm = new OAuthRegistrationForm();
        Set<ConstraintViolation<OAuthRegistrationForm>> violations = validator.validate(oAuthRegistrationForm);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);
        assertEquals(5, allErrorValues.size());
        String givenNameMissingMsg = allErrorValues.get("givenNames");
        String familyName = allErrorValues.get("familyName");
        String email = allErrorValues.get("email");
        String password = allErrorValues.get("password");
        String termsAccepted = allErrorValues.get("termsAccepted");
        assertEquals("Please enter a first name.", givenNameMissingMsg);
        assertEquals("Please enter your e-mail address (example: jdoe@institution.edu).", email);
        assertEquals("Please enter a last name.", familyName);
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", password);
        assertEquals("You must accept the terms and conditions in order to register", termsAccepted);

    }

    @Test
    public void testDataIncorrectFormat() throws Exception {
        OAuthRegistrationForm oAuthRegistrationForm = new OAuthRegistrationForm();
        oAuthRegistrationForm.setEmail("john/bad.email.address");

        Set<ConstraintViolation<OAuthRegistrationForm>> violations = validator.validate(oAuthRegistrationForm);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(violations);
        String email = allErrorValues.get("email");
        assertEquals("Please enter your email address in the proper format (example: jdoe@institution.edu).", email);

        oAuthRegistrationForm.setPassword("Doesn'tconformtoregex");
        violations = validator.validate(oAuthRegistrationForm);
        allErrorValues = retrieveErrorKeyAndMessage(violations);
        String passwordRegexError = allErrorValues.get("password");
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol", passwordRegexError);

    }

    @Test
    public void testCrossFieldValidation() throws Exception {
        OAuthRegistrationForm oAuthRegistrationForm = new OAuthRegistrationForm();
        String password = "a Password";
        String confirmedPassword = "which doesn't match";

        String email1 = "email1@email.com";
        String email2 = "email2@email.com";

        oAuthRegistrationForm.setEmail(email1);
        oAuthRegistrationForm.setConfirmedEmail(email2);

        oAuthRegistrationForm.setPassword(password);
        oAuthRegistrationForm.setConfirmedPassword(confirmedPassword);

        Set<ConstraintViolation<OAuthRegistrationForm>> violations = validator.validate(oAuthRegistrationForm);
        Set<String> allErrorValues = retrieveErrorValuesOnly(violations);

        assertTrue(allErrorValues.contains("The password and confirmed password must match"));
        assertTrue(allErrorValues.contains("The email and confirmed email must match"));

        oAuthRegistrationForm.setConfirmedEmail(email1);
        oAuthRegistrationForm.setConfirmedPassword(password);

        violations = validator.validate(oAuthRegistrationForm);
        allErrorValues = retrieveErrorValuesOnly(violations);
        assertFalse(allErrorValues.contains("The password and confirmed password must match"));
        assertFalse(allErrorValues.contains("The email and confirmed email must match"));

    }

}
