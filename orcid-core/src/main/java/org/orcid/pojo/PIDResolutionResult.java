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
package org.orcid.pojo;

public class PIDResolutionResult {
    private Boolean resolved;
    private Boolean attemptedResolution;
    private Boolean isNormalizable;
    private String generatedUrl;
    
    public static PIDResolutionResult NOT_ATTEMPTED = new PIDResolutionResult(false,false,false,null);
    
    public PIDResolutionResult(Boolean resolved, Boolean attemptedResolution, Boolean validFormat, String generatedUrl) {
        super();
        this.resolved=resolved;
        this.attemptedResolution = attemptedResolution;
        this.generatedUrl = generatedUrl;
        this.isNormalizable = validFormat;
    }
    
    public Boolean isResolved() {
        return resolved;
    }
    public Boolean getAttemptedResolution() {
        return attemptedResolution;
    }

    public String getGeneratedUrl() {
        return generatedUrl;
    }

    public Boolean isValidFormat() {
        return isNormalizable;
    }

}
