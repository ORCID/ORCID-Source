package org.orcid.core.exception;

public class InvalidTokenException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public InvalidTokenException(String s) {
        super(s);
    }
}
