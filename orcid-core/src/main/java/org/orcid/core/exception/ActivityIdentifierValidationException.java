package org.orcid.core.exception;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class ActivityIdentifierValidationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ActivityIdentifierValidationException() {
    }

    public ActivityIdentifierValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityIdentifierValidationException(String message) {
        super(message);
    }

    public ActivityIdentifierValidationException(Throwable cause) {
        super(cause);
    }

}
