package org.orcid.core.utils.v3.activities;

import java.util.Collections;
import java.util.Comparator;

import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.grouping.FundingGroup;

public class FundingComparators {

    private static final String TITLE_SORT_KEY = "title";

    private static final String DATE_SORT_KEY = "date";

    private static final String TYPE_SORT_KEY = "type";
    
    public static Comparator<FundingGroup> getInstance(String key, boolean sortAsc) {
        Comparator<FundingGroup> comparator = null;
        if (DATE_SORT_KEY.equals(key)) {
            comparator = FundingComparators.DATE_COMPARATOR;
        } else if (TITLE_SORT_KEY.equals(key)) {
            comparator = FundingComparators.TITLE_COMPARATOR;
        } else if (TYPE_SORT_KEY.equals(key)) {
            comparator = FundingComparators.TYPE_COMPARATOR;
        }
        
        if (sortAsc) {
            return comparator;
        } else {
            return Collections.reverseOrder(comparator);
        }
    }

    public static Comparator<FundingGroup> TITLE_COMPARATOR = (g1, g2) -> {
        if (g1.getTitle() == null && g2.getTitle() == null) {
            return 0;
        }
        if (g1.getTitle() == null) {
            return -1;
        }
        
        if (g2.getTitle() == null) {
            return 1;
        }
        return g1.getTitle().toLowerCase().compareTo(g2.getTitle().toLowerCase());
    };

    public static Comparator<FundingGroup> TYPE_COMPARATOR = (g1, g2) -> {
        FundingForm f1 = g1.getFundings().get(0);
        FundingForm f2 = g2.getFundings().get(0);
        
        if (f1.getFundingType() == null && f2.getFundingType() == null) {
            return TITLE_COMPARATOR.compare(g1, g2);
        }
        if (f1.getFundingType() == null) {
            return -1;
        }
        
        if (f2.getFundingType() == null) {
            return 1;
        }
        
        return f1.getFundingType().getValue().compareTo(f2.getFundingType().getValue());
    };
    
    public static Comparator<FundingGroup> END_DATE_COMPARATOR = (g1, g2) -> {
        if (g1.getEndDate() == null && g2.getEndDate() == null)
            return TITLE_COMPARATOR.compare(g1, g2);
        //Null = to present and should sort first
        if (g1.getEndDate() == null)
            return 1;
        if (g2.getEndDate() == null)
            return -1;
        if (g1.getEndDate().compareTo(g2.getEndDate()) == 0){
            return TITLE_COMPARATOR.compare(g1, g2);
        }
        return g1.getEndDate().compareTo(g2.getEndDate());
    };

    public static Comparator<FundingGroup> DATE_COMPARATOR = (g1, g2) -> {
        if (g1.getStartDate() == null && g2.getStartDate() == null) {
            return TITLE_COMPARATOR.compare(g1, g2);
        }
        if (g1.getStartDate() == null) {
            return -1;
        }
        
        if (g2.getStartDate() == null) {
            return 1;
        }
        if (g1.getStartDate().compareTo(g2.getStartDate()) == 0){
            return END_DATE_COMPARATOR.compare(g1, g2);
        }
        return g1.getStartDate().compareTo(g2.getStartDate());
    };

}
