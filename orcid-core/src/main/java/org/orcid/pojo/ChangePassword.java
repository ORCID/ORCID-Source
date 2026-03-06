package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

public class ChangePassword extends AuthChallenge {
    private String retypedPassword;

    private String oldPassword;

    private boolean success = false;

    private boolean passwordContainsEmail = false;

    public String getRetypedPassword() {
        return retypedPassword;
    }

    public void setRetypedPassword(String retypedPassword) {
        this.retypedPassword = retypedPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isPasswordContainsEmail() {
        return passwordContainsEmail;
    }

    public void setPasswordContainsEmail(boolean passwordContainsEmail) {
        this.passwordContainsEmail = passwordContainsEmail;
    }
}
