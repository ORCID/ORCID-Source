package org.orcid.api.common.security.oauth;

/**
 * Exception thrown when a bearer token has been revoked, expired, or is otherwise inactive.
 * This represents an authentication failure (401 Unauthorized), not an authorization failure (403 Forbidden).
 * The token itself is invalid and the client must obtain a new one.
 */
public class RevokedTokenException extends RuntimeException {
    public RevokedTokenException(String msg) {
        super(msg);
    }

    public RevokedTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
