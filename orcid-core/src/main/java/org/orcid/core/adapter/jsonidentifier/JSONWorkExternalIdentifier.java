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

public class JSONWorkExternalIdentifier implements Serializable {

    private static final long serialVersionUID = 1L;

    private String relationship;
    
    private JSONUrl url;
    
    private String workExternalIdentifierType;
    
    private WorkExternalIdentifierId workExternalIdentifierId;
    
    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public JSONUrl getUrl() {
        return url;
    }

    public void setUrl(JSONUrl url) {
        this.url = url;
    }

    public String getWorkExternalIdentifierType() {
        return workExternalIdentifierType;
    }

    public void setWorkExternalIdentifierType(String workExternalIdentifierType) {
        this.workExternalIdentifierType = workExternalIdentifierType;
    }

    public WorkExternalIdentifierId getWorkExternalIdentifierId() {
        return workExternalIdentifierId;
    }

    public void setWorkExternalIdentifierId(WorkExternalIdentifierId workExternalIdentifierId) {
        this.workExternalIdentifierId = workExternalIdentifierId;
    }

    public static class WorkExternalIdentifierId implements Serializable {
        private static final long serialVersionUID = 1L;
        public String content;

        public WorkExternalIdentifierId() {
        }

        public WorkExternalIdentifierId(String value) {
            this.content = value;
        }
    }

}
