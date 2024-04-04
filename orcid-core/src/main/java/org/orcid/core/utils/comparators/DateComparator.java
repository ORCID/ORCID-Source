package org.orcid.core.utils.comparators;

import java.util.Comparator;

import org.orcid.jaxb.model.v3.release.common.PublicationDate;
import org.orcid.jaxb.model.v3.release.record.summary.WorkGroup;

public class DateComparator implements Comparator<WorkGroup> {
    @Override
    public int compare(WorkGroup o1, WorkGroup o2) {
        PublicationDate date1 = o1.getWorkSummary().get(0).getPublicationDate();
        PublicationDate date2 = o2.getWorkSummary().get(0).getPublicationDate();
        if (date1 == null && date2 == null) {
            return new TitleComparator().compare(o1, o2) * -1; // reverse
                                                               // secondary
                                                               // order
        }

        if (date1 == null) {
            return -1;
        }

        if (date2 == null) {
            return 1;
        }
        if (date1.compareTo(date2) == 0) {
            return new TitleComparator().compare(o1, o2) * -1; // reverse
                                                               // secondary
                                                               // order
        }

        return o1.getWorkSummary().get(0).getPublicationDate().compareTo(o2.getWorkSummary().get(0).getPublicationDate());
    }
}
