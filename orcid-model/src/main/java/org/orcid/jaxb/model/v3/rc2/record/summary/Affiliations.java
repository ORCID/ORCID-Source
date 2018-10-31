package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.record.GroupsContainer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "groups" })
public abstract class Affiliations<T extends AffiliationSummary> implements GroupsContainer {
    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;
    
    @XmlElement(name = "affiliation-group", namespace = "http://www.orcid.org/ns/activities")
    protected Collection<AffiliationGroup<T>> groups;
   
    @XmlAttribute
    protected String path;

    public abstract Collection<AffiliationGroup<T>> retrieveGroups();

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((groups == null) ? 0 : groups.hashCode());
        return result;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Affiliations other = (Affiliations) obj;
        if (groups == null) {
            if (other.groups != null)
                return false;
        } else if (!groups.equals(other.groups))
            return false;
        return true;
    }

    public LastModifiedDate getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LastModifiedDate lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
