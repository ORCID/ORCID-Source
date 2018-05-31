package org.orcid.core.exception;

import java.util.Map;

public class InvalidFuzzyDateException extends ApplicationException {

    private static final long serialVersionUID = 1L;
    
    public InvalidFuzzyDateException(Map<String, String> params) {
        super(params);
    }

}
