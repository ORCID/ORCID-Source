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
package org.orcid.core.adapter.jsonidentifier;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties("scope")
public class JSONWorkExternalIdentifiers implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private List<JSONWorkExternalIdentifier> workExternalIdentifier;

    public List<JSONWorkExternalIdentifier> getWorkExternalIdentifier() {
        if (workExternalIdentifier == null) {
            workExternalIdentifier = new ArrayList<>();
        }
        return workExternalIdentifier;
    }

    public void setWorkExternalIdentifier(List<JSONWorkExternalIdentifier> workExternalIdentifier) {
        this.workExternalIdentifier = workExternalIdentifier;
    }
    
}
