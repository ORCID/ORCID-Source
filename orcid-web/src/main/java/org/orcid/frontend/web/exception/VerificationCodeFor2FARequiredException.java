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
package org.orcid.frontend.web.exception;

import org.springframework.security.core.AuthenticationException;

public class VerificationCodeFor2FARequiredException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public VerificationCodeFor2FARequiredException() {
        super("Verfication code for 2FA required");
    }

}
