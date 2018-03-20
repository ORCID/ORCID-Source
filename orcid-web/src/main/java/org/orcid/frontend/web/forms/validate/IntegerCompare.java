package org.orcid.frontend.web.forms.validate;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Target( { TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@Constraint(validatedBy = FirstIntegerNotHigherThanSecondIntegerValidator.class)
@Documented
public @interface IntegerCompare {
    String message() default "{constraints.fieldmatch}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * @return The first field
     */
    String firstNumber();

    /**
     * @return The second field
     */
    String secondNumber();

    /**
     * Defines several <code>@FieldMatch</code> annotations on the same element
     * 
     * @see FieldMatch
     */
    @Target( { TYPE, ANNOTATION_TYPE })
    @Retention(RUNTIME)
    @Documented
    @interface List {
        IntegerCompare[] value();
    }
}