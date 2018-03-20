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
@Constraint(validatedBy = IntegerStringCrossFieldValidator.class)
@Documented
public @interface IntegerStringCrossField {

    String message() default "{integerStringCrossField}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int indexToIgnoreValidation();

    String theFieldToIndex();

    String theFieldToIgnoreValidation();

}
