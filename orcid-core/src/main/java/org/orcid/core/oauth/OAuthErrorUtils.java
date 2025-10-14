package org.orcid.core.oauth;

import javax.ws.rs.core.Response.Status;

import de.undercouch.citeproc.helper.tool.MissingArgumentException;
import org.orcid.core.exception.ClientDeactivatedException;
import org.orcid.core.exception.DeactivatedException;
import org.orcid.core.exception.LockedException;
import org.orcid.core.exception.OrcidDeprecatedException;
import org.orcid.core.exception.OrcidInvalidScopeException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.common.exceptions.UnsupportedGrantTypeException;

public class OAuthErrorUtils {

    public static OAuthError getOAuthError(Throwable t) {
        OAuthError error = new OAuthError();
        error.setErrorDescription(t.getMessage());
        if (LockedException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.UNAUTHORIZED_CLIENT);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if (ClientDeactivatedException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.UNAUTHORIZED_CLIENT);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if (UnsupportedGrantTypeException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.UNSUPPORTED_GRANT_TYPE);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if (OrcidInvalidScopeException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.INVALID_SCOPE);
            error.setResponseStatus(Status.UNAUTHORIZED);
        } else if (InvalidScopeException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.INVALID_SCOPE);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if (InsufficientAuthenticationException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.UNAUTHORIZED_CLIENT);
            error.setResponseStatus(Status.UNAUTHORIZED);
        } else if (IllegalArgumentException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.INVALID_REQUEST);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if (InvalidGrantException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.INVALID_GRANT);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if(DeactivatedException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.UNAUTHORIZED_CLIENT);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if(OrcidDeprecatedException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.UNAUTHORIZED_CLIENT);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if (InvalidTokenException.class.isAssignableFrom(t.getClass())) {
            error.setError(OAuthError.UNAUTHORIZED_CLIENT);
            error.setResponseStatus(Status.BAD_REQUEST);
        } else if(MissingArgumentException.class.isAssignableFrom(t.getClass())) {

        } else {
            error.setError(OAuthError.SERVER_ERROR);
            error.setResponseStatus(Status.INTERNAL_SERVER_ERROR);
        }
        return error;
    }

}
