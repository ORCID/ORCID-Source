package org.orcid.api.common.exception;

import javax.ws.rs.core.Response;

import org.orcid.core.exception.OrcidApiException;

public class SearchStartParameterLimitExceededException extends OrcidApiException {

    private static final long serialVersionUID = 7814378059339926519L;
    
    public SearchStartParameterLimitExceededException(String message) {
        super(message, Response.Status.BAD_REQUEST);
    }

    public SearchStartParameterLimitExceededException(String message, Throwable t) {
        super(message, Response.Status.BAD_REQUEST, t);
    }
    
}
