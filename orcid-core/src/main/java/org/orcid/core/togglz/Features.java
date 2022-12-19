package org.orcid.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

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

    @Label("Store and read top_contributors_json")
    STORE_TOP_CONTRIBUTORS,

    @Label("Restrict delegator access to account settings")
    RESTRICTED_DELEGATORS,

    @Label("HelpHero")
    ORCID_ANGULAR_HELP_HERO,

    @Label("Orcid Angular Lazy Load Peer Reviews")
    ORCID_ANGULAR_LAZY_LOAD_PEER_REVIEWS,

    @Label("Orcid Angular Works Contributors")
    ORCID_ANGULAR_WORKS_CONTRIBUTORS,

    @Label("Orcid Angular Account settings ")
    ORCID_ANGULAR_ACCOUNT_SETTINGS,

    @Label("Orcid Angular Public Page")
    ORCID_ANGULAR_PUBLIC_PAGE,

    @Label("Disable Badges")
    DISABLE_BADGES,

    @Label("Orcid Angular My Orcid")
    ORCID_ANGULAR_MY_ORCID,

    @Label("Spam button")
    SPAM_BUTTON,

    @Label("Orcid Angular Search")
    ORCID_ANGULAR_SEARCH,

    @Label("Works pagination")
    WORKS_PAGINATION,

    @Label("Email status dropdown option")
    EMAIL_STATUS_DROPDOWN_OPTION,

    @Label("Enable user menu")
    ENABLE_USER_MENU,

    @Label("Enable 2019 header")
    ENABLE_HEADER2,
	
    @Label("Add works based on ArXiv, DOI or PubMed id metadata")
    ADD_WORKS_WITH_EXTERNAL_ID,
	
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
    
    @Label("Allow members to delete their own elements even with revoked tokens")
    ALLOW_DELETE_WITH_REVOKED_TOKENS,
    
    @Label("Stop caching works when doing bulk reads")
    READ_BULK_WORKS_DIRECTLY_FROM_DB, 
        
    @Label("Enable new salesforce microservice")
    SALESFORCE_MICROSERVICE,
    
    @Label("Organization search add sort by popularity")

    ORG_SEARCH_SORT_BY_POPULARITY,
    
    @Label("Store failing login attempts to the database")
    ENABLE_ACCOUNT_LOCKOUT,
    
    @Label("Do not lock the account on the UI when the lockout threshold is exceeded")
    ACCOUNT_LOCKOUT_SIMULATION;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
