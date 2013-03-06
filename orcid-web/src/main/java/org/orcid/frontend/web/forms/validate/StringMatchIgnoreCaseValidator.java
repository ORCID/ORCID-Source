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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;

/**
 * 
 * passit-web-commons - Jan 10, 2012 - FieldMatchValidator
 * 
 * @author Stolen from <a href=
 *         "http://stackoverflow.com/questions/1972933/cross-field-validation-with-hibernate-validator-jsr-303"
 *         >stackoverflow.com</a>
 **/

public class StringMatchIgnoreCaseValidator implements ConstraintValidator<StringMatchIgnoreCase, Object> {
    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final StringMatchIgnoreCase constraintAnnotation) {
        firstFieldName = constraintAnnotation.first();
        secondFieldName = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            final Object firstObj = BeanUtils.getProperty(value, firstFieldName);
            final Object secondObj = BeanUtils.getProperty(value, secondFieldName);
            if (firstObj instanceof String && secondObj instanceof String) {
                String firstString = (String) firstObj;
                String secondString = (String) secondObj;
                return firstString.equalsIgnoreCase(secondString);
            }
            return firstObj == null && secondObj == null;
        } catch (final Exception ignore) {
            // ignore
        }
        return true;
    }
}