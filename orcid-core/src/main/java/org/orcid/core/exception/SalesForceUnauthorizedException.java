package org.orcid.core.exception;

/**
 * 
 * @author Will Simpson
 *
 */
public class SalesForceUnauthorizedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SalesForceUnauthorizedException() {
    }

    public SalesForceUnauthorizedException(String message) {
        super(message);
    }

    public SalesForceUnauthorizedException(Throwable cause) {
        super(cause);
    }

    public SalesForceUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    public SalesForceUnauthorizedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
