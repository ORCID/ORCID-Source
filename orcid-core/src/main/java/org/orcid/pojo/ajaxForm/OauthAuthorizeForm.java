package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OauthAuthorizeForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    private Text userName;
    private Text password;    
    private boolean approved = false;
    private boolean persistentTokenEnabled = false;
    private String redirectUrl;
    private boolean emailAccessAllowed = false;
    private boolean verificationCodeRequired;
    private boolean badVerificationCode;
    private boolean badRecoveryCode;
    private Text verificationCode;
    private Text recoveryCode;

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getUserName() {
        return userName;
    }

    public void setUserName(Text userName) {
        this.userName = userName;
    }

    public Text getPassword() {
        return password;
    }

    public void setPassword(Text password) {
        this.password = password;
    }

    public boolean getApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean getPersistentTokenEnabled() {
        return persistentTokenEnabled;
    }

    public void setPersistentTokenEnabled(boolean persistentTokenEnabled) {
        this.persistentTokenEnabled = persistentTokenEnabled;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isEmailAccessAllowed() {
        return emailAccessAllowed;
    }

    public void setEmailAccessAllowed(boolean emailAccessAllowed) {
        this.emailAccessAllowed = emailAccessAllowed;
    }

    public boolean isVerificationCodeRequired() {
        return verificationCodeRequired;
    }

    public void setVerificationCodeRequired(boolean verificationCodeRequired) {
        this.verificationCodeRequired = verificationCodeRequired;
    }

    public boolean isBadVerificationCode() {
        return badVerificationCode;
    }

    public void setBadVerificationCode(boolean badVerificationCode) {
        this.badVerificationCode = badVerificationCode;
    }

    public boolean isBadRecoveryCode() {
        return badRecoveryCode;
    }

    public void setBadRecoveryCode(boolean badRecoveryCode) {
        this.badRecoveryCode = badRecoveryCode;
    }

    public Text getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(Text verificationCode) {
        this.verificationCode = verificationCode;
    }

    public Text getRecoveryCode() {
        return recoveryCode;
    }

    public void setRecoveryCode(Text recoveryCode) {
        this.recoveryCode = recoveryCode;
    }
    
    
}
