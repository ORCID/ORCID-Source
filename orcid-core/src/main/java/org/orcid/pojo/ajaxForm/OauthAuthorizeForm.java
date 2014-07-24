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
package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class OauthAuthorizeForm implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();
    Text userName;
    Text password;
    Text clientId;
    Text redirectUri;
    Text scope;
    Text responseType;
    boolean approved=false;
    
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
    public Text getClientId() {
        return clientId;
    }
    public void setClientId(Text clientId) {
        this.clientId = clientId;
    }
    public Text getRedirectUri() {
        return redirectUri;
    }
    public void setRedirectUri(Text redirectUri) {
        this.redirectUri = redirectUri;
    }
    public Text getScope() {
        return scope;
    }
    public void setScope(Text scope) {
        this.scope = scope;
    }
    public Text getResponseType() {
        return responseType;
    }
    public void setResponseType(Text responseType) {
        this.responseType = responseType;
    }
    public boolean getApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }     
    
}
