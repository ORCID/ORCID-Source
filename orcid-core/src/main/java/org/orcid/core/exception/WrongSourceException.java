package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Will Simpson
 * 
 */
public class WrongSourceException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public WrongSourceException(Map<String, String> params) {
    	super(params);
    }
}
