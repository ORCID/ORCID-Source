package org.orcid.core.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Declan Newman (declan) Date: 01/03/2012
 */
public class OrcidNotFoundException extends ApplicationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public OrcidNotFoundException(Map<String, String> params) {
        super(params);
    }

    public static OrcidNotFoundException newInstance(String orcid) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("orcid", orcid);
        return new OrcidNotFoundException(params);
    }
    
}
