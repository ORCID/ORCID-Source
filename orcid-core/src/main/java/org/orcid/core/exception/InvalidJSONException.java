package org.orcid.core.exception;

public class InvalidJSONException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public InvalidJSONException(String message) {
        super(message);
    }

    public InvalidJSONException(String message, Throwable cause) {
        super(message, cause);
    }

}
