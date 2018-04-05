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
package org.orcid.activitiesindexer.exception;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidDeprecated;

public class DeprecatedRecordException extends Exception {
    private static final long serialVersionUID = 1L;
    
    //1.2 Error message
    OrcidDeprecated orcidDeprecated;
    
    //2.0 Error message
    OrcidError orcidError;
    
    public DeprecatedRecordException (OrcidDeprecated orcidDeprecated) {
        this.orcidDeprecated = orcidDeprecated;
    }
    
    public DeprecatedRecordException (OrcidError orcidError) {
        this.orcidError = orcidError;
    }

    public OrcidDeprecated getOrcidDeprecated() {
        return orcidDeprecated;
    }

    public OrcidError getOrcidError() {
        return orcidError;
    }    
}
