package org.orcid.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {
    @Label("Track user events")
    EVENTS,

    @Label("Source sorting")
    SOURCE_SORTING,

    @Label("Professional activities")
    PROFESSIONAL_ACTIVITIES,

    @Label("Redirect PUT token actions from *.pub.orcid.org to *.orcid.org")
    REDIRECT_PUT_TOKEN_ENDPOINT,

    @Label("New Developer tools")
    NEW_DEVELOPER_TOOLS,

    @Label("Stop sending notification if work has not been updated")
    STOP_SENDING_NOTIFICATION_WORK_NOT_UPDATED,

    @Label("Move client from a member to another member")
    MOVE_CLIENT,

    @Label("Add other people contributions via BIBTEXT")
    ADD_OTHER_WORK_CONTRIBUTORS_WITH_BIBTEX,

    @Label("Disable matching subdomains")
    DISABLE_MATCHING_SUBDOMAINS,
    
    @Label("Enable Crazy Egg")
    CRAZY_EGG,

    @Label("Add other people contributions via DOI & PUBMED")
    ADD_OTHER_WORK_CONTRIBUTORS_WITH_DOI_PUBMED,

    @Label("Add other people contributions manually")
    ADD_OTHER_WORK_CONTRIBUTORS,    

    @Label("Restrict delegator access to account settings")
    RESTRICTED_DELEGATORS,

    @Label("HelpHero")
    ORCID_ANGULAR_HELP_HERO,

    @Label("Disable Badges")
    DISABLE_BADGES,

    @Label("Spam button")
    SPAM_BUTTON,

    @Label("Email status dropdown option")
    EMAIL_STATUS_DROPDOWN_OPTION,

    @Label("Enable 2019 header")
    ENABLE_HEADER2,
	
    @Label("Change view privacy from work/funding/affiliation form dialogs")
    DIALOG_PRIVACY_OPTION,
	
    @Label("Shows an alert message when a user goes to /signin and is already signed in\n")
    RE_LOGGIN_ALERT,

    @Label("Affiliation org ID in UI")
    AFFILIATION_ORG_ID,

    @Label("Affiliation search")
    AFFILIATION_SEARCH,
    
    @Label("Https for links to iDs")
    HTTPS_IDS,

    @Label("Arabic translation")
    LANG_AR,

    @Label("New footer")
    NEW_FOOTER,
    
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

    @Label("Disable reCAPTCHA")
    DISABLE_RECAPTCHA,    

    @Label("Check external id resolution in UI")
    EX_ID_RESOLVER,

    @Label("Remove https://orcid.org from OpenID id_tokens")
    OPENID_SIMPLE_SUBJECT,
    
    @Label("Enable manual work grouping")
    MANUAL_WORK_GROUPING,

    @Label("Grouping suggestions")
    GROUPING_SUGGESTIONS,

    @Label("Enable promotion of chosen orgs in search")
    ENABLE_PROMOTION_OF_CHOSEN_ORGS,
    
    @Label("Enable the API record create endpoint for QA purposes")
    ENABLE_RECORD_CREATE_ENDPOINT,
    
    @Label("Hide unverified emails")
    HIDE_UNVERIFIED_EMAILS,
    
    @Label("User OBO")
    USER_OBO,

    @Label("Works failure debug")
    WORKS_FAILURE_DEBUG,
    
    @Label("Disable the 2.0 release candidates")
    V2_DISABLE_RELEASE_CANDIDATES,

    @Label("Disable the 3.0 release candidates")
    V3_DISABLE_RELEASE_CANDIDATES,

    @Label("ID token 24 hours lifespan")
    ID_TOKEN_24_HOURS_LIFESPAN,
    
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
    
    @Label("Enable the new 100M ID's range")
    ENABLE_NEW_IDS,
    
    @Label("Send verification emails for 2, 7 and 28 days. If disabled 2 days only verification emails will be sent.")
    SEND_ALL_VERIFICATION_EMAILS;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
