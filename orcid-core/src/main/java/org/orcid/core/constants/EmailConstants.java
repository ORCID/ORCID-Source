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
    
    public static final String DO_NOT_REPLY_NOTIFY_ORCID_ORG = "ORCID - Do not reply <DoNotReply@notify.orcid.org>";
    
    public static final String DO_NOT_REPLY_VERIFY_ORCID_ORG = "ORCID - Do not reply <DoNotReply@verify.orcid.org>";
}
