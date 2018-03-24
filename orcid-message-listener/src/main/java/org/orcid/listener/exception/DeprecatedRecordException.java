package org.orcid.listener.exception;

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
