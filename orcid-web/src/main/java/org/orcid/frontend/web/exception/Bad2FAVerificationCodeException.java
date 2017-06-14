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

import org.springframework.security.authentication.BadCredentialsException;

public class Bad2FAVerificationCodeException extends BadCredentialsException {

    private static final long serialVersionUID = 1L;

    public Bad2FAVerificationCodeException() {
        super("Invalid verification code");
    }
    
}
