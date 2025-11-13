package org.orcid.core.utils.v3.activities;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.orcid.core.utils.v3.SourceUtils;
import org.orcid.pojo.ajaxForm.FundingForm;
import org.orcid.pojo.grouping.FundingGroup;
import org.orcid.pojo.grouping.PeerReviewGroup;

public class FundingComparators {

    private final String TITLE_SORT_KEY = "title";

    private final String DATE_SORT_KEY = "date";

    private final String TYPE_SORT_KEY = "type";
    
    private final String START_DATE_KEY = "start";
    
    private final String END_DATE_KEY = "end";

    public FundingComparators() {}

    public Comparator<FundingGroup> getInstance(String key, boolean sortAsc, String orcid) {
        Comparator<FundingGroup> comparator = null;
        if (DATE_SORT_KEY.equals(key)) {
            comparator = FundingComparators.DATE_COMPARATOR;
        } else if (TITLE_SORT_KEY.equals(key)) {
            comparator = FundingComparators.TITLE_COMPARATOR;
        } else if (TYPE_SORT_KEY.equals(key)) {
            comparator = FundingComparators.TYPE_COMPARATOR;
        } else if (START_DATE_KEY.equals(key)) {
            comparator = FundingComparators.START_DATE_COMPARATOR;
        } else if (END_DATE_KEY.equals(key)) {
            comparator = FundingComparators.END_DATE_COMPARATOR;
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
    
    public static Comparator<FundingGroup> START_DATE_COMPARATOR = (g1, g2) -> {
        if (g1.getStartDate() == null && g2.getStartDate() == null)
            return TITLE_COMPARATOR.compare(g1, g2) * -1; // reverse secondary order;;
        //Null = to present and should sort first
        if (g1.getStartDate() == null)
            return 1;
        if (g2.getStartDate() == null)
            return -1;
        if (g1.getStartDate().compareTo(g2.getStartDate()) == 0){
            return TITLE_COMPARATOR.compare(g1, g2) * -1; // reverse secondary order;;
        }
        return g1.getStartDate().compareTo(g2.getStartDate());
    };
    
    public static Comparator<FundingGroup> END_DATE_COMPARATOR = (g1, g2) -> {
        if (g1.getEndDate() == null && g2.getEndDate() == null)
            return TITLE_COMPARATOR.compare(g1, g2) * -1; // reverse secondary order;;
        //Null = to present and should sort first
        if (g1.getEndDate() == null)
            return 1;
        if (g2.getEndDate() == null)
            return -1;
        if (g1.getEndDate().compareTo(g2.getEndDate()) == 0){
            return TITLE_COMPARATOR.compare(g1, g2) * -1; // reverse secondary order;;
        }
        return g1.getEndDate().compareTo(g2.getEndDate());
    };

    public static Comparator<FundingGroup> DATE_COMPARATOR = (g1, g2) -> {
        if (g1.getStartDate() == null && g2.getStartDate() == null) {
            return TITLE_COMPARATOR.compare(g1, g2) * -1; // reverse secondary order;;
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

    public List<FundingGroup> sortBySource(List<FundingGroup> fundingGroups, boolean sortAsc, String orcid) {
        List<FundingGroup> selfAsserted = fundingGroups.stream()
                .filter(fundingGroup -> SourceUtils.isSelfAsserted(fundingGroup.getDefaultFunding(), orcid))
                .collect(Collectors.toList());

        List<FundingGroup> validated = fundingGroups.stream()
                .filter(fundingGroup -> !SourceUtils.isSelfAsserted(fundingGroup.getDefaultFunding(), orcid))
                .collect(Collectors.toList());

        selfAsserted.sort(new FundingComparators().TITLE_COMPARATOR);
        validated.sort(new FundingComparators().TITLE_COMPARATOR);

        return (sortAsc ? Stream.concat(validated.stream(), selfAsserted.stream()) : Stream.concat(selfAsserted.stream(), validated.stream()))
                .collect(Collectors.toList());
    }
}
