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
