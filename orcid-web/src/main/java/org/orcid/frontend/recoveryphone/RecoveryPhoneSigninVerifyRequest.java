package org.orcid.frontend.recoveryphone;

/**
 * Confirms the code that was texted to the recovery number, which disables 2FA
 * on the account so the ordinary sign in can be replayed without a code.
 */
public class RecoveryPhoneSigninVerifyRequest {

    private String username;

    private String password;

    private String verificationCode;

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

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

}
