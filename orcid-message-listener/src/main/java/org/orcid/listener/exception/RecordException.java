package org.orcid.listener.exception;

import org.orcid.jaxb.model.error_v2.OrcidError;

public abstract class RecordException extends Exception {
    /**
     * 
     */
    private static final long serialVersionUID = 7502124745928167896L;

    // 2.0 Error message
    OrcidError orcidError;

    // 3.0 Error message
    org.orcid.jaxb.model.v3.release.error.OrcidError v3OrcidError;
    
    public RecordException (org.orcid.jaxb.model.error_v2.OrcidError orcidError) {
        this.orcidError = orcidError;
    }

    public RecordException(org.orcid.jaxb.model.v3.release.error.OrcidError v3OrcidError) {
        this.v3OrcidError = v3OrcidError;
    }
    
    public OrcidError getOrcidError() {
        return orcidError;
    }

    public org.orcid.jaxb.model.v3.release.error.OrcidError getV3OrcidError() {
        return v3OrcidError;
    }
}
