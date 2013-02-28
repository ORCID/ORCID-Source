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

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = StartDateBeforeEndDateValidator.class)
@Documented
public @interface DateCompare {
    String message() default "{constraints.datecompare}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return The first field
     */
    String startDate();

    /**
     * @return The second field
     */
    String endDate();

    /**
     * @return The date format both fields will use to convert into a date to do
     *         the comparison
     */
    String dateFormat() default "MM/dd/yyyy";

    /**
     * The array of fields that should be bound to the form. This enables us to
     * target areas of the form for the user to correct in the event of
     * validation failure.
     * 
     * @return the array of bound fields. Default is an empty array.
     */
    String[] boundFields();

    /**
     * Defines several <code>@DateCompare</code> annotations on the same element
     * 
     * @see DateCompare
     */
    @Target({ TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        DateCompare[] value();
    }
}