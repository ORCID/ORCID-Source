package org.orcid.core.exception;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class PutCodeFormatException extends ApplicationException {

    private static final long serialVersionUID = 1L;

	public PutCodeFormatException() {
	}
	
	public PutCodeFormatException(String message) {
		super(message);
	}
	
	public PutCodeFormatException(Throwable cause) {
		super(cause);
	}
	
	public PutCodeFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}
