/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.controllers;

import org.orcid.core.oauth.OrcidProfileUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class BaseControllerUtil {

    public OrcidProfileUserDetails getCurrentUser(SecurityContext context) {
        if (context == null) 
            return null;
        Authentication authentication = context.getAuthentication();
        if ((authentication instanceof UsernamePasswordAuthenticationToken || authentication instanceof PreAuthenticatedAuthenticationToken)
                && authentication.getDetails() instanceof OrcidProfileUserDetails) {
            return ((OrcidProfileUserDetails) authentication.getDetails());
        } else {
            return null;
        }
    }

}
