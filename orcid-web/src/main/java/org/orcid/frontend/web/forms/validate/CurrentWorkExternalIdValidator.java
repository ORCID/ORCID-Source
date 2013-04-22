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

import org.orcid.frontend.web.forms.CurrentWorkExternalId;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Iterator;
import java.util.List;

/**
 * 2011-2012 - Semantico Ltd.
 *
 * @author Declan Newman (declan)
 *         Date: 12/10/2012
 */
public class CurrentWorkExternalIdValidator implements ConstraintValidator<ValidCurrentWorkExternalId, List<CurrentWorkExternalId>> {
    /**
     * Initialize the validator in preparation for isValid calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p/>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     *
     * @param constraintAnnotation
     *         annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidCurrentWorkExternalId constraintAnnotation) {
        // Nothing to see here
    }

    /**
     * Implement the validation logic.
     * The state of <code>value</code> must not be altered.
     * <p/>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value
     *         object to validate
     * @param context
     *         context in which the constraint is evaluated
     * @return false if <code>value</code> does not pass the constraint
     */
    @Override
    public boolean isValid(List<CurrentWorkExternalId> value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        } else {
            Iterator<CurrentWorkExternalId> iterator = value.iterator();
            while (iterator.hasNext()) {
                CurrentWorkExternalId next = iterator.next();
                if (next.isBlank()) {
                    iterator.remove();
                } else {
                    if (!next.isValid()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
