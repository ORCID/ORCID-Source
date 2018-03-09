package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class OrcidNoResultException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OrcidNoResultException() {
    }

    public OrcidNoResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrcidNoResultException(String message) {
        super(message);
    }

    public OrcidNoResultException(Throwable cause) {
        super(cause);
    }

    public OrcidNoResultException(Map<String, String> params) {
        super(params);
    }
}
