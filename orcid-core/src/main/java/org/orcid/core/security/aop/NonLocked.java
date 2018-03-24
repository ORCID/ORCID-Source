package org.orcid.core.security.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author Angel Montenegro
 */
@Target( { java.lang.annotation.ElementType.METHOD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface NonLocked {

}
