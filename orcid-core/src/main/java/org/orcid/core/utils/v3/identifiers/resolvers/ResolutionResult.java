/**
 * =============================================================================
 *
 * ORCID (R) Open Source
 * http://orcid.org
 *
 * Copyright (c) 2012-2014 ORCID, Inc.
 * Licensed under an MIT-Style License (MIT)
 * http://orcid.org/open-source-license
 *
 * This copyright and license information (including a link to the full license)
 * shall be included in its entirety in all copies or substantial portion of
 * the software.
 *
 * =============================================================================
 */
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
