package org.orcid.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;


public enum Features implements Feature {

    @Label("Public record header with summary")
    NEW_RECORD_HEADER_WITH_SUMMARY,

    @Label("Sign in updates V1")
    SIGN_IN_UPDATES_V1,

    @Label("Homepage Headless WordPress")
    WORDPRESS_HOME_PAGE,

    @Label("Registration 2.1: Add affiliations on registration")
    REGISTRATION_2_1,
   
    @Label("Registration 2.0")
    REGISTRATION_2_0,

    @Label("Track user events")
    EVENTS,

    @Label("Professional activities")
    PROFESSIONAL_ACTIVITIES,

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
    PAPI_EVENTS;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
