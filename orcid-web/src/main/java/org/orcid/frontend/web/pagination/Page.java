package org.orcid.frontend.web.pagination;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int nextOffset;
    
    private int totalGroups;
    
    private List<T> groups;

    public List<T> getGroups() {
        return groups;
    }

    public void setGroups(List<T> workGroups) {
        this.groups = workGroups;
    }

    public void addWorkGroup(T workGroup) {
        if(groups == null) {
            groups = new ArrayList<T>();
        }
        groups.add(workGroup);
    }
    
    public int getNextOffset() {
        return nextOffset;
    }

    public void setNextOffset(int nextOffset) {
        this.nextOffset = nextOffset;
    }

    public int getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(int totalGroups) {
        this.totalGroups = totalGroups;
    }

}
