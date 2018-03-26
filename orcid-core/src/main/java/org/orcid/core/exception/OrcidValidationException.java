package org.orcid.core.exception;

/**
 * 
 * @author Will Simpson
 * 
 */
@Deprecated
public class OrcidValidationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OrcidValidationException() {
    }

    public OrcidValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrcidValidationException(String message) {
        super(message);
    }

    public OrcidValidationException(Throwable cause) {
        super(cause);
    }

}
