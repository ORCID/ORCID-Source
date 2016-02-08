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

public class OauthAuthorizeForm implements OauthForm, ErrorsInterface, Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> errors = new ArrayList<String>();
    Text userName;
    Text password;
    Text clientId;
    Text clientName;
    Text memberName;    
    Text responseType;
    Text stateParam;
    boolean approved = false;
    boolean persistentTokenEnabled = false;

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
    
    public Text getClientName() {
        return clientName;
    }

    public void setClientName(Text clientName) {
        this.clientName = clientName;
    }

    public Text getMemberName() {
        return memberName;
    }

    public void setMemberName(Text memberName) {
        this.memberName = memberName;
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

    public boolean getPersistentTokenEnabled() {
        return persistentTokenEnabled;
    }

    public void setPersistentTokenEnabled(boolean persistentTokenEnabled) {
        this.persistentTokenEnabled = persistentTokenEnabled;
    }

    public Text getStateParam() {
        return stateParam;
    }

    public void setStateParam(Text stateParam) {
        this.stateParam = stateParam;
    }        
}
