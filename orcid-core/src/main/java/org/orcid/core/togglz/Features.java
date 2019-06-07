package org.orcid.core.togglz;

import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum Features implements Feature {

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

    @Label("New style badges on member details")
    NEW_BADGES,
    
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

    @Label("Set the 2.0 API as the default one in the public API")
    PUB_API_2_0_BY_DEFAULT,

    @Label("Set the 2.0 API as the default one in the members API")
    MEMBER_API_2_0_BY_DEFAULT,

    @Label("Disable reCAPTCHA")
    DISABLE_RECAPTCHA,

    @Label("Disable 1.2 API from the public API")
    DISABLE_1_2_ON_PUB_API,

    @Label("Disable 1.2 API from the member API")
    DISABLE_1_2_ON_MEMBER_API,

    @Label("Check external id resolution in UI")
    EX_ID_RESOLVER,

    @Label("Remove https://orcid.org from OpenID id_tokens")
    OPENID_SIMPLE_SUBJECT,
    
    @Label("Disable 1.1 version from any API")
    DISABLE_1_1,
    
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
    VERBOSE_NOTIFICATIONS;
    
    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }
}
