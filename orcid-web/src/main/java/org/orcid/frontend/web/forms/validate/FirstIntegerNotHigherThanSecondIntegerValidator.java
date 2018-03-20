package org.orcid.frontend.web.forms.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

public class FirstIntegerNotHigherThanSecondIntegerValidator implements ConstraintValidator<IntegerCompare, Object> {

    private String firstFieldName;
    private String secondFieldName;

    @Override
    public void initialize(final IntegerCompare constraintAnnotation) {
        firstFieldName = constraintAnnotation.firstNumber();
        secondFieldName = constraintAnnotation.secondNumber();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {

            String firstNumberField = BeanUtils.getProperty(value, firstFieldName);
            String secondNumberField = BeanUtils.getProperty(value, secondFieldName);

            if (!StringUtils.isNumeric(firstNumberField) || !StringUtils.isNumeric(secondNumberField)) {
                return true;
            }

            Integer firstNumValue = Integer.valueOf(firstNumberField);
            Integer secondNumValue = Integer.valueOf(secondNumberField);
            return secondNumValue.compareTo(firstNumValue) >= 0;

        } catch (final Exception ignore) {
            ignore.printStackTrace();

        }
        return false;
    }
}