package org.orcid.core.utils.comparators;

import java.util.Comparator;

import org.orcid.pojo.WorkGroupExtended;

public class TypeComparatorWorkGroupExtended implements Comparator<WorkGroupExtended> {
    @Override
    public int compare(WorkGroupExtended o1, WorkGroupExtended o2) {
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
