package org.orcid.pojo;

import java.util.ArrayList;
import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

/**
 * 
 * @author Will Simpson
 * 
 */
public class ManageDelegate implements ErrorsInterface {

    private List<String> errors = new ArrayList<String>();
    private String delegateToManage;
    private String delegateEmail;
    private String password;

    public String getDelegateToManage() {
        return delegateToManage;
    }

    public void setDelegateToManage(String delegateToManage) {
        this.delegateToManage = delegateToManage;
    }

    public String getDelegateEmail() {
        return delegateEmail;
    }

    public void setDelegateEmail(String delegateEmail) {
        this.delegateEmail = delegateEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

}
