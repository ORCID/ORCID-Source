package org.orcid.pojo.ajaxForm;

public class OauthRegistrationForm extends Registration {
    private static final long serialVersionUID = 1L;
    boolean approved = false;
    boolean persistentTokenEnabled = false;
    boolean emailAccessAllowed = false;
    private String redirectUrl;

    public OauthRegistrationForm() {
        super();
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
