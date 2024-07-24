package org.orcid.core.exception;

import java.util.Map;

public class BannedException extends ApplicationException {

    private static final long serialVersionUID = 1L;

    public BannedException() {
        super();
    }

    public BannedException(Map<String, String> params) {
        super(params);
    }

}
