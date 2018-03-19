package org.orcid.core.exception;

import javax.ws.rs.core.Response;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public class OrcidUnauthorizedException extends OrcidApiException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OrcidUnauthorizedException(String message) {
        super(message, Response.Status.UNAUTHORIZED);
    }

    public OrcidUnauthorizedException(String message, Throwable t) {
        super(message, Response.Status.UNAUTHORIZED, t);
    }
}
