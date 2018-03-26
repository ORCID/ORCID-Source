package org.orcid.core.exception;

import javax.ws.rs.core.Response;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public class OrcidBadRequestException extends OrcidApiException {

    /**
     * 
     */
    private static final long serialVersionUID = 7814378059339926519L;

    public OrcidBadRequestException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }

    public OrcidBadRequestException(String message, Throwable t) {
        super(message, Response.Status.BAD_REQUEST, t);
    }

}
