package org.orcid.frontend.web.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 
 * @author Will Simpson
 * 
 */
public class SwitchUserAuthenticationException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public SwitchUserAuthenticationException(String msg) {
        super(msg);
    }

    public SwitchUserAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

}
