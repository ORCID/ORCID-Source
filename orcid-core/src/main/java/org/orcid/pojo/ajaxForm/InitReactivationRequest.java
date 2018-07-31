package org.orcid.pojo.ajaxForm;

public class InitReactivationRequest {

    private String email;
    private String error;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
