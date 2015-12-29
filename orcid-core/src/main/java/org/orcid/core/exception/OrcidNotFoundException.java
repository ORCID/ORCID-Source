/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
