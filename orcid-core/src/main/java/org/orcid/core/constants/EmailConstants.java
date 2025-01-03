package org.orcid.core.constants;

import org.orcid.jaxb.model.v3.release.common.VerificationDate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class EmailConstants {

    public static final String WILDCARD_VERIFICATION_URL = "${verification_url}";

    /*
     * session attribute that is used to see if we should check and notify the
     * user if their primary email isn't verified.
     */
    public static String CHECK_EMAIL_VALIDATED = "CHECK_EMAIL_VALIDATED";

    public static final String ORCID_EMAIL_VALIDATOR_CLIENT_NAME = "ORCID email validation";

    public static final String ORCID_EMAIL_VALIDATOR_CLIENT_ID = "0000-0000-0000-0000";
    
    public static final int MAX_EMAIL_COUNT = 30;
    
    public static final String LAST_RESORT_ORCID_USER_EMAIL_NAME = "ORCID Registry User";
    
    public static final String DO_NOT_REPLY_NOTIFY_ORCID_ORG = "ORCID - Do not reply <DoNotReply@notify.orcid.org>";
    
    public static final String DO_NOT_REPLY_VERIFY_ORCID_ORG = "ORCID - Do not reply <DoNotReply@verify.orcid.org>";

    static {
        XMLGregorianCalendar gregorianCutoffDate = null;
        VerificationDate verificationDate = null;

        try {
            gregorianCutoffDate = DatatypeFactory.newInstance().newXMLGregorianCalendar();

            gregorianCutoffDate.setYear(2024);
            gregorianCutoffDate.setMonth(10);
            gregorianCutoffDate.setDay(27);

            verificationDate = new VerificationDate(gregorianCutoffDate);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException("Error initializing XMLGregorianCalendar", e);
        }

    }
}
