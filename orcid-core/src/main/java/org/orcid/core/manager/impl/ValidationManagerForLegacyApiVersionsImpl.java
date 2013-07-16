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
package org.orcid.core.manager.impl;

import org.apache.commons.lang3.StringUtils;
import org.orcid.jaxb.model.message.OrcidMessage;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ValidationManagerForLegacyApiVersionsImpl extends ValidationManagerImpl {

    @Override
    protected void doSchemaValidation(OrcidMessage orcidMessage) {
        // Hack to support legacy API versions
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) requestAttributes;
            String contentType = servletRequestAttributes.getRequest().getContentType();
            if (!StringUtils.containsIgnoreCase(contentType, "json")) {
                super.doSchemaValidation(orcidMessage);
            }
        } else {
            super.doSchemaValidation(orcidMessage);
        }
    }

}
