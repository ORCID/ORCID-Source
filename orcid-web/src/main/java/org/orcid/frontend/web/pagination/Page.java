package org.orcid.frontend.web.pagination;

import java.io.Serializable;
import java.util.List;

import org.orcid.pojo.WorkGroup;

public class Page<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private int nextOffset;
    
    private int totalGroups;
    
    private List<T> workGroups;

    public List<T> getWorkGroups() {
        return workGroups;
    }

    public void setWorkGroups(List<T> workGroups) {
        this.workGroups = workGroups;
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
