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

import static org.junit.Assert.assertTrue;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.junit.Test;
import org.orcid.frontend.web.forms.EmailAddressForm;

public class ResetPasswordFormValidatorTest extends AbstractConstraintValidator<EmailAddressForm> {

    @Test
    public void testEmailEmpty() {

        EmailAddressForm form = new EmailAddressForm();
        Set<ConstraintViolation<EmailAddressForm>> violations = validator.validate(form);
        Set<String> allErrorValues = retrieveErrorValuesOnly(violations);
        String errorMessage = "Please enter your email address";
        assertTrue(allErrorValues.size() == 1 && allErrorValues.contains(errorMessage));
    }

    @Test
    public void testEmailInvalidFormat() {
        EmailAddressForm form = new EmailAddressForm();
        form.setUserEmailAddress("jim_invalid_email@co.");
        Set<ConstraintViolation<EmailAddressForm>> violations = validator.validate(form);
        Set<String> allErrorValues = retrieveErrorValuesOnly(violations);
        String errorMessage = "not a well-formed email address";
        assertTrue(allErrorValues.size() == 1 && allErrorValues.contains(errorMessage));
    }

}
