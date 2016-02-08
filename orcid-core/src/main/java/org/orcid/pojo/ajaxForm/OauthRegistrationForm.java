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

public class OauthRegistrationForm extends Registration implements OauthForm {
    private static final long serialVersionUID = 1L;
    Text clientId;
    Text clientName;
    Text memberName;
    Text redirectUri;
    Text responseType;
    Text stateParam;
    boolean approved = false;
    boolean persistentTokenEnabled = false;

    public OauthRegistrationForm() {
        super();
        this.clientId = new Text();
        this.redirectUri = new Text();
        this.responseType = new Text();
    }

    public OauthRegistrationForm(Registration reg) {
        this.setErrors(reg.getErrors());
        this.setSendChangeNotifications(reg.getSendChangeNotifications());
        this.setSendOrcidNews(reg.getSendOrcidNews());
        this.setTermsOfUse(reg.getTermsOfUse());
        this.setActivitiesVisibilityDefault(reg.getActivitiesVisibilityDefault());
        this.setPassword(reg.getPassword());
        this.setPasswordConfirm(reg.getPasswordConfirm());
        this.setEmail(reg.getEmail());
        this.setEmailConfirm(reg.getEmailConfirm());
        this.setGivenNames(reg.getGivenNames());
        this.setFamilyNames(reg.getFamilyNames());
        this.setCreationType(reg.getCreationType());
        this.setReferredBy(reg.getReferredBy());
        this.setSendEmailFrequencyDays(reg.getSendEmailFrequencyDays());
        this.clientId = new Text();
        this.redirectUri = new Text();
        this.responseType = new Text();
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
    
    public Text getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(Text redirectUri) {
        this.redirectUri = redirectUri;
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
