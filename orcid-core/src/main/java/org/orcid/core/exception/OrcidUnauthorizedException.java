package org.orcid.core.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public class OrcidUnauthorizedException extends OrcidApiException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OrcidUnauthorizedException(String message) {
        super(message, HttpServletResponse.SC_UNAUTHORIZED);
    }

    public OrcidUnauthorizedException(String message, Throwable t) {
        super(message, HttpServletResponse.SC_UNAUTHORIZED, t);
    }
}
