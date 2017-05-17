package org.orcid.frontend.spring;

import org.springframework.security.authentication.BadCredentialsException;

public class Bad2FAVerificationCodeException extends BadCredentialsException {

    private static final long serialVersionUID = 1L;

    public Bad2FAVerificationCodeException() {
        super("Invalid verification code");
    }
    
}
