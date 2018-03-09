package org.orcid.core.utils.v3.activities;

import java.util.Comparator;

import org.orcid.jaxb.model.v3.dev1.common.Title;
import org.orcid.jaxb.model.v3.dev1.record.summary.WorkGroup;
import org.orcid.jaxb.model.v3.dev1.record.summary.WorkSummary;
import org.orcid.jaxb.model.v3.dev1.record.WorkTitle;
import org.orcid.jaxb.model.v3.dev1.record.WorkType;
import org.orcid.pojo.ajaxForm.PojoUtil;

/**
 * 
 * @author Will Simpson
 *
 */
public class WorkComparators {

    private static Comparator<WorkSummary> DISPLAY_INDEX = (w1, w2) -> {
        return w1.compareTo(w2);
    };

    private static Comparator<WorkSummary> REVERSE_CHRONOLOGICAL = (w1, w2) -> {
        String dateString1 = PojoUtil.createDateSortString(null, w1.getPublicationDate());
        String dateString2 = PojoUtil.createDateSortString(null, w2.getPublicationDate());
        return -dateString1.compareTo(dateString2);
    };

    private static Comparator<String> TITLE_CONTENT = Comparator.nullsLast(Comparator.<String> naturalOrder());

    private static Comparator<Title> TITLE = Comparator.nullsLast((t1, t2) -> {
        return TITLE_CONTENT.compare(t1.getContent(), t2.getContent());
    });

    private static Comparator<WorkTitle> WORK_TITLE = Comparator.nullsLast((t1, t2) -> {
        return TITLE.compare(t1.getTitle(), t2.getTitle());
    });

    private static Comparator<WorkSummary> WORK_SUMMARY_WORK_TITLE = Comparator.nullsLast((w1, w2) -> {
        return WORK_TITLE.compare(w1.getTitle(), w2.getTitle());
    });

    private static Comparator<WorkType> WORK_TYPE = Comparator.nullsLast(Comparator.<WorkType> naturalOrder());

    private static Comparator<WorkSummary> WORK_SUMMARY_WORK_TYPE = Comparator.nullsLast((w1, w2) -> {
        return WORK_TYPE.compare(w1.getType(), w2.getType());
    });

    public static Comparator<WorkSummary> ALL_EXCEPT_DISPLAY_INDEX = REVERSE_CHRONOLOGICAL.thenComparing(WORK_SUMMARY_WORK_TITLE).thenComparing(WORK_SUMMARY_WORK_TYPE);

    public static Comparator<WorkSummary> ALL = DISPLAY_INDEX.thenComparing(ALL_EXCEPT_DISPLAY_INDEX);

    public static Comparator<WorkGroup> GROUP = (g1, g2) -> {
        WorkSummary w1 = (WorkSummary) g1.getActivities().iterator().next();
        WorkSummary w2 = (WorkSummary) g2.getActivities().iterator().next();
        return WorkComparators.ALL_EXCEPT_DISPLAY_INDEX.compare(w1, w2);
    };

}
