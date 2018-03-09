package org.orcid.core.exception;

import java.util.Map;

public class InvalidJSONException extends ApplicationException {

    private static final long serialVersionUID = 1L;
    
    public InvalidJSONException(Map<String, String> params) {
        super(params);
    }

}
