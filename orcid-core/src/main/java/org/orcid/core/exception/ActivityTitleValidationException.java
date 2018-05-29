package org.orcid.core.exception;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class ActivityTitleValidationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ActivityTitleValidationException() {
    }

    public ActivityTitleValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityTitleValidationException(String message) {
        super(message);
    }

    public ActivityTitleValidationException(Throwable cause) {
        super(cause);
    }

}
