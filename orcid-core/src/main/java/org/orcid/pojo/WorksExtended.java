package org.orcid.pojo;


import org.orcid.jaxb.model.v3.release.common.LastModifiedDate;
import org.orcid.jaxb.model.v3.release.record.Group;
import org.orcid.jaxb.model.v3.release.record.GroupsContainer;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "workGroup" })
@XmlRootElement(name = "works", namespace = "http://www.orcid.org/ns/activities")
public class WorksExtended implements GroupsContainer, Serializable {

    private static final long serialVersionUID = 3293976926416154039L;
    @XmlElement(name = "last-modified-date", namespace = "http://www.orcid.org/ns/common")
    protected LastModifiedDate lastModifiedDate;
    @XmlElement(name = "group", namespace = "http://www.orcid.org/ns/activities")
    private List<WorkGroupExtended> workGroup;
    @XmlAttribute
    protected String path;

    public List<WorkGroupExtended> getWorkGroup() {
        if (workGroup == null) {
            workGroup = new ArrayList<WorkGroupExtended>();
        }
        return workGroup;
    }

    @Override
    public Collection<? extends Group> retrieveGroups() {
        return getWorkGroup();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((workGroup == null) ? 0 : workGroup.hashCode());
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
        WorksExtended other = (WorksExtended) obj;
        if (workGroup == null) {
            if (other.workGroup != null)
                return false;
        } else if (!workGroup.equals(other.workGroup))
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
