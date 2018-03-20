package org.orcid.pojo;

import java.util.List;

import org.orcid.pojo.ajaxForm.ErrorsInterface;

public class EmailRequest implements ErrorsInterface {
    
    private List<String> errors;
    
    private String successMessage;
    
    private String email;

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSuccessMessage() {
        return successMessage;
    }

    public void setSuccessMessage(String successMessage) {
        this.successMessage = successMessage;
    }
    
}
