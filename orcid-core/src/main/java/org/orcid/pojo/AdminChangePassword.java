package org.orcid.pojo;



public class AdminChangePassword {
    private String password;

    private String orcidOrEmail;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOrcidOrEmail() {
        return orcidOrEmail;
    }

    public void setOrcidOrEmail(String orcidOrEmail) {        
        this.orcidOrEmail = orcidOrEmail;
    }

}
