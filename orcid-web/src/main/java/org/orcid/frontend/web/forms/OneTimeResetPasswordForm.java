package org.orcid.frontend.web.forms;

import java.util.List;

import org.orcid.pojo.AuthChallenge;
import org.orcid.pojo.ajaxForm.Text;

public class OneTimeResetPasswordForm extends AuthChallenge {

    private String orcid;

    private Text newPassword;

    private Text retypedPassword;

    private String token;
    
    private String successRedirectLocation;
    
    private List<String> errors;

    public Text getNewPassword() {
        if (newPassword == null) {
            newPassword = new Text();
        }
        return newPassword;
    }

    public void setNewPassword(Text newPassword) {
        this.newPassword = newPassword;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
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
    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSuccessRedirectLocation() {
        return successRedirectLocation;
    }

    public void setSuccessRedirectLocation(String successRedirectLocation) {
        this.successRedirectLocation = successRedirectLocation;
    }

}
