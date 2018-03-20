package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class PutCodeRequiredException extends ApplicationException {

    private static final long serialVersionUID = 1L;

	public PutCodeRequiredException(Map<String, String> params) {
		super(params);
	}

}
