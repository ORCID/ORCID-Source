package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class ProfileDeprecationRequest implements ErrorsInterface {
    private List<String> errors = new ArrayList<String>();
    private ProfileDetails deprecatedAccount;
    private ProfileDetails primaryAccount;
    private String successMessage;

    public ProfileDeprecationRequest() {
        this.deprecatedAccount = new ProfileDetails();
        this.primaryAccount = new ProfileDetails();
    }

    public ProfileDetails getDeprecatedAccount() {
        return deprecatedAccount;
    }

    public void setDeprecatedAccount(ProfileDetails deprecatedAccount) {
        this.deprecatedAccount = deprecatedAccount;
    }

    public ProfileDetails getPrimaryAccount() {
        return primaryAccount;
    }

    public void setPrimaryAccount(ProfileDetails primaryAccount) {
        this.primaryAccount = primaryAccount;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
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
