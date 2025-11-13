package org.orcid.core.utils.comparators;

import java.util.Comparator;

import org.orcid.pojo.WorkGroupExtended;
import org.orcid.pojo.WorkSummaryExtended;

public class TitleComparatorWorkGroupExtended implements Comparator<WorkGroupExtended> {
    @Override
    public int compare(WorkGroupExtended o1, WorkGroupExtended o2) {
        String firstTitle = getTitle(o1.getWorkSummary().get(0));
        String secondTitle = getTitle(o2.getWorkSummary().get(0));

        if (firstTitle == null && secondTitle != null) {
            return -1;
        }

        if (secondTitle == null && firstTitle != null) {
            return 1;
        }

        int comparison = 0;
        if (firstTitle != null && secondTitle != null) {
            comparison = firstTitle.compareTo(secondTitle);
        }

        if (comparison == 0) {
            String firstSubtitle = getSubtitle(o1.getWorkSummary().get(0));
            String secondSubtitle = getSubtitle(o2.getWorkSummary().get(0));

            if (firstSubtitle == null && secondSubtitle == null) {
                return 0;
            }

            if (firstSubtitle == null) {
                return -1;
            }

            if (secondSubtitle == null) {
                return 1;
            }

            comparison = firstSubtitle.compareTo(secondSubtitle);
        }
        return comparison;
    }

    private String getTitle(WorkSummaryExtended workSummary) {
        if (workSummary.getTitle() == null) {
            return null;
        }

        if (workSummary.getTitle().getTitle() == null) {
            return null;
        }

        if (workSummary.getTitle().getTitle().getContent() == null) {
            return null;
        }

        return workSummary.getTitle().getTitle().getContent().toLowerCase();
    }

    private String getSubtitle(WorkSummaryExtended workSummary) {
        if (workSummary.getTitle() == null) {
            return null;
        }

        if (workSummary.getTitle().getSubtitle() == null) {
            return null;
        }

        if (workSummary.getTitle().getSubtitle().getContent() == null) {
            return null;
        }

        return workSummary.getTitle().getSubtitle().getContent().toLowerCase();
    }
}
