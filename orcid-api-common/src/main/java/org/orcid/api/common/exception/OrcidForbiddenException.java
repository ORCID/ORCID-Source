package org.orcid.api.common.exception;

import javax.ws.rs.core.Response;

import org.orcid.core.exception.OrcidApiException;

public class OrcidForbiddenException extends OrcidApiException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OrcidForbiddenException(String message) {
        super(message, Response.Status.FORBIDDEN);
    }

    public OrcidForbiddenException(String message, Throwable t) {
        super(message, Response.Status.FORBIDDEN, t);
    }
}
