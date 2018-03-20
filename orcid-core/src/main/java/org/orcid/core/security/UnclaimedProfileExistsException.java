package org.orcid.core.security;

import org.springframework.security.authentication.BadCredentialsException;

/**
 * 
 * @author Will Simpson
 *
 */
public class UnclaimedProfileExistsException extends BadCredentialsException {

    private static final long serialVersionUID = 1L;

    public UnclaimedProfileExistsException(String msg) {
        super(msg);
    }

    public UnclaimedProfileExistsException(String msg, Throwable t) {
        super(msg, t);
    }

}
