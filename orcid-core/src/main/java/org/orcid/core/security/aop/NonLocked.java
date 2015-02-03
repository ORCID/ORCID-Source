package org.orcid.core.security.aop;

import java.lang.annotation.Target;

/**
 * @author Angel Montenegro
 */
@Target( { java.lang.annotation.ElementType.METHOD })
public @interface NonLocked {

}
