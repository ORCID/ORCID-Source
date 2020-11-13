package org.orcid.core.exception;

public class ClientAlreadyActiveException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public ClientAlreadyActiveException(String message) {
        super(message);
    }

}
