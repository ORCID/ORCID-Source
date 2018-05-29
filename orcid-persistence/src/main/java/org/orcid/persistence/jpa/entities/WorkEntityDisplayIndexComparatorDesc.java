package org.orcid.persistence.jpa.entities;

import java.io.Serializable;
import java.util.Comparator;

/**
 * 
 * @author Angel Montenegro
 * 
 */
public class WorkEntityDisplayIndexComparatorDesc<T> implements Comparator<WorkEntity>, Serializable {

    private static final long serialVersionUID = 1L;
    @Override
    public int compare(WorkEntity o1, WorkEntity o2) {
        Long index = o1.getDisplayIndex();
        Long otherIndex = o2.getDisplayIndex();
        if (index == otherIndex) return o2.compareTo(o1);
        if (index == null) return 1;
        if (otherIndex == null) return -1;
        return otherIndex.compareTo(index);
    }
}