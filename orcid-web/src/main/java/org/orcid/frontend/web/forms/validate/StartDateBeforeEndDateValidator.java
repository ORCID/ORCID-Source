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

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 */

public class StartDateBeforeEndDateValidator implements ConstraintValidator<DateCompare, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StartDateBeforeEndDateValidator.class);

    private String startDate;
    private String endDate;
    private SimpleDateFormat dateFormat;
    private String[] boundFields;

    @Override
    public void initialize(final DateCompare constraintAnnotation) {
        startDate = constraintAnnotation.startDate();
        endDate = constraintAnnotation.endDate();
        dateFormat = new SimpleDateFormat(constraintAnnotation.dateFormat());
        boundFields = constraintAnnotation.boundFields();
    }

    /**
     * Method to validate the date fields of a form Rules to validate If the
     * date format is missing - the form is automatically invalid. If the dates
     * are missing, the form is **VALID** because we can't assess. If dates
     * cannot be parsed, the form is invalid If the end date is before the start
     * date, the form is invalid.
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        try {
            String startDateRepresentation = BeanUtils.getProperty(value, startDate);
            String endDateRepresentation = BeanUtils.getProperty(value, endDate);
            if (StringUtils.isBlank(startDateRepresentation) && StringUtils.isBlank(endDateRepresentation)) {
                // not our concern if the fields are both missing
                return true;
            }

            Date parsedStartDate = dateFormat.parse(startDateRepresentation);
            Date parsedEndDate = dateFormat.parse(endDateRepresentation);

            if (parsedEndDate.before(parsedStartDate) && !parsedEndDate.equals(parsedStartDate)) {
                for (String binding : boundFields) {
                    context.buildConstraintViolationWithTemplate("Start date must be before end date").addNode(binding).addConstraintViolation();
                }
            } else {
                return true;
            }

        } catch (NoSuchMethodException e) {
            LOGGER.error("Cannot find method for validation of dates", e);
        } catch (ParseException e) {
            LOGGER.error("Invalid date format passed in as argument. Please check the date validation annotation", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("The method you're using for validation is not accessible", e);
        } catch (InvocationTargetException e) {
            LOGGER.error("The underlying method used for date validation threw an exception", e);
        }

        return false;

    }

}
