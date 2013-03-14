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

import java.lang.reflect.InvocationTargetException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntegerStringCrossFieldValidator implements ConstraintValidator<IntegerStringCrossField, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntegerStringCrossFieldValidator.class);
    private int indexToPotentiallyIgnore;
    private String fieldToindex;
    private String fieldToConditionallyValidate;

    @Override
    public void initialize(final IntegerStringCrossField constraintAnnotation) {
        indexToPotentiallyIgnore = constraintAnnotation.indexToIgnoreValidation();
        fieldToConditionallyValidate = constraintAnnotation.theFieldToIgnoreValidation();
        fieldToindex = constraintAnnotation.theFieldToIndex();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {

        try {
            String indexFieldValue = BeanUtils.getProperty(value, fieldToindex);
            String validationField = BeanUtils.getProperty(value, fieldToConditionallyValidate);

            if (indexFieldValue.equals(String.valueOf(indexToPotentiallyIgnore))) {
                // it's valid if the value associated with the null index field is blank
                return StringUtils.isBlank(validationField);
            }

            return StringUtils.isNotBlank(validationField);
        } catch (NoSuchMethodException e) {
            LOGGER.error("Cannot find method for validation of data", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("The method you're using for validation is not accessible", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("The underlying method used for date validation threw an exception", e);
        }

        return false;
    }

}
