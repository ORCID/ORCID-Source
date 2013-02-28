/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2013 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.frontend.web.forms;

/**
 * <p>
 * Copyright 2011-2012 ORCID
 * <p/>
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
