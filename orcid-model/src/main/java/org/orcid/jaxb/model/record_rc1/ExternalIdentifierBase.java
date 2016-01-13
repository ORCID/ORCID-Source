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
package org.orcid.jaxb.model.record_rc1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc1.Url;

import com.fasterxml.jackson.annotation.JsonIgnore;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "url", "relationship" })
public abstract class ExternalIdentifierBase implements GroupAble { 
    @XmlElement(name="relationship", namespace = "http://www.orcid.org/ns/common")
    protected Relationship relationship;
    @XmlElement(name="external-identifier-url", namespace = "http://www.orcid.org/ns/common")
    protected Url url;
    
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
    
    @Override
    @JsonIgnore
    public boolean isGroupAble() {
        //Dont group if it is a part-of identifier
        if(Relationship.PART_OF.equals(relationship))
            return false;
        return true;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((relationship == null) ? 0 : relationship.hashCode());
        result = prime * result + ((url == null) ? 0 : url.hashCode());
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
        ExternalIdentifierBase other = (ExternalIdentifierBase) obj;
        if (relationship != other.relationship)
            return false;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }
    
}
