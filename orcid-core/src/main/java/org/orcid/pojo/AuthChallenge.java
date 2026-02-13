package org.orcid.pojo;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

import java.util.ArrayList;
import java.util.List;

public class AuthChallenge implements ErrorsInterface {
    private List<String> errors = new ArrayList<String>();

    private String twoFactorCode;
    private String twoFactorRecoveryCode;
    private String password;

    private boolean twoFactorEnabled;
    private boolean invalidTwoFactorCode;
    private boolean invalidTwoFactorRecoveryCode;
    private boolean invalidPassword;

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

    public boolean isInvalidPassword() {
        return invalidPassword;
    }

    public void setInvalidPassword(boolean invalidPassword) {
        this.invalidPassword = invalidPassword;
    }

    @Override
    public List<String> getErrors() {
        return this.errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}