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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.common_rc1.CreatedDate;
import org.orcid.jaxb.model.common_rc1.Filterable;
import org.orcid.jaxb.model.common_rc1.LastModifiedDate;
import org.orcid.jaxb.model.common_rc1.Source;
import org.orcid.jaxb.model.common_rc1.Visibility;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "email", "createdDate", "lastModifiedDate", "source" })
@XmlRootElement(name = "email", namespace = "http://www.orcid.org/ns/email")
public class Email implements Filterable, Serializable, SourceAware {
    private static final long serialVersionUID = 7986448691143979246L;
    @XmlElement(namespace = "http://www.orcid.org/ns/email")
    protected String email;
    @XmlElement(namespace = "http://www.orcid.org/ns/common")
    protected Source source;
    @XmlAttribute
    protected Visibility visibility;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "last-modified-date")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(namespace = "http://www.orcid.org/ns/common", name = "created-date")
    protected CreatedDate createdDate;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }    

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((lastModifiedDate == null) ? 0 : lastModifiedDate.hashCode());
        result = prime * result + ((source == null) ? 0 : source.hashCode());
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
        Email other = (Email) obj;

        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;

        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
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
