package org.orcid.scheduler.loader.source;

public class LoadSourceDisabledException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoadSourceDisabledException(String sourceType) {
        super(sourceType + " load source is disabled");
    }
    
}
