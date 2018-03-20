package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Shobhit Tyagi
 * 
 */
public class OrcidWebhookNotFoundException extends ApplicationException {

    private static final long serialVersionUID = 1L;

	public OrcidWebhookNotFoundException(Map<String, String> params) {
		super(params);
	}
}
