package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.common.Url;
import org.orcid.jaxb.model.message.WorkExternalIdentifierId;
import org.orcid.jaxb.model.message.WorkExternalIdentifierType;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.Relationship;

public class WorkExternalIdentifier implements ErrorsInterface, Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text workExternalIdentifierId;

    private Text workExternalIdentifierType;
    
    private Text url;
    
    private Text relationship;
    
    public static WorkExternalIdentifier valueOf(org.orcid.jaxb.model.message.WorkExternalIdentifier workExternalIdentifier) {
        WorkExternalIdentifier wi = new WorkExternalIdentifier();
        if (workExternalIdentifier != null) {
            if (workExternalIdentifier.getWorkExternalIdentifierId() != null)
                wi.setWorkExternalIdentifierId(Text.valueOf(workExternalIdentifier.getWorkExternalIdentifierId().getContent()));
            if (workExternalIdentifier.getWorkExternalIdentifierType() != null)
                wi.setWorkExternalIdentifierType(Text.valueOf(workExternalIdentifier.getWorkExternalIdentifierType().value()));            
        }                
        return wi;

    }
    
    public static WorkExternalIdentifier valueOf(org.orcid.jaxb.model.record_v2.ExternalID workExternalIdentifier) {
        WorkExternalIdentifier wi = new WorkExternalIdentifier();
        if (workExternalIdentifier != null) {
            if (workExternalIdentifier.getValue() != null)
                wi.setWorkExternalIdentifierId(Text.valueOf(workExternalIdentifier.getValue()));
            if (workExternalIdentifier.getType() != null)
                wi.setWorkExternalIdentifierType(Text.valueOf(workExternalIdentifier.getType()));
            if(workExternalIdentifier.getRelationship() != null)
                wi.setRelationship(Text.valueOf(workExternalIdentifier.getRelationship().value()));
            if(workExternalIdentifier.getUrl() != null)
                wi.setUrl(Text.valueOf(workExternalIdentifier.getUrl().getValue()));
        }
        return wi;

    }
    
    public static WorkExternalIdentifier valueOf(org.orcid.jaxb.model.v3.dev1.record.ExternalID workExternalIdentifier) {
        WorkExternalIdentifier wi = new WorkExternalIdentifier();
        if (workExternalIdentifier != null) {
            if (workExternalIdentifier.getValue() != null)
                wi.setWorkExternalIdentifierId(Text.valueOf(workExternalIdentifier.getValue()));
            if (workExternalIdentifier.getType() != null)
                wi.setWorkExternalIdentifierType(Text.valueOf(workExternalIdentifier.getType()));
            if(workExternalIdentifier.getRelationship() != null)
                wi.setRelationship(Text.valueOf(workExternalIdentifier.getRelationship().value()));
            if(workExternalIdentifier.getUrl() != null)
                wi.setUrl(Text.valueOf(workExternalIdentifier.getUrl().getValue()));
        }
        return wi;

    }
    
    public org.orcid.jaxb.model.message.WorkExternalIdentifier toWorkExternalIdentifier() {
        org.orcid.jaxb.model.message.WorkExternalIdentifier we = new org.orcid.jaxb.model.message.WorkExternalIdentifier();
        if (!PojoUtil.isEmpty(this.getWorkExternalIdentifierId())) 
            we.setWorkExternalIdentifierId(new WorkExternalIdentifierId(this.getWorkExternalIdentifierId().getValue()));
        if (!PojoUtil.isEmpty(this.getWorkExternalIdentifierType()))
            we.setWorkExternalIdentifierType(WorkExternalIdentifierType.fromValue(this.getWorkExternalIdentifierType().getValue()));
        return we;
    }
    
    public org.orcid.jaxb.model.v3.dev1.record.ExternalID toRecordWorkExternalIdentifier() {
        ExternalID we = new ExternalID();
        if (!PojoUtil.isEmpty(this.getWorkExternalIdentifierId())) 
            we.setValue(this.getWorkExternalIdentifierId().getValue());
        if (!PojoUtil.isEmpty(this.getWorkExternalIdentifierType()))
            we.setType(this.getWorkExternalIdentifierType().getValue());
        if(!PojoUtil.isEmpty(this.getRelationship())) 
            we.setRelationship(Relationship.fromValue(this.getRelationship().getValue()));
        if(!PojoUtil.isEmpty(this.getUrl()))
            we.setUrl(new Url(this.getUrl().getValue()));
        return we;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Text getWorkExternalIdentifierId() {
        return workExternalIdentifierId;
    }

    public void setWorkExternalIdentifierId(Text workExternalIdentifierId) {
        this.workExternalIdentifierId = workExternalIdentifierId;
    }

    public Text getWorkExternalIdentifierType() {
        return workExternalIdentifierType;
    }

    public void setWorkExternalIdentifierType(Text workExternalIdentifierType) {
        this.workExternalIdentifierType = workExternalIdentifierType;
    }
    
    public Text getUrl() {
        return url;
    }

    public void setUrl(Text url) {
        this.url = url;
    }

    public Text getRelationship() {
        return relationship;
    }

    public void setRelationship(Text relationship) {
        this.relationship = relationship;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((workExternalIdentifierId == null) ? 0 : workExternalIdentifierId.hashCode());
        result = prime * result + ((workExternalIdentifierType == null) ? 0 : workExternalIdentifierType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkExternalIdentifier other = (WorkExternalIdentifier) obj;
        if (relationship == null) {
            if (other.relationship != null)
                return false;
        } else if (!relationship.equals(other.relationship))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (workExternalIdentifierId == null) {
            if (other.workExternalIdentifierId != null)
                return false;
        } else if (!workExternalIdentifierId.equals(other.workExternalIdentifierId))
            return false;
        if (workExternalIdentifierType == null) {
            if (other.workExternalIdentifierType != null)
                return false;
        } else if (!workExternalIdentifierType.equals(other.workExternalIdentifierType))
            return false;
        return true;
    }          
}
