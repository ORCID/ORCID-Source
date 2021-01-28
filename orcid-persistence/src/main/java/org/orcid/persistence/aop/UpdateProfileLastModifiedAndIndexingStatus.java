package org.orcid.persistence.aop;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author George Nash
 * 
 */
@Target( { java.lang.annotation.ElementType.METHOD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Documented
public @interface UpdateProfileLastModifiedAndIndexingStatus {

}
