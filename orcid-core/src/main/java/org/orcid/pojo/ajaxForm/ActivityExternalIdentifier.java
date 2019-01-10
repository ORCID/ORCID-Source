package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.common.Relationship;
import org.orcid.jaxb.model.v3.rc2.common.Url;
import org.orcid.jaxb.model.v3.rc2.record.ExternalID;

public class ActivityExternalIdentifier implements ErrorsInterface, Serializable {
    
    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text externalIdentifierId;

    private Text externalIdentifierType;
    
    private Text url;
    
    private Text relationship;
    
    private Text normalized;
    
    public Text getNormalized() {
        return normalized;
    }

    public void setNormalized(Text normalized) {
        this.normalized = normalized;
    }

    private Text normalizedUrl;
    
    public Text getNormalizedUrl() {
        return normalizedUrl;
    }

    public void setNormalizedUrl(Text normalizedUrl) {
        this.normalizedUrl = normalizedUrl;
    }

    public static ActivityExternalIdentifier valueOf(ExternalID externalIdentifier) {
        ActivityExternalIdentifier wi = new ActivityExternalIdentifier();
        if (externalIdentifier != null) {
            if (externalIdentifier.getValue() != null)
                wi.setExternalIdentifierId(Text.valueOf(externalIdentifier.getValue()));
            if (externalIdentifier.getType() != null)
                wi.setExternalIdentifierType(Text.valueOf(externalIdentifier.getType()));
            if(externalIdentifier.getRelationship() != null)
                wi.setRelationship(Text.valueOf(externalIdentifier.getRelationship().value()));
            if(externalIdentifier.getUrl() != null)
                wi.setUrl(Text.valueOf(externalIdentifier.getUrl().getValue()));
            if(externalIdentifier.getNormalized() != null){
                wi.setNormalized(Text.valueOf(externalIdentifier.getNormalized().getValue()));
                wi.getNormalized().setRequired(false);
            }
            if(externalIdentifier.getNormalizedUrl() != null){
                wi.setNormalizedUrl(Text.valueOf(externalIdentifier.getNormalizedUrl().getValue()));
                wi.getNormalizedUrl().setRequired(false);
            }
        }
        return wi;

    }
    
    public ExternalID toExternalIdentifier() {
        ExternalID we = new ExternalID();
        if (!PojoUtil.isEmpty(this.getExternalIdentifierId())) 
            we.setValue(this.getExternalIdentifierId().getValue());
        if (!PojoUtil.isEmpty(this.getExternalIdentifierType()))
            we.setType(this.getExternalIdentifierType().getValue());
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

    public Text getExternalIdentifierId() {
        return externalIdentifierId;
    }

    public void setExternalIdentifierId(Text externalIdentifierId) {
        this.externalIdentifierId = externalIdentifierId;
    }

    public Text getExternalIdentifierType() {
        return externalIdentifierType;
    }

    public void setExternalIdentifierType(Text externalIdentifierType) {
        this.externalIdentifierType = externalIdentifierType;
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

    //NOTE: Ignores normalized values
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((externalIdentifierId == null) ? 0 : externalIdentifierId.hashCode());
        result = prime * result + ((externalIdentifierType == null) ? 0 : externalIdentifierType.hashCode());
        return result;
    }

    //NOTE: Ignores normalized values
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ActivityExternalIdentifier other = (ActivityExternalIdentifier) obj;
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
        if (externalIdentifierId == null) {
            if (other.externalIdentifierId != null)
                return false;
        } else if (!externalIdentifierId.equals(other.externalIdentifierId))
            return false;
        if (externalIdentifierType == null) {
            if (other.externalIdentifierType != null)
                return false;
        } else if (!externalIdentifierType.equals(other.externalIdentifierType))
            return false;
        return true;
    }          
}
