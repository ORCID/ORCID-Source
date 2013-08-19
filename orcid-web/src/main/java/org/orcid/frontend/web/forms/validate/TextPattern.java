package org.orcid.frontend.web.forms.validate;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.Documented;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;

/**
 * The annotated Text must match the following regular expression.
 * The regular expression follows the Java regular expression conventions
 * see {@link java.util.regex.Pattern}.
 *
 * Accepts Text objects. <code>null</code> elements are considered valid.
 *
 * @author It is almost an identical copy of javax.validation.constraints.Pattern by Emmanuel Bernard
 */
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {TextPatternValidator.class})
public @interface TextPattern {
    /**
     * @return The regular expression to match.
     */
    String regexp();

    /**
     * @return Array of <code>Flag</code>s considered when resolving the regular expression.
     */
    Flag[] flags() default {};

    /**
     * @return The error message template.
     */
    String message() default "{javax.validation.constraints.Pattern.message}";

    /**
     * @return The groups the constraint belongs to.
     */
    Class<?>[] groups() default { };

    /**
     * @return The payload associated to the constraint
     */
    Class<? extends Payload>[] payload() default {};
    
    /**
     * Defines several <code>@Pattern</code> annotations on the same element
     * @see Pattern
     *
     * @author Emmanuel Bernard
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {
            Pattern[] value();
    }
}
