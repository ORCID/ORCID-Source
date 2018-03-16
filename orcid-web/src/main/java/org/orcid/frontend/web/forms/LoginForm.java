package org.orcid.frontend.web.forms;

/**
 * <p>
 * User: Declan Newman (declan) Date: 09/02/2012
 * </p>
 */
public class LoginForm {

    private String userId;
    private String loginOption;
    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLoginOption() {
        return loginOption;
    }

    public void setLoginOption(String loginOption) {
        this.loginOption = loginOption;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
