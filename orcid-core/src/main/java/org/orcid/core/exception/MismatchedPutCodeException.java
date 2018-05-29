package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Will Simpson
 *
 */
public class MismatchedPutCodeException extends ApplicationException {

    private static final long serialVersionUID = 1L;

	public MismatchedPutCodeException(Map<String, String> params) {
		super(params);
	}

}
