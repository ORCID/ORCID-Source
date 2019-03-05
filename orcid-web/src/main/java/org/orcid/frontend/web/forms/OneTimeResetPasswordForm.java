package org.orcid.frontend.web.forms;

import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;
import org.orcid.pojo.ajaxForm.Text;

public class OneTimeResetPasswordForm implements ErrorsInterface {

    private Text password;

    private Text retypedPassword;

    private String encryptedEmail;
    
    private String successRedirectLocation;
    
    private List<String> errors;

    public Text getPassword() {
        if (password == null) {
            password = new Text();
        }
        return password;
    }

    public void setPassword(Text password) {
        this.password = password;
    }

    public Text getRetypedPassword() {
        if (retypedPassword == null) {
            retypedPassword = new Text();
        }
        return retypedPassword;
    }

    public void setRetypedPassword(Text retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
    
    public String getEncryptedEmail() {
        return encryptedEmail;
    }

    public void setEncryptedEmail(String encryptedEmail) {
        this.encryptedEmail = encryptedEmail;
    }

    public String getSuccessRedirectLocation() {
        return successRedirectLocation;
    }

    public void setSuccessRedirectLocation(String successRedirectLocation) {
        this.successRedirectLocation = successRedirectLocation;
    }

}
