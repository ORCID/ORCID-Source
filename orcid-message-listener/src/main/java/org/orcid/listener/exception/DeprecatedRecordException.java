package org.orcid.listener.exception;

public class DeprecatedRecordException extends RecordException {

    /**
     * 
     */
    private static final long serialVersionUID = -7131616904201657225L;

    public DeprecatedRecordException(org.orcid.jaxb.model.error_v2.OrcidError orcidError) {
        super(orcidError);
    }

    public DeprecatedRecordException(org.orcid.jaxb.model.v3.release.error.OrcidError v3OrcidError) {
        super(v3OrcidError);
    }

}
