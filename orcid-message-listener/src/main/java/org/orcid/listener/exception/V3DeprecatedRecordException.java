package org.orcid.listener.exception;

import org.orcid.jaxb.model.v3.rc2.error.OrcidError;

public class V3DeprecatedRecordException extends Exception {
    private static final long serialVersionUID = 1L;

    OrcidError orcidError;

    public V3DeprecatedRecordException(OrcidError orcidError) {
        this.orcidError = orcidError;
    }

    public OrcidError getOrcidError() {
        return orcidError;
    }
}
