package org.orcid.api.common.security.oauth;

import org.springframework.security.access.AccessDeniedException;

/**
 * Exception thrown when a bearer token has been revoked.
 * This represents an authorization failure (403 Forbidden), not an authentication failure (401 Unauthorized).
 * The token exists and is valid, but it lacks permission to perform the requested action.
 */
public class RevokedTokenException extends AccessDeniedException {
    public RevokedTokenException(String msg) {
        super(msg);
    }

    public RevokedTokenException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
