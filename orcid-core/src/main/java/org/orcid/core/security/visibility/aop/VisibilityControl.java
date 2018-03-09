package org.orcid.core.security.visibility.aop;

import org.orcid.jaxb.model.message.Visibility;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Declan Newman (declan) Date: 16/03/2012
 */
@Target( { java.lang.annotation.ElementType.METHOD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface VisibilityControl {

    Visibility[] visibilities() default { Visibility.PUBLIC };

}
