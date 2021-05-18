package org.orcid.core.utils.activities;

import org.apache.commons.lang3.StringUtils;

public class ExternalIdentifierFundedByHelper {
    private static final String EXT_ID_GRANT_NUMBER = "grant_number";
    private static final String EXT_ID_PROPOSAL_ID = "proposal-id";
    private static final String EXT_ID_URI = "uri";
    private static final String EXT_ID_DOI = "doi";
    
    public static boolean isExtIdTypeAllowedForFundedBy(String extIdType) {
        if(extIdType != null && !StringUtils.equalsIgnoreCase(extIdType,EXT_ID_GRANT_NUMBER)
                && !StringUtils.equalsIgnoreCase(extIdType,EXT_ID_PROPOSAL_ID)
                && !StringUtils.equalsIgnoreCase(extIdType,EXT_ID_URI)
                && !StringUtils.equalsIgnoreCase(extIdType,EXT_ID_DOI)) {
            return false;  
        }   
        return true;
    }
}
