package org.orcid.core.constants;

public class EmailConstants {

    public static final String WILDCARD_VERIFICATION_URL = "${verification_url}";

    /*
     * session attribute that is used to see if we should check and notify the
     * user if their primary email ins't verified.
     */
    public static String CHECK_EMAIL_VALIDATED = "CHECK_EMAIL_VALIDATED";    
    
    public static final int MAX_EMAIL_COUNT = 30;
    
    public static final String LAST_RESORT_ORCID_USER_EMAIL_NAME = "ORCID Registry User";
    
    public static final String SUPPORT_VERIFY_ORCID_ORG = "ORCID <support@verify.orcid.org>";

    public static final String RESET_NOTIFY_ORCID_ORG = "ORCID <reset@notify.orcid.org>";

    public static final String CLAIM_NOTIFY_ORCID_ORG = "ORCID <claim@notify.orcid.org>";

    public static final String DEACTIVATE_NOTIFY_ORCID_ORG = "ORCID <deactivate@notify.orcid.org>";

    public static final String LOCKED_NOTIFY_ORCID_ORG = "ORCID <locked@notify.orcid.org>";

    public static final String EMAIL_CHANGED_NOTIFY_ORCID_ORG = "ORCID <email-changed@notify.orcid.org>";
}
