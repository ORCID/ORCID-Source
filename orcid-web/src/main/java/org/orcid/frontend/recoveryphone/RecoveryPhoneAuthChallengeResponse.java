package org.orcid.frontend.recoveryphone;

public class RecoveryPhoneAuthChallengeResponse {
    private final boolean success;

    public RecoveryPhoneAuthChallengeResponse(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}