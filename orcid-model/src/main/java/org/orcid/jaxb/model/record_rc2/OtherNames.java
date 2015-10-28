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

import org.orcid.jaxb.model.common.LastModifiedDate;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType( propOrder = { "otherNames", "lastModifiedDate" })
@XmlRootElement(name = "other-names", namespace = "http://www.orcid.org/ns/other-name")
public class OtherNames implements Serializable {        
    private static final long serialVersionUID = 6312730308815255894L;
    
    @XmlElement(name = "other-name", namespace = "http://www.orcid.org/ns/other-name")
    List<OtherName> otherNames;
    @XmlElement(name="last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;

    public List<OtherName> getOtherNames() {
        return otherNames;
    }

    public void setOtherNames(List<OtherName> otherNames) {
        this.otherNames = otherNames;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((otherNames == null) ? 0 : otherNames.hashCode());
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
        OtherNames other = (OtherNames) obj;
        if (otherNames == null) {
            if (other.otherNames != null)
                return false;
        } else if (!otherNames.equals(other.otherNames))
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
