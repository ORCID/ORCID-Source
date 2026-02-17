package org.orcid.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;


public enum Features implements Feature {

    @Label("Search and Link Wizard with certified and featured links") 
    SEARCH_AND_LINK_WIZARD_WITH_CERTIFIED_AND_FEATURED_LINKS,

    @Label("Permission Notifications")
    PERMISSION_NOTIFICATIONS,

    @Label("FEATURED_AFFILIATIONS")
    FEATURED_AFFILIATIONS,

    @Label("Header Compact")
    HEADER_COMPACT,

    @Label("OAuth - affiliation interstitial")
    OAUTH_AFFILIATION_INTERSTITIAL,

    @Label("Login - affiliation interstitial")
    LOGIN_AFFILIATION_INTERSTITIAL,

    @Label("Login - domains interstitial")
    LOGIN_DOMAINS_INTERSTITIAL,

    @Label("OAUTH - domains interstitial")
    OAUTH_DOMAINS_INTERSTITIAL,

    @Label("New Relic Browser Monitoring")
    NEW_RELIC_BROWSER_MONITORING,
  
    @Label("Homepage Headless WordPress")
    WORDPRESS_HOME_PAGE,

    @Label("Track user events")
    EVENTS,

    @Label("Redirect PUT token actions from *.pub.orcid.org to *.orcid.org")
    REDIRECT_PUT_TOKEN_ENDPOINT,

    @Label("Enable Crazy Egg")
    CRAZY_EGG,

    @Label("HelpHero")
    ORCID_ANGULAR_HELP_HERO,
    
    @Label("Stop caching works when doing bulk reads")
    READ_BULK_WORKS_DIRECTLY_FROM_DB, 
        
    @Label("Store failing login attempts to the database")
    ENABLE_ACCOUNT_LOCKOUT,
    
    @Label("Do not lock the account on the UI when the lockout threshold is exceeded")
    ACCOUNT_LOCKOUT_SIMULATION,    
    
    @Label("Send verification emails for 2, 7 and 28 days. If disabled 2 days only verification emails will be sent.")
    SEND_ALL_VERIFICATION_EMAILS,

    @Label("Send add works emails for 7, 28 and 90 days.")
    SEND_ADD_WORKS_EMAILS,

    @Label("Delete events older than 90 days from the DB ")
    DELETE_EVENTS,

    @Label("Track public events stats ")
    PAPI_EVENTS, 
    
    @Label("Enable summary endpoint in the Members API")
    MAPI_SUMMARY_ENDPOINT,

    @Label("Enable email domains")
    EMAIL_DOMAINS,

    @Label("Enable email domains in the UI")
    EMAIL_DOMAINS_UI,
    
    @Label("Enforce rate limiting for public API when disabled the rate monitoring is on. When disabled is the mode is monitoring only.")
    ENABLE_PAPI_RATE_LIMITING,

    @Label("Use the authorization server to sign in")
    OAUTH_SIGNIN,

    @Label("Use the authorization server to authorize OAUTH requests")
    OAUTH_AUTHORIZATION,

    @Label("Use the authorization server to exchange authorization codes for authentication tokens")
    OAUTH_AUTHORIZATION_CODE_EXCHANGE,

    @Label("Use the authorization server to validate the token on every API request that contains one")
    OAUTH_TOKEN_VALIDATION,

    @Label("Enable featured works logic in the UI")
    FEATURED_WORKS_UI,

    @Label("Send email to primary address on deactivation")
    SEND_EMAIL_ON_DEACTIVATION,

    @Label("Send email on email list change")
    SEND_EMAIL_ON_EMAIL_LIST_CHANGE,
    
    @Label("Send email on deprecate record")
    SEND_EMAIL_ON_DEPRECATE_RECORD,
    
    @Label("Send email on reset password")
    SEND_EMAIL_ON_RESET_PASSWORD;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
