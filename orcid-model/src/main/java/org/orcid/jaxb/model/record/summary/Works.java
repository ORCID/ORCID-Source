package org.orcid.jaxb.model.record.summary;

import java.io.Serializable;

public class Works implements Serializable {

    private static final long serialVersionUID = 3293976926416154039L;
    WorkGroup workGroup;

    public WorkGroup getWorkGroup() {
        return workGroup;
    }

    public void setWorkGroup(WorkGroup workGroup) {
        this.workGroup = workGroup;
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
        Works other = (Works) obj;
        if (workGroup == null) {
            if (other.workGroup != null)
                return false;
        } else if (!workGroup.equals(other.workGroup))
            return false;
        return true;
    }

}
