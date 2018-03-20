package org.orcid.core.exception;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidSearchException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrcidSearchException() {
    }

    public OrcidSearchException(String message) {
        super(message);
    }

    public OrcidSearchException(Throwable cause) {
        super(cause);
    }

    public OrcidSearchException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrcidSearchException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
