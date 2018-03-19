package org.orcid.core.exception;

public class ExceedMaxNumberOfPutCodesException extends ApplicationException {

    private static final long serialVersionUID = 1L;
    
    public ExceedMaxNumberOfPutCodesException(Integer maxNumber) {
        super("Too many put codes specified: maximum is " + maxNumber);
    }
    
}
