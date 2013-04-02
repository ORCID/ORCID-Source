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
import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

/**
 * Validation annotation to validate that 2 fields have the same value. An array
 * of fields and their matching confirmation fields can be supplied.
 * 
 * Example, compare 1 pair of fields:
 * 
 * @FieldMatch(first = "email", second = "confirmEmail", message =
 *                   "The email fields must match")
 * 
 *                   Example, compare more than 1 pair of fields:
 * @FieldMatch.List({
 * @FieldMatch(first = "email", second = "confirmEmail", message =
 *                   "The email fields must match"),
 * @FieldMatch(first = "email", second = "confirmEmail", message =
 *                   "The email fields must match")})
 * 
 * @author Stolen from <a href=
 *         "http://stackoverflow.com/questions/1972933/cross-field-validation-with-hibernate-validator-jsr-303"
 *         >stackoverflow.com</a>
 */
@Target( { TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = StringMatchIgnoreCaseValidator.class)
@Documented
public @interface StringMatchIgnoreCase {
    String message() default "{constraints.stringmatchignorecase}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return The first field
     */
    String first();

    /**
     * @return The second field
     */
    String second();

    /**
     * Defines several <code>@StringMatchIgnoreCase</code> annotations on the
     * same element
     * 
     * @see StringMatchIgnoreCase
     */
    @Target( { TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        StringMatchIgnoreCase[] value();
    }
}