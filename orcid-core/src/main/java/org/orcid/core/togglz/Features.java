package org.orcid.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

    @Label("Orcid Angular Inbox")
    ORCID_ANGULAR_INBOX,

    @Label("New Info Site")
    NEW_INFO_SITE,

    @Label("Orcid Angular Signin")
    ORCID_ANGULAR_SIGNIN,

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

    @Label("Wider grid")
    WIDE_GRID,
	
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

    @Label("New style badges on member details")
    NEW_BADGES,
    
    @Label("Enable Hotjar tracking")
    HOTJAR,
    
    @Label("Https for links to iDs")
    HTTPS_IDS,

    @Label("Arabic translation")
    LANG_AR,

    @Label("Last modified")
    LAST_MOD,

    @Label("New footer")
    NEW_FOOTER,
    
    @Label("Research resource actvities section in the UI")
    RESEARCH_RESOURCE,
    
    @Label("Reset password send email in all cases")
    RESET_PASSWORD_EMAIL,

    @Label("Revoke access token if authorization code is reused")
    REVOKE_TOKEN_ON_CODE_REUSE,

    @Label("Affiliations in search results")
    SEARCH_RESULTS_AFFILIATIONS,

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
    
    @Label("Enable group affiliations")
    GROUP_AFFILIATIONS,
    
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
    
    @Label("Verbose notifications")
    VERBOSE_NOTIFICATIONS,

    @Label("Works failure debug")
    WORKS_FAILURE_DEBUG,
    
    @Label("Set the 3.0 API as the default one in the public API")
    PUB_API_DEFAULT_TO_V3,

    @Label("Set the 3.0 API as the default one in the members API")
    MEMBER_API_DEFAULT_TO_V3,
    
    @Label("Disable the 2.0 release candidates")
    V2_DISABLE_RELEASE_CANDIDATES;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
