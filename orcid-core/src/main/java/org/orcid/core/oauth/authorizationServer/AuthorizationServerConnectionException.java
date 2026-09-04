package org.orcid.core.oauth.authorizationServer;

public class AuthorizationServerConnectionException extends RuntimeException {
    public AuthorizationServerConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
