package org.orcid.jaxb.model.v3.rc1.record;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import org.apache.commons.lang.StringUtils;
import org.orcid.jaxb.model.v3.rc1.common.TransientError;
import org.orcid.jaxb.model.v3.rc1.common.TransientNonEmptyString;
import org.orcid.jaxb.model.v3.rc1.common.Url;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.annotations.ApiModel;

/**
 * New external identifier class
 * 
 * @author tom
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "type", "value", "normalized", "normalizedError", "url", "relationship" })
@ApiModel(value = "ExternalIDV3_0_rc1")
public class ExternalID implements GroupAble, Cloneable, Serializable {
    private static final long serialVersionUID = -8081196017392914381L;
    @XmlElement(name = "external-id-type", namespace = "http://www.orcid.org/ns/common", required = true)
    protected String type;
    @XmlElement(name = "external-id-value", namespace = "http://www.orcid.org/ns/common", required = true)
    protected String value;
    @XmlElement(name = "external-id-normalized", namespace = "http://www.orcid.org/ns/common")
    protected TransientNonEmptyString normalized;
    @XmlElement(name = "external-id-normalized-error", namespace = "http://www.orcid.org/ns/common")
    protected TransientError normalizedError;    
    @XmlElement(name = "external-id-url", namespace = "http://www.orcid.org/ns/common")
    protected Url url;
    @XmlElement(name = "external-id-relationship", namespace = "http://www.orcid.org/ns/common")
    protected Relationship relationship;
    
    @JsonIgnore
    @XmlTransient
    protected TransientNonEmptyString normalizedUrl;

    @JsonIgnore
    @XmlTransient
    protected TransientError normalizedUrlError;    

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public TransientNonEmptyString getNormalized() {
        return normalized;
    }

    public void setNormalized(TransientNonEmptyString normalized) {
        this.normalized = normalized;
    }

    public TransientError getNormalizedError() {
        return normalizedError;
    }
    
    public void setNormalizedError(TransientError normalizeError) {
        this.normalizedError = normalizeError;
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public TransientNonEmptyString getNormalizedUrl() {
        return normalizedUrl;
    }

    public void setNormalizedUrl(TransientNonEmptyString normalizedUrl) {
        this.normalizedUrl = normalizedUrl;
    }

    public TransientError getNormalizedUrlError() {
        return normalizedUrlError;
    }

    public void setNormalizedUrlError(TransientError normalizedUrlError) {
        this.normalizedUrlError = normalizedUrlError;
    }
    
    /**
     * If we have a normalized value, use that to generate Group ID;
     */
    @Override
    @JsonIgnore
    public String getGroupId() {
        String workIdVal = null;
        if (this.getNormalized() != null && !StringUtils.isEmpty(this.getNormalized().getValue())) {
            workIdVal = this.getNormalized().getValue();
        } else {
            workIdVal = this.value == null ? null : this.value;
        }
        String typeVal = this.type == null ? null : this.type;
        return workIdVal + typeVal;
    }

    @Override
    @JsonIgnore
    public boolean isGroupAble() {
        // Dont group if it is a part-of identifier
        if (Relationship.PART_OF.equals(relationship))
            return false;

        // Dont groups works where the external id is empty
        if (this.getValue() == null || this.getValue().isEmpty())
            return false;

        return true;
    }

    public static ExternalID fromMessageExtId(org.orcid.jaxb.model.message.WorkExternalIdentifier oldExtId) {
        ExternalID id = new ExternalID();
        id.setType(oldExtId.getWorkExternalIdentifierType().value());
        id.setValue(oldExtId.getWorkExternalIdentifierId().getContent());
        return id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        if (this.getNormalized() != null && !StringUtils.isEmpty(this.getNormalized().getValue())) {
            result = prime * result + this.getNormalized().hashCode();
        } else {
            result = prime * result + ((value == null) ? 0 : value.hashCode());
        }
        //ignore normalizedError for equality checking/hash
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
        ExternalID other = (ExternalID) obj;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        if (this.getNormalized() != null && !StringUtils.isEmpty(this.getNormalized().getValue())) {
            // if we have a normalized value, use that.
            if (!getNormalized().equals(other.getNormalized()))
                return false;
        } else {
            // otherwise check the provided value
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
        }
        //ignore normalizedError for equality checking/hash
        return true;
    }

    public ExternalID clone() {
        ExternalID id = new ExternalID();
        id.type = this.getType();
        id.value = this.getValue();
        if (this.getNormalized() != null)
            id.setNormalized(this.getNormalized());
        if (this.getNormalizedError() != null)
            id.setNormalizedError(this.getNormalizedError());
        if (this.getUrl() != null)
            id.url = new Url(this.getUrl().getValue());
        if (this.getRelationship() != null)
            id.relationship = this.getRelationship();
        if (this.getNormalizedUrl() != null)
            id.setNormalizedUrl(this.getNormalizedUrl());
        if (this.getNormalizedUrlError() != null)
            id.setNormalizedUrlError(this.getNormalizedUrlError());
        return id;
    }

    @Override
    public String toString() {
        return "ExternalID [type=" + type + ", value=" + value + ", normalized=" + normalized + ", normalizedError=" + normalizedError + ", url=" + url
                + ", relationship=" + relationship + ", normalizedUrl=" + normalizedUrl + ", normalizedUrlError=" + normalizedUrlError + "]";
    }

}
