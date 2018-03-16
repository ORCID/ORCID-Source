package org.orcid.pojo.ajaxForm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.orcid.jaxb.model.v3.dev1.common.Url;
import org.orcid.jaxb.model.v3.dev1.record.ExternalID;
import org.orcid.jaxb.model.v3.dev1.record.Relationship;

public class AffiliationExternalIdentifier implements ErrorsInterface, Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> errors = new ArrayList<String>();

    private Text type;
    
    private Text value;
    
    private Text url;
    
    private Text relationship;

    public static AffiliationExternalIdentifier valueOf(ExternalID externalID) {
        AffiliationExternalIdentifier identifier = new AffiliationExternalIdentifier();
        if (externalID != null) {
            if (externalID.getValue() != null)
                identifier.setValue(Text.valueOf(externalID.getValue()));
            if (externalID.getType() != null)
                identifier.setType(Text.valueOf(externalID.getType()));
            if (externalID.getRelationship() != null)
                identifier.setRelationship(Text.valueOf(externalID.getRelationship().value()));
            if (externalID.getUrl() != null)
                identifier.setUrl(Text.valueOf(externalID.getUrl().getValue()));
        }
        return identifier;

    }

    public ExternalID toExternalID() {
        ExternalID externalID = new ExternalID();
        if (!PojoUtil.isEmpty(value))
            externalID.setValue(value.getValue());
        if (!PojoUtil.isEmpty(type))
            externalID.setType(type.getValue());
        if (!PojoUtil.isEmpty(relationship))
            externalID.setRelationship(Relationship.fromValue(relationship.getValue()));
        if (!PojoUtil.isEmpty(url))
            externalID.setUrl(new Url(url.getValue()));
        return externalID;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
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
    
    public Text getType() {
        return type;
    }

    public void setType(Text type) {
        this.type = type;
    }

    public Text getValue() {
        return value;
    }

    public void setValue(Text value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        AffiliationExternalIdentifier other = (AffiliationExternalIdentifier) obj;
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
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }
}
