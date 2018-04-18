package org.orcid.core.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 
 * @author Angel Montenegro
 *
 */
public class InvalidUserTypeException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public InvalidUserTypeException(String msg) {
        super(msg);
    }

    public InvalidUserTypeException(String msg, Throwable t) {
        super(msg, t);
    }

}
