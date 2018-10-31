package org.orcid.jaxb.model.v3.rc2.record.summary;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.orcid.jaxb.model.v3.rc2.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.rc2.record.Group;
import org.orcid.jaxb.model.v3.rc2.record.GroupsContainer;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "researchResourceGroup" })
@XmlRootElement(name = "research-resources", namespace = "http://www.orcid.org/ns/activities")
public class ResearchResources implements GroupsContainer, Serializable {

    private static final long serialVersionUID = 1L;
    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(name = "group", namespace = "http://www.orcid.org/ns/activities")
    private List<ResearchResourceGroup> researchResourceGroup;
    @XmlAttribute
    protected String path;

    public List<ResearchResourceGroup> getResearchResourceGroup() {
        if (researchResourceGroup == null) {
            researchResourceGroup = new ArrayList<ResearchResourceGroup>();
        }
        return researchResourceGroup;
    }

    @Override
    public Collection<? extends Group> retrieveGroups() {
        return getResearchResourceGroup();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((researchResourceGroup == null) ? 0 : researchResourceGroup.hashCode());
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
        ResearchResources other = (ResearchResources) obj;
        if (researchResourceGroup == null) {
            if (other.researchResourceGroup != null)
                return false;
        } else if (!researchResourceGroup.equals(other.researchResourceGroup))
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
