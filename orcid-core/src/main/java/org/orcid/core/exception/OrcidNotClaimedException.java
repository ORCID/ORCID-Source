package org.orcid.core.exception;

import java.util.Map;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class OrcidNotClaimedException extends ApplicationException {
    private static final long serialVersionUID = 1L;

    public static final String ORCID = "orcid";

    public OrcidNotClaimedException() {
        super();
    }

    public OrcidNotClaimedException(String message) {
        super(message);
    }
    
    public OrcidNotClaimedException(Map<String, String> params) {
        super(params);
    }
}
