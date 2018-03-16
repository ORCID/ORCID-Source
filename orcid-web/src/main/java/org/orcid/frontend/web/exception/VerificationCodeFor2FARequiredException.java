package org.orcid.frontend.web.exception;

import org.springframework.security.core.AuthenticationException;

public class VerificationCodeFor2FARequiredException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public VerificationCodeFor2FARequiredException() {
        super("Verfication code for 2FA required");
    }

}
