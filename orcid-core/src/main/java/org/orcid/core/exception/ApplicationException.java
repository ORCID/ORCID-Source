package org.orcid.core.exception;

import java.util.Map;

public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    protected Map<String, String> params; 

    public ApplicationException() {
        super();
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(Throwable cause) {
        super(cause);
    }

    public ApplicationException(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> getParams() {
        return params;
    }       
}
