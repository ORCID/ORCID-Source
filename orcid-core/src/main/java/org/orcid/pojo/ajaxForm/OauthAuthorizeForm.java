/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OauthAuthorizeForm implements ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    private Text userName;
    private Text password;    
    private boolean approved = false;
    private boolean persistentTokenEnabled = false;
    private String redirectUrl;
    private boolean emailAccessAllowed = false;

    @Override
    public List<String> getErrors() {
        return errors;
    }

    @Override
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getUserName() {
        return userName;
    }

    public void setUserName(Text userName) {
        this.userName = userName;
    }

    public Text getPassword() {
        return password;
    }

    public void setPassword(Text password) {
        this.password = password;
    }

    public boolean getApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean getPersistentTokenEnabled() {
        return persistentTokenEnabled;
    }

    public void setPersistentTokenEnabled(boolean persistentTokenEnabled) {
        this.persistentTokenEnabled = persistentTokenEnabled;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public boolean isEmailAccessAllowed() {
        return emailAccessAllowed;
    }

    public void setEmailAccessAllowed(boolean emailAccessAllowed) {
        this.emailAccessAllowed = emailAccessAllowed;
    }
    
}
