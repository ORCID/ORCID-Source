package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class DeprecateProfile implements ErrorsInterface {

    private List<String> errors = new ArrayList<String>();

    private List<String> deprecatingEmails;
    
    private String deprecatingPassword;
    
    private String deprecatingAccountName;
    
    private String deprecatingOrcidOrEmail;
    
    private String deprecatingOrcid;
    
    private List<String> primaryEmails;
    
    private String primaryOrcid;
    
    private String primaryAccountName;
    
    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getDeprecatingEmails() {
        return deprecatingEmails;
    }

    public void setDeprecatingEmails(List<String> deprecatingEmails) {
        this.deprecatingEmails = deprecatingEmails;
    }

    public String getDeprecatingPassword() {
        return deprecatingPassword;
    }

    public void setDeprecatingPassword(String deprecatingPassword) {
        this.deprecatingPassword = deprecatingPassword;
    }

    public String getDeprecatingAccountName() {
        return deprecatingAccountName;
    }

    public void setDeprecatingAccountName(String deprecatingAccountName) {
        this.deprecatingAccountName = deprecatingAccountName;
    }

    public String getDeprecatingOrcidOrEmail() {
        return deprecatingOrcidOrEmail;
    }

    public void setDeprecatingOrcidOrEmail(String deprecatingOrcidOrEmail) {
        this.deprecatingOrcidOrEmail = deprecatingOrcidOrEmail;
    }

    public List<String> getPrimaryEmails() {
        return primaryEmails;
    }

    public void setPrimaryEmails(List<String> primaryEmails) {
        this.primaryEmails = primaryEmails;
    }

    public String getPrimaryOrcid() {
        return primaryOrcid;
    }

    public void setPrimaryOrcid(String primaryOrcid) {
        this.primaryOrcid = primaryOrcid;
    }

    public String getPrimaryAccountName() {
        return primaryAccountName;
    }

    public void setPrimaryAccountName(String primaryAccountName) {
        this.primaryAccountName = primaryAccountName;
    }

    public String getDeprecatingOrcid() {
        return deprecatingOrcid;
    }

    public void setDeprecatingOrcid(String deprecatingOrcid) {
        this.deprecatingOrcid = deprecatingOrcid;
    }
    
}
