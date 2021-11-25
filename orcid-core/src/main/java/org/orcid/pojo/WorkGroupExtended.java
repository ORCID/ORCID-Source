package org.orcid.pojo;

import io.swagger.annotations.ApiModel;
import org.orcid.jaxb.model.v3.release.record.GroupableActivity;
import org.orcid.jaxb.model.v3.release.record.summary.ActivityGroup;

import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = { "lastModifiedDate", "identifiers", "workSummary" })
@XmlRootElement(name = "work-group", namespace = "http://www.orcid.org/ns/activities")
@ApiModel(value = "WorkGroupExtendedV3_0")
public class WorkGroupExtended extends ActivityGroup implements Serializable {

    private static final long serialVersionUID = -6172489241759247746L;
    @XmlElement(name = "work-summary", namespace = "http://www.orcid.org/ns/work")
    private List<WorkSummaryExtended> workSummary;

    public List<WorkSummaryExtended> getWorkSummary() {
        if (workSummary == null)
            workSummary = new ArrayList<WorkSummaryExtended>();
        return workSummary;
    }

    @Override
    public Collection<? extends GroupableActivity> getActivities() {
        return getWorkSummary();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((workSummary == null) ? 0 : workSummary.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkGroupExtended other = (WorkGroupExtended) obj;
        if (workSummary == null) {
            if (other.workSummary != null)
                return false;
        } else if (!workSummary.equals(other.workSummary))
            return false;
        return true;
    }
}

