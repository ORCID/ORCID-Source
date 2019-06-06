package org.orcid.listener.exception;

import org.orcid.jaxb.model.v3.release.error.OrcidError;

public class V3LockedRecordException extends Exception {
    private static final long serialVersionUID = 1L;

    OrcidError orcidError;

    public V3LockedRecordException(OrcidError orcidError) {
        this.orcidError = orcidError;
    }

    public OrcidError getOrcidError() {
        return orcidError;
    }
}
