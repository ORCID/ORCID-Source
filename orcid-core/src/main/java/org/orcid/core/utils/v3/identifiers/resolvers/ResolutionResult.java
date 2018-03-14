package org.orcid.core.utils.v3.identifiers.resolvers;

public class ResolutionResult {
    private Boolean attemptedResolution;
    private String resolvedUrl;
    
    public ResolutionResult(Boolean attemptedResolution, String resolvedUrl) {
        super();
        this.attemptedResolution = attemptedResolution;
        this.resolvedUrl = resolvedUrl;
    }
    
    public Boolean isResolved() {
        return (resolvedUrl != null) ;
    }
    public Boolean getAttemptedResolution() {
        return attemptedResolution;
    }

    public String getResolvedUrl() {
        return resolvedUrl;
    }
}
