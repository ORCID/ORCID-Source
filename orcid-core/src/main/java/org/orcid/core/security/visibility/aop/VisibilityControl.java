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
package org.orcid.core.security.visibility.aop;

import org.orcid.jaxb.model.message.Visibility;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 2011-2012 ORCID
 * 
 * @author Declan Newman (declan) Date: 16/03/2012
 */
@Target({ java.lang.annotation.ElementType.METHOD })
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface VisibilityControl {

    Visibility[] visibilities() default { Visibility.PUBLIC };

    boolean removeAttributes() default true;
}
