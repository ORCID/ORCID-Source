package org.orcid.core.constants;

public class EmailConstants {

    public static final String WILDCARD_VERIFICATION_URL = "${verification_url}";

    /*
     * session attribute that is used to see if we should check and notify the
     * user if their primary email ins't verified.
     */
    public static String CHECK_EMAIL_VALIDATED = "CHECK_EMAIL_VALIDATED";    
    
    public static final int MAX_EMAIL_COUNT = 30;
}
