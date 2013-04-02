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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.runner.RunWith;
import org.springframework.context.MessageSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:orcid-core-context.xml", "classpath:orcid-frontend-web-servlet.xml" })
public class AbstractConstraintValidator<T> {

    @Resource
    protected Validator validator;

    @Resource
    MessageSource messageSource;

    protected Map<String, String> retrieveErrorKeyAndMessage(Set<ConstraintViolation<T>> violations) {
        HashMap<String, String> allErrors = new HashMap<String, String>();
        for (ConstraintViolation<T> violation : violations) {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            allErrors.put(propertyPath, message);
        }
        return allErrors;
    }

    protected Set<String> retrieveErrorValuesOnly(Set<ConstraintViolation<T>> violations) {
        HashSet<String> allErrors = new HashSet<String>();
        for (ConstraintViolation<T> violation : violations) {
            String message = violation.getMessage();
            allErrors.add(message);
        }
        return allErrors;
    }

    protected String resolveFieldErrorMessage(BindingResult bindingResult, String fieldName) {
        return messageSource.getMessage(bindingResult.getFieldError(fieldName), Locale.ENGLISH);
    }

    protected List<String> resolveAllErrorMessages(BindingResult bindingResult) {
        List<String> messages = new ArrayList<String>();
        for (ObjectError error : bindingResult.getAllErrors()) {
            messages.add(messageSource.getMessage(error, Locale.ENGLISH));
        }
        return messages;
    }

}
