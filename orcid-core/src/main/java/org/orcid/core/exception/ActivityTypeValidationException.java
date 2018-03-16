package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class ActivityTypeValidationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public ActivityTypeValidationException() {
    }

    public ActivityTypeValidationException(Map<String, String> params) {
        super(params);
    }
    
    public ActivityTypeValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActivityTypeValidationException(String message) {
        super(message);
    }

    public ActivityTypeValidationException(Throwable cause) {
        super(cause);
    }

}
