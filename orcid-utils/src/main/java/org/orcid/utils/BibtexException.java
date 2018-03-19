package org.orcid.utils;

public class BibtexException extends Exception {

    private static final long serialVersionUID = 1L;

    public BibtexException() {
    }

    public BibtexException(String message) {
        super(message);
    }

    public BibtexException(Throwable cause) {
        super(cause);
    }

    public BibtexException(String message, Throwable cause) {
        super(message, cause);
    }

}
