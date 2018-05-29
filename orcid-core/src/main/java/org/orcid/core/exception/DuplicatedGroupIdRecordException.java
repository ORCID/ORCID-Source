package org.orcid.core.exception;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class DuplicatedGroupIdRecordException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public DuplicatedGroupIdRecordException() {
    }

    public DuplicatedGroupIdRecordException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatedGroupIdRecordException(String message) {
        super(message);
    }

    public DuplicatedGroupIdRecordException(Throwable cause) {
        super(cause);
    }

}
