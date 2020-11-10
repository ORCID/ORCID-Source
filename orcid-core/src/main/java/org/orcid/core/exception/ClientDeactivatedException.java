package org.orcid.core.exception;

public class ClientDeactivatedException extends ApplicationException {
    
    private static final long serialVersionUID = 1L;

    public ClientDeactivatedException(String clientId) {
        super("Client " + clientId + " is deactivated");
    }

}
