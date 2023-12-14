package org.orcid.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;


public enum Features implements Feature {
    @Label("Registration 2.0")
    REGISTRATION_2_0,

    @Label("Track user events")
    EVENTS,

    @Label("Source sorting")
    SOURCE_SORTING,

    @Label("Professional activities")
    PROFESSIONAL_ACTIVITIES,

    @Label("Redirect PUT token actions from *.pub.orcid.org to *.orcid.org")
    REDIRECT_PUT_TOKEN_ENDPOINT,

    @Label("Stop sending notification if work has not been updated")
    STOP_SENDING_NOTIFICATION_WORK_NOT_UPDATED,

    @Label("Enable Crazy Egg")
    CRAZY_EGG,

    @Label("Restrict delegator access to account settings")
    RESTRICTED_DELEGATORS,

    @Label("HelpHero")
    ORCID_ANGULAR_HELP_HERO,

    @Label("Disable Badges")
    DISABLE_BADGES,

    @Label("Spam button")
    SPAM_BUTTON,
    
    @Label("Shows an alert message when a user goes to /signin and is already signed in\n")
    RE_LOGGIN_ALERT,
    
    @Label("Reset password send email in all cases")
    RESET_PASSWORD_EMAIL,

    @Label("Revoke access token if authorization code is reused")
    REVOKE_TOKEN_ON_CODE_REUSE,

    @Label("Survey link")
    SURVEY,

    @Label("Two factor authentication")
    TWO_FACTOR_AUTHENTICATION,

    @Label("Self service org ids")
    SELF_SERVICE_ORG_IDS,

    @Label("Remove https://orcid.org from OpenID id_tokens")
    OPENID_SIMPLE_SUBJECT,
    
    @Label("Enable the API record create endpoint for QA purposes")
    ENABLE_RECORD_CREATE_ENDPOINT,
    
    @Label("User OBO")
    USER_OBO,

    @Label("Works failure debug")
    WORKS_FAILURE_DEBUG,
    
    @Label("Promote a client from public client to be a member")
    UPGRADE_PUBLIC_CLIENT, 
    
    @Label("Stop caching works when doing bulk reads")
    READ_BULK_WORKS_DIRECTLY_FROM_DB, 
        
    @Label("Organization search add sort by popularity")
    ORG_SEARCH_SORT_BY_POPULARITY,
    
    @Label("Store failing login attempts to the database")
    ENABLE_ACCOUNT_LOCKOUT,
    
    @Label("Do not lock the account on the UI when the lockout threshold is exceeded")
    ACCOUNT_LOCKOUT_SIMULATION,    
    
    @Label("Send verification emails for 2, 7 and 28 days. If disabled 2 days only verification emails will be sent.")
    SEND_ALL_VERIFICATION_EMAILS,

    @Label("Send add works emails for 7, 28 and 90 days.")
    SEND_ADD_WORKS_EMAILS;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
