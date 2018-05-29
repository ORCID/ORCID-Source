package org.orcid.listener.exception;

import org.orcid.jaxb.model.error_v2.OrcidError;
import org.orcid.jaxb.model.message.OrcidMessage;

public class LockedRecordException extends Exception {
    private static final long serialVersionUID = 1L;
    
    //1.2 Error message
    OrcidMessage orcidMessage;
    
    //2.0 Error message
    OrcidError orcidError;
    
    public LockedRecordException (OrcidMessage orcidMessage) {
        this.orcidMessage = orcidMessage;
    }
    
    public LockedRecordException (OrcidError orcidError) {
        this.orcidError = orcidError;
    }

    public OrcidMessage getOrcidMessage() {
        return orcidMessage;
    }

    public OrcidError getOrcidError() {
        return orcidError;
    }        
}
