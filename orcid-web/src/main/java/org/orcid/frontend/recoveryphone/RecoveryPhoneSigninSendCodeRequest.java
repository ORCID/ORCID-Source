package org.orcid.frontend.recoveryphone;

/**
 * Asks for a code to be sent to the recovery number stored on the account the
 * credentials belong to.
 *
 * The caller cannot choose the number: it is read from the record, so a user
 * who has lost their authenticator cannot redirect the text somewhere else.
 */
public class RecoveryPhoneSigninSendCodeRequest {

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
