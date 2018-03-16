package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class ExceedMaxNumberOfElementsException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public ExceedMaxNumberOfElementsException() {
        super();
    }

    public ExceedMaxNumberOfElementsException(Map<String, String> params) {
        super(params);
    }
}
