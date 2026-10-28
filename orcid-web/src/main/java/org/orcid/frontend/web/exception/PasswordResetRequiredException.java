package org.orcid.frontend.web.exception;

import org.springframework.security.authentication.AccountStatusException;

/**
 * The password was correct, but the record is flagged for a mandatory password
 * reset (profile.force_password_reset), so it cannot sign in with a password
 * until the password is changed.
 *
 * An AccountStatusException rather than a BadCredentialsException: the sign in
 * lock must not count it as a failed attempt, and the provider manager must not
 * fall through to another provider.
 */
public class PasswordResetRequiredException extends AccountStatusException {

    private static final long serialVersionUID = 1L;

    public PasswordResetRequiredException() {
        super("Password reset required");
    }

}
