package org.orcid.core.exception;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class GroupIdRecordNotFoundException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public GroupIdRecordNotFoundException() {
    }

    public GroupIdRecordNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupIdRecordNotFoundException(String message) {
        super(message);
    }

    public GroupIdRecordNotFoundException(Throwable cause) {
        super(cause);
    }

}
