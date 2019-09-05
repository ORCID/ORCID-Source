package org.orcid.listener.exception;

public class LockedRecordException extends RecordException {

    /**
     * 
     */
    private static final long serialVersionUID = -3110450520936529486L;

    public LockedRecordException(org.orcid.jaxb.model.error_v2.OrcidError orcidError) {
        super(orcidError);
    }

    public LockedRecordException(org.orcid.jaxb.model.v3.release.error.OrcidError v3OrcidError) {
        super(v3OrcidError);
    }  
          
}
