package org.orcid.core.security;

import org.springframework.security.core.AuthenticationException;

/**
 * 
 * @author Will Simpson
 * 
 */
public class MethodNotAllowedException extends AuthenticationException {

    private static final long serialVersionUID = 1L;

    public MethodNotAllowedException(String msg) {
        super(msg);
    }

    public MethodNotAllowedException(String msg, Throwable t) {
        super(msg, t);
    }

}
