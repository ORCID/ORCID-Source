package org.orcid.frontend.web.exception;

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * 
 * @author Will Simpson
 *
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FeatureDisabledException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public FeatureDisabledException() {
    }

    public FeatureDisabledException(String message) {
        super(message);
    }

    public FeatureDisabledException(Throwable cause) {
        super(cause);
    }

    public FeatureDisabledException(String message, Throwable cause) {
        super(message, cause);
    }

    public FeatureDisabledException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
