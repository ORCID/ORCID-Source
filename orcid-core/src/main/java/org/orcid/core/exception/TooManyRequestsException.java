package org.orcid.core.exception;

import java.util.Map;

public class TooManyRequestsException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public TooManyRequestsException() {
        super();
    }

    public TooManyRequestsException(Map<String, String> params) {
        super(params);
    }

}
