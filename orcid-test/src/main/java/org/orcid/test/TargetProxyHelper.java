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
package org.orcid.test;

import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;

public class TargetProxyHelper {

    @SuppressWarnings({ "unchecked" })
    public static <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {
        while ((AopUtils.isJdkDynamicProxy(proxy))) {
            return (T) getTargetObject(((Advised) proxy).getTargetSource().getTarget(), targetClass);
        }
        return (T) proxy; // expected to be cglib proxy then, which is simply a
                          // specialized class
    }
}
