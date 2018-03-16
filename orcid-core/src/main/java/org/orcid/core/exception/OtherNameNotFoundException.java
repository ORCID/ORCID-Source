package org.orcid.core.exception;

/**
 * @author Shobhit Tyagi
 */
public class OtherNameNotFoundException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OtherNameNotFoundException() {
    }

    public OtherNameNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public OtherNameNotFoundException(String message) {
        super(message);
    }

    public OtherNameNotFoundException(Throwable cause) {
        super(cause);
    }

}
