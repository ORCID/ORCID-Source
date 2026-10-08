package org.orcid.frontend.recoveryphone;

/**
 * Asks whether the account the credentials belong to has a recovery number,
 * so the 2FA step of sign in knows which way out to offer (F1.2).
 */
public class RecoveryPhoneSigninStatusRequest {

    private String username;

    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
