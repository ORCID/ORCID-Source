package org.orcid.core.exception;

import javax.servlet.http.HttpServletResponse;

public class SearchStartParameterLimitExceededException extends OrcidApiException {

    private static final long serialVersionUID = 7814378059339926519L;
    
    public SearchStartParameterLimitExceededException(String message) {
        super(message, HttpServletResponse.SC_BAD_REQUEST);
    }

    public SearchStartParameterLimitExceededException(String message, Throwable t) {
        super(message, HttpServletResponse.SC_BAD_REQUEST, t);
    }
    
}
