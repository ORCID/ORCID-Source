package org.orcid.core.exception;

/**
 * 
 * @author Will Simpson
 * 
 */
public class OrcidClientGroupManagementException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public OrcidClientGroupManagementException() {
    }

    public OrcidClientGroupManagementException(String message, Throwable cause) {
        super(message, cause);
    }

    public OrcidClientGroupManagementException(String message) {
        super(message);
    }

    public OrcidClientGroupManagementException(Throwable cause) {
        super(cause);
    }

}
