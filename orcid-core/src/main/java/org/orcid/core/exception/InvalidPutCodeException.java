package org.orcid.core.exception;

import java.util.HashMap;
import java.util.Map;

import org.orcid.core.utils.v3.SourceEntityUtils;
import org.orcid.jaxb.model.v3.rc2.common.Source;

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
	
	public static InvalidPutCodeException forSource(Source activeSource) {
            Map<String, String> params = new HashMap<String, String>();
            if (activeSource != null) {
                params.put("clientName", SourceEntityUtils.getSourceName(activeSource));
            }	
            return new InvalidPutCodeException(params);
	}

}
