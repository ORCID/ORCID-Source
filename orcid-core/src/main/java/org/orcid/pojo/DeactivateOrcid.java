package org.orcid.pojo;

import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.utils.ExpiringLinkService;

import java.util.ArrayList;
import java.util.List;

public class DeactivateOrcid implements ErrorsInterface {

    private List<String> errors = new ArrayList<String>();

    private String password;
    
    private String twoFactorCode;

    private String twoFactorRecoveryCode;

    private boolean twoFactorEnabled;

    private ExpiringLinkService.VerificationResult tokenVerification;

    private boolean deactivationSuccessful;

    private boolean invalidPassword;

    private boolean invalidTwoFactorCode;

    private boolean invalidTwoFactorRecoveryCode;
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTwoFactorCode() {
        return twoFactorCode;
    }

    public void setTwoFactorCode(String twoFactorCode) {
        this.twoFactorCode = twoFactorCode;
    }

    public String getTwoFactorRecoveryCode() {
        return twoFactorRecoveryCode;
    }

    public void setTwoFactorRecoveryCode(String twoFactorRecoveryCode) {
        this.twoFactorRecoveryCode = twoFactorRecoveryCode;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public void setTwoFactorEnabled(boolean twoFactorEnabled) {
        this.twoFactorEnabled = twoFactorEnabled;
    }

    public ExpiringLinkService.VerificationResult getTokenVerification() {
        return tokenVerification;
    }

    public void setTokenVerification(ExpiringLinkService.VerificationResult tokenVerification) {
        this.tokenVerification = tokenVerification;
    }

    public boolean isDeactivationSuccessful() {
        return deactivationSuccessful;
    }

    public void setDeactivationSuccessful(boolean deactivationSuccessful) {
        this.deactivationSuccessful = deactivationSuccessful;
    }

    public boolean isInvalidPassword() {
        return invalidPassword;
    }

    public void setInvalidPassword(boolean invalidPassword) {
        this.invalidPassword = invalidPassword;
    }

    public boolean isInvalidTwoFactorCode() {
        return invalidTwoFactorCode;
    }

    public void setInvalidTwoFactorCode(boolean invalidTwoFactorCode) {
        this.invalidTwoFactorCode = invalidTwoFactorCode;
    }

    public boolean isInvalidTwoFactorRecoveryCode() {
        return invalidTwoFactorRecoveryCode;
    }

    public void setInvalidTwoFactorRecoveryCode(boolean invalidTwoFactorRecoveryCode) {
        this.invalidTwoFactorRecoveryCode = invalidTwoFactorRecoveryCode;
    }
}
