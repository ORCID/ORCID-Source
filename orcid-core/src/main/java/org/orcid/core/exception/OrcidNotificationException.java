package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidNotificationException extends ApplicationException {

    private static final long serialVersionUID = 1L;

	public OrcidNotificationException(Map<String, String> params) {
		super(params);
	}
}
