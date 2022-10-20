package org.orcid.core.exception;

import javax.servlet.http.HttpServletResponse;

public class OrcidForbiddenException extends OrcidApiException {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OrcidForbiddenException(String message) {
        super(message, HttpServletResponse.SC_FORBIDDEN);
    }

    public OrcidForbiddenException(String message, Throwable t) {
        super(message, HttpServletResponse.SC_FORBIDDEN, t);
    }
}
