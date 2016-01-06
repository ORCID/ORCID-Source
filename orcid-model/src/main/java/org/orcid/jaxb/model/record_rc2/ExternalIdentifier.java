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
package org.orcid.jaxb.model.record_rc2;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common.CreatedDate;
import org.orcid.jaxb.model.common.Filterable;
import org.orcid.jaxb.model.common.LastModifiedDate;
import org.orcid.jaxb.model.common.Source;
import org.orcid.jaxb.model.common.Url;
import org.orcid.jaxb.model.common.Visibility;

import io.swagger.annotations.ApiModelProperty;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "commonName", "reference", "url", "createdDate", "lastModifiedDate", "source" })
@XmlRootElement(name = "external-identifier", namespace = "http://www.orcid.org/ns/external-identifier")
public class ExternalIdentifier implements Serializable, Filterable {
    private static final long serialVersionUID = 8340033850223164314L;
    @XmlElement(name="external-id-common-name", namespace = "http://www.orcid.org/ns/external-identifier")
    protected String commonName;
    @XmlElement(name="external-id-reference", namespace = "http://www.orcid.org/ns/external-identifier")
    protected String reference;
    @XmlElement(namespace = "http://www.orcid.org/ns/external-identifier")
    protected Url url;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Source source;        
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "created-date")
    protected CreatedDate createdDate;    
    @XmlAttribute(name = "put-code")
    @ApiModelProperty(hidden = true)
    protected Long putCode;        
    @XmlAttribute
    protected Visibility visibility;
    @XmlAttribute
    protected String path;
    public String getCommonName() {
        return commonName;
    }
    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }
    public String getReference() {
        return reference;
    }
    public void setReference(String reference) {
        this.reference = reference;
    }
    public Url getUrl() {
        return url;
    }
    public void setUrl(Url url) {
        this.url = url;
    }
    public Source getSource() {
        return source;
    }
    public void setSource(Source source) {
        this.source = source;
    }
    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }
    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
    public CreatedDate getCreatedDate() {
        return createdDate;
    }
    public void setCreatedDate(CreatedDate createdDate) {
        this.createdDate = createdDate;
    }
    public Long getPutCode() {
        return putCode;
    }
    public void setPutCode(Long putCode) {
        this.putCode = putCode;
    }
    public Visibility getVisibility() {
        return visibility;
    }
    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((commonName == null) ? 0 : commonName.hashCode());
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + ((path == null) ? 0 : path.hashCode());
        result = prime * result + ((putCode == null) ? 0 : putCode.hashCode());
        result = prime * result + ((reference == null) ? 0 : reference.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((visibility == null) ? 0 : visibility.hashCode());
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
        ExternalIdentifier other = (ExternalIdentifier) obj;
        if (commonName == null) {
            if (other.commonName != null)
                return false;
        } else if (!commonName.equals(other.commonName))
            return false;
        if (createdDate == null) {
            if (other.createdDate != null)
                return false;
        } else if (!createdDate.equals(other.createdDate))
            return false;
        if (lastModifiedDate == null) {
            if (other.lastModifiedDate != null)
                return false;
        } else if (!lastModifiedDate.equals(other.lastModifiedDate))
            return false;
        if (path == null) {
            if (other.path != null)
                return false;
        } else if (!path.equals(other.path))
            return false;
        if (putCode == null) {
            if (other.putCode != null)
                return false;
        } else if (!putCode.equals(other.putCode))
            return false;
        if (reference == null) {
            if (other.reference != null)
                return false;
        } else if (!reference.equals(other.reference))
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        if (visibility != other.visibility)
            return false;
        return true;
    }
    @Override
    public String retrieveSourcePath() {
        if (source != null) {
            return source.retrieveSourcePath();
        }
        return null;
    }        
}
