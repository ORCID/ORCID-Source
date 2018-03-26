package org.orcid.core.security;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * @author Shobhit Tyagi
 */
public class SocialLoginException extends BadCredentialsException {

    private static final long serialVersionUID = 1L;

    public SocialLoginException(String msg) {
        super(msg);
    }

    public SocialLoginException(String msg, Throwable t) {
        super(msg, t);
    }

}
