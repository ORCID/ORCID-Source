package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class OrcidClientNotFoundException extends ApplicationException {

    private static final long serialVersionUID = 1L;

	public OrcidClientNotFoundException(Map<String, String> params) {
		super(params);
	}
}
