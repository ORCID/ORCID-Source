package org.orcid.frontend.recoveryphone;

/**
 * What the user sends to pass an authentication challenge with their recovery
 * phone number: their password, and the code we texted to the stored number.
 *
 * The number itself is never part of this request. The Registry sent the code
 * to the number it holds, and checks the code against that same number, so a
 * client cannot point the challenge at a number of its own.
 */
public class RecoveryPhoneChallengeVerifyRequest {

    private String password;

    private String verificationCode;

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
