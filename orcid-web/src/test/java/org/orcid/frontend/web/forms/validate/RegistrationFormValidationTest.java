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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.orcid.frontend.web.forms.RegistrationForm;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class RegistrationFormValidationTest extends AbstractConstraintValidator<RegistrationForm> {

    @Resource(name = "validator")
    LocalValidatorFactoryBean localValidatorFactoryBean;

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
        RegistrationForm registrationForm = new RegistrationForm();
        BindingResult bindingResult = new BeanPropertyBindingResult(registrationForm, "registrationForm");
        localValidatorFactoryBean.validate(registrationForm, bindingResult);
        assertEquals("Should be 5 errors", 5, bindingResult.getErrorCount());

        String givenNamesMissing = resolveFieldErrorMessage(bindingResult, "givenNames");
        String email = resolveFieldErrorMessage(bindingResult, "email");
        String password = resolveFieldErrorMessage(bindingResult, "password");
        String retypedPassword = resolveFieldErrorMessage(bindingResult, "confirmedPassword");
        String termsAndConditions = resolveFieldErrorMessage(bindingResult, "acceptTermsAndConditions");

        assertEquals("Please enter your first name.", givenNamesMissing);
        assertEquals("Please enter your e-mail address.", email);
        assertEquals("Please enter a password.", password);
        assertEquals("Passwords must be 8 or more characters and contain at least 1 number and at least 1 alpha character or symbol.", retypedPassword);
        assertEquals("You must accept the terms and conditions to register.", termsAndConditions);
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

        BindingResult bindingResult = new BeanPropertyBindingResult(registrationForm, "registrationForm");
        localValidatorFactoryBean.validate(registrationForm, bindingResult);
        List<String> allErrorValues = resolveAllErrorMessages(bindingResult);

        assertTrue(allErrorValues.contains("The password and confirmed password must match."));
        assertTrue(allErrorValues.contains("The email and confirmed email must match."));

        registrationForm.setConfirmedEmail(email1);
        registrationForm.setConfirmedPassword(password);

        bindingResult = new BeanPropertyBindingResult(registrationForm, "registrationForm");
        localValidatorFactoryBean.validate(registrationForm, bindingResult);
        allErrorValues = resolveAllErrorMessages(bindingResult);
        assertFalse(allErrorValues.contains("The password and confirmed password must match."));
        assertFalse(allErrorValues.contains("The email and confirmed email must match."));
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
        BindingResult bindingResult = new BeanPropertyBindingResult(form, "registrationForm");
        localValidatorFactoryBean.validate(form, bindingResult);
        List<String> allErrorValues = resolveAllErrorMessages(bindingResult);
        assertEquals("Should be a single error", 1, allErrorValues.size());
        assertNotNull(bindingResult.getFieldError("referRegOrcid"));
    }

}
