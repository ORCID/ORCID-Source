package org.orcid.core.profile.history;

public enum ProfileHistoryEventType {
    
    SET_DEFAULT_VIS_TO_PRIVATE("Def vis: private"),
    SET_DEFAULT_VIS_TO_PUBLIC("Def vis: public"),
    SET_DEFAULT_VIS_TO_LIMITED("Def vis: limited"),
    ACCEPTED_TERMS_CONDITIONS("Accepted T&Cs"),
    ACCEPTED_PUBLIC_CLIENT_TERMS_CONDITIONS("Accepted Pub Client T&Cs"),
    EMAIL_FREQUENCY_CREATED_ON_REGISTER("email_frequency on register"),
    EMAIL_FREQUENCY_CREATED_ON_CLAIM("email_frequency on claim"),
    UPDATE_QUARTERLY_TIPS_NOTIF("send_quarterly_tips"),
    UPDATE_AMEND_NOTIF_FREQ("send_change_notifications"),
    UPDATE_ADMINISTRATIVE_NOTIF_FREQ("send_administrative_change_notifications"),
    UPDATE_MEMBER_PERMISSION_NOTIF_FREQ("send_member_update_requests"),
    RESET_PASSWORD("Reset password");
    
    String label;
    
    ProfileHistoryEventType(String label) {
        this.label = label;
    }
    
    public String getLabel() {
        return label;
    }

}
