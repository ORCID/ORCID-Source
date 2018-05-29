package org.orcid.core.exception;

public class OrcidElementCantBeDeletedException extends ApplicationException {

    private static final long serialVersionUID = 5048947096027869417L;

    public OrcidElementCantBeDeletedException(String message) {
        super(message);
    }
}
