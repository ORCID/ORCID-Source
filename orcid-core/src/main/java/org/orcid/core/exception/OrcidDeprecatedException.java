package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidDeprecatedException extends ApplicationException {
    private static final long serialVersionUID = 1L;
    
    public static final String ORCID = "orcid";
    public static final String DEPRECATED_DATE = "deprecated_date";
    public static final String DEPRECATED_ORCID = "deprecated_orcid";

    public OrcidDeprecatedException() {
    	super();
	}
    
	public OrcidDeprecatedException(Map<String, String> params) {
		super(params);
	}
}
