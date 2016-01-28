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
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc2.LastModifiedDate;

/**
 * 
 * @author Angel Montenegro
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"lastModifiedDate", "givenPermissionTo", "givenPermissionBy" })
@XmlRootElement(name = "delegation", namespace = "http://www.orcid.org/ns/person")
public class Delegation implements Serializable {
    private static final long serialVersionUID = 1646402749205402278L;        
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/person", name = "given-permission-to")
    protected List<GivenPermissionTo> givenPermissionTo;
    @XmlElement(namespace = "http://www.orcid.org/ns/person", name = "given-permission-by")
    protected List<GivenPermissionBy> givenPermissionBy;

    public List<GivenPermissionTo> getGivenPermissionTo() {
        return givenPermissionTo;
    }

    public void setGivenPermissionTo(List<GivenPermissionTo> givenPermissionTo) {
        this.givenPermissionTo = givenPermissionTo;
    }

    public List<GivenPermissionBy> getGivenPermissionBy() {
        return givenPermissionBy;
    }

    public void setGivenPermissionBy(List<GivenPermissionBy> givenPermissionBy) {
        this.givenPermissionBy = givenPermissionBy;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((givenPermissionBy == null) ? 0 : givenPermissionBy.hashCode());
        result = prime * result + ((givenPermissionTo == null) ? 0 : givenPermissionTo.hashCode());
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
        Delegation other = (Delegation) obj;
        if (givenPermissionBy == null) {
            if (other.givenPermissionBy != null)
                return false;
        } else if (!givenPermissionBy.equals(other.givenPermissionBy))
            return false;
        if (givenPermissionTo == null) {
            if (other.givenPermissionTo != null)
                return false;
        } else if (!givenPermissionTo.equals(other.givenPermissionTo))
            return false;
        return true;
    }

	public LastModifiedDate getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
