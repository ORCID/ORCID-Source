package org.orcid.core.exception;

public class ClientAlreadyDeactivatedException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public ClientAlreadyDeactivatedException(String message) {
        super(message);
    }

}
