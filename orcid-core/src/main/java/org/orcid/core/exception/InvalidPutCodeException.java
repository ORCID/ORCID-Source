package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class InvalidPutCodeException extends ApplicationException {

    private static final long serialVersionUID = 1L;

	public InvalidPutCodeException(Map<String, String> params) {
		super(params);
	}

}
