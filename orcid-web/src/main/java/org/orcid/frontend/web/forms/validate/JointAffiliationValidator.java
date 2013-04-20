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

import org.apache.commons.lang.StringUtils;
import org.orcid.frontend.web.forms.JointAffiliationForm;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.io.Serializable;

/**
 * Copyright 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 27/02/2012
 */
public class JointAffiliationValidator implements ConstraintValidator<ValidJointAffiliation, JointAffiliationForm>, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = -9044335810242847290L;

    /**
     * Initialize the validator in preparation for isValid calls. The constraint
     * annotation for a given constraint declaration is passed.
     * <p/>
     * This method is guaranteed to be called before any use of this instance
     * for validation.
     * 
     * @param constraintAnnotation
     *            annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidJointAffiliation constraintAnnotation) {
        // To change body of implemented methods use File | Settings | File
        // Templates.
    }

    /**
     * Implement the validation logic. The state of <code>value</code> must not
     * be altered.
     * <p/>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     * 
     * @param value
     *            object to validate
     * @param context
     *            context in which the constraint is evaluated
     * @return false if <code>value</code> does not pass the constraint
     */
    @Override
    public boolean isValid(JointAffiliationForm value, ConstraintValidatorContext context) {

        if (StringUtils.isBlank(value.getAddressLine1()) && StringUtils.isBlank(value.getAddressLine2()) && StringUtils.isBlank(value.getCity())
                && StringUtils.isBlank(value.getCountry()) && StringUtils.isBlank(value.getInstitutionName()) && StringUtils.isBlank(value.getState())
                && (value.getDepartments() == null || value.getDepartments().isEmpty()) && StringUtils.isBlank(value.getZipCode())
                && StringUtils.isBlank(value.getStartDate())) {
            return true;
        } else {
            return validateRequiredFields(value, context);
        }
    }

    private boolean validateRequiredFields(JointAffiliationForm value, ConstraintValidatorContext context) {
        boolean valid = true;
        if (StringUtils.isBlank(value.getInstitutionName())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Joint affiliation institution name is required").addConstraintViolation();
            valid = false;
        }
        if (StringUtils.isBlank(value.getAddressLine1())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Joint affiliation address line 1 is a required field").addConstraintViolation();
            valid = false;
        }
        if (StringUtils.isBlank(value.getCity())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Joint affiliation city is a required field").addConstraintViolation();
            valid = false;
        }
        if (StringUtils.isBlank(value.getCountry())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Joint affiliation country is a required field").addConstraintViolation();
            valid = false;
        }
        if (StringUtils.isBlank(value.getRegistrationRole())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Joint affiliation registration role is a required field").addConstraintViolation();
            valid = false;
        }
        return valid;
    }

}
