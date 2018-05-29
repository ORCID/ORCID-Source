package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class OrcidNotificationNotFoundException extends ApplicationException {

    private static final long serialVersionUID = 1L;

	public OrcidNotificationNotFoundException(Map<String, String> params) {
		super(params);
	}
}
