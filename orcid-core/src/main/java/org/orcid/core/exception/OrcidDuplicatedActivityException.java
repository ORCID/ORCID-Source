package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidDuplicatedActivityException extends ApplicationException {
    private static final long serialVersionUID = 4656868314652702814L;

	public OrcidDuplicatedActivityException(Map<String, String> params) {
		super(params);
	}
}
