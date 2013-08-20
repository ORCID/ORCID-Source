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

import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.ValidatorFactory;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.orcid.frontend.web.forms.ChangePersonalInfoForm;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

public class ChangePersonalInfoValidatorTest extends AbstractConstraintValidator<ChangePersonalInfoForm> {

    @Resource(name = "validator")
    LocalValidatorFactoryBean localValidatorFactoryBean;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void allMandatoryValuesPopulated() {

        ChangePersonalInfoForm form = new ChangePersonalInfoForm();
        form.setFirstName("firstName");
        form.setLastName("lastName");

        Set<ConstraintViolation<ChangePersonalInfoForm>> errors = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(errors);
        assertEquals("Should be 0 errors", 0, allErrorValues.size());

    }

    @Test
    public void noMandatoryValuesPopulated() {
        ChangePersonalInfoForm form = new ChangePersonalInfoForm();

        Set<ConstraintViolation<ChangePersonalInfoForm>> errors = validator.validate(form);
        Map<String, String> allErrorValues = retrieveErrorKeyAndMessage(errors);
        assertEquals("Should be 1 error1", 1, allErrorValues.size());
        String givenNamesMissing = allErrorValues.get("firstName");

        assertEquals("Please enter your first name.", givenNamesMissing);
    }

    @Test
    public void lengthGreaterThan100NotAcceptedForBio() throws Exception {
        String fiveThousandAndOneChars = StringUtils.repeat("a", 5001);
        assertTrue(fiveThousandAndOneChars.length() == 5001);

        ChangePersonalInfoForm form = new ChangePersonalInfoForm();
        form.setFirstName("firstName");
        form.setLastName("lastName");
        form.setBiography(fiveThousandAndOneChars);

        BindingResult bindingResult = new BeanPropertyBindingResult(form, "changePersonalInfoForm");

        localValidatorFactoryBean.validate(form, bindingResult);
        String tooLong = resolveFieldErrorMessage(bindingResult, "biography");
        assertEquals("Should be 1 error", 1, bindingResult.getErrorCount());
        assertEquals("The maximum length for biography is 5000 characters, including line breaks", tooLong);

        String fiveThousandChars = StringUtils.repeat("z", 5000);
        form.setBiography(fiveThousandChars);
        bindingResult = new BeanPropertyBindingResult(form, "changePersonalInfoForm");
        localValidatorFactoryBean.validate(form, bindingResult);
        assertEquals("Should be valid", 0, bindingResult.getErrorCount());

    }
}
