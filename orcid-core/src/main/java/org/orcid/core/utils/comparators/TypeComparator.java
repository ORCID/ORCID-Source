package org.orcid.core.utils.comparators;

import java.util.Comparator;

import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;

public class TypeComparator implements Comparator<WorkGroup>{
    @Override
    public int compare(org.orcid.jaxb.model.v3.release.record.summary.WorkGroup o1, org.orcid.jaxb.model.v3.release.record.summary.WorkGroup o2) {
        if (o1.getWorkSummary().get(0).getType() == null && o2.getWorkSummary().get(0).getType() == null) {
            return 0;
        }
        
        if (o1.getWorkSummary().get(0).getType() == null) {
            return -1;
        }
        
        if (o2.getWorkSummary().get(0).getType() == null) {
            return 1;
        }
        
        return o1.getWorkSummary().get(0).getType().name().compareTo(o2.getWorkSummary().get(0).getType().name());
    }
}
