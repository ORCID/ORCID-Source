package org.orcid.frontend.web.exception;

import org.springframework.security.authentication.BadCredentialsException;

public class Bad2FARecoveryCodeException extends BadCredentialsException {

    private static final long serialVersionUID = 1L;

    public Bad2FARecoveryCodeException() {
        super("Invalid recovery code");
    }
    
}
