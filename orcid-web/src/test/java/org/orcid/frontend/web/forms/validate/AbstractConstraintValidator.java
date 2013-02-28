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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.Before;

public class AbstractConstraintValidator<T> {

    protected Validator validator;

    @Before
    public void resetValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

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

}
